package com.github.obsproth.prismkey.common.generator;

import java.util.List;

import com.github.obsproth.prismkey.PrismKey;
import com.github.obsproth.prismkey.ServiceElement;

public class GeneratorFactory {

	private static GeneratorV1 generatorV1 = null;

	private static GeneratorV1 getGeneratorV1() {
		if (generatorV1 == null) {
			generatorV1 = new GeneratorV1();
		}
		return generatorV1;
	}

	public static AbstractGenerator getLatestGenerator(List<String> config){
		return getGenerator(PrismKey.ALGO_VERSION, config);
	}
	
	public static AbstractGenerator getGenerator(ServiceElement serviceElement) {
		return getGenerator(serviceElement.getVersion(), serviceElement.config);
	}

	private static AbstractGenerator getGenerator(int version, List<String> config) {
		switch (version) {
		case 1:
			return getGeneratorV1();
		case 2:
			return new GeneratorV2(config);
		default:
			throw new UnsupportedOperationException();
		}
	}
}
