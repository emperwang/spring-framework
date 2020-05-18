package com.wk.springdemo.service;

import com.wk.entity.User;

import java.util.List;

public interface UserService {
	int insertOne(User user);
	List<User> selectAll();
}
