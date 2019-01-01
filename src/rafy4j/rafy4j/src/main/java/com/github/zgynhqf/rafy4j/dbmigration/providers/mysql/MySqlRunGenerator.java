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
        this.setDbTypeCoverter(MySqlDbTypeConverter.Instance);
    }

    /**
     * 增加不允许为空的约束
     *
     * @param op 列操作对象的实体对象
     */
    @Override
    protected void AddNotNullConstraint(ColumnOperation op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.write(" MODIFY ");

            sql.plusIndent();
            this.GenerateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), true, op.getIsForeignKey(), op.getDefaultValue());

            this.AddRun(sql);
        }
    }

    /**
     * 生成创建数据库的语句
     *
     * @param op 创建数据库的实例对象
     */
    @Override
    protected void Generate(CreateDatabase op) {
        MySqlCreateDbMigrationRun run = new MySqlCreateDbMigrationRun();
        run.setDatabase(op.getDatabase());
        this.AddRun(run);
    }

    /**
     * 生成删除数据库的语句
     *
     * @param op 删除数据库的实例对象
     */
    @Override
    protected void Generate(DropDatabase op) {
        MySqlDropDbMigrationRun run = new MySqlDropDbMigrationRun();
        run.setDatabase(op.getDatabase());
        this.AddRun(run);
    }

    /**
     * 生成更新备注信息
     *
     * @param op 更新备注的实体对象
     */
    @Override
    protected void Generate(UpdateComment op) {
        if (StringUtils.isBlank(op.getColumnName())) {
            SqlMigrationRun run = new SqlMigrationRun();
            run.setSql(String.format("ALTER TABLE `%1$s` COMMENT '%2$s'", this.Prepare(op.getTableName()), op.getComment()));
            this.AddRun(run);
        } else {
            //mysql 不支持外键修改备注，所以过滤掉外键修改备注
            if (op.getColumnName().compareToIgnoreCase("id") != 0 && op.getTableName().compareTo("BlogUser") != 0) {
                SqlMigrationRun run = new SqlMigrationRun();
                run.setSql(String.format("ALTER TABLE `%1$s` MODIFY COLUMN `%2$s` %3$s COMMENT '%4$s'",
                        this.Prepare(op.getTableName()),
                        this.Prepare(op.getColumnName()),
                        this.getDbTypeCoverter().ConvertToDatabaseTypeName(op.getColumnDbType()), op.getComment()
                ));
                this.AddRun(run);
            }
        }
    }

    /**
     * 生成增加外键约束的语句
     *
     * @param op 增加外键约束的对象
     */
    @Override
    protected void Generate(AddFKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getDependentTable()));
            sql.write("" + "\r\n" + "    ADD CONSTRAINT ");
            sql.write(this.Quote(op.getConstraintName()));
            sql.write("" + "\r\n" + "    FOREIGN KEY ");
            sql.write(this.Quote(op.getDependentTable()));
            sql.write(" (");
            sql.write(this.Quote(op.getDependentTableColumn()));
            sql.write(") REFERENCES ");
            sql.write(this.Quote(op.getPrincipleTable()));
            sql.write("(");
            sql.write(this.Quote(op.getPrincipleTableColumn()));
            sql.write(")");

            if (op.getNeedDeleteCascade()) {
                sql.write(" ON DELETE CASCADE");
            }

            this.AddRun(sql);
        }
    }

    /**
     * 生成删除外键约束的语句
     *
     * @param op 删除外键约束对象
     */
    @Override
    protected void Generate(RemoveFKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getDependentTable()));
            sql.write("" + "\r\n" + "    DROP FOREIGN KEY ");
            sql.write(this.Quote(op.getConstraintName()));

            this.AddRun(sql);
        }
    }

    /**
     * 生成增加主键约束的语句
     *
     * @param op 增加逐渐约束的对象
     */
    @Override
    protected void Generate(AddPKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.write(" ADD CONSTRAINT PK_");
            sql.write(op.getTableName().toUpperCase());
            sql.write(" PRIMARY KEY ");
            sql.write(this.Quote(op.getTableName()));
            sql.write("(");
            sql.write(this.Quote(op.getColumnName()));
            sql.write(")");

            this.AddRun(sql);
        }

        //using (var sql = this.Writer())
        //{
        //    sql.write("ALTER TABLE ");
        //    sql.write(this.Quote(op.TableName));
        //    sql.write(" DROP PRIMARY KEY,ADD PRIMARY KEY(");
        //    sql.write(this.Quote(op.ColumnName));
        //    sql.write(")");
        //    this.AddRun(sql);
        //}
    }

    /**
     * 生成删除主键约束的语句
     *
     * @param op 删除主键约束的对象
     */
    @Override
    protected void Generate(RemovePKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.write(" DROP PRIMARY KEY");

            this.AddRun(sql);
        }
    }

    /**
     * 生成删除数据表的语句
     *
     * @param op 删除表对象
     */
    @Override
    protected void Generate(DropTable op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("DROP TABLE ");
            sql.write(this.Quote(op.getTableName()));

            this.AddRun(sql);
        }
    }

    /**
     * 生成创建表的语句
     *
     * @param op 创建表的对象实例
     */
    @Override
    protected void Generate(CreateTable op) {
        if (StringUtils.isBlank(op.getPKName())) {
            GenerationExceptionRun tempVar = new GenerationExceptionRun();
            tempVar.setMessage("暂时不支持生成没有主键的表：" + op.getTableName());
            this.AddRun(tempVar);
            return;
        }

        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("CREATE TABLE IF NOT EXISTS ");
            sql.writeLine(this.Quote(op.getTableName()));
            sql.writeLine("(");
            sql.plusIndent();
            this.GenerateColumnDeclaration(sql, op.getPKName(), op.getPKDbType(), op.getPKLength(), true, true, null);
            if (op.getPKIdentity()) {
                sql.write(" auto_increment");
            }
            sql.write(" primary key");
            sql.writeLine();
            sql.minusIndent();
            sql.write(")");

            this.AddRun(sql);
        }
    }

    /**
     * 生成修改列的类型的语句
     *
     * @param op 修改列类型的实例对象
     */
    @Override
    protected void Generate(AlterColumnType op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("MODIFY ");

            this.GenerateColumnDeclaration(sql, op.getColumnName(), op.getNewType(), op.getLength(), op.getIsRequired(), op.getIsForeignKey(), op.getDefaultValue());

            this.AddRun(sql);
        }
    }

    /**
     * 生成创建普通的数据列的语句
     *
     * @param op 创建普通列的实例对象
     */
    @Override
    protected void Generate(CreateNormalColumn op) {
        if (op.getIsIdentity()) throw new NotImplementedException("mysql 数据库不支持创建自增列!");

        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("ADD ");
            this.GenerateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), false, op.getIsForeignKey(), op.getDefaultValue());

            this.AddRun(sql);
        }
    }

    /**
     * 生成删除非空的列约束的语句
     *
     * @param op 列操作的实例对象
     */
    @Override
    protected void RemoveNotNullConstraint(ColumnOperation op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("MODIFY ");
            this.GenerateColumnDeclaration(sql, op.getColumnName(), op.getDbType(), op.getLength(), false, op.getIsForeignKey(), op.getDefaultValue());

            this.AddRun(sql);
        }
    }
}