package com.github.zgynhqf.rafy4j.rafy4jtest.entity;

import com.github.zgynhqf.rafy4j.annotation.MappingTable;
import lombok.Data;

/**
 * @author: huqingfang
 * @date: 2018-12-25 15:46
 **/
@Data
@MappingTable
//@Table(name = "user")
public class User {
//    @Column(name = "id", type = MySqlTypeConstant.BIGINT, length = 20, isKey = true, isAutoIncrement = true)
    private int id;
//    @Column(name = "name", type = MySqlTypeConstant.VARCHAR)
    private String name;
//    private String code;
}