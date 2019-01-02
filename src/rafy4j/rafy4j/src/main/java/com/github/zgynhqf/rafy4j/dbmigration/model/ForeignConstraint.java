package com.github.zgynhqf.rafy4j.dbmigration.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 表示某列对应外键关系
 */
public class ForeignConstraint {
    /**
     * 约束名
     */
    private String constraintName;
    /**
     * 外键关系的所有者，外键列。
     */
    private Column fkColumn;
    /**
     * 这个外键对应的主键表的列（不一定是主键列，可以是unique列等）
     */
    private Column pkColumn;
    /**
     * 是否级联删除
     */
    private boolean needDeleteCascade;

    /**
     * @param primaryKeyColumn 这个外键对应的主键表的列（不一定是主键列，可以是unique列等）
     */
    public ForeignConstraint(Column primaryKeyColumn) {
        if (primaryKeyColumn == null) throw new IllegalArgumentException();
        this.setPKColumn(primaryKeyColumn);
    }

    /**
     * @param fkColumn 这个列的外键
     */
    public final void Init(Column fkColumn) {
        if (fkColumn == null) throw new IllegalArgumentException("fkColumn");
        this.setFKColumn(fkColumn);

        if (StringUtils.isBlank(this.getConstraintName())) {
            constraintName = String.format("FK_%1$s_%2$s_%3$s_%4$s", this.getFKColumn().getTable().getName(), this.getFKColumn().getName(), this.getPKColumn().getTable().getName(), this.getPKColumn().getName());
        }
    }

    //region gs
    public final String getConstraintName() {
        return constraintName;
    }

    public final void setConstraintName(String value) {
        constraintName = value;
    }

    public final Column getFKColumn() {
        return fkColumn;
    }

    private void setFKColumn(Column value) {
        fkColumn = value;
    }

    public final Column getPKColumn() {
        return pkColumn;
    }

    private void setPKColumn(Column value) {
        pkColumn = value;
    }

    public final boolean getNeedDeleteCascade() {
        return needDeleteCascade;
    }

    public final void setNeedDeleteCascade(boolean value) {
        needDeleteCascade = value;
    }
    //endregion

    /**
     * 这个外键对应的主键表
     */
    public final Table getPKTable() {
        return this.getPKColumn().getTable();
    }
}