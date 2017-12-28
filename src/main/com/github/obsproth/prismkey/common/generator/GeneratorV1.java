package com.github.obsproth.prismkey.common.generator;

import java.util.Arrays;

import com.github.obsproth.prismkey.ServiceElement;
import com.github.obsproth.prismkey.common.hasher.SaltStretchHasher;
import com.github.obsproth.prismkey.common.reductor.Base64Reductor;
import com.github.obsproth.prismkey.common.reductor.IReductor;

public class GeneratorV1 {

	private static final String SEED_SALT = "ObsPassword";
	private static final int SEEDHASH_LENGTH = 8;
	private static final int STRETCHING = 123456;

	private SaltStretchHasher hasher;
	private IReductor reductor;

	public GeneratorV1() {
		hasher = new SaltStretchHasher(SEED_SALT, SEEDHASH_LENGTH, STRETCHING);
		reductor = new Base64Reductor();
	}

	public String getSeedDigestStr(char[] key){
		return hasher.getSeedDigestStr(key);
	}
	
	public boolean verifySeed(char[] key, ServiceElement element) {
		String seed1 = getSeedDigestStr(key);
		String seed2 = element.getBaseHash();
		return seed1.equals(seed2);
	}

	public char[] generate(char[] seedPassword, ServiceElement element) {
		byte[] digest = hasher.digest(seedPassword, element.getServiceName(), element.getLength());
		char[] password = reductor.generate(digest, element.getLength());
		Arrays.fill(digest, (byte) 0);
		return password;
	}

}
