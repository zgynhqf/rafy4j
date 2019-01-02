package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;

import com.github.zgynhqf.rafy4j.Consts;
import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.model.Column;
import com.github.zgynhqf.rafy4j.dbmigration.model.Database;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbMetaReader;
import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySql数据库结构的读取器
 */
public final class MySqlMetaReader extends DbMetaReader {
    /**
     * 构造函数 初始化配置
     *
     * @param dbSetting 数据库配置信息
     */
    public MySqlMetaReader(DbSetting dbSetting) {
        super(dbSetting);
    }

    /**
     * 加载指定数据库的所有的数据表
     *
     * @param database 待加载表的数据库对象
     */
    @Override
    protected void loadAllTables(Database database) throws SQLException {
        String sql = "SELECT TABLE_NAME" + Consts.NEW_LINE +
                "FROM INFORMATION_SCHEMA.TABLES" + Consts.NEW_LINE +
                "WHERE TABLE_SCHEMA = '" + database.getName() + "'";

        this.getDb().queryDataReader(sql, reader -> {
            while (reader.next()) {
                String tableName = reader.getString("TABLE_NAME");
                Table table = new Table(tableName, database);
                database.getTables().add(table);
            }
            return null;
        });
    }

    /**
     * 加载指定数据库中的每个表的所有列
     *
     * @param database 需要加载列的数据库对象
     */
    @Override
    protected void loadAllColumns(Database database) throws SQLException {
        //用一句 Sql 将所有的表的所有字段都一次性查询出来。
        //不再使用 @"SHOW FULL COLUMNS FROM `" + table.Name + "`;"
        String sql = "SELECT TABLE_NAME, COLUMN_NAME, IS_NULLABLE, COLUMN_TYPE, EXTRA" + Consts.NEW_LINE
                + "FROM INFORMATION_SCHEMA.COLUMNS" + Consts.NEW_LINE
                + "WHERE TABLE_SCHEMA = '" + database.getName() + "'" + Consts.NEW_LINE;
        if (database.getTables().size() > 0) {
            String tableNames = StringUtils.join(database.getTables().stream().map(t -> "'" + t.getName() + "'").toArray(), ",");
            sql += " AND TABLE_NAME IN (" + tableNames + ")" + Consts.NEW_LINE;
        }
        sql += "ORDER BY TABLE_NAME";
//        String sql = "SELECT TABLE_NAME, COLUMN_NAME, IS_NULLABLE, COLUMN_TYPE, EXTRA" + Consts.NEW_LINE
//                + "FROM INFORMATION_SCHEMA.COLUMNS" + Consts.NEW_LINE
//                + "WHERE TABLE_NAME IN ("
//                + StringUtils.join(database.getTables().stream().map(t -> "'" + t.getName() + "'").toArray(), ",")
//                + ") AND TABLE_SCHEMA = '" + database.getName() + "'" + Consts.NEW_LINE
//                + "ORDER BY TABLE_NAME";
        this.getDb().queryDataReader(sql, columnsReader -> {
            Table currentTable = null; //当前正在处理的表。（放在循环外，有缓存的作用。）
            while (columnsReader.next()) {
                //找到该列所对应的表。
                String tableName = columnsReader.getString("TABLE_NAME");
                if (currentTable == null || !currentTable.getName().equalsIgnoreCase(tableName)) {
                    currentTable = database.findTable(tableName);
                }

                String columnName = columnsReader.getString("COLUMN_NAME");
                String sqlType = columnsReader.getString("COLUMN_TYPE");
                JDBCType dbType = MySqlDbTypeConverter.Instance.convertToDbType(sqlType);

                Column column = new Column(columnName, dbType, null, currentTable);

                column.setRequired((new String("NO")).equalsIgnoreCase(columnsReader.getString("IS_NULLABLE")));
                column.setAutoIncrement(columnsReader.getString("EXTRA").toLowerCase().contains("auto_increment"));

                currentTable.getColumns().add(column);
            }

            return null;
        });
    }

    /**
     * 子类实现此方法，实现从数据库中读取出指定数据库的所有约束。
     *
     * @param database The database.
     * @return 以列表的形式返回所有约束数据
     */
    @Override
    protected List<Constraint> readAllConstrains(Database database) throws SQLException {
        ArrayList<Constraint> allConstrains = new ArrayList<>();

        String sql = "SELECT O.CONSTRAINT_SCHEMA, O.CONSTRAINT_NAME, O.TABLE_SCHEMA, O.TABLE_NAME, O.COLUMN_NAME, O.REFERENCED_TABLE_SCHEMA, O.REFERENCED_TABLE_NAME, O.REFERENCED_COLUMN_NAME, O.UPDATE_RULE, O.DELETE_RULE, O.UNIQUE_CONSTRAINT_NAME, T.CONSTRAINT_TYPE" + Consts.NEW_LINE
                + "FROM (" + Consts.NEW_LINE
                + "    SELECT K.CONSTRAINT_SCHEMA, K.CONSTRAINT_NAME, K.TABLE_SCHEMA, K.TABLE_NAME, K.COLUMN_NAME, K.REFERENCED_TABLE_SCHEMA, K.REFERENCED_TABLE_NAME, K.REFERENCED_COLUMN_NAME, R.UPDATE_RULE, R.DELETE_RULE, R.UNIQUE_CONSTRAINT_NAME" + Consts.NEW_LINE
                + "    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE K " + Consts.NEW_LINE
                + "        LEFT JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON K.CONSTRAINT_NAME = R.CONSTRAINT_NAME" + Consts.NEW_LINE
                + ") AS O " + Consts.NEW_LINE
                + "        INNER JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS T ON O.TABLE_NAME = T.TABLE_NAME AND T.CONSTRAINT_NAME = O.CONSTRAINT_NAME" + Consts.NEW_LINE
                + "        WHERE O.CONSTRAINT_SCHEMA != 'mysql' AND O.CONSTRAINT_SCHEMA != 'sys' AND O.CONSTRAINT_SCHEMA = '" + database.getName() + "'";

        this.getDb().queryDataReader(sql, constraintReader -> {
            while (constraintReader.next()) {
                Constraint c = new Constraint();
                c.CONSTRAINT_NAME = constraintReader.getString("CONSTRAINT_NAME");
                c.CONSTRAINT_TYPE = constraintReader.getString("CONSTRAINT_TYPE");
                c.TABLE_NAME = constraintReader.getString("TABLE_NAME");
                c.COLUMN_NAME = constraintReader.getString("COLUMN_NAME");
                c.FK_TABLE_NAME = constraintReader.getString("TABLE_NAME");
                c.FK_COLUMN_NAME = constraintReader.getString("COLUMN_NAME");
                c.PK_TABLE_NAME = constraintReader.getString("REFERENCED_TABLE_NAME");
                c.PK_COLUMN_NAME = constraintReader.getString("REFERENCED_COLUMN_NAME");
                c.UNIQUE_CONSTRAINT_NAME = constraintReader.getString("UNIQUE_CONSTRAINT_NAME");
                c.DELETE_RULE = constraintReader.getString("DELETE_RULE");

                allConstrains.add(c);
            }
            return null;
        });

        return allConstrains;
    }

    @Override
    protected void loadAllIdentities(Database database) {
        //do nothing.
        //自增列，已经在 loadAllColumns 方法中直接加载了。
    }
}