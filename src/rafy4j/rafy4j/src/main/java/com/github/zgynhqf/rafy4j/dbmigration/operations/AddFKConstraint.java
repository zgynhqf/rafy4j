package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddFKConstraint extends FKConstraintOperation
{
	@Override
	protected void Down()
	{
		RemoveFKConstraint tempVar = new RemoveFKConstraint();
		tempVar.setCopyFrom(this);
		this.AddOperation(tempVar);
	}
}