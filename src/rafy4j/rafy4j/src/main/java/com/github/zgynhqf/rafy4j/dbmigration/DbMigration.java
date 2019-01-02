package com.github.zgynhqf.rafy4j.dbmigration;

import com.github.zgynhqf.rafy4j.dbmigration.model.MetadataReader;

import java.time.Instant;

/**
 * 表示一个可升级、可回滚的数据库升级项。
 * <p>
 * 该类及该类的子类需要支持 Xml 序列化，以支持存储到历史库中。
 */
public abstract class DbMigration {
    private java.util.ArrayList<MigrationOperation> _operations = new java.util.ArrayList<MigrationOperation>();

    /**
     * 本次迁移对应的时间点。
     *
     * <value>
     * The time unique identifier.
     * </value>
     */
    public abstract Instant getTimeId();

    /**
     * 迁移描述。
     *
     * <value>
     * The description.
     * </value>
     */
    public abstract String getDescription();

    public final void GenerateUpOperations() {
        this._operations.clear();

        this.Up();
    }

    public final void GenerateDownOperations() {
        this._operations.clear();

        this.Down();
    }

    /**
     * 是否自动生成的更新项
     */
    public abstract MigrationType GetMigrationType();

    private MetadataReader databaseMetaReader;

    public final MetadataReader getDatabaseMetaReader() {
        return databaseMetaReader;
    }

    public final void setDatabaseMetaReader(MetadataReader value) {
        databaseMetaReader = value;
    }

    public final java.util.List<MigrationOperation> getOperations() {
        return this._operations;
    }

    /**
     * 返回当前更新项的类型
     */
    public final MigrationType getMigrationType() {
        return this.GetMigrationType();
    }

    /**
     * 数据库升级
     * 注意，开发者在实现此方法时，不能在方法中直接操作数据库，而是应该通过 <see cref="AddOperation(MigrationOperation)"/>、<see cref="ManualDbMigration.RunCode(Action{IDbAccesser})"/>、<see cref="ManualDbMigration.RunSql(string)"/> 等方法添加一些数据库的操作，进而由框架来统一规划与操作数据库。
     * 另外，为了保证能正确的回滚、升级数据库，在实现 Up 方法的同时，应该实现相应的 <see cref="Down"/> 方法，来实现数据库操作的回滚。如果因为某些原因无法实现 Down 方法的话，则应该保证 Up 方法中的代码是可以重入的。
     */
    protected abstract void Up();

    /**
     * 数据库回滚
     * 详细注释，见：<see cref="Up"/> 方法。
     */
    protected abstract void Down();

    /**
     * 在 Up/Down 方法中调用此方法来添加迁移操作。
     *
     * @param operation
     */
    protected final void AddOperation(MigrationOperation operation) {
        this._operations.add(operation);
    }
}