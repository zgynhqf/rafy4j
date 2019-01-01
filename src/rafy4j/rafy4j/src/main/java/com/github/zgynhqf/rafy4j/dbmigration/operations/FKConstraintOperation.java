package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;
import com.github.zgynhqf.rafy4j.dbmigration.model.ForeignConstraint;

//******************************************************
// * 
// * 作者：胡庆访
// * 创建时间：20110109
// * 说明：所有外键相关的操作。
// * 运行环境：.NET 4.0
// * 版本号：1.0.0
// * 
// * 历史记录：
// * 创建文件 胡庆访 20110109
// * 
//******************************************************


public abstract class FKConstraintOperation extends MigrationOperation
{
	private String privateConstraintName;
	public final String getConstraintName()
	{
		return privateConstraintName;
	}
	public final void setConstraintName(String value)
	{
		privateConstraintName = value;
	}

	/** 
	 外键表
	 
	*/
	private String privateDependentTable;
	public final String getDependentTable()
	{
		return privateDependentTable;
	}
	public final void setDependentTable(String value)
	{
		privateDependentTable = value;
	}

	/** 
	 外键表的字段
	 
	*/
	private String privateDependentTableColumn;
	public final String getDependentTableColumn()
	{
		return privateDependentTableColumn;
	}
	public final void setDependentTableColumn(String value)
	{
		privateDependentTableColumn = value;
	}

	/** 
	 主键表
	 
	*/
	private String privatePrincipleTable;
	public final String getPrincipleTable()
	{
		return privatePrincipleTable;
	}
	public final void setPrincipleTable(String value)
	{
		privatePrincipleTable = value;
	}

	/** 
	 主键表的字段
	 
	*/
	private String privatePrincipleTableColumn;
	public final String getPrincipleTableColumn()
	{
		return privatePrincipleTableColumn;
	}
	public final void setPrincipleTableColumn(String value)
	{
		privatePrincipleTableColumn = value;
	}

	/** 
	 是否需要级联删除
	 
	*/
	private boolean privateNeedDeleteCascade;
	public final boolean getNeedDeleteCascade()
	{
		return privateNeedDeleteCascade;
	}
	public final void setNeedDeleteCascade(boolean value)
	{
		privateNeedDeleteCascade = value;
	}

	public final void setCopyFromConstraint(ForeignConstraint value)
	{
		if (value != null)
		{
			this.setDependentTable(value.getFKColumn().getTable().getName());
			this.setDependentTableColumn(value.getFKColumn().getName());
			this.setPrincipleTable(value.getPKColumn().getTable().getName());
			this.setPrincipleTableColumn(value.getPKColumn().getName());
			this.setNeedDeleteCascade(value.getNeedDeleteCascade());
			this.setConstraintName(value.getConstraintName());
		}
	}

	public final void setCopyFrom(FKConstraintOperation value)
	{
		if (value != null)
		{
			this.setDependentTable(value.getDependentTable());
			this.setDependentTableColumn(value.getDependentTableColumn());
			this.setPrincipleTable(value.getPrincipleTable());
			this.setPrincipleTableColumn(value.getPrincipleTableColumn());
			this.setNeedDeleteCascade(value.getNeedDeleteCascade());
			this.setConstraintName(value.getConstraintName());
		}
	}

	@Override
	public String getDescription()
	{
		return String.format("%1$s: %2$s", super.getDescription(), this.getConstraintName());
	}
}