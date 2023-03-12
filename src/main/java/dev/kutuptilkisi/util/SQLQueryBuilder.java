package dev.kutuptilkisi.util;

import dev.kutuptilkisi.util.filter.Filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SQLQueryBuilder {
    public static class SQLInsertQueryBuilder {
        private String tableName;
        private final HashMap<String, Object> inserts;
        private SQLInsertQueryBuilder(){
            inserts = new HashMap<>();
        }

        public SQLInsertQueryBuilder setTableName(String tableName){
            this.tableName = tableName;
            return this;
        }

        public SQLInsertQueryBuilder insert(String fieldName, Object o){
            inserts.put(fieldName, o);
            return this;
        }

        public PreparedStatement build(Connection connection) throws SQLException {
            StringBuilder insertBuilder = new StringBuilder("insert into "+tableName+"(");
            StringBuilder valuesBuilder = new StringBuilder("values(");

            for(Map.Entry<String, Object> entry : inserts.entrySet()){
                insertBuilder.append(entry.getKey()).append(", ");
                valuesBuilder.append("?").append(", ");
            }
            insertBuilder.delete(insertBuilder.length()-2, insertBuilder.length());
            valuesBuilder.delete(valuesBuilder.length()-2, valuesBuilder.length());
            insertBuilder.append(")");
            valuesBuilder.append(")");
            PreparedStatement preparedStatement = connection.prepareStatement(insertBuilder.toString() + " " + valuesBuilder.toString());
            int idx = 1;
            for(Object o : inserts.values()){
                preparedStatement.setObject(idx, o);
                idx++;
            }
            return preparedStatement;
        }

        public static SQLInsertQueryBuilder newQuery(){
            return new SQLInsertQueryBuilder();
        }
    }

    public static class SQLSelectQueryBuilder {
        public enum Sorting{
            ASCENDING("ASC"),
            DESCENDING("DESC");

            private String SQLKeyword;
            Sorting(String SQLKeyword){
                this.SQLKeyword = SQLKeyword;
            }

            public String getSQLKeyword() {
                return SQLKeyword;
            }
        }

        private String tableName;
        private final List<Filter> conditions;
        private int limit;
        private final Map<String, Sorting> sortings;
        private SQLSelectQueryBuilder(){
            this.conditions = new LinkedList<>();
            this.limit = -1;
            this.sortings = new LinkedHashMap<>();
        }

        public SQLSelectQueryBuilder setTable(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public SQLSelectQueryBuilder filter(Filter filter){
            this.conditions.add(filter);
            return this;
        }

        public SQLSelectQueryBuilder limit(int limit){
            if(limit <= 0){
                this.limit = -1;
            } else {
                this.limit = limit;
            }
            return this;
        }

        public SQLSelectQueryBuilder sortBy(String columnName, Sorting sortBy){
            this.sortings.put(columnName, sortBy);
            return this;
        }

        public PreparedStatement build(Connection connection) throws SQLException {
            StringBuilder selectBuilder = new StringBuilder("select * ");
            selectBuilder.append("from ").append(tableName).append(" ");
            if(conditions.size() != 0) {
                selectBuilder.append("where ");
                for (Filter filter : this.conditions) {
                    selectBuilder.append(filter.getColumn()).append(filter.getFilterType().getOperator()).append("? AND ");
                }
                selectBuilder.delete(selectBuilder.length()-4, selectBuilder.length());
            }
            if(sortings.size() != 0){
                selectBuilder.append("order by ");
                for(Map.Entry<String, Sorting> sortingEntry : sortings.entrySet()){
                    selectBuilder.append(sortingEntry.getKey()).append(" ").append(sortingEntry.getValue().getSQLKeyword()).append(", ");
                }
                selectBuilder.delete(selectBuilder.length()-2, selectBuilder.length());
                selectBuilder.append(" ");
            }
            if(limit != -1){
                selectBuilder.append("limit ").append(limit).append(" ");
            }
            selectBuilder.delete(selectBuilder.length()-1, selectBuilder.length());
            selectBuilder.append(";");
            PreparedStatement statement = connection.prepareStatement(selectBuilder.toString());
            int idx = 1;
            for(Filter filter : this.conditions){
                statement.setObject(idx, filter.getObject());
                idx++;
            }
            return statement;
        }

        public static SQLSelectQueryBuilder newQuery(){
            return new SQLSelectQueryBuilder();
        }
    }
}
