package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddFKConstraint extends FKConstraintOperation
{
	@Override
	protected void down()
	{
		RemoveFKConstraint constraint = new RemoveFKConstraint();
		constraint.setCopyFrom(this);
		this.addOperation(constraint);
	}
}