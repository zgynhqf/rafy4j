package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddFKConstraint extends FKConstraintOperation
{
	@Override
	protected void Down()
	{
		RemoveFKConstraint constraint = new RemoveFKConstraint();
		constraint.setCopyFrom(this);
		this.AddOperation(constraint);
	}
}