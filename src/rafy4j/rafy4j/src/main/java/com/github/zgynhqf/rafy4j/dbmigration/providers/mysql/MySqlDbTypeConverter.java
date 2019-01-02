package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;

import com.github.zgynhqf.rafy4j.dbmigration.providers.DbTypeConverter;
import com.github.zgynhqf.rafy4j.utils.PrimitiveType;
import com.github.zgynhqf.rafy4j.utils.TypeHelper;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * mysql 数据库字段类型的转换器。
 */
public class MySqlDbTypeConverter extends DbTypeConverter {
    public static final MySqlDbTypeConverter Instance = new MySqlDbTypeConverter();

    private MySqlDbTypeConverter() {
    }

    /**
     * 把 DbType的类型值 转换为 mysql 数据库中兼容的数据类型
     * MySql数据精度 http://qimo601.iteye.com/blog/1622368
     *
     * @param fieldType 字段的DbType类型值
     * @param length    数据类型的长度
     * @return 返回MySql数据库中的具体类型值
     */
    @Override
    public String convertToDatabaseTypeName(JDBCType fieldType, String length) {
        switch (fieldType) {
            case VARCHAR:
            case NVARCHAR:
                if (!StringUtils.isBlank(length) && !"max".equalsIgnoreCase(length)) {
                    return "VARCHAR(" + length + ")";
                }
                return "TEXT";
            case DATE:
                return "DATETIME";
//                return "DATE";
            case BOOLEAN:
                return "TINYINT(1)";
            //return "BIT";// Boolean 映射为 BIT 时，批量导入会出错。
            case INTEGER:
                return "INT";
            case BIGINT:
                return "BIGINT";
            case FLOAT:
            case DOUBLE:
                return "DOUBLE";
//            return "FLOAT";
            case BINARY:
            case VARBINARY:
                return "BLOB";
            case TIME:
                return "TIME";
            case NUMERIC:
            case DECIMAL:
                if (!StringUtils.isBlank(length)) {
                    return "DECIMAL(" + length + ")";
                }
                return "DECIMAL(18,2)";
            default:
                break;
        }
        throw new NotImplementedException(String.format("不支持生成列类型：%1$s。", fieldType));
    }

    /**
     * 将从数据库 SCHEMA Meta 中读取出来的列的类型名称，转换为其对应的 DbType。
     *
     * @param dbTypeName 从数据库 SCHEMA Meta 中读取出来的列的类型名称。
     * @return
     */
    @Override
    public JDBCType convertToDbType(String dbTypeName) {
        if (upperContains(dbTypeName, "VARCHAR")) return JDBCType.VARCHAR;
        if (upperContains(dbTypeName, "CHAR")) return JDBCType.CHAR;
        if (upperContains(dbTypeName, "TINYTEXT")) return JDBCType.VARCHAR;
        if (upperContains(dbTypeName, "MEDIUMTEXT")) return JDBCType.VARCHAR;
        if (upperContains(dbTypeName, "LONGTEXT")) return JDBCType.VARCHAR;
        if (upperContains(dbTypeName, "TEXT")) return JDBCType.VARCHAR;
        if (upperContains(dbTypeName, "TINYINT(1)")) return JDBCType.BOOLEAN;
        if (upperContains(dbTypeName, "INT")) {
            if (dbTypeName.indexOf('(') > 0) {
                dbTypeName = dbTypeName.substring(0, dbTypeName.indexOf('('));
            }
//            if (dbTypeName.compareToIgnoreCase("TINYINT") == 0) return JDBCType.Byte;
//            if (dbTypeName.compareToIgnoreCase("SMALLINT") == 0) return JDBCType.Int16;
            if (dbTypeName.compareToIgnoreCase("BIGINT") == 0) return JDBCType.BIGINT;
            if ((dbTypeName.compareToIgnoreCase("INT") == 0) || (dbTypeName.compareToIgnoreCase("YEAR") == 0)) {
                return JDBCType.INTEGER;
            }
        }
        if (upperContains(dbTypeName, "FLOAT")) return JDBCType.FLOAT;
        if (upperContains(dbTypeName, "DOUBLE")) return JDBCType.DOUBLE;
        if (upperContains(dbTypeName, "DECIMAL")) return JDBCType.DECIMAL;
        if (upperContains(dbTypeName, "BLOB")) return JDBCType.BINARY;

        if (upperContains(dbTypeName, "TIME")) {
            if (dbTypeName.indexOf('(') > 0) {
                dbTypeName = dbTypeName.substring(0, dbTypeName.indexOf('('));
            }
            if (dbTypeName.compareToIgnoreCase("TIME") == 0) return JDBCType.TIME;
            if (dbTypeName.compareToIgnoreCase("DATETIME") == 0) return JDBCType.DATE;
            if (dbTypeName.compareToIgnoreCase("TIMESTAMP") == 0) return JDBCType.TIMESTAMP;
        }
        if (upperContains(dbTypeName, "DATE")) return JDBCType.DATE;

        throw new NotImplementedException(String.format("不支持读取数据库中的列类型：%1$s。", dbTypeName));
    }

    /**
     * 将指定的值转换为一个兼容数据库类型的值。
     * 该值可用于与下层的 ADO.NET 交互。
     *
     * @param value
     * @return
     */
    @Override
    public Object toDbParameterValue(Object value) {
        value = super.toDbParameterValue(value);

        if (value != null) {
            Class<?> valueClass = value.getClass();
            if (TypeHelper.isPrimitiveTypeOrClass(valueClass, PrimitiveType.BOOLEAN)) {
                value = (boolean) value ? 1 : 0;
            } else if (valueClass.isEnum()) {
                value = TypeHelper.coerceValue(Integer.class, value);
            } else if (value instanceof LocalDateTime) {
                value = TypeHelper.coerceValue(Date.class, value);
            }
        }

        return value;
    }

    /**
     * 将指定的值转换为一个 CLR 类型的值。
     *
     * @param dbValue The database value.
     * @param jreType Type of the color.
     * @return
     */
    @Override
    public Object toJreValue(Object dbValue, Class jreType) {
        dbValue = super.toJreValue(dbValue, jreType);

        if (dbValue == null && jreType == String.class) {
            dbValue = ""; //null 转换为空字符串
        }

        // DateTime to LocalDateTime
        // https://msdn.microsoft.com/zh-cn/library/bb546101.aspx
        // DateTime DateTime2 区别 http://www.studyofnet.com/news/1050.html
        if (jreType == LocalDateTime.class && dbValue != null) {
            dbValue = TypeHelper.coerceValue(LocalDateTime.class, dbValue);
        }

        return dbValue;
    }

    private static boolean upperContains(String mySqlType, String targetType) {
        return mySqlType.toUpperCase().contains(targetType);
    }
}