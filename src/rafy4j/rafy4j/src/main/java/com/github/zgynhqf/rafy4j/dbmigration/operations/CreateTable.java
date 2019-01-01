package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateTable extends TableOperation
{
	@Override
	protected void Down()
	{
		DropTable tempVar = new DropTable();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}