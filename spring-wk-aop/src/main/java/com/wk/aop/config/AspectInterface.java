package com.wk.aop.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

public interface AspectInterface {
	@Before(value = "MyPointcut2()")
	public void beforeAdvise(JoinPoint jpt);

	@After(value = "MyPointcut2()")
	public void afterAdvise();

	@AfterThrowing(value = "MyPointcut2()")
	public void exception(JoinPoint joinPoint);

	@AfterReturning(value = "MyPointcut2()")
	public void afterReturn(JoinPoint joinPoint);

	@Around(value = "MyPointcut2()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable;
}
