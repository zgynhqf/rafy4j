package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

/**
 * 该操作需要执行某个特定的 SQL
 */
public class RunSql extends MigrationOperation {
    private String privateSql;

    public final String getSql() {
        return privateSql;
    }

    public final void setSql(String value) {
        privateSql = value;
    }

    @Override
    protected void Down() {
    }
}