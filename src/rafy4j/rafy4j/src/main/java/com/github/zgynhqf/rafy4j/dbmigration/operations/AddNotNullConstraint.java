package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddNotNullConstraint extends ColumnOperation
{
	@Override
	protected void Down()
	{
		RemoveNotNullConstraint tempVar = new RemoveNotNullConstraint();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}