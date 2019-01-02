package com.github.zgynhqf.rafy4j.dbmigration.model;

import lombok.var;
import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示数据库表的 SCHEMA 定义
 */
public class Table {
    /**
     * 表名
     */
    private String name;
    /**
     * 表的注释。
     */
    private String comment;
    /**
     * 所在的数据库 SCHEMA
     */
    private Database dataBase;
    /**
     * 表中的所有列定义
     */
    private List<Column> columns;

    public Table(String name, Database dataBase) {
        if (StringUtils.isBlank(name)) throw new IllegalArgumentException("name");
        if (dataBase == null) throw new IllegalArgumentException("dataBase");

        columns = new ArrayList<>();
        this.setName(name);
        this.setDataBase(dataBase);
    }

    //region gs
    public final String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(String value) {
        comment = value;
    }

    public final Database getDataBase() {
        return dataBase;
    }

    public void setDataBase(Database value) {
        dataBase = value;
    }

    public final List<Column> getColumns() {
        return columns;
    }
    //endregion

    /**
     * 找到第一个主键定义
     *
     * @return
     */
    public final Column FindPrimaryColumn() {
        return this.getColumns().stream().filter(c -> c.isPrimaryKey()).findFirst().orElse(null);
    }

    /**
     * 找到除主键外的所有列
     *
     * @return
     */
    public final List<Column> FindNormalColumns() {
        return this.getColumns().stream().filter(c -> !c.isPrimaryKey()).collect(Collectors.toList());
    }

    //public IList<Column> FindPrimaryColumns()
    //{
    //    return this.Columns.Where(c => c.isPrimaryKey).ToList();
    //}

    /**
     * 这个表引用的外键表
     */
    public final List<Table> GetForeignTables() {
        java.util.ArrayList<Table> tables = new java.util.ArrayList<Table>();

        for (var column : this.getColumns()) {
            if (column.isForeignKey()) {
                if (!tables.contains(column.getForeignConstraint().getPKTable())) {
                    tables.add(column.getForeignConstraint().getPKTable());
                }
            }
        }

        return tables;
    }

    /**
     * 通过列名找到对应的列
     * 忽略大小写。
     *
     * @param name
     * @return
     */
    public final Column FindColumn(String name) {
        return this.getColumns().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public final Column AddColumn(String name, JDBCType type) {
        return this.AddColumn(name, type, null);
    }

    public final Column AddColumn(String name, JDBCType type, String length) {
        return this.AddColumn(name, type, length, false, false, null);
    }

    public final Column AddColumn(String name, JDBCType type, boolean isRequired, boolean isPrimaryKey) {
        return this.AddColumn(name, type, null, isRequired, isPrimaryKey, null);
    }

    /**
     * 添加一列到这个表中。
     *
     * @param name              The name.
     * @param type              The type.
     * @param length            The length.
     * @param isRequired        if set to <c>true</c> [is required].
     * @param isPrimaryKey      if set to <c>true</c> [is primary key].
     * @param foreignConstraint The foreign constraint.
     * @return
     */
    public final Column AddColumn(String name, JDBCType type, String length, boolean isRequired, boolean isPrimaryKey, ForeignConstraint foreignConstraint) {
        Column tempVar = new Column(name, type, length, this);
        tempVar.setRequired(isRequired);
        tempVar.setPrimaryKey(isPrimaryKey);
        tempVar.setForeignConstraint(foreignConstraint);
        Column column = tempVar;

        this.getColumns().add(column);

        return column;
    }

    public final void sortColumns() {
        columns.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
    }

    @Override
    public String toString() {
        return String.format("%s - (columns:%s)", name, columns.size());
    }
}