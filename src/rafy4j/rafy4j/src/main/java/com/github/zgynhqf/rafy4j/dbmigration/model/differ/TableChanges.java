package com.github.zgynhqf.rafy4j.dbmigration.model.differ;

import com.github.zgynhqf.rafy4j.dbmigration.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 两个表的变更记录
 */
public class TableChanges {
    private Table oldTable;
    private Table newTable;
    private List<ColumnChanges> columnsChanged;
    /**
     * 当表的字段被增/删/改了，则这个值为Changed。
     * 其它则是表示表被删除/增加。
     */
    private ChangeType changeType = ChangeType.UNCHANGED;

    public TableChanges(Table oldTable, Table newTable, ChangeType changeType) {
        this.setOldTable(oldTable);
        this.setNewTable(newTable);
        this.setChangeType(changeType);
        this.setColumnsChanged(new ArrayList<ColumnChanges>());
    }

    public final Table getOldTable() {
        return oldTable;
    }

    private void setOldTable(Table value) {
        oldTable = value;
    }

    public final Table getNewTable() {
        return newTable;
    }

    private void setNewTable(Table value) {
        newTable = value;
    }

    public final List<ColumnChanges> getColumnsChanged() {
        return columnsChanged;
    }

    private void setColumnsChanged(List<ColumnChanges> value) {
        columnsChanged = value;
    }

    public final ChangeType getChangeType() {
        return changeType;
    }

    private void setChangeType(ChangeType value) {
        changeType = value;
    }

    public final String getName() {
        return this.GetCoreTable().getName();
    }

    private Table GetCoreTable() {
        if (this.getChangeType() == ChangeType.REMOVED) {
            return this.getOldTable();
        }

        return this.getNewTable();
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getName(), changeType);
    }
}