/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Parser for the @{@link ComponentScan} annotation.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 * @see ClassPathBeanDefinitionScanner#scan(String...)
 * @see ComponentScanBeanDefinitionParser
 */
class ComponentScanAnnotationParser {

	private final Environment environment;

	private final ResourceLoader resourceLoader;

	private final BeanNameGenerator beanNameGenerator;

	private final BeanDefinitionRegistry registry;


	public ComponentScanAnnotationParser(Environment environment, ResourceLoader resourceLoader,
			BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {

		this.environment = environment;
		this.resourceLoader = resourceLoader;
		this.beanNameGenerator = beanNameGenerator;
		this.registry = registry;
	}

	// 解析componentScan 路径上的组件
	public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
		// ClassPathBeanDefinitionScanner 是具体的扫描实现
		// componentScan.getBoolean("useDefaultFilters") 获取注解的属性值,是否使用默认的过滤
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
				componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);
		// 名字生成器
		// componentScan.getClass("nameGenerator") 获取注解中指定的名字生成器
		Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
		// 如果没有设置则使用默认的名字生成器
		boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
		scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator :
				BeanUtils.instantiateClass(generatorClass));
		// scope mode
		// componentScan.getEnum("scopedProxy") 获取注解中 属性值
		ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
		if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
			scanner.setScopedProxyMode(scopedProxyMode);
		}
		else {
			// componentScan.getClass("scopeResolver") 获取属性值 解析scope
			Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
			scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));
		}
		//componentScan.getString("resourcePattern") 获取属性值 得到资源的正则
		scanner.setResourcePattern(componentScan.getString("resourcePattern"));
		// 添加 includeFilter 过滤器
		// 获取属性中 componentScan.getAnnotationArray("includeFilters") 包含的过滤器
		for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
			for (TypeFilter typeFilter : typeFiltersFor(filter)) {
				scanner.addIncludeFilter(typeFilter);
			}
		}
		// 添加 excludeFilters 过滤器
		for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
			for (TypeFilter typeFilter : typeFiltersFor(filter)) {
				scanner.addExcludeFilter(typeFilter);
			}
		}
		// 是否是  懒加载
		boolean lazyInit = componentScan.getBoolean("lazyInit");
		if (lazyInit) {
			scanner.getBeanDefinitionDefaults().setLazyInit(true);
		}
		// 获取要扫描的包
		Set<String> basePackages = new LinkedHashSet<>();
		String[] basePackagesArray = componentScan.getStringArray("basePackages");
		for (String pkg : basePackagesArray) {
			String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			Collections.addAll(basePackages, tokenized);
		}
		for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}
		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(declaringClass));
		}

		scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
			@Override
			protected boolean matchClassName(String className) {
				return declaringClass.equals(className);
			}
		});
		// 万事俱备, 开始扫描
		// 重点  重点  重点
		// 前面配置了过滤器 资源正则 等属性,现在开始扫描
		return scanner.doScan(StringUtils.toStringArray(basePackages));
	}

	private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
		List<TypeFilter> typeFilters = new ArrayList<>();
		FilterType filterType = filterAttributes.getEnum("type");

		for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
			switch (filterType) {
				case ANNOTATION:
					Assert.isAssignable(Annotation.class, filterClass,
							"@ComponentScan ANNOTATION type filter requires an annotation type");
					@SuppressWarnings("unchecked")
					Class<Annotation> annotationType = (Class<Annotation>) filterClass;
					typeFilters.add(new AnnotationTypeFilter(annotationType));
					break;
				case ASSIGNABLE_TYPE:
					typeFilters.add(new AssignableTypeFilter(filterClass));
					break;
				case CUSTOM:
					Assert.isAssignable(TypeFilter.class, filterClass,
							"@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
					TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
					ParserStrategyUtils.invokeAwareMethods(
							filter, this.environment, this.resourceLoader, this.registry);
					typeFilters.add(filter);
					break;
				default:
					throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
			}
		}

		for (String expression : filterAttributes.getStringArray("pattern")) {
			switch (filterType) {
				case ASPECTJ:
					typeFilters.add(new AspectJTypeFilter(expression, this.resourceLoader.getClassLoader()));
					break;
				case REGEX:
					typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
					break;
				default:
					throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
			}
		}

		return typeFilters;
	}

}
