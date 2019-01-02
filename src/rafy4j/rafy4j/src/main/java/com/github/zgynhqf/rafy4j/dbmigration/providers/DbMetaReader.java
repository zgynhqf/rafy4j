package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库的元数据读取器
 */
public abstract class DbMetaReader implements MetadataReader {
    private DbSetting dbSetting;

    private DbAccessor db;

    public DbMetaReader(DbSetting dbSetting) {
        Objects.requireNonNull(dbSetting);

        this.dbSetting = dbSetting;
        this.db = new SimpleDbAccessor(dbSetting);
    }

    public final DbAccessor getDb() {
        return this.db;
    }

    public final Database read() {
        Database database = new Database(this.dbSetting.getDatabase());

        try {
            this.loadAllTables(database);

            this.loadAllColumns(database);

            for (Table table : database.getTables()) {
                table.sortColumns();
            }

            this.LoadAllConstraints(database);

            this.loadAllIdentities(database);
        } catch (SQLException e) {
            database.setRemoved(true);
        }

        return database;
    }

    /**
     * 加载指定数据库的所有的数据表
     *
     * @param database 待加载表的数据库对象
     */
    protected abstract void loadAllTables(Database database) throws SQLException;

    /**
     * 加载指定数据库中的每个表的所有列
     *
     * @param database 需要加载列的数据库对象
     */
    protected abstract void loadAllColumns(Database database) throws SQLException;

    /**
     * 加载指定数据库的所有表中的自增列。
     *
     * @param database 指定的数据库对象
     */
    protected abstract void loadAllIdentities(Database database) throws SQLException;

    /**
     * 加载主键、外键等约束。
     *
     * @param database 需要加载约束的数据库对象
     */
    protected void LoadAllConstraints(Database database) throws SQLException {
        List<Constraint> allConstrains = this.readAllConstrains(database);

        for (Table table : database.getTables()) {
            for (Column column : table.getColumns()) {
                this.DealColumnConstraints(column, allConstrains);
            }
        }
    }

    /**
     * 处理主键和外键
     *
     * @param column
     * @param allConstraints 所有的约束
     */
    private void DealColumnConstraints(Column column, List<Constraint> allConstraints) {
        Database database = column.getTable().getDataBase();

        List<Constraint> constraints = allConstraints.stream()
                .filter(c -> column.getName().equals(c.COLUMN_NAME) &&
                        column.getTable().getName().equals(c.TABLE_NAME)
                ).collect(Collectors.toList());

        for (Constraint constraint : constraints) {
            if (constraint.CONSTRAINT_TYPE.equalsIgnoreCase("PRIMARY KEY")) {
                //主键
                column.setPrimaryKey(true);
            } else if (constraint.CONSTRAINT_TYPE.equalsIgnoreCase("FOREIGN KEY")) {
                //外键
                boolean deleteCascade = constraint.DELETE_RULE.equalsIgnoreCase("CASCADE");
                Table pkTable = database.findTable(constraint.PK_TABLE_NAME);
                if (pkTable == null) throw new IllegalStateException();
                Column pkColumn = pkTable.findColumn(constraint.PK_COLUMN_NAME);

                ForeignConstraint tempVar = new ForeignConstraint(pkColumn);
                tempVar.setNeedDeleteCascade(deleteCascade);
                tempVar.setConstraintName(constraint.CONSTRAINT_NAME);
                column.setForeignConstraint(tempVar);
            }
        }
    }

    /**
     * 子类实现此方法，实现从数据库中读取出指定数据库的所有约束。
     *
     * @param database The database.
     * @return 以列表的形式返回所有约束数据
     */
    protected abstract List<Constraint> readAllConstrains(Database database) throws SQLException;

    protected static class Constraint {
        public Constraint() {
        }

        public String CONSTRAINT_NAME;
        public String CONSTRAINT_TYPE;
        public String TABLE_NAME;
        public String COLUMN_NAME;
        public String FK_TABLE_NAME;
        public String FK_COLUMN_NAME;
        public String PK_TABLE_NAME;
        public String PK_COLUMN_NAME;
        public String UNIQUE_CONSTRAINT_NAME;
        public String DELETE_RULE;
    }
}