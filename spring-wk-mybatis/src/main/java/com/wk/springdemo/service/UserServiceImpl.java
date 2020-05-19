package com.wk.springdemo.service;

import com.wk.entity.User;
import com.wk.springdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;


	@Override
	@Transactional(isolation = Isolation.DEFAULT)
	public int insertOne(User user) {
		int count = 0;
		count += userMapper.insertOne(user);
		//int i = 1/0;
		return count;
	}

	@Override
	@Transactional(isolation = Isolation.DEFAULT, readOnly = true)
	public List<User> selectAll() {
		List<User> users = userMapper.selectAll();
		System.out.println(users.toString());
		return users;
	}
}
