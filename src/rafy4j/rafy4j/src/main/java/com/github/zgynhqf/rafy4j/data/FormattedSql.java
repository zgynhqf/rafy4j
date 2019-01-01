package com.github.zgynhqf.rafy4j.data;

import org.springframework.jdbc.core.SqlParameterValue;

import java.sql.JDBCType;

/**
 * 表示一个使用 {0}、{1}、{name} 来代表参数命名的格式化后的 Sql 及 参数列表。
 * 该方案以实现简单的跨数据库及参数化查询。
 *
 * <p>
 * 如果想直接面向 Sql 字符串进行操作，请使用 append 系方法。
 */
public class FormattedSql {
    /**
     * 最终生成的 Sql 字符串的 TextWriter
     */
    private StringBuilder sql;

    public FormattedSql() {
        sql = new StringBuilder();
    }

    public FormattedSql(String sql) {
        this.sql = new StringBuilder(sql);
    }

    private FormattedSqlParameters parameters = new FormattedSqlParameters();

    /**
     * 当前可用的参数
     */
    public final FormattedSqlParameters getParameters() {
        return parameters;
    }

    /**
     * 写入一个参数值。
     *
     * @param value
     */
    public final FormattedSql AppendParameter(Object value) {
        parameters.writeParameter(this.sql, value);
        return this;
    }

    /**
     * 写入一个参数值。
     *
     * @param value  The value.
     * @param dbType 可以明确指定列的 DbType。
     * @return
     */
    public final FormattedSql AppendParameter(Object value, JDBCType dbType) {
        SqlParameterValue parameter = new SqlParameterValue(dbType.getVendorTypeNumber(), value);
        parameters.writeParameter(this.sql, parameter);
        return this;
    }

    //直接添加原始 SQL 语句的方法

//    /**
//     * 获取内部的 TextWriter，用于直接面向字符串进行文本输出。
//     * 同时，也可以使用新的 TextWriter 来装饰当前的 TextWriter。
//     */
//    public final Writer getInnerWriter() {
//        return sql;
//    }
//
//    public final void setInnerWriter(Writer value) {
//        sql = value;
//    }

    /**
     * 直接添加 " AND "。
     *
     * @return
     */
    public final FormattedSql AppendAnd() {
        this.sql.append(" AND ");
        return this;
    }

    /**
     * 直接添加 " OR "。
     *
     * @return
     */
    public final FormattedSql AppendOr() {
        this.sql.append(" OR ");
        return this;
    }

    /**
     * 直接添加指定的字符串。
     *
     * @return
     */
    public final FormattedSql Append(String value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的 char 值。
     *
     * @return
     */
    public final FormattedSql Append(char value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的 int 值。
     *
     * @return
     */
    public final FormattedSql Append(int value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的 double 值。
     *
     * @return
     */
    public final FormattedSql Append(double value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的 object 值。
     *
     * @return
     */
    public final FormattedSql Append(Object value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的 bool 值。
     *
     * @return
     */
    public final FormattedSql Append(boolean value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 直接添加指定的字符串值，并添加回车。
     *
     * @return
     */
    public final FormattedSql AppendLine(String value) {
        this.sql.append(value);
        return this;
    }

    /**
     * 添加回车
     *
     * @return
     */
    public final FormattedSql AppendLine() {
        this.sql.append("\r\n");
        return this;
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}