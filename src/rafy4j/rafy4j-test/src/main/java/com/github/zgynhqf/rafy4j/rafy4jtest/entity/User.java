package com.github.zgynhqf.rafy4j.rafy4jtest.entity;

import com.github.zgynhqf.rafy4j.annotation.MappingTable;
import lombok.Data;

/**
 * @author: huqingfang
 * @date: 2018-12-25 15:46
 **/
@Data
public class User {
    private long id;
    private String name;
    private int age;
    private boolean isRejected;
    private double money;
}