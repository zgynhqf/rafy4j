package com.github.zgynhqf.rafy4j.annotation;

import java.lang.annotation.*;


/**
 * 创建表时的表名
 *
 * @author huqingfang
 * @date: 2018-12-30 14:22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MappingTable {
    /**
     * 要映射的表名。
     * 如果未填写，则通过实体名进行映射。
     */
    String name() default "";

    /**
     * 是否需要映射所有字段到列。
     * 使用默认的映射规则。
     *
     * @return
     */
    boolean mapAllPropertiesToColumn() default true;
}