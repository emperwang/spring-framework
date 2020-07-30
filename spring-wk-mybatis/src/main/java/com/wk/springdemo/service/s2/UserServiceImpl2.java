package com.wk.springdemo.service.s2;

import com.wk.entity.User;
import com.wk.springdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl2 implements UserService2{
	@Autowired
	private UserMapper userMapper;

	@Override
	public int insertOne(User user) {
		int count = 0;
		count += userMapper.insertOne(user);
		//int i = 1/0;
		return count;
	}

	@Override
	public List<User> selectAll() {
		List<User> users = userMapper.selectAll();
		System.out.println(users.toString());
		return users;
	}
}
