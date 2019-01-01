package com.github.zgynhqf.rafy4j.dbmigration.model.differ;

import com.github.zgynhqf.rafy4j.dbmigration.model.*;
import com.github.zgynhqf.rafy4j.dbmigration.providers.DbTypeConverter;
import com.github.zgynhqf.rafy4j.dbmigration.providers.IDbIdentifierQuoter;

import java.util.List;

public class ModelDiffer {
    public IDbIdentifierQuoter IDbIdentifierProvider;

    private DbTypeConverter dbTypeConverter;

    public ModelDiffer(DbTypeConverter dbTypeConverter) {
        this.dbTypeConverter = dbTypeConverter;
    }

    /**
     * 计算出两个数据库元数据的所有表差别
     *
     * @param oldDatabase 旧数据库
     * @param newDatabase 新数据库
     * @return
     */
    public final DatabaseChanges Distinguish(Database oldDatabase, DestinationDatabase newDatabase) {
        if (!oldDatabase.getRemoved()) {
            oldDatabase.OrderByRelations();
        }
        if (!newDatabase.getRemoved()) {
            newDatabase.OrderByRelations();
        }

        java.util.ArrayList<TableChanges> result = new java.util.ArrayList<TableChanges>();

        if (!oldDatabase.getRemoved() && !newDatabase.getRemoved()) {
            //先找出所有被删除的表
            List<Table> tables = oldDatabase.getTables();
            for (int i = tables.size() - 1; i >= 0; i--) {
                Table oldTable = tables.get(i);
                if (FindTable(newDatabase, oldTable.getName()) == null && !newDatabase.IsIgnored(oldTable.getName())) {
                    result.add(new TableChanges(oldTable, null, ChangeType.Removed));
                }
            }

            for (Table newTable : newDatabase.getTables()) {
                if (!newDatabase.IsIgnored(newTable.getName())) {
                    Table oldTable = FindTable(oldDatabase, newTable.getName());
                    //如果没有找到旧表，说明这个表是新加的。
                    if (oldTable == null) {
                        result.add(new TableChanges(null, newTable, ChangeType.Added));
                    } else {
                        //即不是新表，也不是旧表，计算两个表的区别
                        TableChanges record = Distinguish(oldTable, newTable);

                        //如果有区别，则记录下来
                        if (record != null) {
                            result.add(record);
                        }
                    }
                }
            }
        }

        return new DatabaseChanges(oldDatabase, newDatabase, result);
    }

    /**
     * 计算出新旧表之间的数据列差别
     *
     * @param oldTable 旧表
     * @param newTable 新表
     * @return 返回表之间区别，如果没有区别，则返回null
     */
    private TableChanges Distinguish(Table oldTable, Table newTable) {
        //if (newTable == null) throw new ArgumentNullException("newTable");
        //if (oldTable == null) throw new ArgumentNullException("oldTable");
        //if (newTable.Name != oldTable.Name) throw new InvalidOperationException("newTable.Name != oldTable.Name must be false.");

        TableChanges record = new TableChanges(oldTable, newTable, ChangeType.Modified);

        //先找到已经删除的列
        for (Column oldColumn : oldTable.getColumns()) {
            if (FindColumn(newTable, oldColumn.getName()) == null) {
                record.getColumnsChanged().add(new ColumnChanges(oldColumn, null, ChangeType.Removed));
            }
        }

        //记录新增的和更改过的列
        for (Column column : newTable.getColumns()) {
            Column oldColumn = FindColumn(oldTable, column.getName());

            if (oldColumn == null) {
                ColumnChanges columnChanged = new ColumnChanges(null, column, ChangeType.Added);
                record.getColumnsChanged().add(columnChanged);
            } else {
                ColumnChanges columnChanged = Distinguish(oldColumn, column);
                //新增的 或者 修改的 列
                if (columnChanged != null) {
                    record.getColumnsChanged().add(columnChanged);
                }
            }
        }

        //如果被修改了，则返回record；否则返回null
        if (record.getColumnsChanged().size() > 0) {
            return record;
        }

        return null;
    }

    private ColumnChanges Distinguish(Column oldColumn, Column newColumn) {
        //if (newColumn == null) throw new ArgumentNullException("newColumn");
        //if (newColumn.Name.EqualsIgnoreCase(oldColumn.Name)) throw new InvalidOperationException("newColumn.Name.EqualsIgnoreCase(oldColumn.Name) must be false.");
        //if (newColumn.Table.Name.EqualsIgnoreCase(oldColumn.Table.Name)) throw new InvalidOperationException("newColumn.Table.Name.EqualsIgnoreCase(oldColumn.Table.Name) must be false.");

        ColumnChanges columnChanged = null;
        if (!equals(newColumn, oldColumn)) {
            columnChanged = new ColumnChanges(oldColumn, newColumn, ChangeType.Modified);

            if (newColumn.isRequired() != oldColumn.isRequired()) {
                columnChanged.setIsRequiredChanged(true);
            }

            if (newColumn.isPrimaryKey() != oldColumn.isPrimaryKey()) {
                columnChanged.setIsPrimaryKeyChanged(true);
            }

            if (!dbTypeConverter.IsCompatible(newColumn.getDbType(), oldColumn.getDbType())) {
                columnChanged.setIsDbTypeChanged(true);
            }

            //ForeignRelationChangeType
            columnChanged.setForeignRelationChangeType(ChangeType.UnChanged);
            if (!newColumn.isForeignKey() && oldColumn.isForeignKey()) {
                columnChanged.setForeignRelationChangeType(ChangeType.Removed);
            } else if (newColumn.isForeignKey() && !oldColumn.isForeignKey()) {
                columnChanged.setForeignRelationChangeType(ChangeType.Added);
            } else if (newColumn.isForeignKey() && oldColumn.isForeignKey()) {
                if (!equals(newColumn.getForeignConstraint(), oldColumn.getForeignConstraint())) {
                    columnChanged.setForeignRelationChangeType(ChangeType.Modified);
                }
            }
        }
        return columnChanged;
    }

    private boolean equals(Column a, Column b) {
        if (a.getTable().getName().equalsIgnoreCase(b.getTable().getName()) &&
                a.getName().equalsIgnoreCase(b.getName()) &&
                dbTypeConverter.IsCompatible(a.getDbType(), b.getDbType()) &&
                a.isRequired() == b.isRequired() &&
                a.isForeignKey() == b.isForeignKey() &&
                a.isPrimaryKey() == b.isPrimaryKey()) {
            //判断外键是否相等
            //暂时不考虑NeedDeleteCascade是否相同的问题
            if (a.isForeignKey() &&
                    !a.getForeignConstraint().getPKColumn().getTable().getName().equalsIgnoreCase(b.getForeignConstraint().getPKColumn().getTable().getName())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean equals(ForeignConstraint a, ForeignConstraint b) {
        return a.getNeedDeleteCascade() == b.getNeedDeleteCascade() &&
                a.getPKTable().getName().equalsIgnoreCase(b.getPKTable().getName());
    }

    /**
     * 不使用 <see cref="Database.FindTable(string)"/> 的原因是因为要考虑标识符的截断。
     *
     * @param database
     * @param name
     * @return
     */
    private Table FindTable(Database database, String name) {
        List<Table> tables = database.getTables();

        for (int i = 0, c = tables.size(); i < c; i++) {
            Table table = tables.get(i);

            String tableName = this.Prepare(table.getName());
            name = this.Prepare(name);

            if (tableName.equalsIgnoreCase(name)) {
                return table;
            }
        }

        return null;
    }

    /**
     * 不使用 <see cref="Table.FindColumn(string)"/> 的原因是因为要考虑标识符的截断。
     *
     * @param table
     * @param name
     * @return
     */
    private Column FindColumn(Table table, String name) {
        List<Column> columns = table.getColumns();

        for (int i = 0, c = columns.size(); i < c; i++) {
            Column column = columns.get(i);

            String columnName = this.Prepare(column.getName());
            name = this.Prepare(name);

            if (columnName.equalsIgnoreCase(name)) {
                return column;
            }
        }

        return null;
    }

    /**
     * 对比时需要考虑截断。
     * 由于在数据库生成时，有可能额外地对标识符进行一些处理，所以这里需要对这些情况做兼容性处理。
     *
     * @param identifier
     * @return
     */
    private String Prepare(String identifier) {
        return this.IDbIdentifierProvider != null ? this.IDbIdentifierProvider.Prepare(identifier) : identifier;
    }
}