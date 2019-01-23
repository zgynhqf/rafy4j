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
    //region bean
    private static BeanFactory beanFactory;
    private static BeanDefinitionRegistry beanDefinitionRegistry;

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
     * 获取指定类型的 Bean，如果这个 Bean 的定义找不到，则会主动向容器中添加这个定义。
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType){
        return beanFactory.getBean(requiredType);
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
    //endregion

    private static DbSettingRepository dbSettingRepository;

    /**
     * 获取容器中的 DbSettingRepository。
     * 若容器中没有，则创建一个。
     *
     * @return
     */
    public static DbSettingRepository getDbSettingRepository() {
        if (dbSettingRepository == null) {
            dbSettingRepository = getBean(DbSettingRepository.class);
        }
        return dbSettingRepository;
    }
}
