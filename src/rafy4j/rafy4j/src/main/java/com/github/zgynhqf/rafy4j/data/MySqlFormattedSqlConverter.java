package com.github.zgynhqf.rafy4j.data;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SqlParameterValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * MySql数据库链接字符串转换器
 *
 * @author: huqingfang
 * @date: 2018-12-25 21:38
 **/
public class MySqlFormattedSqlConverter implements FormattedSqlConverter {
    @Override
    public SqlWithParameters convert(FormattedSql formattedSql) {
        FormattedSqlParameters formattedSqlParameters = formattedSql.getParameters();

        if (formattedSqlParameters.getCount() == 0) {
            return new SqlWithParameters(formattedSql.toString(), formattedSqlParameters.toArray());
        }

        List<Object> parameters = new ArrayList<>(formattedSqlParameters.getCount());

        String sql = formattedSql.toString();
        Matcher matcher = ParameterPattern.matcher(sql);

        while (matcher.find()) {
            String pName = matcher.group(PARAMETER_NAME_IN_PATTERN);
            //如果 pName 是数字，则直接是下标。
            if (StringUtils.isNumeric(pName)) {
                int pIndex = Integer.parseInt(pName);//目前都是数字索引。
                parameters.add(formattedSqlParameters.get(pIndex));
            } else {
                //如果 pName 是名称，则需要在参数列表中转换为 SqlParameterValue 并用名称进行匹配查找。
                boolean found = false;
                for (int i = 0; i < formattedSqlParameters.getCount(); i++) {
                    SqlParameterValue p = (SqlParameterValue) formattedSqlParameters.get(i);
                    if (p != null && p.getName().equalsIgnoreCase(pName)) {
                        parameters.add(p);
                        found = true;
                        break;
                    }
                }
                if (!found) throw new IllegalArgumentException(String.format("未找到名为 %1 的 SqlParameterValue。", pName));
            }
        }

        String finalSql = matcher.replaceAll("?");

        return new SqlWithParameters(finalSql, parameters.toArray());
    }

//    /**
//     * 根据索引获取参数名称
//     *
//     * @param number
//     * @return
//     */
//    @Override
//    public String getParameterName(int index) {
//        return "?";//jdbc 不支持命名参数
////        return "?p" + index;
//    }
}
