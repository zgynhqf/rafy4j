package com.github.zgynhqf.rafy4j.dbmigration;

//******************************************************
// * 
// * 作者：胡庆访
// * 创建时间：20120102
// * 说明：此文件只包含一个类，具体内容见类型注释。
// * 运行环境：.NET 4.0
// * 版本号：1.0.0
// * 
// * 历史记录：
// * 创建文件 胡庆访 20120102
// * 
//******************************************************


import com.github.zgynhqf.rafy4j.data.IDbAccesser;

/**
 代表每一个数据库升级执行项
 
*/
public abstract class MigrationRun
{
	/** 
	 通过指定的数据库连接执行
	 
	 @param db
	*/
	public final void Run(IDbAccesser db)
	{
		this.RunCore(db);
	}

	protected abstract void RunCore(IDbAccesser db);
}