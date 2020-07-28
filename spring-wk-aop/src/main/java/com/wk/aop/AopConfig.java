package com.wk.aop;

import com.wk.aop.api.Calculator;
import com.wk.aop.api.CalculatorImpl;
import com.wk.aop.config.AspectImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
// 此注解向容器中注入了aop相关的bean
@EnableAspectJAutoProxy
public class AopConfig {

	@Bean
	public Calculator calc(){
		return new CalculatorImpl();
	}

	/*	@Bean
	public AspectConfig config(){
		return new AspectConfig();
	}*/

	@Bean
	public AspectImpl config2(){
		return new AspectImpl();
	}
}
