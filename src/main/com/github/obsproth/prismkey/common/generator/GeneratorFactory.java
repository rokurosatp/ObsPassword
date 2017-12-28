package com.github.obsproth.prismkey.common.generator;

import com.github.obsproth.prismkey.ServiceElement;

public class GeneratorFactory {

	public static GeneratorV1 getGenerator(ServiceElement serviceElement){
		return new GeneratorV1();
	}
}
