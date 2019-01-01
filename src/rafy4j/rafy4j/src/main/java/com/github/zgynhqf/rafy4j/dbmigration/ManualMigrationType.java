package com.github.zgynhqf.rafy4j.dbmigration;

/**
 * 手动升级的类型
 */
public enum ManualMigrationType {
    /**
     * 手动结构升级
     */
    Schema,

    /**
     * 手动数据升级
     */
    Data;

    public int getValue() {
        return this.ordinal();
    }

    public static ManualMigrationType forValue(int value) {
        return values()[value];
    }
}