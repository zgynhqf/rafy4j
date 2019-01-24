package com.github.zgynhqf.rafy4j.annotation;

import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: huqingfang
 * @date: 2018-12-30 14:31
 **/
class RafyBeanImporter implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RafyEnvironment.setBeanDefinitionRegistry(registry);

        GenericBeanDefinition rafyConfigBean = new GenericBeanDefinition();
        rafyConfigBean.setBeanClass(RafyConfig.class);
        registry.registerBeanDefinition(RafyConfig.class.getName(), rafyConfigBean);

        RafyApplicationAnnotationHolder.setAnnotationAttributes(
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RafyApplication.class.getName()))
        );
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        RafyEnvironment.setBeanFactory(beanFactory);
    }
}
