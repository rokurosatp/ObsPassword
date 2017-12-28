package com.github.obsproth.prismkey.common.reductor;

import java.util.Base64;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ExBase58Reductor implements IReductor{

	private static final String NUMBERS = "123456789";
	private static final String CAPS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
	private static final String SMALLS = "abcdefghijkmnopqrstuvwxyz";
	private static final int LIMIT = 64;

	private boolean allowNumbers, allowCaps, allowSmalls;
	private String symbols;

	public ExBase58Reductor(boolean allowNumbers, boolean allowCaps, boolean allowSmalls, String symbols){
		this.allowNumbers = allowNumbers;
		this.allowCaps = allowCaps;
		this.allowSmalls = allowSmalls;
		this.symbols = symbols;
	}

	private static int[] convertBase(int[] unsignedHash, int base){
		int tmp = 0;
		int tmpMax = 0;
		int[] converted = new int[unsignedHash.length * 256 / base];
		int p = 0;
		for (int i = 0; i < unsignedHash.length; i++) {
			tmp += unsignedHash[i];
			tmpMax += 256;
			//
			while(tmpMax >= base){
				converted[p++] = tmp % base;
				tmp /= base;
				tmpMax /= base;
			}
		}
		return converted;
	}

	private static char[] intToChar(int[] converted, String charset){
		char[] password = new char[converted.length];
		for (int i = 0; i < converted.length; i++) {
			password[i] = charset.charAt(converted[i]);
		}
		return password;
	}
	
	char[] resizedPassword(char[] password, int length) {
		char[] subChars = Arrays.copyOfRange(password, 0, length);
		Arrays.fill(password, '\n');
		return subChars;
	}

	public char[] generate(byte[] hash, int length) {
		int[] unsignedHash = new int[hash.length];
		for (int i = 0; i < hash.length; i++) {
			unsignedHash[i] = hash[i] & 0xFF;
		}
		StringBuilder sb = new StringBuilder(LIMIT);
		if (allowNumbers) {
			sb.append(NUMBERS);
		}
		if (allowCaps) {
			sb.append(CAPS);
		}
		if (allowSmalls) {
			sb.append(SMALLS);
		}
		sb.append(symbols);
		String charset = sb.toString();
		int base = charset.length();
		assert 0 < base && base <= LIMIT;
		int[] converted = convertBase(unsignedHash, base);
		char[] fullPassword = intToChar(converted, charset);
		char[] password = this.resizedPassword(fullPassword, length);
		Arrays.fill(unsignedHash, 0);
		Arrays.fill(converted, 0);
		Arrays.fill(fullPassword, '0');
		return password;
	}
}
