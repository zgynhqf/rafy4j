package com.github.zgynhqf.rafy4j.rafy4jtest;

import com.github.zgynhqf.rafy4j.rafy4jtest.entity.User;
import com.github.zgynhqf.rafy4j.rafy4jtest.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rafy4jTest_Plugin_Stamp {
    @Autowired
    private UserMapper userMapper;

    @Before
    public void init() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("hqf", "password")
        );
    }

    @Test
    public void setCreateStampsOnInsert() {
        User user = new User();
        user.setName("hqf");
        userMapper.insert(user);

        User userAfterInsert = userMapper.selectById(user.getId());
        Assert.assertNotNull(userAfterInsert.getCreateTime());
        Assert.assertEquals("hqf", userAfterInsert.getCreator());
        Assert.assertNotNull(userAfterInsert.getUpdateTime());
        Assert.assertEquals("hqf", userAfterInsert.getUpdater());

        //开始更新，等待一些时间，使得两个时间不一致。
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userAfterInsert.setName("hqf_changed");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("rafy4j", "password")
        );

        userMapper.updateById(userAfterInsert);

        User userAfterUpdate = userMapper.selectById(user.getId());
        Assert.assertEquals(userAfterInsert.getCreateTime(), userAfterUpdate.getCreateTime());
        Assert.assertEquals("hqf", userAfterUpdate.getCreator());
        Assert.assertNotNull(userAfterUpdate.getUpdateTime());
        Assert.assertTrue(userAfterInsert.getUpdateTime().before(userAfterUpdate.getUpdateTime()));
        Assert.assertEquals("rafy4j", userAfterUpdate.getUpdater());

        //再次更新，也需要能更新此值。
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userMapper.updateById(userAfterUpdate);
        User userAfterUpdate2 = userMapper.selectById(user.getId());
        Assert.assertNotNull(userAfterUpdate2.getUpdateTime());
        Assert.assertTrue(userAfterUpdate.getUpdateTime().before(userAfterUpdate2.getUpdateTime()));

        userMapper.delete(null);
    }
}

