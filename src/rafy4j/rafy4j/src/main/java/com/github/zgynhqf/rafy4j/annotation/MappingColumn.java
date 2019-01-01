package com.github.zgynhqf.rafy4j.annotation;

import java.lang.annotation.*;

/**
 * 建表的必备注解
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:12:48
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MappingColumn {

    /**
     * 字段名
     * 如果未设置，则根据默认规则进行生成。
     */
    public String name() default "";

    /**
     * 字段长度
     * 可以是数字，也可以是字符串，如：MAX。
     */
    public String length() default "";
//
//    /**
//     * 字段类型
//     * 如果未设置，则根据字段的类型来通过规则生成。
//     */
//    public String type() default "";
//
//    /**
//     * 是否可空列
//     */
//    public OptionalBoolean isNullable() default OptionalBoolean.ANY;

//    /**
//     * 是否是主键
//     */
//    public OptionalBoolean isKey() default OptionalBoolean.ANY;
//
//    /**
//     * 是否自动递增，只有主键才能使用
//     */
//    public OptionalBoolean isAutoIncrement() default OptionalBoolean.ANY;

    /**
     * 默认值
     */
    public String defaultValue() default "";
}
