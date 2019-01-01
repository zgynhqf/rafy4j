package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;

import java.sql.JDBCType;

public abstract class TableOperation extends MigrationOperation {
    private String privateTableName;

    public final String getTableName() {
        return privateTableName;
    }

    public final void setTableName(String value) {
        privateTableName = value;
    }

    /**
     * 如果有主键，则这个字段表示主键的名称
     * 目前只简单地支持单一主键
     * <p>
     * 注意，这个主键目前还会是自增长的列。
     */
    private String privatePKName;

    public final String getPKName() {
        return privatePKName;
    }

    public final void setPKName(String value) {
        privatePKName = value;
    }

    /**
     * 如果有主键，则这个字段表示主键的名称
     * 目前只简单地支持单一主键
     */
    private JDBCType privatePKDbType;

    public final JDBCType getPKDbType() {
        return privatePKDbType;
    }

    public final void setPKDbType(JDBCType value) {
        privatePKDbType = value;
    }

    /**
     * 主键列的长度。
     */
    private String privatePKLength;

    public final String getPKLength() {
        return privatePKLength;
    }

    public final void setPKLength(String value) {
        privatePKLength = value;
    }

    /**
     * 主键是否为自增列。
     */
    private boolean privatePKIdentity;

    public final boolean getPKIdentity() {
        return privatePKIdentity;
    }

    public final void setPKIdentity(boolean value) {
        privatePKIdentity = value;
    }

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
                this.setPKIdentity(pk.isIdentity());
                this.setPKLength(pk.getLength());
            }
        }
    }

    public final void setCopyFrom(TableOperation value) {
        if (value != null) {
            this.setTableName(value.getTableName());
            this.setPKName(value.getPKName());
            this.setPKDbType(value.getPKDbType());
            this.setPKIdentity(value.getPKIdentity());
            this.setPKLength(value.getPKLength());
        }
    }
}