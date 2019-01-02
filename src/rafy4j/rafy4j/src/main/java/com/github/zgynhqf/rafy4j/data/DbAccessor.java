package com.github.zgynhqf.rafy4j.data;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.Closeable;

/**
 * 数据库访问器。
 * @author: huqingfang
 * @date: 2018-12-25 21:21
 **/
public interface DbAccessor extends Closeable, ControllableConnection {
    JdbcOperations getJdbcOperations();

    /**
     * Execute a sql which is not a database procudure, return rows effected.
     * @param sql a FormattedSql
     * @return
     */
    int executeText(FormattedSql sql);

    int executeText(String sql);

    <T> T queryDataReader(String sql, ResultSetExtractor<T> rse);

    <T> T queryDataReader(FormattedSql sql, ResultSetExtractor<T> rse);
}
