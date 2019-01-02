package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemovePKConstraint extends ColumnOperation
{
	@Override
	protected void down()
	{
		AddPKConstraint op = new AddPKConstraint();
		op.copyFrom(this);
		this.addOperation(op);
	}
}