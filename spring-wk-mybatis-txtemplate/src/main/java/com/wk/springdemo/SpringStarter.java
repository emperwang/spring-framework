package com.wk.springdemo;

import com.wk.entity.UserEntity;
import com.wk.springdemo.config.MybatisConfig;
import com.wk.springdemo.mapper.UserMapper;
import com.wk.springdemo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringStarter {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MybatisConfig.class);
		//insertAndGet(context);
		getById(context,6);
	}

	public static void insertAndGet(ApplicationContext context){
		UserEntity user = new UserEntity("wangwu", 20, "gz");
		UserService userService = context.getBean(UserService.class);
		userService.insertOne(user);
		//userService.selectAll();

	}


	public static void getById(ApplicationContext context, int id){
		UserMapper bean = context.getBean(UserMapper.class);
		UserEntity user = bean.selectById(id);
		System.out.println(user.toString());
	}
}
