package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

/**
 * 该操作需要执行某个特定的 SQL
 */
public class RunSql extends MigrationOperation {
    private String sql;

    public final String getSql() {
        return sql;
    }

    public final void setSql(String value) {
        sql = value;
    }

    @Override
    protected void Down() {
    }
}