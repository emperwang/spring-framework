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

package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;

import org.springframework.aop.ClassFilter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

/**
 * Simple ClassFilter that looks for a specific Java 5 annotation
 * being present on a class.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationMatchingPointcut
 */
public class AnnotationClassFilter implements ClassFilter {

	private final Class<? extends Annotation> annotationType;

	private final boolean checkInherited;


	/**
	 * Create a new AnnotationClassFilter for the given annotation type.
	 * @param annotationType the annotation type to look for
	 */
	public AnnotationClassFilter(Class<? extends Annotation> annotationType) {
		this(annotationType, false);
	}

	/**
	 * Create a new AnnotationClassFilter for the given annotation type.
	 * @param annotationType the annotation type to look for
	 * @param checkInherited whether to also check the superclasses and
	 * interfaces as well as meta-annotations for the annotation type
	 * (i.e. whether to use {@link AnnotatedElementUtils#hasAnnotation}
	 * semantics instead of standard Java {@link Class#isAnnotationPresent})
	 */
	public AnnotationClassFilter(Class<? extends Annotation> annotationType, boolean checkInherited) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		// 此annotationtype的类型为 Async 或 Asynchronous
		// 此annotationType只能为其中之一,  因为会创建多个annotationClassFilter
		this.annotationType = annotationType;
		// 当进行 aync的匹配时, 此处设置的为true
		this.checkInherited = checkInherited;
	}

	// pointcut进行匹配的方法
	@Override
	public boolean matches(Class<?> clazz) {
		// 如果设置了 checkInherited,则查找注解时,会从 类  接口  父类等去查找
		// 1. 先分析方法本身的注解
		// 2. 分析方法所在类 的接口的信息
		// 3. 分析方法的父类的注解信息
		// 4. 分析方法所在的类的父类的接口的注解信息
		// 分析类时 :
		// 1. 分析类的注解信息
		// 2. 分析类的父类的注解信息

		// 如果设置checkInherited 为false, 则只查找当前类
		return (this.checkInherited ? AnnotatedElementUtils.hasAnnotation(clazz, this.annotationType) :
				clazz.isAnnotationPresent(this.annotationType));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AnnotationClassFilter)) {
			return false;
		}
		AnnotationClassFilter otherCf = (AnnotationClassFilter) other;
		return (this.annotationType.equals(otherCf.annotationType) && this.checkInherited == otherCf.checkInherited);
	}

	@Override
	public int hashCode() {
		return this.annotationType.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + this.annotationType;
	}

}
