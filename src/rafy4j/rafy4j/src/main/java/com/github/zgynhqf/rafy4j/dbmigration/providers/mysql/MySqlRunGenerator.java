package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;

import com.github.zgynhqf.rafy4j.dbmigration.IndentedTextWriter;
import com.github.zgynhqf.rafy4j.dbmigration.operations.*;
import com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.run.MySqlCreateDbMigrationRun;
import com.github.zgynhqf.rafy4j.dbmigration.providers.mysql.run.MySqlDropDbMigrationRun;
import com.github.zgynhqf.rafy4j.dbmigration.providers.SqlRunGenerator;
import com.github.zgynhqf.rafy4j.dbmigration.run.GenerationExceptionRun;
import com.github.zgynhqf.rafy4j.dbmigration.run.SqlMigrationRun;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

/**
 * MySql的执行项生成器
 */
public final class MySqlRunGenerator extends SqlRunGenerator {
    public MySqlRunGenerator() {
        this.setIdentifierQuoter(MySqlIdentifierQuoter.Instance);
        this.setDbTypeConverter(MySqlDbTypeConverter.Instance);
    }

    /**
     * 增加不允许为空的约束
     *
     * @param op 列操作对象的实体对象
     */
    @Override
    protected void addNotNullConstraint(ColumnOperation op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.write(" MODIFY ");

            sql.plusIndent();
            this.generateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), true, op.isForeignKey(), op.getDefaultValue());

            this.addRun(sql);
        }
    }

    /**
     * 生成创建数据库的语句
     *
     * @param op 创建数据库的实例对象
     */
    @Override
    protected void generate(CreateDatabase op) {
        MySqlCreateDbMigrationRun run = new MySqlCreateDbMigrationRun();
        run.setDatabase(op.getDatabase());
        this.addRun(run);
    }

    /**
     * 生成删除数据库的语句
     *
     * @param op 删除数据库的实例对象
     */
    @Override
    protected void generate(DropDatabase op) {
        MySqlDropDbMigrationRun run = new MySqlDropDbMigrationRun();
        run.setDatabase(op.getDatabase());
        this.addRun(run);
    }

    /**
     * 生成更新备注信息
     *
     * @param op 更新备注的实体对象
     */
    @Override
    protected void generate(UpdateComment op) {
        if (StringUtils.isBlank(op.getColumnName())) {
            SqlMigrationRun run = new SqlMigrationRun();
            run.setSql(String.format("ALTER TABLE `%1$s` COMMENT '%2$s'", this.prepare(op.getTableName()), op.getComment()));
            this.addRun(run);
        } else {
            //mysql 不支持外键修改备注，所以过滤掉外键修改备注
            if (op.getColumnName().compareToIgnoreCase("id") != 0 && op.getTableName().compareTo("BlogUser") != 0) {
                SqlMigrationRun run = new SqlMigrationRun();
                run.setSql(String.format("ALTER TABLE `%1$s` MODIFY COLUMN `%2$s` %3$s COMMENT '%4$s'",
                        this.prepare(op.getTableName()),
                        this.prepare(op.getColumnName()),
                        this.getDbTypeConverter().convertToDatabaseTypeName(op.getColumnDbType()), op.getComment()
                ));
                this.addRun(run);
            }
        }
    }

    /**
     * 生成增加外键约束的语句
     *
     * @param op 增加外键约束的对象
     */
    @Override
    protected void generate(AddFKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    ADD CONSTRAINT ");
            sql.write(this.quote(op.getConstraintName()));
            sql.writeLine();
            sql.write("    FOREIGN KEY ");
            sql.write(this.quote(op.getDependentTable()));
            sql.write(" (");
            sql.write(this.quote(op.getDependentTableColumn()));
            sql.write(") REFERENCES ");
            sql.write(this.quote(op.getPrincipleTable()));
            sql.write("(");
            sql.write(this.quote(op.getPrincipleTableColumn()));
            sql.write(")");

            if (op.getNeedDeleteCascade()) {
                sql.write(" ON DELETE CASCADE");
            }

            this.addRun(sql);
        }
    }

    /**
     * 生成删除外键约束的语句
     *
     * @param op 删除外键约束对象
     */
    @Override
    protected void generate(RemoveFKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    DROP FOREIGN KEY ");
            sql.write(this.quote(op.getConstraintName()));

            this.addRun(sql);
        }
    }

    /**
     * 生成增加主键约束的语句
     *
     * @param op 增加逐渐约束的对象
     */
    @Override
    protected void generate(AddPKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.write(" ADD CONSTRAINT PK_");
            sql.write(op.getTableName().toUpperCase());
            sql.write(" PRIMARY KEY ");
            sql.write(this.quote(op.getTableName()));
            sql.write("(");
            sql.write(this.quote(op.getColumnName()));
            sql.write(")");

            this.addRun(sql);
        }

        //using (var sql = this.writer())
        //{
        //    sql.write("ALTER TABLE ");
        //    sql.write(this.quote(op.TableName));
        //    sql.write(" DROP PRIMARY KEY,ADD PRIMARY KEY(");
        //    sql.write(this.quote(op.ColumnName));
        //    sql.write(")");
        //    this.addRun(sql);
        //}
    }

    /**
     * 生成删除主键约束的语句
     *
     * @param op 删除主键约束的对象
     */
    @Override
    protected void generate(RemovePKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.write(" DROP PRIMARY KEY");

            this.addRun(sql);
        }
    }

    /**
     * 生成删除数据表的语句
     *
     * @param op 删除表对象
     */
    @Override
    protected void generate(DropTable op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("DROP TABLE ");
            sql.write(this.quote(op.getTableName()));

            this.addRun(sql);
        }
    }

    /**
     * 生成创建表的语句
     *
     * @param op 创建表的对象实例
     */
    @Override
    protected void generate(CreateTable op) {
        if (StringUtils.isBlank(op.getPKName())) {
            GenerationExceptionRun exceptionRun = new GenerationExceptionRun();
            exceptionRun.setMessage("暂时不支持生成没有主键的表：" + op.getTableName());
            this.addRun(exceptionRun);
            return;
        }

        try (IndentedTextWriter sql = this.writer()) {
            sql.write("CREATE TABLE IF NOT EXISTS ");
            sql.writeLine(this.quote(op.getTableName()));
            sql.writeLine("(");
            sql.plusIndent();
            this.generateColumnDeclaration(sql, op.getPKName(), op.getPKDbType(), op.getPKLength(), true, true, null);
            if (op.isPKAutoIncrement()) {
                sql.write(" auto_increment");
            }
            sql.write(" primary key");
            sql.writeLine();
            sql.minusIndent();
            sql.write(")");

            this.addRun(sql);
        }
    }

    /**
     * 生成修改列的类型的语句
     *
     * @param op 修改列类型的实例对象
     */
    @Override
    protected void generate(AlterColumnType op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("MODIFY ");

            this.generateColumnDeclaration(sql, op.getColumnName(), op.getNewType(), op.getLength(), op.isRequired(), op.isForeignKey(), op.getDefaultValue());

            this.addRun(sql);
        }
    }

    /**
     * 生成创建普通的数据列的语句
     *
     * @param op 创建普通列的实例对象
     */
    @Override
    protected void generate(CreateNormalColumn op) {
        if (op.isAutoIncrement()) throw new NotImplementedException("mysql 数据库不支持创建自增列!");

        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("ADD ");
            this.generateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), false, op.isForeignKey(), op.getDefaultValue());

            this.addRun(sql);
        }
    }

    /**
     * 生成删除非空的列约束的语句
     *
     * @param op 列操作的实例对象
     */
    @Override
    protected void removeNotNullConstraint(ColumnOperation op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("MODIFY ");
            this.generateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), false, op.isForeignKey(), op.getDefaultValue());

            this.addRun(sql);
        }
    }
}