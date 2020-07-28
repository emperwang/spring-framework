package com.wk.springdemo.service.s2;

import com.wk.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 加上此注解,子类的事务会生效;验证:
// 1. 添加此注解, 实现类引发异常,事务回滚,插入不会成功
// 2. 去除此注解, 实现类引发异常,事务不会回滚,插入成功
@Transactional(isolation = Isolation.DEFAULT)
public interface UserService2 {
	int insertOne(User user);
	List<User> selectAll();
}
