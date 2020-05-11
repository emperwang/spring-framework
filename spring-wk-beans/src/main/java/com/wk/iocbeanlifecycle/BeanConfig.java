package com.wk.iocbeanlifecycle;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.wk.iocbeanlifecycle"})
// @import注解的使用
// 第一种: 把一个bean简单的注入到容器
//@Import(value = {InstA.class})
// 第二种: 注册进入一个配置类
//@Import(value = {WkImportBeanDefinition.class})
@Import(value = {ImportSelector.class})
public class BeanConfig {

	@Bean
	public Person person(){
		return new Person();
	}
}
