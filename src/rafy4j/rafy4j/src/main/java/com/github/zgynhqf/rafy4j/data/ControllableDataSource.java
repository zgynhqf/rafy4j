package com.github.zgynhqf.rafy4j.data;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: huqingfang
 * @date: 2018-12-29 18:28
 **/
class ControllableDataSource extends AbstractDataSource implements SmartDataSource, ControllableConnection {
    private DataSource dataSource;
    private Connection connection;
    private int useLevel = 0;

    public ControllableDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection startConnection() throws SQLException {
        if (useLevel == 0) {
            connection = dataSource.getConnection();
        }

        useLevel++;

        return connection;
    }

    @Override
    public Connection startTransaction() throws SQLException {
        Connection connection = this.startConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (useLevel == 1) {
            connection.commit();
        }
    }

    @Override
    public void endConnection() {
        useLevel--;
        if (useLevel == 0) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        } else if (useLevel < 0) {
            useLevel = 0;
        }
    }

    public boolean isControlling() {
        return useLevel > 0;
    }

    @Override
    public boolean shouldClose(Connection con) {
        if (connection != null) return false;

        if (dataSource instanceof SmartDataSource) {
            return ((SmartDataSource) dataSource).shouldClose(con);
        }

        return true;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null) return connection;
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (connection != null) return connection;
        return dataSource.getConnection(username, password);
    }
}
