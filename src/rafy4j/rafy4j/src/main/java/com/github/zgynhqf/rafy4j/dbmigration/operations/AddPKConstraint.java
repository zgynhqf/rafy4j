package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class AddPKConstraint extends ColumnOperation
{
	@Override
	protected void Down()
	{
		RemovePKConstraint op = new RemovePKConstraint();
		op.copyFrom(this);
		this.AddOperation(op);
	}
}