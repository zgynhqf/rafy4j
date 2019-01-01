package com.github.zgynhqf.rafy4j.data;

/**
 * @author: huqingfang
 * @date: 2018-12-29 22:27
 **/
class FormattedSqlConverterFactory {
    public static FormattedSqlConverter createSqlConverter(String driverName){
        return new MySqlFormattedSqlConverter();
    }
}
