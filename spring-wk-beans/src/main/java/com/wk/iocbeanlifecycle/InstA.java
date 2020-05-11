package com.wk.iocbeanlifecycle;

import org.springframework.stereotype.Component;

@Component
public class InstA {

	public InstA() {
		System.out.println("InstA 构造器.");
	}
}
