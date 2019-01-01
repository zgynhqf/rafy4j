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
    private String privateTableName;

    public final String getTableName() {
        return privateTableName;
    }

    public final void setTableName(String value) {
        privateTableName = value;
    }

    /**
     * 要添加注释的表的字段的名字。
     * 如果本字段为空，则表示给表加注释，而不是给字段加注释。
     */
    private String privateColumnName;

    public final String getColumnName() {
        return privateColumnName;
    }

    public final void setColumnName(String value) {
        privateColumnName = value;
    }

    /**
     * 修改字段注释信息的字段类型
     */
    private JDBCType privateColumnDbType;

    public final JDBCType getColumnDbType() {
        return privateColumnDbType;
    }

    public final void setColumnDbType(JDBCType value) {
        privateColumnDbType = value;
    }

    /**
     * 注释内容
     */
    private String privateComment;

    public final String getComment() {
        return privateComment;
    }

    public final void setComment(String value) {
        privateComment = value;
    }

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