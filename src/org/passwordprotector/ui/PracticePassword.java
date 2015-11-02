package org.passwordprotector.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.passwordprotector.PasswordPair;

public class PracticePassword {
	private static ArrayList<PasswordPair> b;

	public static void decrypt(SecretKey keySpec) throws IOException {

		InputStream in = new FileInputStream("myfile.out");
		int numPass = in.read();

		System.out.println("Number of Passwords: " + numPass);
		System.out.print("ArrayList after: ");
		for (int k = 0; k < numPass; k++) {
			PasswordPair p = PasswordPair.decryptPasswordPair(keySpec, in);
			b.add(p);
			System.out.print(p.getPassword() + "  ");
		}

	}

	public static void encrypt(SecretKey keySpec) throws IOException {
		OutputStream out = new FileOutputStream("myfile.out");
		out.write(b.size());
		System.out.print("ArrayList before: ");
		for (PasswordPair p : b) {
			PasswordPair.encryptPasswordPair(keySpec, p, out);
			System.out.print(p.getPassword() + "  ");
		}
		out.close();
		System.out.println();
	}

	public static void main(String[] args) {
		b = new ArrayList<>();
		String loginPass = "blehqwertqwerqwe";

		b.add(new PasswordPair("A Practice Password", "henryiscool", "justkidding"));
		b.add(new PasswordPair("Second try", "see if this works", "hmmm"));

		SecretKeySpec keySpec = new SecretKeySpec(loginPass.getBytes(), "AES");
		try {
			decrypt(keySpec);
		} catch (IOException e) {
			System.out.println("writing bad");
		}
		try {
			encrypt(keySpec);
		} catch (IOException e) {
			System.out.println("writing bad");
		}

		b.clear();

	}
}
