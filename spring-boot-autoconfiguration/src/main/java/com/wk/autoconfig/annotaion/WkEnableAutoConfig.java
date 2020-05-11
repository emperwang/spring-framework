package com.wk.autoconfig.annotaion;

import com.wk.autoconfig.config.WkCusAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {WkCusAutoConfig.class})
public @interface WkEnableAutoConfig {
}
