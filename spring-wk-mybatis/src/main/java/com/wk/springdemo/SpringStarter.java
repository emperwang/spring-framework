package com.wk.springdemo;

import com.wk.entity.User;
import com.wk.springdemo.config.MybatisConfig;
import com.wk.springdemo.mapper.UserMapper;
import com.wk.springdemo.service.UserService;
import com.wk.springdemo.service.s2.UserService2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringStarter {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MybatisConfig.class);
		insertAndGet(context);
		//insertAndGet2(context);
	}

	public static void insertAndGet2(ApplicationContext context){
		User user = new User();
		user.setAge(1);
		user.setAddress("bj");
		user.setName("zhangsan");

		UserService2 service2 = context.getBean(UserService2.class);
		service2.insertOne(user);
		service2.selectAll();
	}

	public static void insertAndGet(ApplicationContext context){
		User user = new User();
		user.setAge(1);
		user.setAddress("bj");
		user.setName("zhangsan");
		UserService userService = context.getBean(UserService.class);
		userService.insertOne(user);
		userService.selectAll();

	}

	public static void errorInsert(ApplicationContext context){

	}

	public static void getById(ApplicationContext context, int id){
		UserMapper bean = context.getBean(UserMapper.class);
		User user = bean.selectById(1);
		System.out.println(user.toString());
	}
}
