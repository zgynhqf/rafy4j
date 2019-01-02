package com.github.zgynhqf.rafy4j.dbmigration.run;


import com.github.zgynhqf.rafy4j.data.DbAccessor;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

import java.util.function.Consumer;

/**
 * 执行某段特定的代码
 */
public class ActionMigrationRun extends MigrationRun {
    private Consumer<DbAccessor> action;

    public final Consumer<DbAccessor> getAction() {
        return action;
    }

    public final void setAction(Consumer<DbAccessor> value) {
        action = value;
    }

    @Override
    protected void RunCore(DbAccessor db) {
        action.accept(db);
    }
}