package com.wk.iocbeanlifecycle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanStarter {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(BeanConfig.class);

		Person person = (Person) applicationContext.getBean("person");

		System.out.println(person.toString());
	}
}
