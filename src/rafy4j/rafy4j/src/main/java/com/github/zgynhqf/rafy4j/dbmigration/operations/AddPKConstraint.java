package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddPKConstraint extends ColumnOperation
{
	@Override
	protected void down()
	{
		RemovePKConstraint op = new RemovePKConstraint();
		op.copyFrom(this);
		this.addOperation(op);
	}
}