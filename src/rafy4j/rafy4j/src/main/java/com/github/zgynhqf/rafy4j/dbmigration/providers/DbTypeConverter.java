package com.github.zgynhqf.rafy4j.dbmigration.providers;

import com.github.zgynhqf.rafy4j.utils.PrimitiveType;
import com.github.zgynhqf.rafy4j.utils.TimeHelper;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * 数据库字段类型的转换器。
 */
public abstract class DbTypeConverter {
    public String ConvertToDatabaseTypeName(JDBCType fieldType) {
        return this.ConvertToDatabaseTypeName(fieldType, null);
    }

    /**
     * 将 JDBCType 转换为数据库中的列的类型名称。
     *
     * @param fieldType
     * @param length
     * @return
     */
    public abstract String ConvertToDatabaseTypeName(JDBCType fieldType, String length);

    /**
     * 将从数据库 Schema Meta 中读取出来的列的类型名称，转换为其对应的 JDBCType。
     *
     * @param databaseTypeName 从数据库 Schema Meta 中读取出来的列的类型名称。
     * @return
     */
    public abstract JDBCType ConvertToDbType(String databaseTypeName);

    /**
     * 返回 CLR 类型默认映射的数据库的类型。
     *
     * @param clazz
     * @return
     */
    public JDBCType FromClrType(Class clazz) {
        if (clazz.isEnum() || clazz == String.class) {
            return JDBCType.VARCHAR;
        }
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.INTEGER)) return JDBCType.INTEGER;
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.LONG)) return JDBCType.BIGINT;
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.BOOLEAN)) return JDBCType.BOOLEAN;
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.FLOAT)) return JDBCType.FLOAT;
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.DOUBLE)) return JDBCType.DOUBLE;
        if (TypeHelper.isPrimitiveTypeOrClass(clazz, PrimitiveType.CHAR)) return JDBCType.CHAR;
        if (clazz == Date.class || clazz == LocalDateTime.class ||
                clazz == LocalDate.class || clazz == LocalTime.class) {
            return JDBCType.DATE;
        }
        if (clazz == byte[].class) return JDBCType.BINARY;

        if (TypeHelper.IsNullable(clazz)) {
            return this.FromClrType(TypeHelper.IgnoreNullable(clazz));
        }
        if (clazz == UUID.class) return JDBCType.VARCHAR;

        //default
        return JDBCType.VARCHAR;
    }

    /**
     * 将指定的值转换为一个兼容数据库类型的值。
     * 该值可用于与下层的 ADO.NET 交互。
     *
     * @param value
     * @return
     */
    public Object ToDbParameterValue(Object value) {
        return value;
        //        return (value != null) ? value : DBNull.getValue();
    }

    /**
     * 将指定的值转换为一个 CLR 类型的值。
     *
     * @param dbValue The database value.
     * @param clrType Type of the color.
     * @return
     */
    public Object ToClrValue(Object dbValue, Class clrType) {
        return dbValue;
//        return dbValue == DBNull.getValue() ? null : dbValue;
    }

    /**
     * 获取指定的数据库字段类型所对应的默认值。
     *
     * @param type
     * @return
     */
    public Object GetDefaultValue(JDBCType type) {
        switch (type) {
            case VARCHAR:
            case NVARCHAR:
                return "";
            case DATE:
                return TimeHelper.toDate(LocalDateTime.of(2000, 1, 1, 0, 0));
            case BOOLEAN:
                return Boolean.FALSE;
            case INTEGER:
                return 0;
            case BIGINT:
                return 0L;
            case FLOAT:
                return 0f;
            case DOUBLE:
                return 0d;
            case DECIMAL:
                return BigDecimal.valueOf(0);
            case BINARY:
                return new byte[0];
            default:
                throw new NotImplementedException("");
        }
    }

    /**
     * 由于不同的 JDBCType 映射到库中后的类型可能是相同的，所以这里需要对类型进行兼容性判断。
     *
     * @param oldColumnType
     * @param newColumnType
     * @return
     */
    public boolean IsCompatible(JDBCType oldColumnType, JDBCType newColumnType) {
        if (oldColumnType == newColumnType) return true;

        //如果两个列都属性同一类型的数据库类型，这里也表示库的类型没有变化。
        for (int i = 0, c = CompatibleTypes.length; i < c; i++) {
            JDBCType[] sameTypes = CompatibleTypes[i];
            if (ArrayUtils.contains(sameTypes, oldColumnType) &&
                    ArrayUtils.contains(sameTypes, newColumnType)
            ) {
                return true;
            }
        }

        return false;
    }

    private static JDBCType[][] CompatibleTypes = new JDBCType[][]{
            new JDBCType[]{JDBCType.VARCHAR, JDBCType.NVARCHAR},
            new JDBCType[]{JDBCType.INTEGER, JDBCType.BIGINT, JDBCType.FLOAT, JDBCType.DOUBLE}
    };
}