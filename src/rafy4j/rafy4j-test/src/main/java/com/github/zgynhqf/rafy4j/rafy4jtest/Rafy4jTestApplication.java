package com.github.zgynhqf.rafy4j.rafy4jtest;

import com.github.zgynhqf.rafy4j.annotation.RafyApplication;
import com.github.zgynhqf.rafy4j.rafy4jtest.entity.User;
import com.github.zgynhqf.rafy4j.rafy4jtest.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RafyApplication(entityPackageClasses = {User.class})
@MapperScan(basePackageClasses = {UserMapper.class})
public class Rafy4jTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(Rafy4jTestApplication.class, args);
    }
}