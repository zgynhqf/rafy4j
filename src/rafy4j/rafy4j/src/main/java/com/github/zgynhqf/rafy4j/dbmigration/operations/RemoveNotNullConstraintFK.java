package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveNotNullConstraintFK extends ColumnOperation
{
	@Override
	protected void Down()
	{
		AddNotNullConstraintFK tempVar = new AddNotNullConstraintFK();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}