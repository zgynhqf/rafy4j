package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class DropDatabase extends DatabaseOperation
{
	@Override
	protected void Down()
	{
		CreateDatabase tempVar = new CreateDatabase();
		tempVar.setDatabase(this.getDatabase());
		this.AddOperation(tempVar);
	}
}