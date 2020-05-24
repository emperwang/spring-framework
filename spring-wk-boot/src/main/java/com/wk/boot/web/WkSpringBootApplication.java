package com.wk.boot.web;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;

public class WkSpringBootApplication {
	public static void run(){
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		try {
			//tomcat.addWebapp("//","E:\\code-workSpace\\gitHub\\spring-framework\\spring-wk-boot\\out\\production\\classes");
			tomcat.addWebapp("//","/Users/whitejenney/code_workspace/spring-framework/spring-wk-boot/build/classes");
			//tomcat.addWebapp("//","D:\\");
			tomcat.start();
			tomcat.getServer().await();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
}
