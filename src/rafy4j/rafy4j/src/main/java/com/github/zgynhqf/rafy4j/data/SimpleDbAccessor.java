package com.github.zgynhqf.rafy4j.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import sun.rmi.runtime.Log;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: huqingfang
 * @date: 2018-12-25 21:33
 **/
public class SimpleDbAccessor implements DbAccessor {
    private static Logger logger = LoggerFactory.getLogger(SimpleDbAccessor.class);

    private ControllableDataSource dataSource;
    private JdbcTemplate jdbc;
    private FormattedSqlConverter fsProvider;

    public SimpleDbAccessor(DbSetting dbSetting) {
        dataSource = new ControllableDataSource(dbSetting.getDataSource());
        jdbc = new JdbcTemplate(dataSource);
        this.fsProvider = FormattedSqlConverterFactory.createSqlConverter(dbSetting.getDriverName());
    }

    public SimpleDbAccessor(DataSource dataSource, FormattedSqlConverter formattedSqlProvider) {
        this.dataSource = new ControllableDataSource(dataSource);
        jdbc = new JdbcTemplate(this.dataSource);
        this.fsProvider = formattedSqlProvider;
    }

//    public DbAccessor(JdbcOperations jdbcOperations, FormattedSqlConverter formattedSqlProvider) {
//        jdbc = jdbcOperations;
//        this.fsProvider = formattedSqlProvider;
//    }

    @Override
    public Connection startConnection() throws SQLException {
        return dataSource.startConnection();
    }

    @Override
    public Connection startTransaction() throws SQLException {
        return dataSource.startTransaction();
    }

    @Override
    public void commit() throws SQLException {
        dataSource.commit();
    }

    @Override
    public void endConnection() {
        dataSource.endConnection();
    }

    @Override
    public JdbcOperations getJdbcOperations() {
        return jdbc;
    }

    @Override
    public int executeText(FormattedSql formattedSql) {
        SqlWithParameters sqlWithParameters = fsProvider.convert(formattedSql);

        logSql(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
        return jdbc.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
    }

    @Override
    public int executeText(String sql) {
        logSql(sql);
        return jdbc.update(sql);
    }

    @Override
    public <T> T queryDataReader(String sql, ResultSetExtractor<T> rse) {
        logSql(sql);
        return jdbc.query(sql, rse);
    }

    @Override
    public <T> T queryDataReader(FormattedSql sql, ResultSetExtractor<T> rse) {
        SqlWithParameters sqlWithParameters = fsProvider.convert(sql);

        logSql(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
        return jdbc.query(sqlWithParameters.getSql(), sqlWithParameters.getParameters(), rse);
    }

    private void logSql(String sql, @Nullable Object... args){
        logger.debug("execute sql:\r\n{},\r\nparameters: {}", sql, args);
    }

    @Override
    public void close() throws IOException {
        if (dataSource.isControlling()) {
            dataSource.endConnection();
        }
    }
}