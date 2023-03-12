package dev.kutuptilkisi.internal.database.structures;

import java.util.List;

public class TableData {
    private final String tableName;
    private final List<ColumnData> columns;

    public TableData(String tableName, List<ColumnData> columns){
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnData> getColumns() {
        return columns;
    }

    public ColumnData getColumnByName(String name){
        for(ColumnData columnData : columns){
            if(columnData.getColumnName().equals(name)){
                return columnData;
            }
        }
        return null;
    }

    public ColumnData getColumnByFieldName(String name){
        for(ColumnData columnData : columns){
            if(columnData.getFieldName().equals(name)){
                return columnData;
            }
        }
        return null;
    }
}
