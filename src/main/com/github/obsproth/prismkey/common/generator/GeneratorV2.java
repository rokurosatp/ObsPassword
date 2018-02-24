package com.github.obsproth.prismkey.common.generator;

import java.util.Iterator;
import java.util.List;

import com.github.obsproth.prismkey.common.hasher.SaltStretchHasher;
import com.github.obsproth.prismkey.common.reductor.ExBase58Reductor;

public class GeneratorV2 extends AbstractGenerator {

	private static final String SEED_SALT = "PrismKeyV2";
	private static final int SEEDHASH_LENGTH = 8;
	private static final int STRETCHING = 123456;
	
	public static final String DEFAULT_SYMBOLS = "!#$&@+*.";

	public GeneratorV2(List<String> config) {
		hasher = new SaltStretchHasher(SEED_SALT, SEEDHASH_LENGTH, STRETCHING);
		Iterator<String> iter = config.iterator();
		boolean allowNumbers = Boolean.parseBoolean(iter.next());
		boolean allowCaps = Boolean.parseBoolean(iter.next());
		boolean allowSmalls = Boolean.parseBoolean(iter.next());
		String symbols = iter.next();
		reductor = new ExBase58Reductor(allowNumbers, allowCaps, allowSmalls, symbols);
	}

}
