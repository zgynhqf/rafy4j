package com.github.zgynhqf.rafy4j.dbmigration;


import com.github.zgynhqf.rafy4j.dbmigration.model.Column;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.ChangeType;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.ColumnChanges;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.DatabaseChanges;
import com.github.zgynhqf.rafy4j.dbmigration.model.differ.TableChanges;
import com.github.zgynhqf.rafy4j.dbmigration.model.Table;
import com.github.zgynhqf.rafy4j.dbmigration.operations.*;
import com.github.zgynhqf.rafy4j.env.EntityConvention;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 职责：通过数据库变更记录来生成迁移操作列表
 */
public class AutomationMigration {
    private List<MigrationOperation> _operations = new ArrayList<>();

    DbMigrationContext Context;

    private List<Supplier> _relationActions = new ArrayList<>();

    public final List<MigrationOperation> getOperations() {
        return this._operations;
    }

    public final void GenerateOpertions(DatabaseChanges dbChanges) {
        this._operations.clear();

        switch (dbChanges.getChangeType()) {
            case ADDED:
                this.CreateDatabase(dbChanges);
                break;
            case REMOVED:
                this.DropDatabase(dbChanges);
                break;
            case MODIFIED:
                //为了保证外键的变化与表的变化不冲突，按照以下顺序生成操作：添加的表、修改的表（外键）、删除的表。
                List<TableChanges> addedTables = dbChanges.getTablesChanged().stream()
                        .filter(t -> t.getChangeType() == ChangeType.ADDED).collect(Collectors.toList());
                for (TableChanges item : addedTables) {
                    this.GenerateOpertions(item);
                }
                List<TableChanges> modifiedTables = dbChanges.getTablesChanged().stream()
                        .filter(t -> t.getChangeType() == ChangeType.MODIFIED).collect(Collectors.toList());
                for (TableChanges item : modifiedTables) {
                    this.GenerateOpertions(item);
                }
                List<TableChanges> removedTables = dbChanges.getTablesChanged().stream()
                        .filter(t -> t.getChangeType() == ChangeType.REMOVED).collect(Collectors.toList());
                for (TableChanges item : removedTables) {
                    this.GenerateOpertions(item);
                }
                break;
            default:
                break;
        }

        for (Supplier action : this._relationActions) {
            action.get();
        }

        this._relationActions.clear();
    }

    private void GenerateOpertions(TableChanges tableChanges) {
        switch (tableChanges.getChangeType()) {
            case ADDED:
                this.AddTable(tableChanges.getNewTable());
                break;
            case REMOVED:
                this.RemoveTable(tableChanges.getOldTable());
                break;
            case MODIFIED:
                for (ColumnChanges column : tableChanges.getColumnsChanged()) {
                    this.GenerateOpertions(column);
                }
                break;
            default:
                break;
        }
    }

    private void GenerateOpertions(ColumnChanges columnChanges) {
        switch (columnChanges.getChangeType()) {
            case ADDED:
                this.AddColumn(columnChanges.getNewColumn());
                break;
            case REMOVED:
                this.RemoveColumn(columnChanges.getOldColumn());
                break;
            case MODIFIED:
                this.ModifyColumn(columnChanges);
                break;
            default:
                break;
        }
    }

    private void CreateDatabase(DatabaseChanges dbChanges) {
        CreateDatabase tempVar = new CreateDatabase();
        tempVar.setDatabase(dbChanges.getNewDatabase().getName());
        this.AddOperation(tempVar);

        for (Table table : dbChanges.getNewDatabase().getTables()) {
            if (!dbChanges.getNewDatabase().IsIgnored(table.getName())) {
                this.AddTable(table);
            }
        }
    }

    private void DropDatabase(DatabaseChanges dbChanges) {
        //反向按表间的引用关系删除表。
        List<Table> tables = dbChanges.getOldDatabase().getTables();
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (!dbChanges.getNewDatabase().IsIgnored(tables.get(i).getName())) {
                this.RemoveTable(tables.get(i));
            }
        }
//
//        //当版本号嵌入到当前数据库中时，也不支持自动 DropDatabase。
//        if (!Context.getDbVersionProvider().IsEmbaded()) {
//            DropDatabase tempVar = new DropDatabase();
//            tempVar.setDatabase(dbChanges.getOldDatabase().getName());
//            this.AddOperation(tempVar);
//        }
    }

    private void AddTable(Table table) {
        CreateTable op = new CreateTable();
        op.setCopyFromTable(table);
        this.AddOperation(op);

        for (Column column : table.FindNormalColumns()) {
            this.AddColumn(column);
        }
    }

    private void RemoveTable(Table table) {
        if (Context.getRunDataLossOperation().hasValue(DataLossOperation.DropTable)) {
            Context.NotifyDataLoss("删除表");

            for (Column column : table.FindNormalColumns()) {
                this.RemoveColumn(column);
            }

            DropTable tempVar = new DropTable();
            tempVar.setCopyFromTable(table);
            this.AddOperation(tempVar);
        }
    }

    private void AddColumn(Column column) {
        CreateNormalColumn op = new CreateNormalColumn();
        op.copyFromColumn(column);
        op.setPrimaryKey(column.isPrimaryKey());
        op.setAutoIncrement(column.isAutoIncrement());
        this.AddOperation(op);

        //自增列必然是不可空的，在创建列时已经同时把不可空约束给创建好了，所以这里不需要重复添加了。
        if (column.isRequired() && !column.isAutoIncrement()) {
            AddNotNullConstraint constraint = new AddNotNullConstraint();
            constraint.copyFromColumn(column);
            this.AddOperation(constraint);
        }

        if (column.isForeignKey()) {
            this.AddRelationAction(() -> {
                AddFKConstraint fkConstraint = new AddFKConstraint();
                fkConstraint.setCopyFromConstraint(column.getForeignConstraint());
                this.AddOperation(fkConstraint);
                return null;
            });
        }
    }

    private void RemoveColumn(Column column) {
        if (Context.getRunDataLossOperation().hasValue(DataLossOperation.DropColumn)) {
            Context.NotifyDataLoss("删除列");

            if (column.isForeignKey()) {
                RemoveFKConstraint tempVar = new RemoveFKConstraint();
                tempVar.setCopyFromConstraint(column.getForeignConstraint());
                this.AddOperation(tempVar);
            }

            //加上列名称不等于id的判断原因如下：
            //rafy 现在的删除表的逻辑是先删除主键之外的列，如果列的属性为 not null 的，需要先讲 not null 修改成 null，
            //在 rafy 的实体中 id 都是自增长，not null 的，
            //如果表的主键不是 id ，那么在删除 id 列的时候，将 id 属性的 not null 修改为 null 就会有问题，因为 id 是自增长的，
            //所以当表的主键名称不是 id 的时候不能走 RemoveNotNullConstraint 这个方法。
            if (column.isRequired() && !column.getName().equalsIgnoreCase(EntityConvention.IdColumnName)) {
                RemoveNotNullConstraint tempVar2 = new RemoveNotNullConstraint();
                tempVar2.copyFromColumn(column);
                this.AddOperation(tempVar2);
            }

            DropNormalColumn tempVar3 = new DropNormalColumn();
            tempVar3.copyFromColumn(column);
            tempVar3.setPrimaryKey(column.isPrimaryKey());
            tempVar3.setAutoIncrement(column.isAutoIncrement());
            this.AddOperation(tempVar3);
        }
    }

    private void ModifyColumn(ColumnChanges columnChanges) {
        //数据类型
        if (columnChanges.getIsDbTypeChanged()) {
            AlterColumnType op = new AlterColumnType();
            op.copyFromColumn(columnChanges.getOldColumn());
            op.setNewType(columnChanges.getNewColumn().getDbType());
            op.setIsRequired(columnChanges.getOldColumn().isRequired());
            this.AddOperation(op);
        }

        //是否主键
        if (columnChanges.getIsPrimaryKeyChanged()) {
            Column column = columnChanges.getNewColumn();
            if (column.isPrimaryKey()) {
                AddPKConstraint constraint = new AddPKConstraint();
                constraint.copyFromColumn(column);
                this.AddOperation(constraint);
            } else {
                RemovePKConstraint constraint = new RemovePKConstraint();
                constraint.copyFromColumn(column);
                this.AddOperation(constraint);
            }
        }

        //可空性
        if (columnChanges.getIsRequiredChanged()) {
            this.ModifyColumnRequired(columnChanges);
        }

        //外键
        if (columnChanges.getForeignRelationChangeType() != ChangeType.UNCHANGED) {
            this.ModifyColumnForeignConstraint(columnChanges);
        }
    }

    private void ModifyColumnRequired(ColumnChanges columnChanges) {
        if (columnChanges.getNewColumn().isRequired()) {
            if (columnChanges.getOldColumn().isForeignKey()) {
                AddNotNullConstraintFK op = new AddNotNullConstraintFK();
                op.copyFromColumn(columnChanges.getNewColumn());
                this.AddOperation(op);
            } else {
                AddNotNullConstraint op = new AddNotNullConstraint();
                op.copyFromColumn(columnChanges.getNewColumn());
                this.AddOperation(op);
            }
        } else {
            if (columnChanges.getOldColumn().isForeignKey()) {
                RemoveNotNullConstraintFK op = new RemoveNotNullConstraintFK();
                op.copyFromColumn(columnChanges.getNewColumn());
                this.AddOperation(op);
            } else {
                RemoveNotNullConstraint op = new RemoveNotNullConstraint();
                op.copyFromColumn(columnChanges.getNewColumn());
                this.AddOperation(op);
            }
        }
    }

    private void ModifyColumnForeignConstraint(ColumnChanges columnChanges) {
        ChangeType value = columnChanges.getForeignRelationChangeType();
        switch (value) {
            case ADDED:
                AddFKConstraint op = new AddFKConstraint();
                op.setCopyFromConstraint(columnChanges.getNewColumn().getForeignConstraint());
                this.AddOperation(op);
                break;
            case REMOVED:
                RemoveFKConstraint constraint = new RemoveFKConstraint();
                constraint.setCopyFromConstraint(columnChanges.getOldColumn().getForeignConstraint());
                this.AddOperation(constraint);
                break;
            case MODIFIED:
                //throw new NotSupportedException("暂时不支持外键修改。");
                RemoveFKConstraint removeFKConstraint = new RemoveFKConstraint();
                removeFKConstraint.setCopyFromConstraint(columnChanges.getOldColumn().getForeignConstraint());
                this.AddOperation(removeFKConstraint);
                AddFKConstraint addFKConstraint = new AddFKConstraint();
                addFKConstraint.setCopyFromConstraint(columnChanges.getNewColumn().getForeignConstraint());
                this.AddOperation(addFKConstraint);
                break;
            default:
                break;
        }
    }

    //region 私有方法

    private void AddOperation(MigrationOperation operation) {
        this._operations.add(operation);
    }

    private void AddRelationAction(Supplier action) {
        this._relationActions.add(action);
    }

    //endregion
}