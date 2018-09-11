package com.publictalkgenerator.domain;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

class DBConnection {
    private static ConnectionSource connectionSource;
    static {
        // put the path to your local database
        String dbURL = "jdbc:sqlite:src/main/resources/data.db";
        try{
            connectionSource = new JdbcConnectionSource(dbURL);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    static ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
