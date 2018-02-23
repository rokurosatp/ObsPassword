package com.github.obsproth.prismkey.common.generator;

import java.util.Arrays;

import com.github.obsproth.prismkey.ServiceElement;
import com.github.obsproth.prismkey.common.hasher.SaltStretchHasher;
import com.github.obsproth.prismkey.common.reductor.Base64Reductor;
import com.github.obsproth.prismkey.common.reductor.IReductor;

public class GeneratorV1 extends AbstractGenerator{

	private static final String SEED_SALT = "ObsPassword";
	private static final int SEEDHASH_LENGTH = 8;
	private static final int STRETCHING = 123456;

	public GeneratorV1() {
		hasher = new SaltStretchHasher(SEED_SALT, SEEDHASH_LENGTH, STRETCHING);
		reductor = new Base64Reductor();
	}

}
