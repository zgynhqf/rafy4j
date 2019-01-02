package com.github.zgynhqf.rafy4j.dbmigration;

import com.github.zgynhqf.rafy4j.Consts;
import com.github.zgynhqf.rafy4j.dbmigration.run.SqlMigrationRun;

import java.sql.SQLException;

class DbMigrationExceptionMessageFormatter {
    static String formatMessage(SQLException ex, SqlMigrationRun sqlRun) {
//        String errorMsg = "";
//
//        if (ex instanceof SQLException) {
//            var sqlEx = (SqlException) ((ex instanceof SqlException) ? ex : null);
//            var no = sqlEx.Number;
//            switch (no) {
//                case 515:
//                    errorMsg = "把一个可空字段变更为非空字段时，由于存在不可空约束，所以数据库该表中不能有该字段为空的数据行。\r\n";
//                    break;
//                case 547:
//                    errorMsg = "把一个字段变更为非空外键字段时，由于外键约束，所以数据库该表中不能有该字段数据不满足引用约束的数据行。\r\n";
//                    break;
//                default:
//                    break;
//            }
//        }

        String errorMsg = ex.getMessage();

        String error = "执行数据库迁移时出错：" + errorMsg;

        if (sqlRun != null) {
            error += Consts.NEW_LINE + "对应的 SQL：" + sqlRun.getSql();
        }

        return error;
    }
}