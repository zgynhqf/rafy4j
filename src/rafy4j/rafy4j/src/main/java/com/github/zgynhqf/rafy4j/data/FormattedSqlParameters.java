package com.github.zgynhqf.rafy4j.data;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * FormattedSql 中的参数列表封装
 */
public class FormattedSqlParameters {
    private List<Object> parameters = new ArrayList<>();

    /**
     * 添加一个参数，并返回该参数应该使用的索引号
     * <p>
     * 当在 Sql 中直接写入 {0} 时，可以使用本方法直接添加一个参数到参数列表中。
     *
     * @param value object or a instance of DbAccesserParameter
     * @return
     */
    public final int add(Object value) {
        this.parameters.add(value);
        return this.parameters.size() - 1;
    }

    /**
     * 添加一个参数，并在 SQL 中添加相应的索引号
     *
     * @param sql
     * @param value
     * @return
     */
    public final void writeParameter(StringBuilder sql, Object value) {
        sql.append('{').append(this.parameters.size()).append('}');
        this.parameters.add(value);
    }

    /**
     * 添加一个参数，并在 SQL 中添加相应的索引号
     *
     * @param sql
     * @param value
     * @return
     */
    public final void writeParameter(Writer sql, Object value) {
        try {
            sql.write('{');
            sql.write(this.parameters.size());
            sql.write('}');
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.parameters.add(value);
    }

    /**
     * 获取指定位置的参数值
     *
     * @param index
     * @return
     */
    public final Object get(int index) {
        return parameters.get(index);
    }

    /**
     * 当前参数的个数
     */
    public final int getCount() {
        return this.parameters.size();
    }

    /**
     * 按照添加时的索引，返回所有的参数值数组。
     * 此数组可以直接使用在 DBAccesser 方法中。
     *
     * @return
     */
    public final Object[] toArray() {
        return this.parameters.toArray(new Object[]{});
    }

    @Override
    public String toString() {
        return StringUtils.collectionToCommaDelimitedString(parameters);
    }
}