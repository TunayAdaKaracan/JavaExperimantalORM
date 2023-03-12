package dev.kutuptilkisi.internal.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.kutuptilkisi.internal.database.inner.ModalHandler;
import dev.kutuptilkisi.internal.database.structures.Modal;
import dev.kutuptilkisi.util.SQLQueryBuilder;

import java.sql.*;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseConnector {

    private final ModalHandler modalHandler;
    private final HikariConfig CONFIGURATION;
    private HikariDataSource hikari;

    public DatabaseConnector(DatabaseCredentials credentials){
        this.modalHandler = new ModalHandler(this);

        this.CONFIGURATION = new HikariConfig();
        this.CONFIGURATION.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        this.CONFIGURATION.setJdbcUrl("jdbc:mysql//"+credentials.HOST()+":"+credentials.PORT());
        this.CONFIGURATION.addDataSourceProperty("databaseName", credentials.DATABASE());
        this.CONFIGURATION.setUsername(credentials.USERNAME());
        this.CONFIGURATION.setPassword(credentials.PASSWORD());
    }

    public void connect(){
        hikari = new HikariDataSource(CONFIGURATION);
    }

    public void disconnect(){
        hikari.close();
    }

    public boolean isConnected(){
        return hikari != null;
    }

    public HikariDataSource getHikari(){
        return hikari;
    }

    public void registerModal(Class<? extends Modal> clazz){
        modalHandler.registerModal(clazz);
    }

    public <T extends Modal> T create(Class<T> clazz, SQLQueryBuilder.SQLInsertQueryBuilder values){
        return modalHandler.create(clazz, values);
    }

    public <T extends Modal> T create(T modal){
        return modalHandler.create((Class<T>) modal.getClass(), modal);
    }

    public <T extends Modal> T findOne(T modal){
        return modalHandler.findOne((Class<T>) modal.getClass(), modal);
    }

    public <T extends Modal> List<T> findMany(T modal){
        return modalHandler.findMany((Class<T>) modal.getClass(), modal);
    }

    public void executeRawSQL(String sql){
        try(Connection connection = getHikari().getConnection()){
            PreparedStatement p = connection.prepareStatement(sql);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeInsert(SQLQueryBuilder.SQLInsertQueryBuilder query){
        try(Connection connection = getHikari().getConnection()){
            query.build(connection).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeSelect(SQLQueryBuilder.SQLSelectQueryBuilder query, Consumer<ResultSet> consumer){
        try(Connection connection = getHikari().getConnection()){
            PreparedStatement statement = query.build(connection);
            System.out.println(statement.toString());
            ResultSet resultSet = statement.executeQuery();
            consumer.accept(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}