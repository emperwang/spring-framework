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

package org.springframework.scheduling.annotation;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * Abstract base {@code Configuration} class providing common structure for enabling
 * Spring's asynchronous method execution capability.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableAsync
 */
@Configuration
public abstract class AbstractAsyncConfiguration implements ImportAware {
	// 存储 @Async注解的属性信息
	@Nullable
	protected AnnotationAttributes enableAsync;
	// 记录配置的线程池
	@Nullable
	protected Supplier<Executor> executor;
	// 记录配置的 未捕获异常的处理函数
	@Nullable
	protected Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

	// 获取注解的信息
	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableAsync = AnnotationAttributes.fromMap(
				importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
		if (this.enableAsync == null) {
			throw new IllegalArgumentException(
					"@EnableAsync is not present on importing class " + importMetadata.getClassName());
		}
	}

	/**
	 * Collect any {@link AsyncConfigurer} beans through autowiring.
	 */
	// 这里就可以看到,通过实现 AsyncConfigurer, 继承AsyncConfigurerSupport实现的配置,就这里进行了解析
	@Autowired(required = false)
	void setConfigurers(Collection<AsyncConfigurer> configurers) {
		if (CollectionUtils.isEmpty(configurers)) {
			return;
		}
		if (configurers.size() > 1) {
			throw new IllegalStateException("Only one AsyncConfigurer may exist");
		}
		AsyncConfigurer configurer = configurers.iterator().next();
		// 获取通过 AsyncConfigurer 实现的async的配置的线程池
		this.executor = configurer::getAsyncExecutor;
		// 获取通过 AsyncConfigurer 实现的 未捕获异常的处理
		this.exceptionHandler = configurer::getAsyncUncaughtExceptionHandler;
	}

}
