package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddPKConstraint extends ColumnOperation
{
	@Override
	protected void Down()
	{
		RemovePKConstraint tempVar = new RemovePKConstraint();
		tempVar.copyFrom(this);
		this.AddOperation(tempVar);
	}
}