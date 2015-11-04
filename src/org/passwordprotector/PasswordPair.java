package org.passwordprotector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javafx.collections.ObservableList;

public class PasswordPair {
	public static final String DEF_PASSWORD = "password";

	public static PasswordPair decryptPasswordPair(SecretKey masterPassword, InputStream in) throws IOException {
		try {
			DataInputStream dis = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
			Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Load the description, not encrypted.
			int descriptLength = dis.readInt();
			byte[] description = new byte[descriptLength];
			dis.readFully(description);

			// Load the username and password, which are encrypted.
			String userName = decryptString(masterPassword, decrypter, dis);
			String password = decryptString(masterPassword, decrypter, dis);

			return new PasswordPair(new String(description, "UTF-8"), userName, password);
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	public static void encryptPasswordPair(SecretKey masterPassword, PasswordPair ppair, OutputStream out) throws IOException {
		try {
			Cipher encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			DataOutputStream dos = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);

			// Load the description, not encrypted.
			byte[] description = ppair.description.getBytes("UTF-8");
			dos.writeInt(description.length);
			dos.write(description);

			// Load the username and password, which are encrypted.
			encryptString(masterPassword, encrypter, ppair.userName, dos);
			encryptString(masterPassword, encrypter, ppair.password, dos);
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Loads all the accounts located in the file specified into the list
	 *
	 * @param accounts the output list of accounts
	 * @param accountsFile the input file that contains the accounts.
	 * @param password the password that protects the accounts
	 * @throws IOException when an I/O error occurred while reading the accounts
	 * @throws PasswordInvalidException when the user inputs an invalid password.
	 */
	@SuppressWarnings("unused")
	public static void loadAccounts(ObservableList<PasswordPair> accounts, File accountsFile, byte[] password) throws IOException, PasswordInvalidException {
		MessageDigest digest;

		// Load message digest, this algorithm SHOULD exist.
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Unable to load message digest");
		}

		if (!accountsFile.exists()) {
			Arrays.equals(password, DEF_PASSWORD.getBytes("UTF-8"));
			return;
		}

		try (DataInputStream in = new DataInputStream(new FileInputStream(accountsFile))) {
			byte[] salt = new byte[8];
			byte[] passCheckSum = new byte[digest.getDigestLength()];
			boolean verified = false;
			in.readFully(salt);
			in.readFully(passCheckSum);

			for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
				digest.reset();
				digest.update(salt);
				digest.update(password);
				digest.update((byte) i);
				byte[] passDigest = digest.digest();

				verified = Arrays.equals(passCheckSum, passDigest);
				if (verified) {
					break;
				}
				Arrays.fill(passDigest, (byte) 0);
			}

			if (!verified) {
				throw new PasswordInvalidException("Invalid password");
			}

			byte[] passDigest = new byte[16];
			System.arraycopy(digest.digest(password), 0, passDigest, 0, passDigest.length);
			SecretKeySpec keySpec = new SecretKeySpec(passDigest, "AES");
			Arrays.fill(passDigest, (byte) 0);

			int numPass = in.readInt();
			for (int i = 0; i < numPass; i++) {
				PasswordPair p = PasswordPair.decryptPasswordPair(keySpec, in);
				accounts.add(p);
			}
		}
	}

	/**
	 * Saves all the accounts from a list into a file.
	 *
	 * @param accounts the list of accounts
	 * @param accountsFile the file to save to
	 * @param password the password that protects the accounts
	 * @throws IOException when an I/O error occurred while reading the accounts
	 */
	@SuppressWarnings("unused")
	public static void saveAccounts(ObservableList<PasswordPair> accounts, File accountsFile, byte[] password) throws IOException {
		SecureRandom random;
		MessageDigest digest;

		// Load message digest and secure randomizer. These SHOULD exist.
		try {
			digest = MessageDigest.getInstance("SHA-256");
			random = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Unable to initialize password.");
		}

		byte[] passDigest = new byte[16];
		System.arraycopy(digest.digest(password), 0, passDigest, 0, passDigest.length);
		SecretKeySpec keySpec = new SecretKeySpec(passDigest, "AES");
		Arrays.fill(passDigest, (byte) 0);

		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(accountsFile.toPath()))) {
			byte[] salt = new byte[8];
			byte pepper;

			random.nextBytes(salt);
			pepper = (byte) random.nextInt();

			digest.reset();
			digest.update(salt);
			digest.update(password);
			digest.update(pepper);

			pepper = 0;

			out.write(salt);
			out.write(digest.digest());

			ArrayList<PasswordPair> accountsCopy = new ArrayList<>(accounts);
			out.writeInt(accountsCopy.size());

			for (PasswordPair account : accountsCopy) {
				account.encrypt(keySpec, out);
			}

			accountsCopy.clear();
		}
	}

	private static String decryptString(SecretKey key, Cipher decrypter, DataInputStream dis) throws GeneralSecurityException, IOException {
		byte[] iv = new byte[decrypter.getBlockSize()];
		dis.readFully(iv);

		int length = dis.readInt();
		byte[] data = new byte[length];
		dis.readFully(data);

		decrypter.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return new String(decrypter.doFinal(data), "UTF-8");
	}

	private static void encryptString(SecretKey key, Cipher encrypter, String data, DataOutputStream dis) throws GeneralSecurityException, IOException {
		encrypter.init(Cipher.ENCRYPT_MODE, key);
		dis.write(encrypter.getIV());

		byte[] encrypted = encrypter.doFinal(data.getBytes("UTF-8"));
		dis.writeInt(encrypted.length);
		dis.write(encrypted);
	}

	private String description;
	private String userName;
	private String password;

	public PasswordPair() {
		this("", "", "");
	}

	public PasswordPair(String description) {
		this(description, "", "");
	}

	public PasswordPair(String description, String userName) {
		this(description, userName, "");
	}

	public PasswordPair(String description, String userName, String password) {
		super();
		this.description = description;
		this.userName = userName;
		this.password = password;
	}

	public void encrypt(SecretKey masterPassword, OutputStream out) throws IOException {
		encryptPasswordPair(masterPassword, this, out);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return description;
	}

}
