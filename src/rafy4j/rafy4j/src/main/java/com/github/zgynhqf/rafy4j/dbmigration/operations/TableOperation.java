package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;

import java.sql.JDBCType;

public abstract class TableOperation extends MigrationOperation {
    private String tableName;
    /**
     * 如果有主键，则这个字段表示主键的名称
     * 目前只简单地支持单一主键
     * <p>
     * 注意，这个主键目前还会是自增长的列。
     */
    private String pkName;
    /**
     * 如果有主键，则这个字段表示主键的名称
     * 目前只简单地支持单一主键
     */
    private JDBCType pkDbType;
    /**
     * 主键列的长度。
     */
    private String pkLength;
    /**
     * 主键是否为自增列。
     */
    private boolean pkAutoIncrement;

    //region gs
    public final String getTableName() {
        return tableName;
    }

    public final void setTableName(String value) {
        tableName = value;
    }

    public final String getPKName() {
        return pkName;
    }

    public final void setPKName(String value) {
        pkName = value;
    }

    public final JDBCType getPKDbType() {
        return pkDbType;
    }

    public final void setPKDbType(JDBCType value) {
        pkDbType = value;
    }

    public final String getPKLength() {
        return pkLength;
    }

    public final void setPKLength(String value) {
        pkLength = value;
    }

    public final boolean isPKAutoIncrement() {
        return pkAutoIncrement;
    }

    public final void setPKAutoIncrement(boolean value) {
        pkAutoIncrement = value;
    }
    //endregion

    @Override
    public String getDescription() {
        return String.format("%1$s: %2$s", super.getDescription(), this.getTableName());
    }

    public final void setCopyFromTable(Table value) {
        if (value != null) {
            this.setTableName(value.getName());
            Column pk = value.FindPrimaryColumn();
            if (pk != null) {
                this.setPKName(pk.getName());
                this.setPKDbType(pk.getDbType());
                this.setPKAutoIncrement(pk.isAutoIncrement());
                this.setPKLength(pk.getLength());
            }
        }
    }

    public final void setCopyFrom(TableOperation value) {
        if (value != null) {
            this.setTableName(value.getTableName());
            this.setPKName(value.getPKName());
            this.setPKDbType(value.getPKDbType());
            this.setPKAutoIncrement(value.isPKAutoIncrement());
            this.setPKLength(value.getPKLength());
        }
    }
}