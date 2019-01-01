package com.github.zgynhqf.rafy4j.dbmigration.run;


import com.github.zgynhqf.rafy4j.data.IDbAccesser;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

import java.util.function.Consumer;

/**
 * 执行某段特定的代码
 */
public class ActionMigrationRun extends MigrationRun {
    private Consumer<IDbAccesser> action;

    public final Consumer<IDbAccesser> getAction() {
        return action;
    }

    public final void setAction(Consumer<IDbAccesser> value) {
        action = value;
    }

    @Override
    protected void RunCore(IDbAccesser db) {
        action.accept(db);
    }
}