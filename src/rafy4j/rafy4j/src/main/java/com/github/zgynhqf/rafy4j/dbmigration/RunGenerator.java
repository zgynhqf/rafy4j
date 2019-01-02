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

    public final List<MigrationRun> generate(Iterable<MigrationOperation> operations) {
        this.runList.clear();

        for (MigrationOperation op : operations) {
            this.distribute(op);
        }

        return this.runList;
    }

    /**
     * 把抽象的操作分发到具体的生成方法上。
     *
     * @param op
     */
    private void distribute(MigrationOperation op) {
        //手工分发的原因：类并不多、可以处理类之间的继承层次关系。

        if (op instanceof CreateNormalColumn) {
            this.generate((CreateNormalColumn) op);
        } else if (op instanceof DropNormalColumn) {
            this.generate((DropNormalColumn) op);
        } else if (op instanceof AddPKConstraint) {
            this.generate((AddPKConstraint) op);
        } else if (op instanceof RemovePKConstraint) {
            this.generate((RemovePKConstraint) op);
        } else if (op instanceof AddNotNullConstraint) {
            this.generate((AddNotNullConstraint) op);
        } else if (op instanceof RemoveNotNullConstraint) {
            this.generate((RemoveNotNullConstraint) op);
        } else if (op instanceof AddNotNullConstraintFK) {
            this.generate((AddNotNullConstraintFK) op);
        } else if (op instanceof RemoveNotNullConstraintFK) {
            this.generate((RemoveNotNullConstraintFK) op);
        } else if (op instanceof AlterColumnType) {
            this.generate((AlterColumnType) op);
        } else if (op instanceof AddFKConstraint) {
            this.generate((AddFKConstraint) op);
        } else if (op instanceof RemoveFKConstraint) {
            this.generate((RemoveFKConstraint) op);
        } else if (op instanceof CreateTable) {
            this.generate((CreateTable) op);
        } else if (op instanceof DropTable) {
            this.generate((DropTable) op);
        } else if (op instanceof CreateDatabase) {
            this.generate((CreateDatabase) op);
        } else if (op instanceof DropDatabase) {
            this.generate((DropDatabase) op);
        } else if (op instanceof RunSql) {
            this.generate((RunSql) op);
        } else if (op instanceof RunAction) {
            this.generate((RunAction) op);
        } else if (op instanceof UpdateComment) {
            this.generate((UpdateComment) op);
        } else {
            this.generate(op);
        }
    }

    // 需要子类实现的抽象方法

    protected abstract void generate(CreateNormalColumn op);

    protected abstract void generate(DropNormalColumn op);

    protected abstract void generate(AddPKConstraint op);

    protected abstract void generate(RemovePKConstraint op);

    protected abstract void generate(AddNotNullConstraint op);

    protected abstract void generate(RemoveNotNullConstraint op);

    protected abstract void generate(AddNotNullConstraintFK op);

    protected abstract void generate(RemoveNotNullConstraintFK op);

    protected abstract void generate(AlterColumnType op);

    protected abstract void generate(AddFKConstraint op);

    protected abstract void generate(RemoveFKConstraint op);

    protected abstract void generate(CreateTable op);

    protected abstract void generate(DropTable op);

    protected abstract void generate(CreateDatabase op);

    protected abstract void generate(DropDatabase op);

    protected abstract void generate(UpdateComment op);

    protected void generate(MigrationOperation op) {
        throw new NotImplementedException("generate(MigrationOperation op)");
    }

    //直接实现的两个子类：RunSql、RunAction

    protected void generate(RunSql op) {
        SqlMigrationRun run = new SqlMigrationRun();
        run.setSql(op.getSql());
        this.addRun(run);
    }

    protected void generate(RunAction op) {
        ActionMigrationRun run = new ActionMigrationRun();
        run.setAction(op.getAction());
        this.addRun(run);
    }

    /**
     * 获取一个缩进的 TextWriter 用于写 SQL。
     *
     * @return
     */
    protected final IndentedTextWriter writer() {
        return new IndentedTextWriter(new StringWriter());
    }

    /**
     * 添加一个 SQL 语句的执行项
     *
     * @param sql
     */
    protected final void addRun(IndentedTextWriter sql) {
        SqlMigrationRun op = new SqlMigrationRun();
        op.setSql(sql.toString());
        this.addRun(op);
    }

    /**
     * 添加一个执行项
     *
     * @param run
     */
    protected final void addRun(MigrationRun run) {
        this.runList.add(run);
    }
}