package com.github.zgynhqf.rafy4j.dbmigration;

/**
 * 造成数据丢失的操作。
 */
public class DataLossOperation {
    /**
     * 不执行丢失数据的操作。
     */
    public static final DataLossOperation None = new DataLossOperation(0);
    /**
     * 删除表
     */
    public static final DataLossOperation DropTable = new DataLossOperation(1);
    /**
     * 删除列
     */
    public static final DataLossOperation DropColumn = new DataLossOperation(2);
    /**
     * 所有操作。
     */
    public static final DataLossOperation All = DataLossOperation.combine(DropTable, DropColumn);

    private int value;

    private DataLossOperation(int value) {
        this.value = value;
    }

    //region gs
    public int getValue() {
        return value;
    }

    public boolean hasValue(DataLossOperation item) {
        return (value & item.value) != 0;
    }
    //endregion

    public static DataLossOperation combine(DataLossOperation... items) {
        int value = 0;
        for (int i = 0; i < items.length; i++) {
            DataLossOperation item = items[i];
            value |= item.value;
        }
        return new DataLossOperation(value);
    }
}