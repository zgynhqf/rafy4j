//package com.github.zgynhqf.rafy4j.annotation;
//
//import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.context.annotation.ImportSelector;
//import org.springframework.core.type.AnnotationMetadata;
//
///**
// * @author: huqingfang
// * @date: 2018-12-30 14:22
// **/
//class RafyConfigurationImportSelector implements ImportSelector, BeanFactoryAware {
//    @Override
//    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//        return new String[]{"Rafy.annotation.RafyConfig"};
//    }
//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        RafyEnvironment.setBeanFactory(beanFactory);
//    }
//}
