package com.core.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义注解主键
 * @author 彭佳佳
 * @data 2018年3月7日
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Id {
}
