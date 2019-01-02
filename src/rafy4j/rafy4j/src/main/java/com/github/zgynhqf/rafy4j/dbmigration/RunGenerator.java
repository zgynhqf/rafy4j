package com.github.zgynhqf.rafy4j.dbmigration;

import com.github.zgynhqf.rafy4j.dbmigration.operations.*;
import com.github.zgynhqf.rafy4j.dbmigration.run.ActionMigrationRun;
import com.github.zgynhqf.rafy4j.dbmigration.run.SqlMigrationRun;
import org.apache.commons.lang3.NotImplementedException;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库执行项 MigrationRun 的生成器
 * <p>
 * 子类继承此类以实现不同类型数据库对指定数据库操作的执行项生成。
 */
public abstract class RunGenerator {
    private List<MigrationRun> runList = new ArrayList<>();

    public final List<MigrationRun> Generate(Iterable<MigrationOperation> operations) {
        this.runList.clear();

        for (MigrationOperation op : operations) {
            this.Distribute(op);
        }

        return this.runList;
    }

    /**
     * 把抽象的操作分发到具体的生成方法上。
     *
     * @param op
     */
    private void Distribute(MigrationOperation op) {
        //手工分发的原因：类并不多、可以处理类之间的继承层次关系。

        if (op instanceof CreateNormalColumn) {
            this.Generate((CreateNormalColumn) op);
        } else if (op instanceof DropNormalColumn) {
            this.Generate((DropNormalColumn) op);
        } else if (op instanceof AddPKConstraint) {
            this.Generate((AddPKConstraint) op);
        } else if (op instanceof RemovePKConstraint) {
            this.Generate((RemovePKConstraint) op);
        } else if (op instanceof AddNotNullConstraint) {
            this.Generate((AddNotNullConstraint) op);
        } else if (op instanceof RemoveNotNullConstraint) {
            this.Generate((RemoveNotNullConstraint) op);
        } else if (op instanceof AddNotNullConstraintFK) {
            this.Generate((AddNotNullConstraintFK) op);
        } else if (op instanceof RemoveNotNullConstraintFK) {
            this.Generate((RemoveNotNullConstraintFK) op);
        } else if (op instanceof AlterColumnType) {
            this.Generate((AlterColumnType) op);
        } else if (op instanceof AddFKConstraint) {
            this.Generate((AddFKConstraint) op);
        } else if (op instanceof RemoveFKConstraint) {
            this.Generate((RemoveFKConstraint) op);
        } else if (op instanceof CreateTable) {
            this.Generate((CreateTable) op);
        } else if (op instanceof DropTable) {
            this.Generate((DropTable) op);
        } else if (op instanceof CreateDatabase) {
            this.Generate((CreateDatabase) op);
        } else if (op instanceof DropDatabase) {
            this.Generate((DropDatabase) op);
        } else if (op instanceof RunSql) {
            this.Generate((RunSql) op);
        } else if (op instanceof RunAction) {
            this.Generate((RunAction) op);
        } else if (op instanceof UpdateComment) {
            this.Generate((UpdateComment) op);
        } else {
            this.Generate(op);
        }
    }

    // 需要子类实现的抽象方法

    protected abstract void Generate(CreateNormalColumn op);

    protected abstract void Generate(DropNormalColumn op);

    protected abstract void Generate(AddPKConstraint op);

    protected abstract void Generate(RemovePKConstraint op);

    protected abstract void Generate(AddNotNullConstraint op);

    protected abstract void Generate(RemoveNotNullConstraint op);

    protected abstract void Generate(AddNotNullConstraintFK op);

    protected abstract void Generate(RemoveNotNullConstraintFK op);

    protected abstract void Generate(AlterColumnType op);

    protected abstract void Generate(AddFKConstraint op);

    protected abstract void Generate(RemoveFKConstraint op);

    protected abstract void Generate(CreateTable op);

    protected abstract void Generate(DropTable op);

    protected abstract void Generate(CreateDatabase op);

    protected abstract void Generate(DropDatabase op);

    protected abstract void Generate(UpdateComment op);

    protected void Generate(MigrationOperation op) {
        throw new NotImplementedException("Generate(MigrationOperation op)");
    }

    //直接实现的两个子类：RunSql、RunAction

    protected void Generate(RunSql op) {
        SqlMigrationRun run = new SqlMigrationRun();
        run.setSql(op.getSql());
        this.AddRun(run);
    }

    protected void Generate(RunAction op) {
        ActionMigrationRun run = new ActionMigrationRun();
        run.setAction(op.getAction());
        this.AddRun(run);
    }

    /**
     * 获取一个缩进的 TextWriter 用于写 SQL。
     *
     * @return
     */
    protected final IndentedTextWriter Writer() {
        return new IndentedTextWriter(new StringWriter());
    }

    /**
     * 添加一个 SQL 语句的执行项
     *
     * @param sql
     */
    protected final void AddRun(IndentedTextWriter sql) {
        SqlMigrationRun op = new SqlMigrationRun();
        op.setSql(sql.toString());
        this.AddRun(op);
    }

    /**
     * 添加一个执行项
     *
     * @param run
     */
    protected final void AddRun(MigrationRun run) {
        this.runList.add(run);
    }
}