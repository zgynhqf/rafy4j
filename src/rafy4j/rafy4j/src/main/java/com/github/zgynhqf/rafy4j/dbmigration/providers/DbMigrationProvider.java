package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.model.MetadataReader;
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
    private DbSetting dbSetting;

    public final DbSetting getDbSetting() {
        return dbSetting;
    }

    public final void setDbSetting(DbSetting value) {
        dbSetting = value;
    }

    /**
     * 创建一个数据库结构读取器
     *
     * @return
     */
    public abstract MetadataReader createSchemaReader();

    /**
     * 创建一个执行生成器
     *
     * @return
     */
    public abstract RunGenerator createRunGenerator();
//
//    /**
//     * 创建一个数据库备份器
//     *
//     * @return
//     */
//    public abstract IDbBackuper CreateDbBackuper();
}