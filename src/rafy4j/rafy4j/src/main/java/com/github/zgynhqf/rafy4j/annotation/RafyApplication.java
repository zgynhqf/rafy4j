package com.github.zgynhqf.rafy4j.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
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
}
