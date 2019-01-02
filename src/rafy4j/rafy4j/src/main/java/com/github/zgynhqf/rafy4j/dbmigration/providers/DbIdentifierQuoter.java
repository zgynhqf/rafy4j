package com.github.zgynhqf.rafy4j.dbmigration.providers;

/**
 * 数据库标识符的处理器。
 */
public interface DbIdentifierQuoter {
    /**
     * 准备 SQL 中所使用到的任意一个关键标识（表、字段、外键、主键、约束名等）。
     *
     * @param identifier
     * @return
     */
    String prepare(String identifier);

    /**
     * 标识符被引用时的起始字符
     */
    char getQuoteStart();

    /**
     * 标识符被引用时的终止字符
     */
    char getQuoteEnd();
}