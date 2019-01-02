package com.github.zgynhqf.rafy4j.dbmigration.run;


import com.github.zgynhqf.rafy4j.data.DbAccesser;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

import java.util.function.Consumer;

/**
 * 执行某段特定的代码
 */
public class ActionMigrationRun extends MigrationRun {
    private Consumer<DbAccesser> action;

    public final Consumer<DbAccesser> getAction() {
        return action;
    }

    public final void setAction(Consumer<DbAccesser> value) {
        action = value;
    }

    @Override
    protected void RunCore(DbAccesser db) {
        action.accept(db);
    }
}