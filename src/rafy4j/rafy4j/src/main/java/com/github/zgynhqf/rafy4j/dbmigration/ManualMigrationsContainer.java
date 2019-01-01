package com.github.zgynhqf.rafy4j.dbmigration;//package Rafy.dbmigration;
//
//import Rafy.data.DbSetting;
//
//import java.util.ArrayList;
//
///**
// * 手工迁移操作的容器对象
// */
//public class ManualMigrationsContainer extends ArrayList<ManualDbMigration> {
//    private DbSetting _dbSetting;
//
//    private boolean _initialized;
//
//    public final void TryInitalize(DbSetting dbSetting) {
//        this._dbSetting = dbSetting;
//
//        if (!this._initialized) {
//            this.OnInit();
//
//            this._initialized = true;
//        }
//    }
//
//    /**
//     * 子类重写此方法并调用 AddByAssembly 方法来自动添加某个程序集类所有对应该数据库的手工迁移
//     */
//    protected void OnInit() {
//    }
//
////	public final void AddByAssembly(Assembly assembly)
////	{
////		for (var type : assembly.GetTypes())
////		{
////			if (type.IsSubclassOf(ManualDbMigration.class) && !type.IsAbstract && !type.IsGenericTypeDefinition)
////			{
////				Object tempVar = Activator.CreateInstance(type, true);
////				var migration = (ManualDbMigration)((tempVar instanceof ManualDbMigration) ? tempVar : null);
////				if (migration.DbSetting == this._dbSetting.getName())
////				{
////					this.Add(migration);
////				}
////			}
////		}
////	}
//}