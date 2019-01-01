package com.github.zgynhqf.rafy4j.data;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: huqingfang
 * @date: 2018-12-25 21:33
 **/
public class DbAccessor implements IDbAccesser {
    private ControllableDataSource dataSource;
    private JdbcTemplate jdbc;
    private FormattedSqlConverter fsProvider;

    public DbAccessor(DbSetting dbSetting) {
        dataSource = new ControllableDataSource(dbSetting.getDataSource());
        jdbc = new JdbcTemplate(dataSource);
        this.fsProvider = FormattedSqlConverterFactory.createSqlConverter(dbSetting.getDriverName());
    }

    public DbAccessor(DataSource dataSource, FormattedSqlConverter formattedSqlProvider) {
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
    public int ExecuteText(FormattedSql formattedSql) {
        SqlWithParameters sqlWithParameters = fsProvider.convert(formattedSql);

        return jdbc.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
    }

    @Override
    public int ExecuteText(String sql) {
        return jdbc.update(sql);
    }

    @Override
    public <T> T QueryDataReader(String sql, ResultSetExtractor<T> rse) {
        return jdbc.query(sql, rse);
    }

    @Override
    public <T> T QueryDataReader(FormattedSql sql, ResultSetExtractor<T> rse) {
        SqlWithParameters sqlWithParameters = fsProvider.convert(sql);

        return jdbc.query(sqlWithParameters.getSql(), sqlWithParameters.getParameters(), rse);
    }

    static final EmptyResultSetExtractor rseSingleton = new EmptyResultSetExtractor();

    @Override
    public void close() throws IOException {
        if (dataSource.isControlling()) {
            dataSource.endConnection();
        }
    }

    private static class EmptyResultSetExtractor implements ResultSetExtractor<ResultSet> {
        @Override
        public ResultSet extractData(ResultSet rs) throws SQLException, DataAccessException {
            return rs;
        }
    }
}