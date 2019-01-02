package com.github.zgynhqf.rafy4j.dbmigration.run;

import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

public class SqlMigrationRun extends MigrationRun {
    private String sql;

    public final String getSql() {
        return sql;
    }

    public final void setSql(String value) {
        sql = value;
    }

    @Override
    protected void runCore(DbAccessor db) {
        db.executeText(this.getSql());
    }
}