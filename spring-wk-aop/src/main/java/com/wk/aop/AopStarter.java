package com.wk.aop;

import com.wk.aop.api.Calculator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopStarter {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AopConfig.class);

		Calculator calc = (Calculator) applicationContext.getBean("calc");
		calc.add(1,2);
	}
}
