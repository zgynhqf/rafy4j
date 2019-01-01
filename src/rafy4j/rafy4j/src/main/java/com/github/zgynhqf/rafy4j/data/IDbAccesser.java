package com.github.zgynhqf.rafy4j.data;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.Closeable;

/**
 * @author: huqingfang
 * @date: 2018-12-25 21:21
 **/
public interface IDbAccesser extends Closeable, ControllableConnection {
    JdbcOperations getJdbcOperations();

    /**
     * Execute a sql which is not a database procudure, return rows effected.
     * @param sql a FormattedSql
     * @return
     */
    int ExecuteText(FormattedSql sql);

    int ExecuteText(String sql);

    <T> T QueryDataReader(String sql, ResultSetExtractor<T> rse);

    <T> T QueryDataReader(FormattedSql sql, ResultSetExtractor<T> rse);
}
