package com.github.zgynhqf.rafy4j.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 表明此 SpringBoot 应用程序是一个 Rafy 应用程序。
 *
 * @author: huqingfang
 * @date: 2018-12-30 14:21
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RafyBeanImporter.class})
//@Import({RafyBeanImporter.class, RafyConfigurationImportSelector.class})
public @interface RafyApplication {
    /**
     * 列出所有实体类所在包的代理类（代理类为包中的任意一个类型）
     *
     * @return
     */
    Class<?>[] entityPackageClasses() default {};
}
