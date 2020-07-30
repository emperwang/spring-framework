package com.wk.springdemo.service;

import com.wk.entity.UserEntity;
import com.wk.springdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private TransactionTemplate transactionTemplate;

	// 由此可见 把注解放到接口上,同样是生效的
	@Override
	public int insertOne(UserEntity user) {
		int count = 0;
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				userMapper.insertOne(user);
				//int i = 1/0;
			}
		});
		return count;
	}

	@Override
	public List<UserEntity> selectAll() {
		List<UserEntity> users = userMapper.selectAll();
		System.out.println(users.toString());
		return users;
	}
}
