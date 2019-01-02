package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

import java.sql.JDBCType;

/**
 * 更新数据库中表或列的注释的操作。
 */
public class UpdateComment extends MigrationOperation {
    /**
     * 要添加注释的表的名字
     */
    private String tableName;

    /**
     * 要添加注释的表的字段的名字。
     * 如果本字段为空，则表示给表加注释，而不是给字段加注释。
     */
    private String columnName;

    /**
     * 修改字段注释信息的字段类型
     */
    private JDBCType columnDbType;

    /**
     * 注释内容
     */
    private String comment;

    //region gs
    public final String getTableName() {
        return tableName;
    }

    public final void setTableName(String value) {
        tableName = value;
    }

    public final String getColumnName() {
        return columnName;
    }

    public final void setColumnName(String value) {
        columnName = value;
    }

    public final JDBCType getColumnDbType() {
        return columnDbType;
    }

    public final void setColumnDbType(JDBCType value) {
        columnDbType = value;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(String value) {
        comment = value;
    }
    //endregion

    @Override
    protected void Down() {
        UpdateComment tempVar = new UpdateComment();
        tempVar.setTableName(this.getTableName());
        tempVar.setColumnName(this.getColumnName());
        tempVar.setColumnDbType(this.getColumnDbType());
        tempVar.setComment("");
        this.AddOperation(tempVar);
    }
}