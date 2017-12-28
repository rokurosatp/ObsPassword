package com.github.obsproth.prismkey.common.hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SaltStretchHasher {

	private static final String HASH_ALGORITHM = "SHA-512";
	
	private MessageDigest md;
	private String seedSalt;
	private int seedHashLength;
	private int stretching;

	public SaltStretchHasher(String seedSalt, int seedHashLength, int stretching) {
		this.seedSalt = seedSalt;
		this.seedHashLength = seedHashLength;
		this.stretching = stretching;
		try {
			md = MessageDigest.getInstance(HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getSeedDigestStr(char[] password) {
		byte[] seedDigest = digest(password, seedSalt, 0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seedHashLength / 2; i++) {
			sb.append(String.format("%02x", seedDigest[i]));
		}
		return sb.toString().substring(0, seedHashLength);
	}

	public byte[] digest(char[] password, String name, int length) {
		byte[] tmp = toOriginalByteArray(password, name, length);
		byte[] tmp_s;
		for (int i = 0; i < stretching; i++) {
			tmp_s = md.digest(tmp);
			Arrays.fill(tmp, (byte) 0);
			tmp = tmp_s;
		}
		return tmp;
	}
	
	private byte[] toOriginalByteArray(char[] password, String name, int length){
		byte[] origin = new byte[password.length + name.length() + 1];
		int i;
		for (i = 0; i < password.length; i++) {
			origin[i] = (byte) password[i];
		}
		final int offset = i;
		for (i = 0; i < name.length(); i++) {
			origin[i + offset] = (byte) name.charAt(i);
		}
		origin[origin.length - 1] = (byte) length;
		return origin;
	}

}
