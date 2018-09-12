package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;

@DatabaseTable
public class Program {

    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField (canBeNull = false, dataType = DataType.DATE_STRING)
    private Date date;

    @DatabaseField (foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Congregation congregation;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Elder elder;

    @DatabaseField
    private boolean isFree;

    private static Dao<Program, String> programDao;

    static {
        try {
            programDao = DaoManager.createDao(DBConnection.getConnectionSource(), Program.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Program.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Program () {}

    public Program(Date date, Congregation congregation, boolean isFree){
        this.date = date;
        this.congregation = congregation;
        this.isFree = isFree;
    }

    public Program(Date date, Congregation congregation, Elder elder) {
        this.date = date;
        this.congregation = congregation;
        this.elder = elder;
    }

    public Date getDate() {
        return date;
    }

    public Congregation getCongregation() {
        return congregation;
    }

    public Elder getElder() {
        return elder;
    }


    public void save() {
        try {
            programDao.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public static Dao<Program, String> getProgramDao () {
        return programDao;
    }
}
