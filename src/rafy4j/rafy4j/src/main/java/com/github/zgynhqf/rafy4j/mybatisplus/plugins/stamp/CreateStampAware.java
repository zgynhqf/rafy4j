package com.github.zgynhqf.rafy4j.mybatisplus.plugins.stamp;

import java.util.Date;

/**
 * 创建时间戳感知实体。
 *
 * @author: huqingfang
 * @date: 2019-01-23 11:10
 **/
public interface CreateStampAware {
    Date getCreateTime();

    void setCreateTime(Date time);

    String getCreator();

    void setCreator(String user);
}
