package com.github.zgynhqf.rafy4j.dbmigration;


/**
 * 数据迁移的一些默认配置。
 */
public final class DbMigrationSettings {
    /**
     * 可设置所有主键及外键的长度。默认为 40。
     * <p>
     * <p>
     * http://stackoverflow.com/questions/2863993/is-of-a-type-that-is-invalid-for-use-as-a-key-column-in-an-index
     * SqlServer 主键最大 450、Oracle 主键最大 400。
     */
    public static String PKFKDbTypeLength = "40";

    /**
     * 可设置所有一般字符串字段的默认长度。默认为 2000。
     */
    public static String StringColumnDbTypeLength = "2000";
}