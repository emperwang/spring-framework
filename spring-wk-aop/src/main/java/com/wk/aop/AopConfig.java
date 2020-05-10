package com.wk.aop;

import com.wk.aop.api.Calculator;
import com.wk.aop.api.CalculatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

	@Bean
	public Calculator calc(){
		return new CalculatorImpl();
	}

	@Bean
	public AspectConfig config(){
		return new AspectConfig();
	}
}
