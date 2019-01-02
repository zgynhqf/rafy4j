package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateTable extends TableOperation
{
	@Override
	protected void down()
	{
		DropTable op = new DropTable();
		op.setCopyFrom(this);
		this.addOperation(op);
	}
}