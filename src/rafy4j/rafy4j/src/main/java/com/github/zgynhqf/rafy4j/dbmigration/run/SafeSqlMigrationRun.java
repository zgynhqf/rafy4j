package com.github.zgynhqf.rafy4j.dbmigration.run;

import com.github.zgynhqf.rafy4j.data.*;

public class SafeSqlMigrationRun extends SqlMigrationRun {
    @Override
    protected void runCore(DbAccessor db) {
        try {
            super.runCore(db);
        } catch (Exception e) {
        }
    }
}