package com.wk.aop.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

// 把方法注解放到接口上 也是可以生效的
@Aspect
public class AspectImpl implements AspectInterface {
	// 此pointcut 不能放在接口中
	@Pointcut(value = "execution(* com.wk.aop.api.*.*(..))")
	public void MyPointcut2() {
	}

	@Override
	public void beforeAdvise(JoinPoint jpt) {
		System.out.println("aspect2 -- beforeAdvise");
	}

	@Override
	public void afterAdvise() {
		System.out.println("aspect2 -- afterAdvise");
	}

	@Override
	public void exception(JoinPoint joinPoint) {
		System.out.println("aspect2 -- exception");
	}

	@Override
	public void afterReturn(JoinPoint joinPoint) {
		System.out.println("aspect2 -- afterReturn");
	}

	@Override
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("aspect2 -- around");
		Object res = pjp.proceed();
		System.out.println("aspect2 -- around");
		return res;
	}
}
