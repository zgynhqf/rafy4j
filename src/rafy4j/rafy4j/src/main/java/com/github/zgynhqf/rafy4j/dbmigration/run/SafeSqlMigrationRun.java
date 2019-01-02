package com.github.zgynhqf.rafy4j.dbmigration.run;

import com.github.zgynhqf.rafy4j.data.*;

public class SafeSqlMigrationRun extends SqlMigrationRun {
    @Override
    protected void RunCore(DbAccesser db) {
        try {
            super.RunCore(db);
        } catch (Exception e) {
        }
    }
}