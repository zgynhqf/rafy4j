package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.data.DbAccessor;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

import java.util.function.Consumer;

/**
 * 操作执行某个具体的代码段
 */
public class RunAction extends MigrationOperation {
    private Consumer<DbAccessor> action;

    public final Consumer<DbAccessor> getAction() {
        return action;
    }

    public final void setAction(Consumer<DbAccessor> value) {
        action = value;
    }

    @Override
    protected void Down() {
    }
}