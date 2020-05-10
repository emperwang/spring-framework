package com.wk.iocbeanlifecycle;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.wk.iocbeanlifecycle"})
public class BeanConfig {

	@Bean
	public Person person(){
		return new Person();
	}
}
