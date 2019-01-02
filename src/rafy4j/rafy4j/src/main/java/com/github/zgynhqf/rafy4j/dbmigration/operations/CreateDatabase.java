package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateDatabase extends DatabaseOperation
{
	@Override
	protected void down()
	{
		DropDatabase op = new DropDatabase();
		op.setDatabase(this.getDatabase());
		this.addOperation(op);
	}
}