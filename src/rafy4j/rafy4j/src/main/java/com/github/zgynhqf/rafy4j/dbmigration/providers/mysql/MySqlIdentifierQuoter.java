package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;

import com.github.zgynhqf.rafy4j.dbmigration.providers.BaseDbIdentifierQuoter;

public class MySqlIdentifierQuoter extends BaseDbIdentifierQuoter {
    public static final MySqlIdentifierQuoter Instance = new MySqlIdentifierQuoter();

    private MySqlIdentifierQuoter() {
    }

    /**
     * https://blog.csdn.net/jiangzc86/article/details/16817589
     */
    private static final int IDENTIFIER_MAX_LENGTH = 64;

    @Override
    public char getQuoteStart() {
        return '`';
    }

    @Override
    public String prepare(String identifier) {
        return limitIdentifier(identifier, IDENTIFIER_MAX_LENGTH);
    }
}