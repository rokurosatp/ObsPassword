package com.github.obsproth.obspassword;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.JPasswordField;

public class HashUtil {

	private static final String HASH_ALGO = "SHA-512";
	private static final String BASE_SALT = "ObsPassword";
	private static final int STRETCHING = 123456;

	public static String getBaseHashStr(JPasswordField passwordField) {
		byte[] passByte = calcHash(passwordField, BASE_SALT, 0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < passByte.length; i++) {
			sb.append(String.format("%02x", passByte[i]));
		}
		return sb.toString();
	}

	public static byte[] calcHash(JPasswordField passwordField, String name, int length) {
		char[] password = passwordField.getPassword();
		byte[] passByte = new byte[password.length + name.length() + 1];
		Arrays.fill(passByte, (byte) 0);
		int i;
		for (i = 0; i < password.length; i++) {
			passByte[i] = (byte) password[i];
		}
		Arrays.fill(password, (char) 0);
		int offset = i;
		for (i = 0; i < name.length(); i++) {
			passByte[i + offset] = (byte) name.charAt(i);
		}
		passByte[passByte.length - 1] = (byte) length;
		//
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(HASH_ALGO);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[] passByte_s;
		for (i = 0; i < STRETCHING; i++) {
			passByte_s = md.digest(passByte);
			Arrays.fill(passByte, (byte) 0);
			passByte = passByte_s;
		}
		return passByte;
	}
}
