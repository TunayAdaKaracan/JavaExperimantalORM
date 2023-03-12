package dev.kutuptilkisi.internal.database.structures;

public class ColumnData {

    // TODO: Better Option Storing

    private String fieldName;
    private String columnName;
    private ColumnType columnType;

    // Data
    private boolean isID;
    private boolean notNull;
    private String defaultString;
    private boolean autoIncrement;

    public ColumnData(String fieldName, String columnName, ColumnType columnType){
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.columnType = columnType;

        this.isID = false;
        this.notNull = false;
        this.autoIncrement = false;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public boolean isID() {
        return isID;
    }

    public void setID(boolean ID) {
        isID = ID;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getDefaultString() {
        return defaultString;
    }

    public void setDefaultString(String defaultString) {
        this.defaultString = defaultString;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
}
