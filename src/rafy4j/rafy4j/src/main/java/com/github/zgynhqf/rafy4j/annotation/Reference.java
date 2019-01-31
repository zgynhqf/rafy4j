package com.github.zgynhqf.rafy4j.annotation;

import java.lang.annotation.*;

/**
 * 引用字段标签
 *
 * @author: huqingfang
 * @date: 2019-01-30 17:48
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Reference {
    /**
     * 引用的实体类型。
     *
     * @return
     */
    Class<?> entity();

    /**
     * 引用类型
     *
     * @return
     */
    ReferenceType referenceType() default ReferenceType.NORMAL;
}
