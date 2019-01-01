package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

public abstract class DatabaseOperation extends MigrationOperation
{
	private String privateDatabase;
	public final String getDatabase()
	{
		return privateDatabase;
	}
	public final void setDatabase(String value)
	{
		privateDatabase = value;
	}

	@Override
	public String getDescription()
	{
		return String.format("%1$s: %2$s", super.getDescription(), this.getDatabase());
	}
}