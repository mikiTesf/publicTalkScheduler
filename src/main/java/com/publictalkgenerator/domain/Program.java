package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class Program {

    @DatabaseField (canBeNull = false)
    private int weekNumber;

    @DatabaseField (canBeNull = false)
    private Congregation congregation;

    @DatabaseField (canBeNull = false)
    private Elder elder;

    private static Dao<Program, String> programDao;

    static {
        try {
            programDao = DaoManager.createDao(DBConnection.getConnectionSource(), Program.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Elder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Program () {}

    public Program(int weekNumber, Congregation congregation, Elder elder) {
        this.weekNumber = weekNumber;
        this.congregation = congregation;
        this.elder = elder;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public Congregation getCongregation() {
        return congregation;
    }

    public Elder getElder() {
        return elder;
    }


    void save() {
        try {
            programDao.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Dao<Program, String> getProgramDao () {
        return programDao;
    }
}
