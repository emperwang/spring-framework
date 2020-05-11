package com.wk.autoconfig;

import com.wk.autoconfig.beans.BeanA;
import com.wk.autoconfig.config.MyConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutoConfigStarter {
	public static void main(String[] args) {
		System.out.println("auto config start...");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
		BeanA bean = context.getBean(BeanA.class);
		System.out.println(bean);
	}
}
