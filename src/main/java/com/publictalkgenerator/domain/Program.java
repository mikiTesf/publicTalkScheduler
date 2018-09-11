package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.time.LocalDate;

@DatabaseTable
public class Program {

    @DatabaseField (canBeNull = false)
    private LocalDate date;

    @DatabaseField (canBeNull = false, foreign = true)
    private Congregation congregation;

    @DatabaseField (canBeNull = false, foreign = true)
    private Elder elder;

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

    public Program(LocalDate date, Congregation congregation, Elder elder) {
        this.date = date;
        this.congregation = congregation;
        this.elder = elder;
    }

    public LocalDate getDate() {
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

    public static Dao<Program, String> getProgramDao () {
        return programDao;
    }
}
