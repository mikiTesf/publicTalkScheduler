package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Objects;

@DatabaseTable
public class Congregation {
    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField (canBeNull = false)
    private int totalElders;

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

    public int getTotalElders() {
        return totalElders;
    }

    public void setTotalElders(int totalElders) {
        this.totalElders = totalElders;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Congregation cong = (Congregation) o;
        return id == cong.getId();
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
