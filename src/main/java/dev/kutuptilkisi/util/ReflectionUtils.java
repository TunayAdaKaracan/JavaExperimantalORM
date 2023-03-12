package dev.kutuptilkisi.util;

import dev.kutuptilkisi.internal.annotations.*;
import dev.kutuptilkisi.internal.database.structures.ColumnData;
import dev.kutuptilkisi.internal.database.structures.ColumnType;
import dev.kutuptilkisi.internal.database.structures.Modal;
import dev.kutuptilkisi.internal.database.structures.TableData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class ReflectionUtils {
    public static void ifHasFieldAnnotationFeed(Field f, Class<? extends Annotation> ann, Consumer<Boolean> consumer){
        consumer.accept(hasFieldAnnotation(f, ann));
    }

    public static boolean hasFieldAnnotation(Field f, Class<? extends Annotation> ann){
        return f.getAnnotation(ann) != null;
    }

    private static List<ColumnData> getAnnotedFieldsWith(Class<? extends Modal> clazz){
        List<ColumnData> columnDataList = new ArrayList<>();

        for(Field field : clazz.getDeclaredFields()){
            Column columnAnnotation = field.getAnnotation(Column.class);
            if(columnAnnotation != null){
                ColumnType columnType = ColumnType.matchType(field.getType());
                if(columnType == ColumnType.UNKNOWN){
                    throw new RuntimeException("Fuck you");
                }
                ColumnData columnData = new ColumnData(field.getName(), columnAnnotation.name(), columnType);
                ifHasFieldAnnotationFeed(field, Id.class, columnData::setID);
                ifHasFieldAnnotationFeed(field, NotNull.class, columnData::setNotNull);
                if(hasFieldAnnotation(field, Default.class)){
                    columnData.setDefaultString(field.getAnnotation(Default.class).defaultString());
                }
                ifHasFieldAnnotationFeed(field, AutoIncrement.class, columnData::setAutoIncrement);
                columnDataList.add(columnData);
            }
        }

        return columnDataList;
    }

    public static TableData getTableData(String tableName, Class<? extends Modal> clazz){
        return new TableData(tableName, getAnnotedFieldsWith(clazz));
    }

    public static Object getValueFromField(Field field, Object o){
        field.setAccessible(true);
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setValueOfField(Field field, Object object, Object value){
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Modal> T createInstanceFromResultSet(Class<T> clazz, TableData data, ResultSet resultSet){
        try {
            Constructor<T> constructor = clazz.getConstructor();
            T instance = constructor.newInstance();
            for(Field field : clazz.getDeclaredFields()){
                if(hasFieldAnnotation(field, Column.class)){
                    ColumnData columnData = data.getColumnByFieldName(field.getName());
                    setValueOfField(field, instance, resultSet.getObject(columnData.getColumnName(), columnData.getColumnType().getJavaClass()));
                }
            }
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
