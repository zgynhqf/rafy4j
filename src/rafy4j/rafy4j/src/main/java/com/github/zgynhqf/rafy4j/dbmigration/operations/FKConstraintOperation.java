package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.ForeignConstraint;

public abstract class FKConstraintOperation extends MigrationOperation {
    private String constraintName;
    /**
     * 外键表
     */
    private String dependentTable;
    /**
     * 外键表的字段
     */
    private String dependentTableColumn;
    /**
     * 主键表
     */
    private String principleTable;
    /**
     * 主键表的字段
     */
    private String principleTableColumn;
    /**
     * 是否需要级联删除
     */
    private boolean needDeleteCascade;

    //region gs
    public final String getConstraintName() {
        return constraintName;
    }

    public final void setConstraintName(String value) {
        constraintName = value;
    }

    public final String getDependentTable() {
        return dependentTable;
    }

    public final void setDependentTable(String value) {
        dependentTable = value;
    }

    public final String getDependentTableColumn() {
        return dependentTableColumn;
    }

    public final void setDependentTableColumn(String value) {
        dependentTableColumn = value;
    }

    public final String getPrincipleTable() {
        return principleTable;
    }

    public final void setPrincipleTable(String value) {
        principleTable = value;
    }

    public final String getPrincipleTableColumn() {
        return principleTableColumn;
    }

    public final void setPrincipleTableColumn(String value) {
        principleTableColumn = value;
    }

    public final boolean getNeedDeleteCascade() {
        return needDeleteCascade;
    }

    public final void setNeedDeleteCascade(boolean value) {
        needDeleteCascade = value;
    }
    //endregion

    public final void setCopyFromConstraint(ForeignConstraint value) {
        if (value != null) {
            this.setDependentTable(value.getFKColumn().getTable().getName());
            this.setDependentTableColumn(value.getFKColumn().getName());
            this.setPrincipleTable(value.getPKColumn().getTable().getName());
            this.setPrincipleTableColumn(value.getPKColumn().getName());
            this.setNeedDeleteCascade(value.getNeedDeleteCascade());
            this.setConstraintName(value.getConstraintName());
        }
    }

    public final void setCopyFrom(FKConstraintOperation value) {
        if (value != null) {
            this.setDependentTable(value.getDependentTable());
            this.setDependentTableColumn(value.getDependentTableColumn());
            this.setPrincipleTable(value.getPrincipleTable());
            this.setPrincipleTableColumn(value.getPrincipleTableColumn());
            this.setNeedDeleteCascade(value.getNeedDeleteCascade());
            this.setConstraintName(value.getConstraintName());
        }
    }

    @Override
    public String getDescription() {
        return String.format("%1$s: %2$s", super.getDescription(), this.getConstraintName());
    }
}