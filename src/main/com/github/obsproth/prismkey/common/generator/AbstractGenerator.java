package com.github.obsproth.prismkey.common.generator;

import java.util.Arrays;

import com.github.obsproth.prismkey.ServiceElement;
import com.github.obsproth.prismkey.common.hasher.SaltStretchHasher;
import com.github.obsproth.prismkey.common.reductor.IReductor;

public abstract class AbstractGenerator {

	protected SaltStretchHasher hasher;
	protected IReductor reductor;

	public String getSeedDigestStr(char[] key) {
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
