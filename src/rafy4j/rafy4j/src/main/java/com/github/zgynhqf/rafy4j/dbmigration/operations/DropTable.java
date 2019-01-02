package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class DropTable extends TableOperation
{
	@Override
	protected void down()
	{
		CreateTable op = new CreateTable();
		op.setCopyFrom(this);
		this.addOperation(op);
	}
}