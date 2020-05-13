package com.wk.aop;

import com.wk.aop.api.Calculator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopStarter {

	public static void main(String[] args) {
		// 注意AnnotationConfigApplicationContext的初始化,此初始化就会注册一些公共处理类到容器中
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);

		Calculator calc = (Calculator) applicationContext.getBean("calc");
		calc.add(1,2);
	}
}
