package com.github.zgynhqf.rafy4j.dbmigration.model;

/**
 * 数据库 Schema 的读取器
 */
public interface IMetadataReader {
    /**
     * 读取整个库的元数据
     *
     * @return
     */
    Database Read();
}