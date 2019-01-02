package com.github.zgynhqf.rafy4j.dbmigration;

import com.github.zgynhqf.rafy4j.data.DbAccessor;

/**
 * 代表每一个数据库升级执行项
 */
public abstract class MigrationRun {
    /**
     * 通过指定的数据库连接执行
     *
     * @param db
     */
    public final void run(DbAccessor db) {
        this.runCore(db);
    }

    protected abstract void runCore(DbAccessor db);
}