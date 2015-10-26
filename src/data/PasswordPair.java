package data;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class PasswordPair {

	public static void encryptPasswordPair(SecretKey masterPassword, InputStream ) throws GeneralSecurityException, UnsupportedEncodingException {
		Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");

		//Load the description, not encrypted.
		int descriptLength = buf.getInt();
		byte[] description = new byte[descriptLength];
		buf.get(description);

		//Load the username and password, which are encrypted.
		String userName = decodeString(masterPassword, decrypter, buf);
		String password = decodeString(masterPassword, decrypter, buf);

		return new PasswordPair(new String(description, "UTF-8"), userName, password);
	}

	public static PasswordPair decryptPasswordPair(SecretKey masterPassword, ByteBuffer buf) throws GeneralSecurityException, UnsupportedEncodingException {
		Cipher decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");

		// Load the description, not encrypted.
		int descriptLength = buf.getInt();
		byte[] description = new byte[descriptLength];
		buf.get(description);

		// Load the username and password, which are encrypted.
		String userName = decodeString(masterPassword, decrypter, buf);
		String password = decodeString(masterPassword, decrypter, buf);

		return new PasswordPair(new String(description, "UTF-8"), userName, password);
	}

	private static String decodeString(SecretKey key, Cipher decrypter, ByteBuffer buf) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] iv = new byte[decrypter.getBlockSize()];
		buf.get(iv);

		int length = buf.getInt();
		byte[] data = new byte[length];

		decrypter.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return new String(decrypter.doFinal(data), "UTF-8");
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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
