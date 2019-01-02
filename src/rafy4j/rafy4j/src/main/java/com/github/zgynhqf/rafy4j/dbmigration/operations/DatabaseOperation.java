package com.github.zgynhqf.rafy4j.dbmigration.operations;

import com.github.zgynhqf.rafy4j.dbmigration.MigrationOperation;

public abstract class DatabaseOperation extends MigrationOperation {
    private String database;

    public final String getDatabase() {
        return database;
    }

    public final void setDatabase(String value) {
        database = value;
    }

    @Override
    public String getDescription() {
        return String.format("%1$s: %2$s", super.getDescription(), this.getDatabase());
    }
}