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
}
