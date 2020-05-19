package com.wk.springdemo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.wk.springdemo.mapper"})
@ComponentScan(value = {"com.wk.springdemo.service"})
@EnableTransactionManagement
public class MybatisConfig {
	//private String MapperLocation = "classpath: /*Mapper.xml";
	private String MapperLocation = "classpath:/mapper/*.xml";

	// 数据源
	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/ssm?useSSL=false&allowMultiQueries=true");
		dataSource.setUsername("root");
		dataSource.setPassword("admin");
		return dataSource;
	}

	// sqlsessionfactory
	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setMapperLocations(patternResolver.getResources(MapperLocation));
		return sessionFactoryBean.getObject();
	}

	// 事务管理器
	@Bean
	public PlatformTransactionManager transactionManager(){
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}
}
