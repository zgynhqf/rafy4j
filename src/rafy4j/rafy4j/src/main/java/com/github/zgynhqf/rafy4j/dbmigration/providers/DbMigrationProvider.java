package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.model.IMetadataReader;
import com.github.zgynhqf.rafy4j.dbmigration.RunGenerator;

/**
 * 数据库迁移的提供器。
 * <p>
 * 各种不同的数据库使用不同的提供器程序。
 */
public abstract class DbMigrationProvider {
    /**
     * 该提供器可用的数据库信息
     */
    private DbSetting privateDbSetting;

    public final DbSetting getDbSetting() {
        return privateDbSetting;
    }

    public final void setDbSetting(DbSetting value) {
        privateDbSetting = value;
    }

    /**
     * 创建一个数据库结构读取器
     *
     * @return
     */
    public abstract IMetadataReader CreateSchemaReader();

    /**
     * 创建一个执行生成器
     *
     * @return
     */
    public abstract RunGenerator CreateRunGenerator();
//
//    /**
//     * 创建一个数据库备份器
//     *
//     * @return
//     */
//    public abstract IDbBackuper CreateDbBackuper();
}