package com.github.zgynhqf.rafy4j.rafy4jtest.entity;

import com.github.zgynhqf.rafy4j.annotation.Reference;
import com.github.zgynhqf.rafy4j.annotation.ReferenceType;
import lombok.Data;

/**
 * @author: huqingfang
 * @date: 2019-01-30 17:40
 **/
@Data
public class Role {
    private long id;
    @Reference(entity = User.class)
    private long userId;
}
