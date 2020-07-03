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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Default implementation of the {@link LifecycleProcessor} strategy.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {

	private final Log logger = LogFactory.getLog(getClass());

	private volatile long timeoutPerShutdownPhase = 30000;

	private volatile boolean running;

	@Nullable
	private volatile ConfigurableListableBeanFactory beanFactory;


	/**
	 * Specify the maximum time allotted in milliseconds for the shutdown of
	 * any phase (group of SmartLifecycle beans with the same 'phase' value).
	 * <p>The default value is 30 seconds.
	 */
	public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
		this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	private ConfigurableListableBeanFactory getBeanFactory() {
		ConfigurableListableBeanFactory beanFactory = this.beanFactory;
		Assert.state(beanFactory != null, "No BeanFactory available");
		return beanFactory;
	}


	// Lifecycle implementation

	/**
	 * Start all registered beans that implement {@link Lifecycle} and are <i>not</i>
	 * already running. Any bean that implements {@link SmartLifecycle} will be
	 * started within its 'phase', and all phases will be ordered from lowest to
	 * highest value. All beans that do not implement {@link SmartLifecycle} will be
	 * started in the default phase 0. A bean declared as a dependency of another bean
	 * will be started before the dependent bean regardless of the declared phase.
	 */
	// 调用Lifecycle类型bean的start方法
	@Override
	public void start() {
		startBeans(false);
		this.running = true;
	}

	/**
	 * Stop all registered beans that implement {@link Lifecycle} and <i>are</i>
	 * currently running. Any bean that implements {@link SmartLifecycle} will be
	 * stopped within its 'phase', and all phases will be ordered from highest to
	 * lowest value. All beans that do not implement {@link SmartLifecycle} will be
	 * stopped in the default phase 0. A bean declared as dependent on another bean
	 * will be stopped before the dependency bean regardless of the declared phase.
	 */
	// 调用Lifecycle类型的bean的stop方法
	@Override
	public void stop() {
		stopBeans();
		this.running = false;
	}
	// 刷新操作，重新调用所有Lifecycle类型的bean的start方法
	// 调用bean的 LifeCycle SmartLifeCycle bean的start方法
	@Override
	public void onRefresh() {
		startBeans(true);
		this.running = true;
	}

	@Override
	public void onClose() {
		stopBeans();
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}


	// Internal helpers

	private void startBeans(boolean autoStartupOnly) {
		// 获取所有的Lifecyel类型的bean
		Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
		Map<Integer, LifecycleGroup> phases = new HashMap<>();
		// 遍历所有的bean,把bean根据不同的阶段进行分组
		// 对SmartLifeCycle类型的bean的处理
		lifecycleBeans.forEach((beanName, bean) -> {
			if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle) bean).isAutoStartup())) {
				// 调用Phased类型bean的 getPhase方法,获取此bean的 phase
				int phase = getPhase(bean);
				LifecycleGroup group = phases.get(phase);
				if (group == null) {
					/*
					this.phase = phase;
					this.timeout = timeoutPerShutdownPhase;
					this.lifecycleBeans = lifecycleBeans;
					this.autoStartupOnly = autoStartupOnly;
					 */
					group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
					phases.put(phase, group);
				}
				// 把相同的 phase 的bean 放到同一个 group中
				group.add(beanName, bean);
			}
		});
		// 把不同阶段的bean进行排序,并按照顺序进行启动
		if (!phases.isEmpty()) {
			List<Integer> keys = new ArrayList<>(phases.keySet());
			Collections.sort(keys);
			// 调用LifeCycle的start 方法
			for (Integer key : keys) {
				phases.get(key).start();
			}
		}
	}

	/**
	 * Start the specified bean as part of the given set of Lifecycle beans,
	 * making sure that any beans that it depends on are started first.
	 * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
	 * @param beanName the name of the bean to start
	 */
	// 真正调用Lifecycle bean的start方法
	private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
		Lifecycle bean = lifecycleBeans.remove(beanName);
		if (bean != null && bean != this) {
			// 先获取此bean的依赖的bean
			String[] dependenciesForBean = getBeanFactory().getDependenciesForBean(beanName);
			// 先启动依赖的bean
			for (String dependency : dependenciesForBean) {
				doStart(lifecycleBeans, dependency, autoStartupOnly);
			}
			if (!bean.isRunning() &&
					(!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle) bean).isAutoStartup())) {
				if (logger.isTraceEnabled()) {
					logger.trace("Starting bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
				}
				try {
					// 调用bean的start方法
					bean.start();
				}
				catch (Throwable ex) {
					throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully started bean '" + beanName + "'");
				}
			}
		}
	}

	private void stopBeans() {
		// 获取所有Lifecycle类型的bean
		Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
		Map<Integer, LifecycleGroup> phases = new HashMap<>();
		// 遍历所有的bean,并根据阶段的不同,进行分组; 把阶段相当的bean放到同一个组中
		lifecycleBeans.forEach((beanName, bean) -> {
			int shutdownPhase = getPhase(bean);
			LifecycleGroup group = phases.get(shutdownPhase);
			if (group == null) {
				group = new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
				phases.put(shutdownPhase, group);
			}
			// 保存相同phase的bean
			group.add(beanName, bean);
		});
		// 调用bean的stop方法
		if (!phases.isEmpty()) {
			List<Integer> keys = new ArrayList<>(phases.keySet());
			keys.sort(Collections.reverseOrder());
			for (Integer key : keys) {
				phases.get(key).stop();
			}
		}
	}

	/**
	 * Stop the specified bean as part of the given set of Lifecycle beans,
	 * making sure that any beans that depends on it are stopped first.
	 * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
	 * @param beanName the name of the bean to stop
	 */
	// 真正调用bean的stop方法
	private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName,
			final CountDownLatch latch, final Set<String> countDownBeanNames) {

		Lifecycle bean = lifecycleBeans.remove(beanName);
		if (bean != null) {
			// 获取此bean的依赖的bean
			String[] dependentBeans = getBeanFactory().getDependentBeans(beanName);
			// 先停止其依赖的bean
			for (String dependentBean : dependentBeans) {
				// 执行依赖的bean方法
				doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
			}
			try {
				// 如果bean正在是正在运行,则执行
				if (bean.isRunning()) {
					// 如果bean是SmartLifecycle
					if (bean instanceof SmartLifecycle) {
						if (logger.isTraceEnabled()) {
							logger.trace("Asking bean '" + beanName + "' of type [" +
									bean.getClass().getName() + "] to stop");
						}
						// 记录已经运行的停止bean
						countDownBeanNames.add(beanName);
						// 运行停止的线程
						((SmartLifecycle) bean).stop(() -> {
							latch.countDown();
							countDownBeanNames.remove(beanName);
							if (logger.isDebugEnabled()) {
								logger.debug("Bean '" + beanName + "' completed its stop procedure");
							}
						});
					}
					else {
						if (logger.isTraceEnabled()) {
							logger.trace("Stopping bean '" + beanName + "' of type [" +
									bean.getClass().getName() + "]");
						}
						// 如果不是SmartLifecycle,则直接运行 stop方法
						bean.stop();
						if (logger.isDebugEnabled()) {
							logger.debug("Successfully stopped bean '" + beanName + "'");
						}
					}
				}
				else if (bean instanceof SmartLifecycle) {
					// Don't wait for beans that aren't running...
					// 如果bean已经不再运行了,直接countDown
					latch.countDown();
				}
			}
			catch (Throwable ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Failed to stop bean '" + beanName + "'", ex);
				}
			}
		}
	}


	// overridable hooks

	/**
	 * Retrieve all applicable Lifecycle beans: all singletons that have already been created,
	 * as well as all SmartLifecycle beans (even if they are marked as lazy-init).
	 * @return the Map of applicable beans, with bean names as keys and bean instances as values
	 */
	// 获取容器中所有的Lifecycle 的bean
	protected Map<String, Lifecycle> getLifecycleBeans() {
		// 获取容器
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 存储Lifecycle的容器
		Map<String, Lifecycle> beans = new LinkedHashMap<>();
		// 获取Lifecycle类型的bean
		String[] beanNames = beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
		for (String beanName : beanNames) { // 遍历所有的bean
			// 获取bean的真实名字
			String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
			// 是否是factorybean
			boolean isFactoryBean = beanFactory.isFactoryBean(beanNameToRegister);
			String beanNameToCheck = (isFactoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
			if ((beanFactory.containsSingleton(beanNameToRegister) &&
					(!isFactoryBean || matchesBeanType(Lifecycle.class, beanNameToCheck, beanFactory))) ||
					matchesBeanType(SmartLifecycle.class, beanNameToCheck, beanFactory)) {
				// 从容器中获取bean
				Object bean = beanFactory.getBean(beanNameToCheck);
				if (bean != this && bean instanceof Lifecycle) {
					beans.put(beanNameToRegister, (Lifecycle) bean);
				}
			}
		}
		return beans;
	}

	private boolean matchesBeanType(Class<?> targetType, String beanName, BeanFactory beanFactory) {
		Class<?> beanType = beanFactory.getType(beanName);
		return (beanType != null && targetType.isAssignableFrom(beanType));
	}

	/**
	 * Determine the lifecycle phase of the given bean.
	 * <p>The default implementation checks for the {@link Phased} interface, using
	 * a default of 0 otherwise. Can be overridden to apply other/further policies.
	 * @param bean the bean to introspect
	 * @return the phase (an integer value)
	 * @see Phased#getPhase()
	 * @see SmartLifecycle
	 */
	protected int getPhase(Lifecycle bean) {
		return (bean instanceof Phased ? ((Phased) bean).getPhase() : 0);
	}


	/**
	 * Helper class for maintaining a group of Lifecycle beans that should be started
	 * and stopped together based on their 'phase' value (or the default value of 0).
	 */
	private class LifecycleGroup {

		private final int phase;

		private final long timeout;

		private final Map<String, ? extends Lifecycle> lifecycleBeans;

		private final boolean autoStartupOnly;

		private final List<LifecycleGroupMember> members = new ArrayList<>();

		private int smartMemberCount;

		public LifecycleGroup(
				int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {

			this.phase = phase;
			this.timeout = timeout;
			this.lifecycleBeans = lifecycleBeans;
			this.autoStartupOnly = autoStartupOnly;
		}

		public void add(String name, Lifecycle bean) {
			this.members.add(new LifecycleGroupMember(name, bean));
			if (bean instanceof SmartLifecycle) {
				this.smartMemberCount++;
			}
		}

		public void start() {
			if (this.members.isEmpty()) {
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Starting beans in phase " + this.phase);
			}
			// 排序
			Collections.sort(this.members);
			// 进行调用
			// 在对 lifeCycle进行调用时,先调用其依赖的bean的 start
			// 之后在调用本bean的start方法
			for (LifecycleGroupMember member : this.members) {
				doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
			}
		}

		public void stop() {
			if (this.members.isEmpty()) {
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Stopping beans in phase " + this.phase);
			}
			this.members.sort(Collections.reverseOrder());
			CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
			Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet<>());
			Set<String> lifecycleBeanNames = new HashSet<>(this.lifecycleBeans.keySet());
			for (LifecycleGroupMember member : this.members) {
				if (lifecycleBeanNames.contains(member.name)) {
					// 1. 开始执行
					// 2. 限制性依赖的bean,之后再执行本bean
					doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
				}
				else if (member.bean instanceof SmartLifecycle) {
					// Already removed: must have been a dependent bean from another phase
					// 直接countDown
					latch.countDown();
				}
			}
			try {
				// 等待所有的 SmartLifeCycle中public void stop(Runnable runnable)线程执行完成。
				latch.await(this.timeout, TimeUnit.MILLISECONDS);
				if (latch.getCount() > 0 && !countDownBeanNames.isEmpty() && logger.isInfoEnabled()) {
					logger.info("Failed to shut down " + countDownBeanNames.size() + " bean" +
							(countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " +
							this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
				}
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}


	/**
	 * Adapts the Comparable interface onto the lifecycle phase model.
	 */
	private class LifecycleGroupMember implements Comparable<LifecycleGroupMember> {

		private final String name;

		private final Lifecycle bean;

		LifecycleGroupMember(String name, Lifecycle bean) {
			this.name = name;
			this.bean = bean;
		}

		@Override
		public int compareTo(LifecycleGroupMember other) {
			int thisPhase = getPhase(this.bean);
			int otherPhase = getPhase(other.bean);
			return Integer.compare(thisPhase, otherPhase);
		}
	}

}
