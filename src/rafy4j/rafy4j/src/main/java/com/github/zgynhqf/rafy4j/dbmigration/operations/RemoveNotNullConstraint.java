package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveNotNullConstraint extends ColumnOperation
{
	@Override
	protected void down()
	{
		AddNotNullConstraint op = new AddNotNullConstraint();
		op.copyFrom(this);
		this.addOperation(op);
	}
}