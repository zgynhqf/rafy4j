package com.github.zgynhqf.rafy4j.rafy4jtest.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @author: huqingfang
 * @date: 2018-12-25 15:46
 **/
@Data
public class User {
    private long id;
    private String name;
    /**
     * 映射不可空的字段。
     */
    private int age;
    /**
     * 映射可空的字段
     */
    private Integer nullableAge;
    private boolean isRejected;
    private double money;
    private UUID extId;
    private BigDecimal decimalValue;
    /**
     * 复合类型，不应该映射数据库。
     */
    private List<User> subUsers;
}