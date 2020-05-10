package com.wk.aop.api;

import com.wk.aop.api.Calculator;

public class CalculatorImpl implements Calculator {
	@Override
	public void add(int a, int b) {
		int c = a+b;
		System.out.println("add result = " + c);
	}

	@Override
	public void sub(int a, int b) {
		int c = a - b;
		System.out.println("sub result = " + c);
	}
}
