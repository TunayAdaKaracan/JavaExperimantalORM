package dev.kutuptilkisi.internal.database.structures;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public enum ColumnType {
    UNKNOWN(""),
    TEXT("TEXT", String.class),
    INT("INT", Integer.class, int.class),
    BOOLEAN("bool", Boolean.class, boolean.class),
    TIMESTAMP("TIMESTAMP", Timestamp.class);

    private List<Class<?>> javaClasses;
    private String SQLName;
    ColumnType(String SQLName, Class<?>... javaClass){
        this.SQLName = SQLName;
        this.javaClasses = Arrays.asList(javaClass);
    }

    public Class<?> getJavaClass(){
        return javaClasses.get(0);
    }

    public String getSQLName(){
        return this.SQLName;
    }

    public static ColumnType matchType(Class<?> clazz){
        for(ColumnType columnType : values()){
            if(columnType.javaClasses.contains(clazz)){
                return columnType;
            }
        }
        return UNKNOWN;
    }
}
