package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveNotNullConstraintFK extends ColumnOperation
{
	@Override
	protected void Down()
	{
		AddNotNullConstraintFK op = new AddNotNullConstraintFK();
		op.copyFrom(this);
		this.AddOperation(op);
	}
}