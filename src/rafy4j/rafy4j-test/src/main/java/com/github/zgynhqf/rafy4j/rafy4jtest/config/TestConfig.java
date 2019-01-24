package com.github.zgynhqf.rafy4j.rafy4jtest.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.github.zgynhqf.rafy4j.mybatisplus.plugins.stamp.StampSqlParser;
import javafx.scene.control.Pagination;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author: huqingfang
 * @date: 2018-12-29 22:33
 **/
@Configuration
public class TestConfig {
    public static final String MAIN_DBSETTING = "test_rafy4j";

//    @Bean
//    public DbSettingRepository dbSettingRepository(){
//        return new DbSettingRepository(){{
//            add(new SimpleDbSetting(){{
//                setName(MAIN_DBSETTING);
//            }});
//        }};
//    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        paginationInterceptor.setSqlParserList(Arrays.asList(new StampSqlParser()));

        return paginationInterceptor;
    }
}
