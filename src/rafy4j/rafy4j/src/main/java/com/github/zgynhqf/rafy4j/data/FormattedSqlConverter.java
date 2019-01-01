package com.github.zgynhqf.rafy4j.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此接口用于把可用于 FormattedSql 转换为特定数据库格式的 Sql 字符串
 *
 * @author: huqingfang
 * @date: 2018-12-25 21:36
 **/
interface FormattedSqlConverter {
    /// <summary>
    /// 把可用于 String.Format 格式的字符串转换为特定数据库格式的字符串
    /// </summary>
    /// <param name="commonSql">可用于String.Format格式的字符串</param>
    /// <returns>可用于特定数据库的sql语句</returns>
    SqlWithParameters convert(FormattedSql formattedSql);

//    String getParameterName(int number);

    /// <summary>
    /// 在 FormatSQL 中的参数格式定义。
    /// </summary>
    static final Pattern ParameterPattern = Pattern.compile("\\{(?<parameterName>[\\d\\w]+)\\}");
    static final String PARAMETER_NAME_IN_PATTERN = "parameterName";

    public static String replaceCommonParameters(String commonSql, String replacement) {
        Matcher matcher = ParameterPattern.matcher(commonSql);
        String res = matcher.replaceAll(replacement);
        return res;

//        demo code:
//        String input = "insert into {0} sdfsdf {1}";
//
//        Pattern pattern = Pattern.compile("\\{(?<index>\\d+)\\}");
//        Matcher matcher = pattern.matcher(input);
//        String s = matcher.replaceAll("?p${index}");
    }
}
