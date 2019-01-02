package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.Consts;
import com.github.zgynhqf.rafy4j.data.*;
import com.github.zgynhqf.rafy4j.dbmigration.DbMigrationSettings;
import com.github.zgynhqf.rafy4j.dbmigration.IndentedTextWriter;
import com.github.zgynhqf.rafy4j.dbmigration.operations.*;
import com.github.zgynhqf.rafy4j.dbmigration.run.FormattedSqlMigrationRun;
import com.github.zgynhqf.rafy4j.dbmigration.RunGenerator;
import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;

//******************************************************
// * 
// * 作者：胡庆访
// * 创建时间：20120102
// * 说明：此文件只包含一个类，具体内容见类型注释。
// * 运行环境：.NET 4.0
// * 版本号：1.0.0
// * 
// * 历史记录：
// * 创建文件 胡庆访 20120102
// * 
//******************************************************


/**
 * SQL 生成器的基类
 */
public abstract class SqlRunGenerator extends RunGenerator {
    private BaseDbIdentifierQuoter privateIdentifierQuoter;

    public final BaseDbIdentifierQuoter getIdentifierQuoter() {
        return privateIdentifierQuoter;
    }

    protected final void setIdentifierQuoter(BaseDbIdentifierQuoter value) {
        privateIdentifierQuoter = value;
    }

    private DbTypeConverter privateDbTypeCoverter;

    public final DbTypeConverter getDbTypeCoverter() {
        return privateDbTypeCoverter;
    }

    protected final void setDbTypeCoverter(DbTypeConverter value) {
        privateDbTypeCoverter = value;
    }

    @Override
    protected void Generate(DropTable op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("DROP TABLE ");
            sql.write(this.Quote(op.getTableName()));

            this.AddRun(sql);
        }
    }

    @Override
    protected void Generate(DropNormalColumn op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("DROP COLUMN ");
            sql.write(this.Quote(op.getColumnName()));

            this.AddRun(sql);
        }
    }

    @Override
    protected void Generate(AddPKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            this.GenerateAddPKConstraint(sql, op.getTableName(), op.getColumnName());

            this.AddRun(sql);
        }
    }

    protected void GenerateAddPKConstraint(IndentedTextWriter sql, String tableName, String columnName) {
        String pkName = String.format("PK_%1$s_%2$s", this.Prepare(tableName), this.Prepare(columnName));

        sql.write("" + Consts.NEW_LINE + "ALTER TABLE ");
        sql.write(this.Quote(tableName));
        sql.write("" + Consts.NEW_LINE + "    ADD CONSTRAINT ");
        sql.write(this.Quote(pkName));
        sql.write("" + Consts.NEW_LINE + "    PRIMARY KEY (");
        sql.write(this.Quote(columnName));
        sql.write(")");
    }

    @Override
    protected void Generate(RemovePKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("" + Consts.NEW_LINE + "ALTER TABLE ");
            sql.write(this.Quote(op.getTableName()));
            sql.write("" + Consts.NEW_LINE + "    DROP CONSTRAINT ");
            sql.write(this.Quote(String.format("PK_%1$s_%2$s", this.Prepare(op.getTableName()), this.Prepare(op.getColumnName()))));

            this.AddRun(sql);
        }
    }

    @Override
    protected void Generate(AddNotNullConstraint op) {
        String sqlString = String.format("UPDATE %1$s SET %2$s = %3$s WHERE %2$s IS NULL", this.Quote(op.getTableName()), this.Quote(op.getColumnName()), "{0}");
        FormattedSql sql = new FormattedSql(sqlString);
        sql.getParameters().add(this.getDbTypeCoverter().GetDefaultValue(op.getDbType()));

        FormattedSqlMigrationRun tempVar = new FormattedSqlMigrationRun();
        tempVar.setSql(sql);
        this.AddRun(tempVar);

        this.AddNotNullConstraint(op);
    }

    @Override
    protected void Generate(AddNotNullConstraintFK op) {
        this.AddNotNullConstraint(op);
    }

    protected abstract void AddNotNullConstraint(ColumnOperation op);

    @Override
    protected void Generate(RemoveNotNullConstraint op) {
        this.RemoveNotNullConstraint(op);
    }

    @Override
    protected void Generate(RemoveNotNullConstraintFK op) {
        this.RemoveNotNullConstraint(op);
    }

    protected abstract void RemoveNotNullConstraint(ColumnOperation op);

    @Override
    protected void Generate(AddFKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    ADD CONSTRAINT ");
            sql.write(this.Quote(op.getConstraintName()));
            sql.writeLine();
            sql.write("    FOREIGN KEY (");
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

    @Override
    protected void Generate(RemoveFKConstraint op) {
        try (IndentedTextWriter sql = this.Writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.Quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    DROP CONSTRAINT ");
            sql.write(this.Quote(op.getConstraintName()));

            this.AddRun(sql);
        }
    }

    /**
     * Generates the column declaration.
     *
     * @param sql          The SQL.
     * @param columnName   Name of the column.
     * @param dataType     Type of the data.
     * @param length       The length.
     * @param isRequired   if set to <c>true</c> [is required].
     * @param isPKorFK     在没有给出字段长度的情况下，如果这个字段是一个主键或外键，则需要自动限制它的长度。
     * @param defaultValue 默认值。
     */
    protected final void GenerateColumnDeclaration(
            IndentedTextWriter sql, String columnName, JDBCType dataType, String length, Boolean isRequired, boolean isPKorFK, String defaultValue
    ) {
        if (StringUtils.isBlank(length)) {
            if (isPKorFK) {
                //http://stackoverflow.com/questions/2863993/is-of-a-type-that-is-invalid-for-use-as-a-key-column-in-an-index
                length = DbMigrationSettings.PKFKDbTypeLength;
            } else if (dataType == JDBCType.VARCHAR) {
                length = DbMigrationSettings.StringColumnDbTypeLength;
            }
        }

        sql.write(this.Quote(columnName));
        sql.write(" ");
        sql.write(this.getDbTypeCoverter().ConvertToDatabaseTypeName(dataType, length));

        if (isRequired != null) {
            if (isRequired) {
                sql.write(" NOT");
            }
            sql.write(" NULL");
        }

        if (!StringUtils.isBlank(defaultValue)) {
            sql.write(" DEFAULT ");
            sql.write(defaultValue);
        }
    }

    protected final String Quote(String identifier) {
        return this.getIdentifierQuoter().Quote(identifier);
    }

    protected final String Prepare(String identifier) {
        return this.getIdentifierQuoter().Prepare(identifier);
    }
}