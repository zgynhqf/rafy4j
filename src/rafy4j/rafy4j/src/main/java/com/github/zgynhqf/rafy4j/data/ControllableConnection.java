package com.github.zgynhqf.rafy4j.data;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 一个可以手动控制连接的上下文
 * @author: huqingfang
 * @date: 2018-12-29 22:14
 **/
public interface ControllableConnection {
    /**
     * 连接并开始使用一个连接
     * @return
     * @throws SQLException
     */
    Connection startConnection() throws SQLException;

    /**
     * 启用一个新的连接并启动新的事务。
     * @return
     * @throws SQLException
     */
    Connection startTransaction() throws SQLException;

    /**
     * 提交当前启动的事务。
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 停止手动控制。
     */
    void endConnection();
}
