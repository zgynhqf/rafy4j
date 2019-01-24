package com.github.zgynhqf.rafy4j.annotation;

import org.springframework.core.annotation.AnnotationAttributes;

/**
 * RafyApplication Annotation 值保存类。
 *
 * @author: huqingfang
 * @date: 2019-01-23 17:00
 **/
public abstract class RafyApplicationAnnotationHolder {
    private static AnnotationAttributes annotationAttributes;

    public static Class<?>[] getEntityPackageClasses() {
        return annotationAttributes.getClassArray("entityPackageClasses");
    }

    static void setAnnotationAttributes(AnnotationAttributes value) {
        annotationAttributes = value;
    }
}
