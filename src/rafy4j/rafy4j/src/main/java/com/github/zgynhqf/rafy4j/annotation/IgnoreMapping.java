package com.github.zgynhqf.rafy4j.annotation;

import java.lang.annotation.*;

/**
 * 如果某个实体类或某个字段不需要映射数据库，则使用此注解。
 * @author: huqingfang
 * @date: 2018-12-30 22:40
 **/
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreMapping {
}
