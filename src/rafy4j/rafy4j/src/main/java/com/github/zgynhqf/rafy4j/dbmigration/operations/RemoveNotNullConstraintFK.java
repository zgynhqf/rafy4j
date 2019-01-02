package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class RemoveNotNullConstraintFK extends ColumnOperation
{
	@Override
	protected void down()
	{
		AddNotNullConstraintFK op = new AddNotNullConstraintFK();
		op.copyFrom(this);
		this.addOperation(op);
	}
}