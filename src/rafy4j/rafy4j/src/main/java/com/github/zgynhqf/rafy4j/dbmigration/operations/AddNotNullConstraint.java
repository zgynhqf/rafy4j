package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddNotNullConstraint extends ColumnOperation
{
	@Override
	protected void down()
	{
		RemoveNotNullConstraint op = new RemoveNotNullConstraint();
		op.copyFrom(this);
		this.addOperation(op);
	}
}