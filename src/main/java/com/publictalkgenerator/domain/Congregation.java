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

    private static Dao<Congregation, Integer> congregationDaoDisk;
    private static Dao<Congregation, Integer> congregationDaoMem;

    public Congregation() {}

    static {
        try {
            congregationDaoDisk   = DaoManager.createDao(DBConnection.getConnectionSourceDisk(), Congregation.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSourceDisk(), Congregation.class);
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

    public static Dao<Congregation, Integer> getCongregationDaoDisk() {
        return congregationDaoDisk;
    }

    public static Dao<Congregation, Integer> getCongregationDaoMem() {
        return congregationDaoMem;
    }

    static void setCongregationDaoMem(Dao<Congregation, Integer> congregationDaoMem) {
        Congregation.congregationDaoMem = congregationDaoMem;
    }

    @Override
    public String toString(){
        return name;
    }


    public void save() {
        try {
            congregationDaoDisk.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
