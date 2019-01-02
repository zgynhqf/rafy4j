package com.github.zgynhqf.rafy4j.utils;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author: huqingfang
 * @date: 2018-12-30 19:21
 **/
public class AnnotationHelper {
    /**
     * 以一种支持组合注解的形式来查找 Annotation。
     * @param annotatedElement
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        //目前通过 Spring 中自带的 AnnotationUtils 来实现。
        return AnnotationUtils.findAnnotation(annotatedElement, annotationType);
    }
}
