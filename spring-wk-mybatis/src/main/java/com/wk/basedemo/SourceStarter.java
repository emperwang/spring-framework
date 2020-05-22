package com.wk.basedemo;

import com.wk.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class SourceStarter {
	public static void main(String[] args) throws IOException {
		sources();
	}

  public static void sources() throws IOException {
    SqlSessionFactory sessionFactory;
    Reader reader = Resources.getResourceAsReader("MyBatis-config.xml");
    // 创建sqlSessionFactory,在这里对xml文件进行了解析
	  // 处理创建的sessionFacory,是DefaultSqlSessionFactory
    sessionFactory = new SqlSessionFactoryBuilder().build(reader);
    // 这里进入DefaultSqlSessionFactory的方法
    SqlSession sqlSession = sessionFactory.openSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.selectById(1);
    System.out.println(user.toString());
  }
}
