package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;

import java.sql.JDBCType;

public abstract class ColumnOperation extends MigrationOperation
{
	private String privateTableName;
	private String privateColumnName;
	private JDBCType privateDbType;
	private String privateLength;
	private boolean privateIsForeignKey;
	private String defaultValue;

    //region gs
    public final String getTableName()
    {
        return privateTableName;
    }
    public final void setTableName(String value)
    {
        privateTableName = value;
    }

    public final String getColumnName()
    {
        return privateColumnName;
    }
    public final void setColumnName(String value)
    {
        privateColumnName = value;
    }

    public final JDBCType getDbType()
    {
        return privateDbType;
    }
    public final void setDbType(JDBCType value)
    {
        privateDbType = value;
    }

    public final String getLength()
    {
        return privateLength;
    }
    public final void setLength(String value)
    {
        privateLength = value;
    }

    public final boolean getIsForeignKey()
    {
        return privateIsForeignKey;
    }
	public final void setIsForeignKey(boolean value)
	{
		privateIsForeignKey = value;
	}

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    //endregion

	public final void copyFromColumn(Column value)
	{
		if (value != null)
		{
			this.setTableName(value.getTable().getName());
			this.setColumnName(value.getName());
			this.setDbType(value.getDbType());
			this.setLength(value.getLength());
			this.setIsForeignKey(value.isForeignKey());
			this.setDefaultValue(value.getDefaultValue());
		}
	}

	public final void copyFrom(ColumnOperation value)
	{
		if (value != null)
		{
			this.setTableName(value.getTableName());
			this.setColumnName(value.getColumnName());
			this.setDbType(value.getDbType());
			this.setLength(value.getLength());
			this.setIsForeignKey(value.getIsForeignKey());
            this.setDefaultValue(value.getDefaultValue());
        }
	}

	@Override
	public String getDescription()
	{
		return String.format("%1$s: %2$s.%3$s", super.getDescription(), this.getTableName(), this.getColumnName());
	}
}