package com.github.zgynhqf.rafy4j.data;

import javax.sql.DataSource;

/**
 * @author: huqingfang
 * @date: 2018-12-29 22:30
 **/
public class SimpleDbSetting implements DbSetting {
    private String name;
    private String database;
    private String driverName;
    private DataSource dataSource;

    //region properties
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    //endregion
}
