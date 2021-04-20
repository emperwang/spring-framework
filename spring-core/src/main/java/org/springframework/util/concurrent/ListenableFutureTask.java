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

package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.springframework.lang.Nullable;

/**
 * Extension of {@link FutureTask} that implements {@link ListenableFuture}.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @param <T> the result type returned by this Future's {@code get} method
 */
// 此对futureTask 进行了扩展, 针对失败和成功的结果 添加了回调
public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {
	// 这是回调函数的注册地,即保存了哪些注册的回调函数
	private final ListenableFutureCallbackRegistry<T> callbacks = new ListenableFutureCallbackRegistry<>();


	/**
	 * Create a new {@code ListenableFutureTask} that will, upon running,
	 * execute the given {@link Callable}.
	 * @param callable the callable task
	 */
	// callable 即真正执行的任务
	public ListenableFutureTask(Callable<T> callable) {
		// 保存任务到父类
		super(callable);
	}

	/**
	 * Create a {@code ListenableFutureTask} that will, upon running,
	 * execute the given {@link Runnable}, and arrange that {@link #get()}
	 * will return the given result on successful completion.
	 * @param runnable the runnable task
	 * @param result the result to return on successful completion
	 */
	public ListenableFutureTask(Runnable runnable, @Nullable T result) {
		super(runnable, result);
	}

	// 添加回调函数
	@Override
	public void addCallback(ListenableFutureCallback<? super T> callback) {
		this.callbacks.addCallback(callback);
	}
	// 添加回调函数
	@Override
	public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
		this.callbacks.addSuccessCallback(successCallback);
		this.callbacks.addFailureCallback(failureCallback);
	}

	@Override
	public CompletableFuture<T> completable() {
		CompletableFuture<T> completable = new DelegatingCompletableFuture<>(this);
		this.callbacks.addSuccessCallback(completable::complete);
		this.callbacks.addFailureCallback(completable::completeExceptionally);
		return completable;
	}

	// 这是 futurTask 留下的一个用于扩展的函数点
	// 此函数在 任务完成时 会被调用
	@Override
	protected void done() {
		Throwable cause;
		try {
			T result = get();
			this.callbacks.success(result);
			return;
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return;
		}
		catch (ExecutionException ex) {
			cause = ex.getCause();
			if (cause == null) {
				cause = ex;
			}
		}
		catch (Throwable ex) {
			cause = ex;
		}
		this.callbacks.failure(cause);
	}

}
