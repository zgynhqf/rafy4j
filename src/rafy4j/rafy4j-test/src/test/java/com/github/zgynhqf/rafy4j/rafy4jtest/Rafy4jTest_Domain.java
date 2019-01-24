package com.github.zgynhqf.rafy4j.rafy4jtest;

import com.github.zgynhqf.rafy4j.ClassMetaReader;
import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.DataLossOperation;
import com.github.zgynhqf.rafy4j.dbmigration.DbMigrationContext;
import com.github.zgynhqf.rafy4j.dbmigration.model.Database;
import com.github.zgynhqf.rafy4j.dbmigration.model.DestinationDatabase;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;
import com.github.zgynhqf.rafy4j.env.RafyEnvironment;
import com.github.zgynhqf.rafy4j.rafy4jtest.config.TestConfig;
import com.github.zgynhqf.rafy4j.rafy4jtest.entity.User;
import com.github.zgynhqf.rafy4j.rafy4jtest.mapper.UserMapper;
import lombok.var;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.JDBCType;
import java.util.List;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rafy4jTest_Domain {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void userCRUD() {
        int oldCount = userMapper.selectCount(null);

        User user = new User();
        user.setName("hqf");
        userMapper.insert(user);

        List<User> users = userMapper.selectList(null);

        int count = users.size();
        Assert.assertEquals(oldCount + 1, count);

        userMapper.delete(null);
    }
}

