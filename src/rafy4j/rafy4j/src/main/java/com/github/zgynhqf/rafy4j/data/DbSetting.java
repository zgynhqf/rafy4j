package com.github.zgynhqf.rafy4j.data;

import javax.sql.DataSource;

/**
 * 数据库配置
 */
public interface DbSetting {
    /**
     * 数据库配置名称。
     * @return
     */
    String getName();

    /**
     * 获取数据库的名称。
     * @return
     */
    String getDatabase();

    /**
     * 获取驱动器的名称
     * @return
     */
    String getDriverName();

    /**
     * 获取其对应的 DataSource
     * @return
     */
    DataSource getDataSource();
}