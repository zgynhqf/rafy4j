package com.github.zgynhqf.rafy4j.env;

import com.github.zgynhqf.rafy4j.data.DbSettingRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * Rafy 环境上下文。
 *
 * @author: huqingfang
 * @date: 2018-12-26 12:22
 **/
public abstract class RafyEnvironment {
    private static BeanFactory beanFactory;
    private static BeanDefinitionRegistry beanDefinitionRegistry;
    private static DbSettingRepository dbSettingRepository;

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static void setBeanFactory(BeanFactory beanFactory) {
        Validate.notNull(beanFactory);

        RafyEnvironment.beanFactory = beanFactory;
    }

    public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return beanDefinitionRegistry;
    }

    public static void setBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) {
        RafyEnvironment.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    /**
     * 获取容器中的 DbSettingRepository。
     * 若容器中没有，则创建一个。
     *
     * @return
     */
    public static DbSettingRepository getDbSettingRepository() {
        if (dbSettingRepository == null) {
            //如果开发者没有注册 DbSettingRepository，则手工注册一个默认的。
            tryCreateDefaultBean(DbSettingRepository.class);

            dbSettingRepository = beanFactory.getBean(DbSettingRepository.class);
        }
        return dbSettingRepository;
    }

    /**
     * 如果指定的类型对应的 Bean 不存在，则直接定义一个默认的 Bean。
     * @param requiredType
     * @param <T>
     */
    public static <T> void tryCreateDefaultBean(Class<T> requiredType) {
        //如果开发者没有注册 requiredType，则手工注册一个默认的。
        if (!beanFactory.containsBean(requiredType.getName())) {
            beanDefinitionRegistry.registerBeanDefinition(requiredType.getName(), new GenericBeanDefinition() {{
                setBeanClass(requiredType);
                setScope(SCOPE_SINGLETON);
            }});
        }
    }
}
