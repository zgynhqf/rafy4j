package com.github.zgynhqf.rafy4j.annotation;

import com.github.zgynhqf.rafy4j.data.DbSettingRepository;
import com.github.zgynhqf.rafy4j.metadata.EntityMetaStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: huqingfang
 * @date: 2018-12-30 14:22
 **/
@Configuration
class RafyConfig {
    @Bean
    @ConditionalOnMissingBean
    public EntityMetaStore entityMetaStore() {
        return new EntityMetaStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public DbSettingRepository dbSettingRepository() {
        return new DbSettingRepository();
    }
}
