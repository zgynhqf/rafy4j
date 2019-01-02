package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.run;

import com.github.zgynhqf.rafy4j.data.DbAccesser;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;
import org.apache.commons.lang3.NotImplementedException;

/**
 * MySql创建数据库
 */
public final class MySqlCreateDbMigrationRun extends MigrationRun {
    /**
     * 设置或者获取当前操作的数据库
     */
    private String database;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String value) {
        database = value;
    }

    /**
     * 执行数据库的创建操作
     *
     * @param db 数据库访问对象
     */
    @Override
    protected void RunCore(DbAccesser db) {
        throw new NotImplementedException("create database operation is not supportted.");
//        String dbConnString = db.ConnectionSchema.ConnectionString;
//        db.Connection.ConnectionString = dbConnString.replace(db.Connection.Database, "mysql");
//        //手动打开连接，并不关闭，让连接一直处于打开的状态，否则不能立刻连接到新的数据库上
//        db.Connection.Open();
//        db.ExecuteText(String.format("CREATE DATABASE IF NOT EXISTS %1$s CHARACTER SET UTF8;", this.getDatabase()));
//        db.ExecuteText("USE " + this.getDatabase() + ";");
//        db.Connection.Close();
//        db.Connection.ConnectionString = dbConnString;
    }
}