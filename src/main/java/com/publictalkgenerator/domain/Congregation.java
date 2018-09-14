package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class Congregation {
    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;
    @DatabaseField
    private String name;

    private static Dao<Congregation, Integer> congregationDao;

    public Congregation() {}

    static {
        try {
            congregationDao = DaoManager.createDao(DBConnection.getConnectionSource(), Congregation.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Congregation.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setId (int id) {
        this.id = id;
    }

    public Congregation (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
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

    public static Dao<Congregation, Integer> getCongregationDao () {
        return congregationDao;
    }
}
