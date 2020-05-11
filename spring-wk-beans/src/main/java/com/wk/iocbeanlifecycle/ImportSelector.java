package com.wk.iocbeanlifecycle;

import org.springframework.core.type.AnnotationMetadata;

public class ImportSelector implements org.springframework.context.annotation.ImportSelector{
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.wk.iocbeanlifecycle.InstC"};
	}
}
