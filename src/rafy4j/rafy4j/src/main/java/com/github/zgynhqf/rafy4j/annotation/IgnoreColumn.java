package com.github.zgynhqf.rafy4j.annotation;

import java.lang.annotation.*;

/**
 * @author: huqingfang
 * @date: 2018-12-30 22:40
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreColumn {
}
