package com.github.zgynhqf.rafy4j.data;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: huqingfang
 * @date: 2018-12-29 22:14
 **/
public interface ControllableConnection {
    //    DataSource getDataSource();
    Connection startConnection() throws SQLException;

    Connection startTransaction() throws SQLException;

    void commit() throws SQLException;

    void endConnection();
}
