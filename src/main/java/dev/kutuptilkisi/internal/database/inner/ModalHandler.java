package dev.kutuptilkisi.internal.database.inner;

import dev.kutuptilkisi.internal.annotations.AutoIncrement;
import dev.kutuptilkisi.internal.annotations.Column;
import dev.kutuptilkisi.internal.annotations.Table;
import dev.kutuptilkisi.internal.database.DatabaseConnector;
import dev.kutuptilkisi.internal.database.structures.ColumnData;
import dev.kutuptilkisi.internal.database.structures.Modal;
import dev.kutuptilkisi.internal.database.structures.TableData;
import dev.kutuptilkisi.util.ReflectionUtils;
import dev.kutuptilkisi.util.SQLGenerator;
import dev.kutuptilkisi.util.SQLQueryBuilder;
import dev.kutuptilkisi.util.filter.Filter;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModalHandler {
    private final DatabaseConnector connector;
    private final HashMap<Class<? extends Modal>, TableData> tableDataHashMap;

    public ModalHandler(DatabaseConnector connector){
        this.connector = connector;
        this.tableDataHashMap = new HashMap<>();
    }

    public void registerModal(Class<? extends Modal> clazz){
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        TableData tableData = ReflectionUtils.getTableData(tableName, clazz);
        this.tableDataHashMap.put(clazz, tableData);

        String generatedSQLCode = SQLGenerator.createTableQuery(tableData);
        if(!connector.isConnected()){
            throw new RuntimeException("You must have been connected to database to register a modal");
        }
        System.out.println(generatedSQLCode);
        connector.executeRawSQL(generatedSQLCode);
    }

    public <T extends Modal> T create(Class<T> clazz, SQLQueryBuilder.SQLInsertQueryBuilder values){
        final TableData tableData = tableDataHashMap.get(clazz);
        if(!connector.isConnected()){
            throw new RuntimeException("You must have been connected to database to register a modal");
        }
        connector.executeInsert(values.setTableName(tableData.getTableName()));
        return null;
    }

    public <T extends Modal> T create(Class<T> clazz, T modal){
        final TableData tableData = tableDataHashMap.get(clazz);

        SQLQueryBuilder.SQLInsertQueryBuilder builder = SQLQueryBuilder.SQLInsertQueryBuilder.newQuery();
        builder.setTableName(tableData.getTableName());

        for(Field field : clazz.getDeclaredFields()){
            if(ReflectionUtils.hasFieldAnnotation(field, Column.class) && !ReflectionUtils.hasFieldAnnotation(field, AutoIncrement.class)){
                ColumnData columnData = tableData.getColumnByFieldName(field.getName());
                Object ob = ReflectionUtils.getValueFromField(field, modal);
                if(ob != null){
                    builder.insert(columnData.getColumnName(), ob);
                }
            }
        }
        connector.executeInsert(builder);
        return findOne(clazz, modal);
    }

    public <T extends Modal> T findOne(Class<T> clazz, T modal){
        final TableData tableData = tableDataHashMap.get(clazz);

        SQLQueryBuilder.SQLSelectQueryBuilder builder = SQLQueryBuilder.SQLSelectQueryBuilder.newQuery();
        builder.setTable(tableData.getTableName());
        builder.limit(1);

        for(Field field : clazz.getDeclaredFields()){
            if(ReflectionUtils.hasFieldAnnotation(field, Column.class)){
                ColumnData columnData = tableData.getColumnByFieldName(field.getName());
                Object ob = ReflectionUtils.getValueFromField(field, modal);
                if(ob != null){
                    builder.filter(Filter.eq(columnData.getColumnName(), ob));
                }
            }
        }
        AtomicReference<T> instance = new AtomicReference<>();
        connector.executeSelect(builder, resultSet -> {
            try {
                if(resultSet.next()){
                    instance.set(ReflectionUtils.createInstanceFromResultSet(clazz, tableData, resultSet));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return instance.get();
    }

    public <T extends Modal> List<T> findMany(Class<T> clazz, T modal){
        final TableData tableData = tableDataHashMap.get(clazz);

        SQLQueryBuilder.SQLSelectQueryBuilder builder = SQLQueryBuilder.SQLSelectQueryBuilder.newQuery();
        builder.setTable(tableData.getTableName());

        for(Field field : clazz.getDeclaredFields()){
            if(ReflectionUtils.hasFieldAnnotation(field, Column.class)){
                ColumnData columnData = tableData.getColumnByFieldName(field.getName());
                Object ob = ReflectionUtils.getValueFromField(field, modal);
                if(ob != null){
                    builder.filter(Filter.eq(columnData.getColumnName(), ob));
                }
            }
        }
        List<T> instances = new ArrayList<>();
        connector.executeSelect(builder, resultSet -> {
            try{
                while(resultSet.next()){
                    instances.add(ReflectionUtils.createInstanceFromResultSet(clazz, tableData, resultSet));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return instances;
    }
}
