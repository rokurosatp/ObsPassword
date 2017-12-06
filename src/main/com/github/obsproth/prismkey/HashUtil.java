package com.github.obsproth.prismkey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HashUtil {

	private static final String HASH_ALGO = "SHA-512";
	private static final String BASE_SALT = "ObsPassword";
	private static final int STRETCHING = 123456;
	public static final int BASEHASH_LENGTH = 8;

	public static String getBaseHashStr(char[] password) {
		return getBaseHashStr(password, true);
	}

	public static String getBaseHashStr(char[] password, boolean clear) {
		byte[] passByte = calcHash(password, clear, BASE_SALT, 0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < passByte.length; i++) {
			sb.append(String.format("%02x", passByte[i]));
		}
		return sb.toString().substring(0, BASEHASH_LENGTH);
	}

	public static byte[] calcHash(char[] password, String name, int length) {
		return calcHash(password, true, name, length);
	}

	public static byte[] calcHash(char[] password, boolean clear, String name, int length) {
		byte[] passByte = new byte[password.length + name.length() + 1];
		int i;
		for (i = 0; i < password.length; i++) {
			passByte[i] = (byte) password[i];
		}
		int offset = i;
		for (i = 0; i < name.length(); i++) {
			passByte[i + offset] = (byte) name.charAt(i);
		}
		passByte[passByte.length - 1] = (byte) length;
		//
		if (clear) {
			Arrays.fill(password, (char) 0);
		}
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
