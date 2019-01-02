package com.github.zgynhqf.rafy4j.dbmigration.run;

import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

public class SqlMigrationRun extends MigrationRun {
    private String privateSql;

    public final String getSql() {
        return privateSql;
    }

    public final void setSql(String value) {
        privateSql = value;
    }

    @Override
    protected void RunCore(DbAccesser db) {
        db.ExecuteText(this.getSql());
    }
}