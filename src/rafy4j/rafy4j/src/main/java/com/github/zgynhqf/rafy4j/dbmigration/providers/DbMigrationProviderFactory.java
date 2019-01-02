package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.data.DbSetting;
import com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.MySqlDbTypeConverter;
import com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.MySqlIdentifierQuoter;
import com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.MySqlMigrationProvider;

/**
 * <see cref="DbMigrationProvider"/>、<see cref="DbIdentifierQuoter"/> 的工厂类型。
 */
public final class DbMigrationProviderFactory {
    public static DbMigrationProvider getProvider(DbSetting dbSetting) {
        DbMigrationProvider provider = null;

        String driverName = dbSetting.getDriverName().toLowerCase();
        if (driverName.contains("mysql")) {
            provider = new MySqlMigrationProvider();
        } else {
            //default
            provider = new MySqlMigrationProvider();
        }
//        switch (dbSetting.getDriverName()) {
//            case DbConnectionSchema.Provider_SqlClient:
//                provider = new SqlServerMigrationProvider();
//                break;
//            case DbConnectionSchema.Provider_SqlCe:
//                provider = new SqlServerCeMigrationProvider();
//                break;
//            case DbConnectionSchema.Provider_MySql:
//                provider = new MySqlMigrationProvider();
//                break;
//            default:
//                if (DbConnectionSchema.IsOracleProvider(dbSetting)) {
//                    provider = new OracleMigrationProvider();
//                    break;
//                }
//                throw new NotSupportedException("This type of database is not supportted now:" + dbSetting.ProviderName);
//        }

        provider.setDbSetting(dbSetting);

        return provider;
    }

    public static BaseDbIdentifierQuoter getIdentifierProvider(String driverName) {
        return MySqlIdentifierQuoter.Instance;

        //        driverName = driverName.toLowerCase();
//        if (driverName.contains("mysql")) {
//            provider = new MySqlMigrationProvider();
//        } else {
//            //default
//            provider = new MySqlMigrationProvider();
//        }
//
//        if (providerName.equals(DbConnectionSchema.Provider_SqlClient) || providerName.equals(DbConnectionSchema.Provider_SqlCe)) {
//            return SqlServerIdentifierQuoter.Instance;
//        }
//        else if (providerName.equals(DbConnectionSchema.Provider_MySql)) {
//            return MySqlIdentifierQuoter.Instance;
//        } else {
//            if (DbConnectionSchema.IsOracleProvider(providerName)) {
//                return OracleIdentifierQuoter.Instance;
//            }
//            throw new NotSupportedException("This type of database is not supportted now:" + providerName);
//        }
    }

    public static DbTypeConverter getDbTypeConverter(String driverName) {
        return MySqlDbTypeConverter.Instance;
        //        if (providerName.equals(DbConnectionSchema.Provider_SqlClient)) {
//            return SqlServerDbTypeConverter.Instance;
//        }
//        else if (providerName.equals(DbConnectionSchema.Provider_SqlCe)) {
//            return SqlServerCeDbTypeConverter.Instance;
//        }
//        else if (providerName.equals(DbConnectionSchema.Provider_MySql)) {
//            return MySqlDbTypeConverter.Instance;
//        } else {
//            if (DbConnectionSchema.IsOracleProvider(providerName)) {
//                return OracleDbTypeConverter.Instance;
//            }
//            throw new NotSupportedException("This type of database is not supportted now:" + providerName);
//        }
    }
}