package com.github.zgynhqf.rafy4j.dbmigration;

import java.time.Instant;

/**
 * Operation 表示一个数据库升级的最小操作。
 * 同时，Operation 同样可以是一个单独的数据库升级项，所以它继承自 dbmigration。
 * <p>
 * Operation 存在的意义是把数据库操作抽象化，以方便跨库。
 * <p>
 * 同时，所有的 MigrationOperation 作为迁移对象时，表示自动迁移。
 */
public abstract class MigrationOperation extends DbMigration {
    @Override
    public MigrationType GetMigrationType() {
        return MigrationType.AutoMigration;
    }

    private Instant runtimeTimeId = Instant.MIN;

    public final Instant getRuntimeTimeId() {
        return runtimeTimeId;
    }

    public final void setRuntimeTimeId(Instant value) {
        runtimeTimeId = value;
    }

    @Override
    public String getDescription() {
        return this.getClass().getName();
    }

    @Override
    public Instant getTimeId() {
        return this.getRuntimeTimeId();
    }

    /**
     * 升级时，生成的操作即是本身。
     */
    @Override
    protected final void Up() {
        this.AddOperation(this);
    }
}