package com.publictalkgenerator.domain;

/**
 * created by nati
 * sep 1 2018
 * **/

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class Congregation {

    @DatabaseField
    private String name;

    private static Dao<Congregation, String> congregationDao;

    static {
        try {
            congregationDao = DaoManager.createDao(DBConnection.getConnectionSource(), Congregation.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Elder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Congregation (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }


    void save() {
        try {
            congregationDao.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Dao<Congregation, String> getCongregationDao () {
        return congregationDao;
    }
}
