package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class DropDatabase extends DatabaseOperation
{
	@Override
	protected void down()
	{
		CreateDatabase op = new CreateDatabase();
		op.setDatabase(this.getDatabase());
		this.addOperation(op);
	}
}