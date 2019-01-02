package com.github.zgynhqf.rafy4j.dbmigration.operations;

import java.sql.JDBCType;

public class AlterColumnType extends ColumnOperation {
    private JDBCType newType;
    private boolean required;

    //region gs
    public final JDBCType getNewType() {
        return newType;
    }

    public final void setNewType(JDBCType value) {
        newType = value;
    }

    public final boolean isRequired() {
        return required;
    }

    public final void setRequired(boolean value) {
        required = value;
    }
    //endregion

    @Override
    protected void down() {
        AlterColumnType tempVar = new AlterColumnType();
        tempVar.copyFrom(this);
        tempVar.setRequired(this.isRequired());
        tempVar.setDbType(this.getNewType());
        tempVar.setNewType(this.getDbType());
        this.addOperation(tempVar);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " From " + this.getDbType() + " To " + this.getNewType();
    }
}