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

    private static Dao<Talk, Integer> talkDaoDisk;
    private static Dao<Talk, Integer> talkDaoMem;

    static {
        try {
            talkDaoDisk = DaoManager.createDao(DBConnection.getConnectionSourceDisk(), Talk.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSourceDisk(), Talk.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Talk() {}

    private Talk(int talkNumber){
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

    public static Dao<Talk, Integer> getTalkDaoDisk() {
        return talkDaoDisk;
    }

    public static Dao<Talk, Integer> getTalkDaoMem() {
        return talkDaoMem;
    }

    static void setTalkDaoMem(Dao<Talk, Integer> talkDaoMem) {
        Talk.talkDaoMem = talkDaoMem;
    }

    @Override
    public String toString(){
        if (title.isEmpty()){
            return Integer.toString(talkNumber);
        }
        return title;
    }

    public void save() {
        try {
            talkDaoDisk.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
