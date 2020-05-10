package com.wk.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class AspectConfig {

	@Pointcut(value = "execution(* com.wk.aop.api.*.*(..))")
	public void MyPointcut(){}


	@Before(value = "MyPointcut()")
	public void beforeAdvise(){
		System.out.println("before advise");
	}

	@After(value = "MyPointcut()")
	public void afterAdvise(){
		System.out.println("after advise");
	}

	@Around(value = "MyPointcut()")
	public Object around(ProceedingJoinPoint pjp){
		System.out.println("start around.");
		Object res = null;
		try {
			 res = pjp.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		System.out.println("around end.");
		return res;
	}
}
