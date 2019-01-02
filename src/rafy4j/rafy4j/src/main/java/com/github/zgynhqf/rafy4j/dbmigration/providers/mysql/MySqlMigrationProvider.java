package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;


import com.github.zgynhqf.rafy4j.dbmigration.model.MetadataReader;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMigrationProvider;
import com.github.zgynhqf.rafy4j.dbmigration.RunGenerator;

/**
 * MySql的数据库迁移提供程序
 */
public final class MySqlMigrationProvider extends DbMigrationProvider {
//    /**
//     * 创建一个数据库备份器
//     *
//     * @return 暂不支持MySql数据的备份
//     */
//    @Override
//    public IDbBackuper CreateDbBackuper() {
//        throw new NotSupportedException("暂时不支持 mysql 数据库的备份。");
//    }

    /**
     * 创建一个执行生成器
     *
     * @return 返回MySqlRunGenerator的实例对象
     */
    @Override
    public RunGenerator CreateRunGenerator() {
        return new MySqlRunGenerator();
    }

    /**
     * 创建一个数据库结构读取器
     *
     * @return 返回MySqlMetaReader的实例对象
     */
    @Override
    public MetadataReader CreateSchemaReader() {
        return new MySqlMetaReader(this.getDbSetting());
    }
}