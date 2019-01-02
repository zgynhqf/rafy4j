package com.github.zgynhqf.rafy4j.dbmigration.model.differ;

import com.github.zgynhqf.rafy4j.dbmigration.model.Database;
import com.github.zgynhqf.rafy4j.dbmigration.model.DestinationDatabase;

import java.util.List;

/**
 * 数据库的变更记录
 */
public class DatabaseChanges {
    private List<TableChanges> allRecords;
    private DestinationDatabase newDatabase;
    private ChangeType changeType = ChangeType.UNCHANGED;
    private Database oldDatabase;

    public DatabaseChanges(Database oldDatabase, DestinationDatabase newDatabase, List<TableChanges> tableChanges) {
        this.setOldDatabase(oldDatabase);
        this.setNewDatabase(newDatabase);
        this.setChangeType(ChangeType.UNCHANGED);

        if (!oldDatabase.getRemoved() || !newDatabase.getRemoved()) {
            if (oldDatabase.getRemoved()) {
                this.setChangeType(ChangeType.ADDED);
                return;
            }

            if (newDatabase.getRemoved()) {
                this.setChangeType(ChangeType.REMOVED);
                return;
            }

            if (tableChanges != null && tableChanges.size() > 0) {
                this.setChangeType(ChangeType.MODIFIED);

                this.allRecords = tableChanges;
            }
        }
    }

    //region gs
    public final Database getOldDatabase() {
        return oldDatabase;
    }

    private void setOldDatabase(Database value) {
        oldDatabase = value;
    }

    public final DestinationDatabase getNewDatabase() {
        return newDatabase;
    }

    private void setNewDatabase(DestinationDatabase value) {
        newDatabase = value;
    }

    public final ChangeType getChangeType() {
        return changeType;
    }

    private void setChangeType(ChangeType value) {
        changeType = value;
    }

    public final List<TableChanges> getTablesChanged() {
        return this.allRecords;
    }

    public final TableChanges FindTable(String tableName) {
        return allRecords.stream().filter(t -> tableName.equalsIgnoreCase(t.getName())).findFirst().orElse(null);
    }
    //endregion

    @Override
    public String toString() {
        switch (this.getChangeType()) {
            case ADDED:
                return this.getNewDatabase().getName() + " ADDED!";
            case REMOVED:
                return this.getOldDatabase().getName() + " REMOVED!";
            case MODIFIED:
                return String.format("All:%1$s, ADDED:%2$s, REMOVED:%3$s, Changed:%4$s",
                        this.allRecords.size(),
                        this.allRecords.stream().filter(r -> r.getChangeType() == ChangeType.ADDED).count(),
                        this.allRecords.stream().filter(r -> r.getChangeType() == ChangeType.REMOVED).count(),
                        this.allRecords.stream().filter(r -> r.getChangeType() == ChangeType.MODIFIED).count()
                );
            default:
                return "";
        }
    }
}