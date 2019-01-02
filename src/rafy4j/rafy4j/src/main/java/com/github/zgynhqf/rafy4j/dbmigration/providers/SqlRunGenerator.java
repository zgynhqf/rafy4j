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

/**
 * SQL 生成器的基类
 */
public abstract class SqlRunGenerator extends RunGenerator {
    private BaseDbIdentifierQuoter identifierQuoter;
    private DbTypeConverter dbTypeConverter;

    //region gs
    public final BaseDbIdentifierQuoter getIdentifierQuoter() {
        return identifierQuoter;
    }

    protected final void setIdentifierQuoter(BaseDbIdentifierQuoter value) {
        identifierQuoter = value;
    }

    public final DbTypeConverter getDbTypeConverter() {
        return dbTypeConverter;
    }

    protected final void setDbTypeConverter(DbTypeConverter value) {
        dbTypeConverter = value;
    }
    //endregion

    @Override
    protected void generate(DropTable op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("DROP TABLE ");
            sql.write(this.quote(op.getTableName()));

            this.addRun(sql);
        }
    }

    @Override
    protected void generate(DropNormalColumn op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.writeLine();

            sql.plusIndent();
            sql.write("DROP COLUMN ");
            sql.write(this.quote(op.getColumnName()));

            this.addRun(sql);
        }
    }

    @Override
    protected void generate(AddPKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            this.generateAddPKConstraint(sql, op.getTableName(), op.getColumnName());

            this.addRun(sql);
        }
    }

    protected void generateAddPKConstraint(IndentedTextWriter sql, String tableName, String columnName) {
        String pkName = String.format("PK_%1$s_%2$s", this.prepare(tableName), this.prepare(columnName));

        sql.write("" + Consts.NEW_LINE + "ALTER TABLE ");
        sql.write(this.quote(tableName));
        sql.write("" + Consts.NEW_LINE + "    ADD CONSTRAINT ");
        sql.write(this.quote(pkName));
        sql.write("" + Consts.NEW_LINE + "    PRIMARY KEY (");
        sql.write(this.quote(columnName));
        sql.write(")");
    }

    @Override
    protected void generate(RemovePKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("" + Consts.NEW_LINE + "ALTER TABLE ");
            sql.write(this.quote(op.getTableName()));
            sql.write("" + Consts.NEW_LINE + "    DROP CONSTRAINT ");
            sql.write(this.quote(String.format("PK_%1$s_%2$s", this.prepare(op.getTableName()), this.prepare(op.getColumnName()))));

            this.addRun(sql);
        }
    }

    @Override
    protected void generate(AddNotNullConstraint op) {
        String sqlString = String.format("UPDATE %1$s SET %2$s = %3$s WHERE %2$s IS NULL", this.quote(op.getTableName()), this.quote(op.getColumnName()), "{0}");
        FormattedSql sql = new FormattedSql(sqlString);
        sql.getParameters().add(this.getDbTypeConverter().getDefaultValue(op.getDbType()));

        FormattedSqlMigrationRun tempVar = new FormattedSqlMigrationRun();
        tempVar.setSql(sql);
        this.addRun(tempVar);

        this.addNotNullConstraint(op);
    }

    @Override
    protected void generate(AddNotNullConstraintFK op) {
        this.addNotNullConstraint(op);
    }

    protected abstract void addNotNullConstraint(ColumnOperation op);

    @Override
    protected void generate(RemoveNotNullConstraint op) {
        this.removeNotNullConstraint(op);
    }

    @Override
    protected void generate(RemoveNotNullConstraintFK op) {
        this.removeNotNullConstraint(op);
    }

    protected abstract void removeNotNullConstraint(ColumnOperation op);

    @Override
    protected void generate(AddFKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    ADD CONSTRAINT ");
            sql.write(this.quote(op.getConstraintName()));
            sql.writeLine();
            sql.write("    FOREIGN KEY (");
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

    @Override
    protected void generate(RemoveFKConstraint op) {
        try (IndentedTextWriter sql = this.writer()) {
            sql.write("ALTER TABLE ");
            sql.write(this.quote(op.getDependentTable()));
            sql.writeLine();
            sql.write("    DROP CONSTRAINT ");
            sql.write(this.quote(op.getConstraintName()));

            this.addRun(sql);
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
    protected final void generateColumnDeclaration(
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

        sql.write(this.quote(columnName));
        sql.write(" ");
        sql.write(this.getDbTypeConverter().convertToDatabaseTypeName(dataType, length));

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

    protected final String quote(String identifier) {
        return this.getIdentifierQuoter().quote(identifier);
    }

    protected final String prepare(String identifier) {
        return this.getIdentifierQuoter().prepare(identifier);
    }
}