package com.github.zgynhqf.rafy4j.metadata;

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
    private JDBCType columnType;
    private String columnLength;

    /**
     * 判断当前的字段是否映射到表的列。
     * @return
     */
    public boolean isMappingColumn(){
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

    public JDBCType getColumnType() {
        return columnType;
    }

    public void setColumnType(JDBCType columnType) {
        this.columnType = columnType;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength;
    }
    //endregion
}
