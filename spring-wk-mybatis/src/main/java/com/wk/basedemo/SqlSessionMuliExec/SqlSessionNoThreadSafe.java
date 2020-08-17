package com.wk.basedemo.SqlSessionMuliExec;

import com.wk.basedemo.UserMapper;
import com.wk.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SqlSessionNoThreadSafe {
	private static SqlSessionFactory sessionFactory;
	private static SqlSession sqlSession;
	static {
		try {
			Reader reader = Resources.getResourceAsReader("MyBatis-config.xml");
			sessionFactory = new SqlSessionFactoryBuilder().build(reader);
			sqlSession = sessionFactory.openSession();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void multiThreadExec(){
		UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		int count = 10;
		CountDownLatch latch = new CountDownLatch(10);
		for (int i=0; i < count; i++){
			new Thread(() -> {
				try {
					latch.await();
					List<User> users = mapper.selectAll();
					System.out.println("current-thread: "+ Thread.currentThread().getName()+", " +
							"res="+users.toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
			latch.countDown();
		}
	}
}
