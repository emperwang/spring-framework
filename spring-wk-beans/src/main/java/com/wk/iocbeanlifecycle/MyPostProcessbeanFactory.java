package com.wk.iocbeanlifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 *  BeanFactoryPostProcessor 是beanDefinition定义好，bean实例化前的操作
 *  此操作可以修改bean对应的beanDefinition，以达到修改类实例化方式
 */
@Component
public class MyPostProcessbeanFactory implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		GenericBeanDefinition instA = (GenericBeanDefinition) beanFactory.getBeanDefinition("instA");

		/**
		 * 此操作修改的instA对应的class，导致实例化的class类型为InstB
		 */
		instA.setBeanClass(InstB.class);
		/**
		 * 设置自动注入的方式, 默认为AutowireCapableBeanFactory.AUTOWIRE_NO  也就是需要在field上写明@Autowired
		 */
		instA.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		// 懒加载
		instA.setLazyInit(true);

		/*GenericBeanDefinition generPerson = (GenericBeanDefinition) beanFactory.getBeanDefinition("person");
		ConstructorArgumentValues strConstruct = new ConstructorArgumentValues();*/
		// 此表示使用第一个参数是字符串的构造器
		//strConstruct.addIndexedArgumentValue(0, "zhangsan");
		// 此表示使用第一个参数是整数的构造器
		//strConstruct.addIndexedArgumentValue(0,20);

		// 此表示使用第一个参数是字符串  第二个参数是整数的构造器
		//strConstruct.addIndexedArgumentValue(0,"zhangsan");
		//strConstruct.addIndexedArgumentValue(1,20);

		//generPerson.setConstructorArgumentValues(strConstruct);
	}
}
