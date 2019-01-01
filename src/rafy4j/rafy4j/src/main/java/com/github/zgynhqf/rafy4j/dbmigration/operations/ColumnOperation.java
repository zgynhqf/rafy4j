package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;

import java.sql.JDBCType;

public abstract class ColumnOperation extends MigrationOperation
{
	private String privateTableName;
	public final String getTableName()
	{
		return privateTableName;
	}
	public final void setTableName(String value)
	{
		privateTableName = value;
	}

	private String privateColumnName;
	public final String getColumnName()
	{
		return privateColumnName;
	}
	public final void setColumnName(String value)
	{
		privateColumnName = value;
	}

	private JDBCType privateDbType;
	public final JDBCType getDbType()
	{
		return privateDbType;
	}
	public final void setDbType(JDBCType value)
	{
		privateDbType = value;
	}

	private String privateLength;
	public final String getLength()
	{
		return privateLength;
	}
	public final void setLength(String value)
	{
		privateLength = value;
	}

	private boolean privateIsForeignKey;
	public final boolean getIsForeignKey()
	{
		return privateIsForeignKey;
	}
	public final void setIsForeignKey(boolean value)
	{
		privateIsForeignKey = value;
	}

	public final void setCopyFromColumn(Column value)
	{
		if (value != null)
		{
			this.setTableName(value.getTable().getName());
			this.setColumnName(value.getName());
			this.setDbType(value.getDbType());
			this.setLength(value.getLength());
			this.setIsForeignKey(value.isForeignKey());
		}
	}

	public final void setCopyFrom(ColumnOperation value)
	{
		if (value != null)
		{
			this.setTableName(value.getTableName());
			this.setColumnName(value.getColumnName());
			this.setDbType(value.getDbType());
			this.setLength(value.getLength());
			this.setIsForeignKey(value.getIsForeignKey());
		}
	}

	@Override
	public String getDescription()
	{
		return String.format("%1$s: %2$s.%3$s", super.getDescription(), this.getTableName(), this.getColumnName());
	}
}