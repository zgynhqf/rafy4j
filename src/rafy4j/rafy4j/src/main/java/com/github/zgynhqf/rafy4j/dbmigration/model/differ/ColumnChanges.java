package com.github.zgynhqf.rafy4j.dbmigration.model.differ;

import com.github.zgynhqf.rafy4j.dbmigration.model.Column;

/**
 * 两个列的区别记录
 */
public class ColumnChanges {
    /**
     * 旧列
     */
    private Column oldColumn;
    /**
     * 新列
     */
    private Column newColumn;
    private ChangeType changeType = ChangeType.UnChanged;
    /**
     * 是否更新了是否为空的设定
     */
    private boolean requiredChanged;
    /**
     * 是否是主键改变
     */
    private boolean primaryKeyChanged;
    /**
     * 是否是类型改变
     */
    private boolean dbTypeChanged;
    /**
     * 添加/删除/修改了 外键关系
     */
    private ChangeType foreignRelationChangeType = ChangeType.UnChanged;

    public ColumnChanges(Column oldColumn, Column newColumn, ChangeType changeType) {
        this.setOldColumn(oldColumn);
        this.setNewColumn(newColumn);
        this.setChangeType(changeType);
    }

    private Column getCoreColumn() {
        if (this.getChangeType() == ChangeType.Removed) {
            return this.getOldColumn();
        }

        return this.getNewColumn();
    }

    public final String getTableName() {
        return this.getCoreColumn().getTable().getName();
    }

    public final String getName() {
        return this.getCoreColumn().getName();
    }

    //region gs
    public final Column getOldColumn() {
        return oldColumn;
    }

    private void setOldColumn(Column value) {
        oldColumn = value;
    }

    public final Column getNewColumn() {
        return newColumn;
    }

    private void setNewColumn(Column value) {
        newColumn = value;
    }

    public final ChangeType getChangeType() {
        return changeType;
    }

    private void setChangeType(ChangeType value) {
        changeType = value;
    }

    public final boolean getIsRequiredChanged() {
        return requiredChanged;
    }

    public final void setIsRequiredChanged(boolean value) {
        requiredChanged = value;
    }

    public final boolean getIsPrimaryKeyChanged() {
        return primaryKeyChanged;
    }

    public final void setIsPrimaryKeyChanged(boolean value) {
        primaryKeyChanged = value;
    }

    public final boolean getIsDbTypeChanged() {
        return dbTypeChanged;
    }

    public final void setIsDbTypeChanged(boolean value) {
        dbTypeChanged = value;
    }

    public final ChangeType getForeignRelationChangeType() {
        return foreignRelationChangeType;
    }

    public final void setForeignRelationChangeType(ChangeType value) {
        foreignRelationChangeType = value;
    }
    //endregion

    @Override
    public String toString() {
        return String.format("%s -> %s", getName(), changeType);
    }
}