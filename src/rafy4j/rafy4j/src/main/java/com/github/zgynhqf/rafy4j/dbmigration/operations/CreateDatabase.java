package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateDatabase extends DatabaseOperation
{
	@Override
	protected void Down()
	{
		DropDatabase tempVar = new DropDatabase();
		tempVar.setDatabase(this.getDatabase());
		this.AddOperation(tempVar);
	}
}