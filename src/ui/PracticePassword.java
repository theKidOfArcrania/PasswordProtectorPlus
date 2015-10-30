package ui;


import java.io.*;
import java.util.*;

import javax.crypto.spec.SecretKeySpec;

import data.*;

public class PracticePassword {
	public static void main (String []args) throws IOException
	{
		String loginPass = "blehqwertqwerqwe";
		List<PasswordPair> b = new ArrayList<PasswordPair>();
		b.add(new PasswordPair("A Practice Password","henryiscool","justkidding"));
		b.add(new PasswordPair("Second try","see if this works","hmmm"));
		SecretKeySpec keySpec = new SecretKeySpec(loginPass.getBytes(), "AES");
		
		OutputStream out = new FileOutputStream("myfile.out");
		out.write(b.size());
		System.out.print("ArrayList before: ");
		for(PasswordPair p:b)
		{
			PasswordPair.encryptPasswordPair(keySpec, p, out);
			System.out.print(p.getPassword()  + "  ");
		}
		System.out.println();
		b.clear();
	
		InputStream in = new FileInputStream("myfile.out");
		int numPass = in.read();
		
		System.out.println("Number of Passwords: " + numPass);
		System.out.print("ArrayList after: ");
		for(int k = 0; k<numPass;k++)
		{
			PasswordPair p = PasswordPair.decryptPasswordPair(keySpec, in);
			b.add(p);
			System.out.print(p.getPassword() + "  ");
		}
		
		
		

	}
}
