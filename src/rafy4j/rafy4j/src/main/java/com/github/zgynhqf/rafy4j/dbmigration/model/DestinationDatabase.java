package com.github.zgynhqf.rafy4j.dbmigration.model;

import java.util.ArrayList;

//******************************************************
// * 
// * 作者：胡庆访
// * 创建时间：20110109
// * 说明：此文件只包含一个类，具体内容见类型注释。
// * 运行环境：.NET 4.0
// * 版本号：1.0.0
// * 
// * 历史记录：
// * 创建文件 胡庆访 20110109
// * 
//******************************************************


/**
 * 用于自动升级的目标数据库
 */
public class DestinationDatabase extends Database {
    /**
     * 在自动升级过程中，需要忽略掉的表的列表。
     */
    private java.util.List<String> ignoreTables = new ArrayList<>();

    public DestinationDatabase(String name) {
        super(name);

        // TODO: 2018/12/25
//		this.getIgnoreTables().add(EmbadedDbVersionProvider.TableName);
    }

    public final java.util.List<String> getIgnoreTables() {
        return ignoreTables;
    }

    private void setIgnoreTables(java.util.List<String> value) {
        ignoreTables = value;
    }

    /**
     * 判断某个表是否已经被忽略升级。
     *
     * @param tableName
     * @return
     */
    public final boolean IsIgnored(String tableName) {
        return ignoreTables.stream().anyMatch(t -> t.equalsIgnoreCase(tableName));
    }
}