package dev.kutuptilkisi.internal.database;

public record DatabaseCredentials(String HOST, int PORT, String DATABASE, String USERNAME, String PASSWORD) {
}