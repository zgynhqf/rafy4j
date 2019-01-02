package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateDatabase extends DatabaseOperation
{
	@Override
	protected void Down()
	{
		DropDatabase op = new DropDatabase();
		op.setDatabase(this.getDatabase());
		this.AddOperation(op);
	}
}