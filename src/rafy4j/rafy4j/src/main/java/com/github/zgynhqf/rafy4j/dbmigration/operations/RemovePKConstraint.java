package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemovePKConstraint extends ColumnOperation
{
	@Override
	protected void Down()
	{
		AddPKConstraint op = new AddPKConstraint();
		op.copyFrom(this);
		this.AddOperation(op);
	}
}