package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveFKConstraint extends FKConstraintOperation
{
	@Override
	protected void Down()
	{
		AddFKConstraint op = new AddFKConstraint();
		op.setCopyFrom(this);
		this.AddOperation(op);
	}
}