package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;

import java.sql.JDBCType;

public abstract class ColumnOperation extends MigrationOperation
{
	private String tableName;
	private String columnName;
	private JDBCType dbType;
	private String length;
	private boolean isForeignKey;
	private String defaultValue;

    //region gs
    public final String getTableName()
    {
        return tableName;
    }
    public final void setTableName(String value)
    {
        tableName = value;
    }

    public final String getColumnName()
    {
        return columnName;
    }
    public final void setColumnName(String value)
    {
        columnName = value;
    }

    public final JDBCType getDbType()
    {
        return dbType;
    }
    public final void setDbType(JDBCType value)
    {
        dbType = value;
    }

    public final String getLength()
    {
        return length;
    }
    public final void setLength(String value)
    {
        length = value;
    }

    public final boolean getIsForeignKey()
    {
        return isForeignKey;
    }
	public final void setIsForeignKey(boolean value)
	{
		isForeignKey = value;
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