package com.github.zgynhqf.rafy4j.dbmigration.providers.mysql;

import com.github.zgynhqf.rafy4j.dbmigration.providers.DbIdentifierQuoter;

public class MySqlIdentifierQuoter extends DbIdentifierQuoter {
    public static final MySqlIdentifierQuoter Instance = new MySqlIdentifierQuoter();

    private MySqlIdentifierQuoter() {
    }

    private static final int IDENTIFIER_MAX_LENGTH = 30;

    @Override
    public char getQuoteStart() {
        return '`';
    }

    @Override
    public String Prepare(String identifier) {
        return LimitIdentifier(identifier, IDENTIFIER_MAX_LENGTH);
    }
}