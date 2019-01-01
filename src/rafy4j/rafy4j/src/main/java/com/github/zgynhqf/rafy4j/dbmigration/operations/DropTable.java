package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class DropTable extends TableOperation
{
	@Override
	protected void Down()
	{
		CreateTable tempVar = new CreateTable();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}