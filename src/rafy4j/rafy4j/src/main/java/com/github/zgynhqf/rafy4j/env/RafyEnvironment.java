package com.github.zgynhqf.rafy4j.env;

import com.github.zgynhqf.rafy4j.data.DbSettingRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author: huqingfang
 * @date: 2018-12-26 12:22
 **/
public abstract class RafyEnvironment {
    private static BeanFactory beanFactory;

    private static BeanDefinitionRegistry registry;

    public static void setBeanFactory(BeanFactory beanFactory) {
        Validate.notNull(beanFactory);

        RafyEnvironment.beanFactory = beanFactory;
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    public static void setRegistry(BeanDefinitionRegistry registry) {
        RafyEnvironment.registry = registry;
    }

    private static DbSettingRepository dbSettingRepository;

    public static DbSettingRepository getDbSettingRepository() {
        if (dbSettingRepository == null) {
            //如果开发者没有注册 DbSettingRepository，则手工注册一个默认的。
            if(!beanFactory.containsBean(DbSettingRepository.class.getName())){
                registry.registerBeanDefinition(DbSettingRepository.class.getName(), new GenericBeanDefinition(){{
                    setBeanClass(DbSettingRepository.class);
                    setScope(SCOPE_SINGLETON);
                }});
            }
            dbSettingRepository = beanFactory.getBean(DbSettingRepository.class);
        }
        return dbSettingRepository;
    }
}
