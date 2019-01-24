package com.github.zgynhqf.rafy4j.mybatisplus.plugins.stamp;

import java.util.Date;

/**
 * 更新时间戳感知实体。
 *
 * @author: huqingfang
 * @date: 2019-01-23 11:13
 **/
public interface UpdateStampAware {
    Date getUpdateTime();

    void setUpdateTime(Date time);

    String getUpdater();

    void setUpdater(String user);
}
