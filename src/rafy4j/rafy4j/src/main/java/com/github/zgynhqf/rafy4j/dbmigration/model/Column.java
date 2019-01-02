package com.github.zgynhqf.rafy4j.dbmigration.model;

import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;

public class Column {
    private boolean required;
    private boolean primaryKey;
    private ForeignConstraint foreignConstraint;
    /**
     * 表示这个主键列是否为自增列。
     */
    private boolean autoIncrement;
    /**
     * 列的注释。
     */
    private String comment;
    /**
     * 所属表
     */
    private Table table;
    /**
     * 数据类型
     */
    private JDBCType dbType;
    /**
     * 可指定列的长度
     * 可以指定数字，或者 MAX。
     * 如果是空，则使用默认长度。
     */
    private String length;
    /**
     * 列名
     */
    private String name;
    /**
     * 默认值。
     */
    private String defaultValue;

    /**
     * Initializes a new instance of the <see cref="Column"/> class.
     *
     * @param name     列名.
     * @param dataType 数据类型.
     * @param length   见 <see cref="Length"/> 属性.
     * @param table    所在表.
     */
    public Column(String name, JDBCType dataType, String length, Table table) {
        if (table == null) {
            throw new IllegalArgumentException("DataTable");
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name");
        }

        this.setDbType(dataType);
        this.setLength(length);
        this.setName(name);
        this.setTable(table);
    }

    //region gs
    public final Table getTable() {
        return table;
    }

    private void setTable(Table value) {
        table = value;
    }

    public final JDBCType getDbType() {
        return dbType;
    }

    private void setDbType(JDBCType value) {
        dbType = value;
    }

    public final String getLength() {
        return length;
    }

    private void setLength(String value) {
        length = value;
    }

    public final String getName() {
        return name;
    }

    private void setName(String value) {
        name = value;
    }

    /**
     * 是否必须的
     */
    public final boolean isRequired() {
        return this.required;
    }

    public final void setRequired(boolean value) {
        this.required = value;

        //如果不是必须的，则肯定不是主键
        if (!value) {
            this.setPrimaryKey(false);
        }
    }

    /**
     * 是否主键
     */
    public final boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public final void setPrimaryKey(boolean value) {
        this.primaryKey = value;

        //是主键，则肯定是必须的
        if (value) {
            this.setRequired(true);
        }
    }

    public final boolean isAutoIncrement() {
        return autoIncrement;
    }

    public final void setAutoIncrement(boolean value) {
        autoIncrement = value;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(String value) {
        comment = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    //endregion

    /**
     * 获取是否外键
     */
    public final boolean isForeignKey() {
        return foreignConstraint != null;
    }

    /**
     * 如果是外键，这表示外键表
     */
    public final ForeignConstraint getForeignConstraint() {
        return foreignConstraint;
    }

    public final void setForeignConstraint(ForeignConstraint value) {
        this.foreignConstraint = value;

        if (value != null) {
            value.Init(this);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}