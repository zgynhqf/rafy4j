package com.github.zgynhqf.rafy4j.dbmigration.operations;

public class CreateNormalColumn extends ColumnOperation
{
	private boolean privateIsPrimaryKey;
	public final boolean getIsPrimaryKey()
	{
		return privateIsPrimaryKey;
	}
	public final void setIsPrimaryKey(boolean value)
	{
		privateIsPrimaryKey = value;
	}
	private boolean privateIsIdentity;
	public final boolean getIsIdentity()
	{
		return privateIsIdentity;
	}
	public final void setIsIdentity(boolean value)
	{
		privateIsIdentity = value;
	}

	@Override
	protected void Down()
	{
		DropNormalColumn tempVar = new DropNormalColumn();
		tempVar.setCopyFrom(this);
		tempVar.setIsPrimaryKey(this.getIsPrimaryKey());
		tempVar.setIsIdentity(this.getIsIdentity());
		this.AddOperation(tempVar);
	}
}