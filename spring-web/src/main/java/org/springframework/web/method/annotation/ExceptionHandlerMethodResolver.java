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

package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Discovers {@linkplain ExceptionHandler @ExceptionHandler} methods in a given class,
 * including all of its superclasses, and helps to resolve a given {@link Exception}
 * to the exception types supported by a given {@link Method}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ExceptionHandlerMethodResolver {

	/**
	 * A filter for selecting {@code @ExceptionHandler} methods.
	 */
	// 带有 ExceptionHandler 注解的方法
	public static final MethodFilter EXCEPTION_HANDLER_METHODS = method ->
			AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class);

		// 记录异常和 处理异常的 方法之间的映射
	private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);
	//此中同样记录 异常和处理方法的 映射
	private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<>(16);


	/**
	 * A constructor that finds {@link ExceptionHandler} methods in the given type.
	 * @param handlerType the type to introspect
	 */
	// 解析 ExceptionHandler
	public ExceptionHandlerMethodResolver(Class<?> handlerType) {
		// MethodIntrospector.selectMethods找到此class有ExceptionHandler注解的方法
		for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
			 // detectExceptionMappings 获取到方法中设置的全部的exception
			for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
				// 添加exception到此method的映射关系,如果出现此异常,那么就执行此方法来进行处理
				// 记录在mappedMethods
				addExceptionMapping(exceptionType, method);
			}
		}
	}


	/**
	 * Extract exception mappings from the {@code @ExceptionHandler} annotation first,
	 * and then as a fallback from the method signature itself.
	 */
	@SuppressWarnings("unchecked")
	private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
		List<Class<? extends Throwable>> result = new ArrayList<>();
		// 获取到ExceptionHandler注解中value的所有的值, 并存储到 result中
		// 父类,接口,都会去查找
		detectAnnotationExceptionMappings(method, result);
		if (result.isEmpty()) {
			for (Class<?> paramType : method.getParameterTypes()) {
				if (Throwable.class.isAssignableFrom(paramType)) {
					result.add((Class<? extends Throwable>) paramType);
				}
			}
		}
		if (result.isEmpty()) {
			throw new IllegalStateException("No exception types mapped to " + method);
		}
		return result;
	}
	// 获取 异常的映射
	private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
		// 查找到方法上的 ExceptionHandler 注解信息
		// 父类, 接口上等 都会去查找
		ExceptionHandler ann = AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
		Assert.state(ann != null, "No ExceptionHandler annotation");
		result.addAll(Arrays.asList(ann.value()));
	}
	// 记录异常的映射值
	private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
		// 记录起来 异常 和 处理此异常的 方法之间的映射
		Method oldMethod = this.mappedMethods.put(exceptionType, method);
		// 如果多个方法处理同一个异常,则会报错
		if (oldMethod != null && !oldMethod.equals(method)) {
			throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" +
					exceptionType + "]: {" + oldMethod + ", " + method + "}");
		}
	}

	/**
	 * Whether the contained type has any exception mappings.
	 */
	public boolean hasExceptionMappings() {
		return !this.mappedMethods.isEmpty();
	}

	/**
	 * Find a {@link Method} to handle the given exception.
	 * Use {@link ExceptionDepthComparator} if more than one match is found.
	 * @param exception the exception
	 * @return a Method to handle the exception, or {@code null} if none found
	 */
	// 解析获取 exception 异常对应的处理method
	@Nullable
	public Method resolveMethod(Exception exception) {
		return resolveMethodByThrowable(exception);
	}

	/**
	 * Find a {@link Method} to handle the given Throwable.
	 * Use {@link ExceptionDepthComparator} if more than one match is found.
	 * @param exception the exception
	 * @return a Method to handle the exception, or {@code null} if none found
	 * @since 5.0
	 */
	// 解析获取 exception 对应的 method
	@Nullable
	public Method resolveMethodByThrowable(Throwable exception) {
		// 去exceptionLookupCache 以及  mappedMethods 中查找
		Method method = resolveMethodByExceptionType(exception.getClass());
		if (method == null) {
			// 没有找到,则获取异常的原因,递归再次查找
			Throwable cause = exception.getCause();
			if (cause != null) {
				method = resolveMethodByExceptionType(cause.getClass());
			}
		}
		return method;
	}

	/**
	 * Find a {@link Method} to handle the given exception type. This can be
	 * useful if an {@link Exception} instance is not available (e.g. for tools).
	 * @param exceptionType the exception type
	 * @return a Method to handle the exception, or {@code null} if none found
	 */
	@Nullable
	public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
		// 先从exceptionLookupCache中来根据exception类型来查找对应的处理method
		Method method = this.exceptionLookupCache.get(exceptionType);
		if (method == null) {
			// 如果没有找打,则再次根据exception类型去mappedMethods中进行查找
			method = getMappedMethod(exceptionType);
			// 然后把查找到关系放入到exceptionLookupCache中
			this.exceptionLookupCache.put(exceptionType, method);
		}
		return method;
	}

	/**
	 * Return the {@link Method} mapped to the given exception type, or {@code null} if none.
	 */
	@Nullable
	private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
		List<Class<? extends Throwable>> matches = new ArrayList<>();
		// 从mappedMethods找到所有可以处理此exception的method
		for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
			if (mappedException.isAssignableFrom(exceptionType)) {
				matches.add(mappedException);
			}
		}
		// 此处ExceptionDepthComparator会根据抛出的exception来查找一个关系最近的一个handler来进行处理
		if (!matches.isEmpty()) {
			matches.sort(new ExceptionDepthComparator(exceptionType));
			return this.mappedMethods.get(matches.get(0));
		}
		else {
			return null;
		}
	}

}
