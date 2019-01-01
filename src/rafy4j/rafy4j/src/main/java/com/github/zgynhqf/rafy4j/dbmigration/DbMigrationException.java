package com.github.zgynhqf.rafy4j.dbmigration;

import java.io.Serializable;

public class DbMigrationException extends RuntimeException implements Serializable {
    public DbMigrationException() {
    }

    public DbMigrationException(String message) {
        super(message);
    }

    public DbMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}