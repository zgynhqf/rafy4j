package com.github.zgynhqf.rafy4j.dbmigration.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于自动升级的目标数据库
 */
public class DestinationDatabase extends Database {
    /**
     * 在自动升级过程中，需要忽略掉的表的列表。
     */
    private List<String> ignoreTables = new ArrayList<>();

    public DestinationDatabase(String name) {
        super(name);

//		this.getIgnoreTables().add(EmbadedDbVersionProvider.TableName);
    }

    //region gs
    public final java.util.List<String> getIgnoreTables() {
        return ignoreTables;
    }

    private void setIgnoreTables(java.util.List<String> value) {
        ignoreTables = value;
    }
    //endregion

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