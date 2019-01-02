package com.github.zgynhqf.rafy4j.dbmigration;//package Rafy.dbmigration;
//
//import Rafy.data.IDbAccesser;
//import Rafy.dbmigration.operations.*;
//import jdk.nashorn.internal.runtime.regexp.joni.Regex;
//
//import java.sql.JDBCType;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.function.Consumer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 表示一个可升级、可回滚的用户数据库升级项。
// * <p>
// * 该类及该类的子类需要支持 Xml 序列化，以支持存储到历史库中。
// */
//public abstract class ManualDbMigration extends dbmigration {
//    @Override
//    public MigrationType GetMigrationType() {
//        return MigrationType.ManualMigration;
//    }
//
//    /**
//     * 对应的数据库
//     */
//    public abstract String getDbSetting();
//
//    /**
//     * 手工迁移的类型：结构/数据。
//     */
//    public abstract ManualMigrationType getType();
//
//    /**
//     * 从类的命名中获取该更新的时间点。手工更新必须使用以下格式命名类："_20110107_093040_ClassName"。
//     *
//     * @return
//     */
//    @Override
//    public Instant getTimeId() {
//        String name = this.getClass().getName();
//        Matcher m = Pattern.compile("^(?<time>_\\d{8}_\\d{6}_)").matcher(name);
//        if (!m.find()) {
//            throw new IllegalStateException("手工更新必须使用以下格式命名类：“_20110107_093040_ClassName”。");
//        }
//        String time = m.group("time");
//
//        Instant value = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss_").parse(time, Instant::from);
////        LocalDateTime value = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss_"));
//
//        return value;
//    }
//
//    //region 方便的 API
//
////        ********************** 代码块解释 *********************************
////         *
////         * 此内的 API 主要方便用户写手工更新时使用。
////         * 暂时写两个意思一下好了，以后看场景的运用再添加。
////         *
////         *********************************************************************
//
//    protected final void RunSql(String sql) {
//        RunSql tempVar = new RunSql();
//        tempVar.setSql(sql);
//        this.AddOperation(tempVar);
//    }
//
//    protected final void RunCode(Consumer<IDbAccesser> action) {
//        RunAction tempVar = new RunAction();
//        tempVar.setAction(action);
//        this.AddOperation(tempVar);
//    }
//
//    protected final void CreateTable(String tableName, String pkName, JDBCType pkDbType, String length, boolean isPkIdentity) {
//        CreateTable tempVar = new CreateTable();
//        tempVar.setTableName(tableName);
//        tempVar.setPKName(pkName);
//        tempVar.setPKDbType(pkDbType);
//        tempVar.setPKLength(length);
//        tempVar.setPKAutoIncrement(isPkIdentity);
//        this.AddOperation(tempVar);
//    }
//
//    protected final void DropTable(String tableName, String pkName, JDBCType pkDbType, String length, boolean isPkIdentity) {
//        DropTable tempVar = new DropTable();
//        tempVar.setTableName(tableName);
//        tempVar.setPKName(pkName);
//        tempVar.setPKDbType(pkDbType);
//        tempVar.setPKLength(length);
//        tempVar.setPKAutoIncrement(isPkIdentity);
//        this.AddOperation(tempVar);
//    }
//
//    //ORIGINAL LINE: protected void CreateNormalColumn(string tableName, string columnName, DbType dataType, string length = null, bool isPrimaryKey = false, bool isAutoIncrement = false)
//    protected final void CreateNormalColumn(String tableName, String columnName, JDBCType dataType, String length, boolean isPrimaryKey, boolean isAutoIncrement) {
//        CreateNormalColumn tempVar = new CreateNormalColumn();
//        tempVar.setTableName(tableName);
//        tempVar.setColumnName(columnName);
//        tempVar.setDbType(dataType);
//        tempVar.setLength(length);
//        tempVar.setPrimaryKey(isPrimaryKey);
//        tempVar.setAutoIncrement(isAutoIncrement);
//        this.AddOperation(tempVar);
//    }
//
//    //ORIGINAL LINE: protected void DropNormalColumn(string tableName, string columnName, DbType dataType, string length = null, bool isPrimaryKey = false, bool isAutoIncrement = false)
//    protected final void DropNormalColumn(String tableName, String columnName, JDBCType dataType, String length, boolean isPrimaryKey, boolean isAutoIncrement) {
//        DropNormalColumn tempVar = new DropNormalColumn();
//        tempVar.setTableName(tableName);
//        tempVar.setColumnName(columnName);
//        tempVar.setDbType(dataType);
//        tempVar.setLength(length);
//        tempVar.setPrimaryKey(isPrimaryKey);
//        tempVar.setAutoIncrement(isAutoIncrement);
//        this.AddOperation(tempVar);
//    }
//
//    protected final void AddPKConstraint(String tableName, String pkName, JDBCType pkDbType, String pkLength) {
//        AddPKConstraint tempVar = new AddPKConstraint();
//        tempVar.setTableName(tableName);
//        tempVar.setColumnName(pkName);
//        tempVar.setDbType(pkDbType);
//        tempVar.setLength(pkLength);
//        this.AddOperation(tempVar);
//    }
//
//    protected final void RemovePKConstraint(String tableName, String pkName, JDBCType pkDbType, String pkLength) {
//        RemovePKConstraint tempVar = new RemovePKConstraint();
//        tempVar.setTableName(tableName);
//        tempVar.setColumnName(pkName);
//        tempVar.setDbType(pkDbType);
//        tempVar.setLength(pkLength);
//        this.AddOperation(tempVar);
//    }
//
//    //endregion
//}