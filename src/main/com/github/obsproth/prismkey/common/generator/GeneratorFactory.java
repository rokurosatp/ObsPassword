package com.github.obsproth.prismkey.common.generator;

import com.github.obsproth.prismkey.ServiceElement;

public class GeneratorFactory {

	private static GeneratorV1 generatorV1 = null;

	private static GeneratorV1 getGeneratorV1() {
		if (generatorV1 == null) {
			generatorV1 = new GeneratorV1();
		}
		return generatorV1;
	}

	public static AbstractGenerator getGenerator(ServiceElement serviceElement) {
		switch (serviceElement.getVersion()) {
		case 1:
			return getGeneratorV1();
		case 2:
			return new GeneratorV2(serviceElement.config);
		default:
			throw new UnsupportedOperationException();
		}
	}
}
