package com.github.zgynhqf.rafy4j.dbmigration.model;

/**
 * 数据库 SCHEMA 的读取器
 */
public interface MetadataReader {
    /**
     * 读取整个库的元数据
     *
     * @return
     */
    Database read();
}