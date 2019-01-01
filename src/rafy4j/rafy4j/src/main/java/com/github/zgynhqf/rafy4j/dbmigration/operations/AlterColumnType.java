package com.github.zgynhqf.rafy4j.dbmigration.operations;

import java.sql.JDBCType;

public class AlterColumnType extends ColumnOperation {
    private JDBCType privateNewType;

    public final JDBCType getNewType() {
        return privateNewType;
    }

    public final void setNewType(JDBCType value) {
        privateNewType = value;
    }

    private boolean privateIsRequired;

    public final boolean getIsRequired() {
        return privateIsRequired;
    }

    public final void setIsRequired(boolean value) {
        privateIsRequired = value;
    }

    @Override
    protected void Down() {
        AlterColumnType tempVar = new AlterColumnType();
        tempVar.copyFrom(this);
        tempVar.setIsRequired(this.getIsRequired());
        tempVar.setDbType(this.getNewType());
        tempVar.setNewType(this.getDbType());
        this.AddOperation(tempVar);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " From " + this.getDbType() + " To " + this.getNewType();
    }
}