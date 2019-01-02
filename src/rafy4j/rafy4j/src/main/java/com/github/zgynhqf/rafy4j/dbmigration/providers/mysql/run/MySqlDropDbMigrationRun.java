package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.run;

import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.MigrationRun;
import org.apache.commons.lang3.NotImplementedException;

/**
 * MySql删除数据库
 */
//[DebuggerDisplay("DROP DATABASE {Database}")]
public final class MySqlDropDbMigrationRun extends MigrationRun {
    /**
     * 获取或者设置当前使用的数据库
     */
    private String database;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String value) {
        database = value;
    }

    /**
     * 运行删除数据库的核心方法
     *
     * @param db 数据库操作对象
     */
    @Override
    protected void RunCore(DbAccessor db) {
        throw new NotImplementedException("drop database operation is not supportted.");
        //var mysqlInstance = new MySqlConnectionStringBuilder(db.Connection.ConnectionString);
        //MySqlConnection.ClearPool(db.Connection as MySqlConnection);

//        try (DbAccessor db2 = new DbAccessor(db.ConnectionSchema.ConnectionString, DbSetting.Provider_MySql)){
//            db2.ExecuteText(String.format("DROP DATABASE IF EXISTS %1$s;", this.getDatabase()));
//        }
    }
}