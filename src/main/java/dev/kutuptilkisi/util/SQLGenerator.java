package dev.kutuptilkisi.util;

import dev.kutuptilkisi.internal.database.structures.ColumnData;
import dev.kutuptilkisi.internal.database.structures.ColumnType;
import dev.kutuptilkisi.internal.database.structures.TableData;

public class SQLGenerator {

    private static void appendIf(StringBuilder builder, String string, boolean v){
        if(v){
            builder.append(string);
        }
    }

    private static String createTypeQuery(ColumnData columnData){
        StringBuilder builder = new StringBuilder();
        builder.append(columnData.getColumnName()+" "+columnData.getColumnType().getSQLName());

        appendIf(builder, " NOT NULL", columnData.isNotNull());
        if(columnData.getColumnType() != ColumnType.TEXT && columnData.getColumnType() != ColumnType.INT){
            appendIf(builder, " DEFAULT "+columnData.getDefaultString(), columnData.getDefaultString() != null);
        } else {
            appendIf(builder, " DEFAULT '"+columnData.getDefaultString()+"'", columnData.getDefaultString() != null);
        }
        appendIf(builder, " AUTO_INCREMENT", columnData.isAutoIncrement());

        return builder.toString();
    }

    public static String createTableQuery(TableData tableData){
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableData.getTableName()+"(");

        ColumnData primaryKey = null;

        for(ColumnData columnData : tableData.getColumns()){
            builder.append(createTypeQuery(columnData)).append(", ");
            if(columnData.isID() && primaryKey != null){
                throw new RuntimeException("You cant have 2 primary keys");
            } else if(columnData.isID()){
                primaryKey = columnData;
            }
        }
        builder.delete(builder.length()-2, builder.length());

        if(primaryKey != null){
            builder.append(", PRIMARY KEY("+primaryKey.getColumnName()+")");
        }
        return builder.toString() + ");";
    }
}
