package com.github.zgynhqf.rafy4j.data;//package Rafy.data;
//
//
//import jdk.nashorn.internal.runtime.regexp.joni.Regex;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//
///**
// * 数据库连接结构/方案
// */
//public class DbConnectionSchema {
//    public static final String Provider_SqlClient = "System.data.SqlClient";
//    public static final String Provider_SqlCe = "System.data.SqlServerCe";
//    public static final String Provider_Odbc = "System.data.Odbc";
//    //public const string Provider_Oracle = "System.data.OracleClient";
//    //public const string Provider_Oracle = "Oracle.DataAccess.Client";
//    //public const string Provider_Oracle_Default = "Oracle.ManagedDataAccess.Client";
//    public static final String Provider_MySql = "mysql.data.MySqlClient";
//    public static final String DbName_LocalServer = "LocalSqlServer";
//
//    private String database;
//
//    /**
//     * 连接字符串
//     */
//    private String connectionString;
//
//    /**
//     * 连接的提供器名称
//     */
//    private String providerName;
//
//    public DbConnectionSchema(String connectionString, String providerName) {
//        this.setConnectionString(connectionString);
//        this.setProviderName(providerName);
//    }
//
//    /**
//     * 子类使用
//     */
//    public DbConnectionSchema() {
//    }
//
//    public final String getConnectionString() {
//        return connectionString;
//    }
//
//    public final void setConnectionString(String value) {
//        connectionString = value;
//    }
//
//    public final String getProviderName() {
//        return providerName;
//    }
//
//    public final void setProviderName(String value) {
//        providerName = value;
//    }
//
//    /**
//     * 对应的数据库名称
//     */
//    public final String getDatabase() {
//        if (this.database == null) {
//            this.ParseDbName();
//        }
//
//        return this.database;
//    }
//
//    private void ParseDbName() {
//        IDbConnection con = this.CreateConnection();
//        var database = con.Database;
//
//        //System.data.OracleClient 解析不出这个值，需要特殊处理。
//        if (String.IsNullOrWhiteSpace(database) && IsOracleProvider(this)) {
//            //Oracle 中，把用户名（Schema）认为数据库名。
//            database = GetOracleUserId(this);
//        }
//
//        this.database = database;
//    }
//
//    /**
//     * 使用当前的结构来创建一个连接。
//     *
//     * @return
//     */
//    public final IDbConnection CreateConnection() {
//        DbProviderFactory factory = DbConnectorFactory.GetFactory(this.getProviderName());
//
//        var connection = factory.CreateConnection();
//        connection.ConnectionString = this.getConnectionString();
//
//        return connection;
//    }
//
//    /**
//     * 判断指定的提供程序是否为 Oracle 提供程序。
//     * 目前已知的 Oracle 提供程序有：
//     * System.data.OracleClient、Oracle.DataAccess.Client、Oracle.ManagedDataAccess.Client
//     *
//     * @param schema The schema.
//     * @return
//     */
//    public static boolean IsOracleProvider(DbConnectionSchema schema) {
//        return IsOracleProvider(schema.getProviderName());
//    }
//
//    /**
//     * 判断指定的提供程序是否为 Oracle 提供程序。
//     * 目前已知的 Oracle 提供程序有：
//     * System.data.OracleClient、Oracle.DataAccess.Client、Oracle.ManagedDataAccess.Client
//     *
//     * @param providerName
//     * @return
//     */
//    public static boolean IsOracleProvider(String providerName) {
//        return providerName.contains("Oracle");
//    }
//
//    /**
//     * 获取 Oracle 连接中的用户 Id。
//     *
//     * @param schema
//     * @return
//     */
//    public static String GetOracleUserId(DbConnectionSchema schema) {
////        var match = Regex.Match(schema.getConnectionString(), "User Id=\\s*(?<userId>\\w+)\\s*");
////        if (!match.Success) {
////            throw new NotSupportedException("无法解析出此数据库连接字符串中的数据库名：" + schema.getConnectionString());
////        }
////        var userId = match.Groups["userId"].getValue();
////        return userId;
//        throw new NotImplementedException();
//    }
//}