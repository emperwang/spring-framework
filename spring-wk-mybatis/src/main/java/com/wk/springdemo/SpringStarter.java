package com.wk.springdemo;

import com.wk.entity.User;
import com.wk.springdemo.config.MybatisConfig;
import com.wk.springdemo.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringStarter {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MybatisConfig.class);
		UserMapper bean = context.getBean(UserMapper.class);
		User user = bean.selectById(1);
		System.out.println(user.toString());
	}
}
