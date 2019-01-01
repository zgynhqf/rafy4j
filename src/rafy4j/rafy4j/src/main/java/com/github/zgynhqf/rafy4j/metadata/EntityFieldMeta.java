package com.github.zgynhqf.rafy4j.metadata;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.JDBCType;

/**
 * @author: huqingfang
 * @date: 2018-12-30 22:04
 **/
public class EntityFieldMeta {
    private Field field;
    private String columnName;
    private String columnType;
    private String columnLength;
    private OptionalBoolean isNullable;
    private OptionalBoolean isPrimaryKey;
    private OptionalBoolean isAutoIncrement;
    private String defaultValue;

    /**
     * 判断当前的字段是否映射到表的列。
     *
     * @return
     */
    public boolean isMappingColumn() {
        return !StringUtils.isBlank(columnName);
    }

    //region gs
    public String getName() {
        return field.getName();
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength;
    }

    public OptionalBoolean getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(OptionalBoolean isNullable) {
        this.isNullable = isNullable;
    }

    public OptionalBoolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(OptionalBoolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public OptionalBoolean getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(OptionalBoolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    //endregion
}
