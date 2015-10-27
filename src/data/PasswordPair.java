package data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class PasswordPair {

	public static PasswordPair decryptPasswordPair(SecretKey masterPassword, InputStream in) throws IOException {
		try {
			DataInputStream dis = new DataInputStream(in);
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
			DataOutputStream dos = new DataOutputStream(out);

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
