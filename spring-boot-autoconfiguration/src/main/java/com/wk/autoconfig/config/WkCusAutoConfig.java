package com.wk.autoconfig.config;

import com.wk.autoconfig.annotaion.WkEnableAutoConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

public class WkCusAutoConfig implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		/**
		 *  此就是柑根据getCandidateConfigurations返回的类的信息,把这些类批量加载到ioc容器中
		 */
		List<String> configurations = getCandidateConfigurations(importingClassMetadata, null);
		String[] ret = new String[configurations.size()];
		return configurations.toArray(ret);
	}

	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
													  AnnotationAttributes attributes) {
		/**
		 * SpringFactoriesLoader.loadFactoryNames 就是从spring.factories中去加载自动配置的信息
		 * 并返回以getSpringFactoriesLoaderFactoryClass返回值为key的对应的bean的信息
		 */
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
				getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
		Assert.notEmpty(configurations,
				"No auto configuration classes found in META-INF/spring.factories. If you "
						+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}

	// 类加载器
	private ClassLoader getBeanClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 *  此就是获取注解的类
	 *  事实上，此类的全类名是key
	 * @return
	 */
	private Class<?> getSpringFactoriesLoaderFactoryClass() {
		return WkEnableAutoConfig.class;
	}
}
