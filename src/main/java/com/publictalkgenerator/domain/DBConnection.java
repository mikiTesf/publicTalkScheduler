package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DBConnection {
    private static ConnectionSource connectionSourceDisk;
    private static ConnectionSource connectionSourceMem;

    static {

        String dbURLDisk = "jdbc:sqlite:src/main/resources/database/data.db";
        String dbURLMem  = "jdbc:sqlite::memory:";

        try {
            connectionSourceDisk = new JdbcConnectionSource(dbURLDisk);
            connectionSourceMem  = new JdbcConnectionSource(dbURLMem);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        try {
            Congregation.setCongregationDaoMem(DaoManager.createDao(connectionSourceMem, Congregation.class));
            Elder.setElderDaoMem(DaoManager.createDao(connectionSourceMem, Elder.class));
            Talk.setTalkDaoMem(DaoManager.createDao(connectionSourceMem, Talk.class));
            Program.setProgramDaoMem(DaoManager.createDao(connectionSourceMem, Program.class));

            TableUtils.createTable(connectionSourceMem, Congregation.class);
            TableUtils.createTable(connectionSourceMem, Elder.class);
            TableUtils.createTable(connectionSourceMem, Talk.class);
            TableUtils.createTable(connectionSourceMem, Program.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionSource getConnectionSourceDisk() {
        return connectionSourceDisk;
    }

//    public static ConnectionSource getConnectionSourceMem() {
//        return connectionSourceMem;
//    }
}
