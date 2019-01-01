package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveFKConstraint extends FKConstraintOperation
{
	@Override
	protected void Down()
	{
		AddFKConstraint tempVar = new AddFKConstraint();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}