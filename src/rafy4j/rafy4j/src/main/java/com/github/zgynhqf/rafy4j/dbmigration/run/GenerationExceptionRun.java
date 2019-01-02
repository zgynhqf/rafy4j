package com.github.zgynhqf.rafy4j.dbmigration.run;

import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.DbMigrationException;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;

/**
 * 在 Generate 的过程中最好不要出现异常，可以使用此类来延迟异常的抛出，在真正开始执行 Sql（run） 时才抛出异常。
 */
public class GenerationExceptionRun extends MigrationRun {
    private String message;

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String value) {
        message = value;
    }

    @Override
    protected void RunCore(DbAccessor db) {
        throw new DbMigrationException(this.getMessage());
    }
}