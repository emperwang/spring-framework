package com.wk.iocbeanlifecycle;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.InitializingBean;

@Setter
@Getter
@ToString
public class Person {

	private String name;
	private int age;

	public Person() {
		this.age = 10;
		this.name="zhangsan";
	}

	public Person(String name) {
		this.name = name;
	}

	public Person(int age) {
		this.age = age;
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
}

