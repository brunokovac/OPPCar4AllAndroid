package hr.fer.android.opp.car4all.dao;

import java.sql.Connection;

public class SQLConnectionProvider {

    private static Connection connection;

    public static void setConnection(Connection con) {
        connection = con;
    }

    public static Connection getConnection() {
        return connection;
    }

}