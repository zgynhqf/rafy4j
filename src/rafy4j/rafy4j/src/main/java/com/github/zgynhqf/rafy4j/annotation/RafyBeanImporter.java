package com.github.zgynhqf.rafy4j.annotation;

import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: huqingfang
 * @date: 2018-12-30 14:31
 **/
public class RafyBeanImporter implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(RafyConfig.class.getName(), new GenericBeanDefinition(){{
            setBeanClass(RafyConfig.class);
        }});
        RafyEnvironment.setRegistry(registry);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        RafyEnvironment.setBeanFactory(beanFactory);
    }
}
