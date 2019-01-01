package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddNotNullConstraintFK extends ColumnOperation
{
	@Override
	protected void Down()
	{
		RemoveNotNullConstraintFK tempVar = new RemoveNotNullConstraintFK();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}