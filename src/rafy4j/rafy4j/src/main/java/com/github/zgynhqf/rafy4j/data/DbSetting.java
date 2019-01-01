package com.github.zgynhqf.rafy4j.data;

import javax.sql.DataSource;

/**
 * 数据库配置
 */
public interface DbSetting {
    /**
     * 数据库配置名称。
     * @return
     */
    String getName();

    /**
     * 获取数据库的名称。
     * @return
     */
    String getDatabase();

    /**
     * 获取驱动器的名称
     * @return
     */
    String getDriverName();

    /**
     * 获取其对应的 DataSource
     * @return
     */
    DataSource getDataSource();
}

//public class DbSetting extends DbConnectionSchema {
//    private static java.util.HashMap<String, DbSetting> _generatedSettings = new java.util.HashMap<String, DbSetting>();
//
//    private DbSetting() {
//    }
//
//    /**
//     * 配置名称
//     */
//    private String privateName;
//
//    public final String getName() {
//        return privateName;
//    }
//
//    private void setName(String value) {
//        privateName = value;
//    }
//
//    /**
//     * 查找或者根据约定创建连接字符串
//     *
//     * @param dbSettingName
//     * @return
//     */
//    public static DbSetting FindOrCreate(String dbSettingName) {
//        if (dbSettingName == null) //可以是空字符串。
//        {
//            throw new ArgumentNullException("dbSetting");
//        }
//
//        DbSetting setting = null;
//
//        if (!((setting = _generatedSettings.get(dbSettingName)) != null)) {
//            synchronized (_generatedSettings) {
//                if (!((setting = _generatedSettings.get(dbSettingName)) != null)) {
////#if NET45
//                    var config = ConfigurationManager.ConnectionStrings[dbSettingName];
////#endif
////#if NS2
//                    var config = ConfigurationHelper.GetConnectionString(dbSettingName);
////#endif
//                    if (config != null) {
//                        DbSetting tempVar = new DbSetting();
//                        tempVar.setConnectionString(config.ConnectionString);
//                        tempVar.setProviderName(config.ProviderName);
//                        setting = tempVar;
//                    } else {
//                        setting = Create(dbSettingName);
//                    }
//
//                    setting.setName(dbSettingName);
//
//                    _generatedSettings.put(dbSettingName, setting);
//                }
//            }
//        }
//
//        return setting;
//    }
//
//    /**
//     * 添加一个数据库连接配置。
//     *
//     * @param name
//     * @param connectionString
//     * @param providerName
//     */
//    public static DbSetting SetSetting(String name, String connectionString, String providerName) {
//        if (DotNetToJavaStringHelper.isNullOrEmpty(name)) {
//            throw new InvalidOperationException("string.IsNullOrEmpty(dbSetting.Name) must be false.");
//        }
//        if (DotNetToJavaStringHelper.isNullOrEmpty(connectionString)) {
//            throw new ArgumentNullException("connectionString");
//        }
//        if (DotNetToJavaStringHelper.isNullOrEmpty(providerName)) {
//            throw new ArgumentNullException("providerName");
//        }
//
//        DbSetting tempVar = new DbSetting();
//        tempVar.setName(name);
//        tempVar.setConnectionString(connectionString);
//        tempVar.setProviderName(providerName);
//        DbSetting setting = tempVar;
//
//        synchronized (_generatedSettings) {
//            _generatedSettings.put(name, setting);
//        }
//
//        return setting;
//    }
//
//    /**
//     * 获取当前已经被生成的 DbSetting。
//     *
//     * @return
//     */
//    public static Iterable<DbSetting> GetGeneratedSettings() {
//        return _generatedSettings.values();
//    }
//
//    private static DbSetting Create(String dbSettingName) {
//        //查找连接字符串时，根据用户的 LocalSqlServer 来查找。
////#if NET45
//        var local = ConfigurationManager.ConnectionStrings[DbName_LocalServer];
////#endif
////#if NS2
//        var local = ConfigurationHelper.GetConnectionString(DbName_LocalServer);
////#endif
//        if (local != null && Provider_SqlClient.equals(local.ProviderName)) {
//            SqlConnectionStringBuilder builder = new SqlConnectionStringBuilder(local.ConnectionString);
//
//            SqlConnectionStringBuilder newCon = new SqlConnectionStringBuilder();
//            newCon.DataSource = builder.DataSource;
//            newCon.InitialCatalog = dbSettingName;
//            newCon.IntegratedSecurity = builder.IntegratedSecurity;
//            if (!newCon.IntegratedSecurity) {
//                newCon.UserID = builder.UserID;
//                newCon.Password = builder.Password;
//            }
//
//            DbSetting tempVar = new DbSetting();
//            tempVar.setConnectionString(newCon.toString());
//            tempVar.setProviderName(local.ProviderName);
//            return tempVar;
//        }
//
//        DbSetting tempVar2 = new DbSetting();
//        tempVar2.setConnectionString(String.format("data Source=%1$s.sdf", dbSettingName));
//        tempVar2.setProviderName(Provider_SqlCe);
//        return tempVar2;
//
//        //return new DbSetting
//        //{
//        //    ConnectionString = string.Format(@"data Source=.\SQLExpress;Initial Catalog={0};Integrated Security=True", dbSetting),
//        //    ProviderName = "System.data.SqlClient"
//        //};
//    }
//}