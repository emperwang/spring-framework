package com.wk.boot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.wk.boot"})
/**
 * 只有添加上EnableWebMvc此注解, WebMvcConfigurer此接口的配置类才能生效
 * 原因就是: EnableWebMvc为容器导入了一个DelegatingWebMvcConfiguration类,此类会处理WebMvcConfigurer的实现类
 */
@EnableWebMvc
public class WkSpringbootConfig implements WebMvcConfigurer{

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
	}
}
