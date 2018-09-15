package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class Talk {
    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField (canBeNull = false)
    private int talkNumber;

    @DatabaseField
    private String title;

    private static Dao<Talk, Integer> talkDao;

    static {
        try {
            talkDao = DaoManager.createDao(DBConnection.getConnectionSource(), Talk.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Talk.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Talk() {}

    Talk (int talkNumber){
        setTalkNumber(talkNumber);
    }

    public Talk(String title, int talkNumber){
        this(talkNumber);
        setTitle(title);
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTalkNumber() {
        return talkNumber;
    }

    public void setTalkNumber(int talkNumber) {
        this.talkNumber = talkNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        if (title.isEmpty()){
            return Integer.toString(talkNumber);
        }
        return title;
    }

    void save() {
        try {
            talkDao.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Dao<Talk, Integer> getTalkDao () {
        return talkDao;
    }
}
