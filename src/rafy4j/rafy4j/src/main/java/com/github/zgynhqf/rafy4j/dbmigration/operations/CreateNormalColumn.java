package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateNormalColumn extends ColumnOperation {
    private boolean primaryKey;
    private boolean autoIncrement;

    //region gs
    public final boolean isPrimaryKey() {
        return primaryKey;
    }

    public final void setPrimaryKey(boolean value) {
        primaryKey = value;
    }

    public final boolean isAutoIncrement() {
        return autoIncrement;
    }

    public final void setAutoIncrement(boolean value) {
        autoIncrement = value;
    }
    //endregion

    @Override
    protected void down() {
        DropNormalColumn op = new DropNormalColumn();
        op.copyFrom(this);
        op.setPrimaryKey(this.isPrimaryKey());
        op.setAutoIncrement(this.isAutoIncrement());
        this.addOperation(op);
    }
}