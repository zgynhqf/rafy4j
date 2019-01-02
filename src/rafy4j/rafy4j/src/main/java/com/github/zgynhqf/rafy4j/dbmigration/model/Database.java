package com.github.zgynhqf.rafy4j.dbmigration.model;

import lombok.var;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个数据库的 SCHEMA 定义
 */
public class Database {
    /**
     * 数据库名称
     */
    private String name;
    /**
     * 数据库中所包含的表
     */
    private List<Table> tables;
    /**
     * 数据库并不存在
     */
    private boolean removed;

    public Database(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }

        this.setName(name);
        this.setTables(new ArrayList<Table>());
    }

    //region gs
    public final String getName() {
        return name;
    }

    private void setName(String value) {
        name = value;
    }

    public final boolean getRemoved() {
        return removed;
    }

    public final void setRemoved(boolean value) {
        removed = value;
    }

    public final List<Table> getTables() {
        return tables;
    }

    private void setTables(List<Table> value) {
        tables = value;
    }
    //endregion

    /**
     * 通过表名找到对应的表。
     * 忽略大小写。
     *
     * @param tableNameIgnoreCase
     * @return
     */
    public final Table findTable(String tableNameIgnoreCase) {
        List<Table> tables = this.getTables();

        for (int i = 0, c = tables.size(); i < c; i++) {
            var table = tables.get(i);
            if (table.getName().equalsIgnoreCase(tableNameIgnoreCase)) {
                return table;
            }
        }

        return null;
    }

    /**
     * 根据外键关系为表排序
     * 有外键关系的表放在后面，没有关系的表放在前面。（被引用的表放在引用表的前面。）
     */
    public final void orderByRelations() {
        int count = 0;

        List<Table> tables = this.getTables();
        for (int i = 0, l = tables.size(); i < l; i++) {
            var left = tables.get(i);
            var foreignTables = left.getForeignTables();
            for (int j = i + 1; j < l; j++) {
                var right = tables.get(j);
                if (foreignTables.contains(right)) {
                    tables.set(i, right);
                    tables.set(j, left);
                    count++;
                    if (count > 100000) {
                        //由于目前本方法很难实现环状外键的排序，所以暂时只有不支持了。
                        throw new IllegalStateException(String.format("在表 %1$s 的外键链条中出现了环（例如：A->B->C->A），目前不支持此类型的数据库迁移。\r\n你可以使用手工迁移完成。", left.getName()));
                    }
                    i--;
                    break;
                }
            }
        }
    }
}