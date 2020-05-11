package com.wk.iocbeanlifecycle;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 *  此类可以实现吧一个bean注册到容器中
 */
public class WkImportBeanDefinition implements ImportBeanDefinitionRegistrar{
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
										BeanDefinitionRegistry registry) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition(InstB.class);
		registry.registerBeanDefinition("instb", beanDefinition);
	}
}
