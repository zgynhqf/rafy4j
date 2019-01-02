package com.github.zgynhqf.rafy4j.data;

/**
 * jdbc 可执行的 Sql 语句与参数
 *
 * @author: huqingfang
 * @date: 2018-12-25 22:45
 **/
class SqlWithParameters {
    private String sql;
    private Object[] parameters;

    public SqlWithParameters(String sql, Object[] parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    //region gs
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    //endregion
}
