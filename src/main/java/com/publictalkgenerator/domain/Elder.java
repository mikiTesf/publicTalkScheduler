package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class Elder {
    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;
    @DatabaseField (canBeNull = false)
    private String firstName;
    @DatabaseField (canBeNull = false)
    private String middleName;
    @DatabaseField
    private String lastName;
    @DatabaseField (canBeNull = false)
    private String phoneNumber;
    @DatabaseField (foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Talk talk;
    @DatabaseField (foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Congregation congregation;

    private static Dao<Elder, String> elderDao;
    
    // this no-argument constructor is required by ORMLite
    public Elder () { }

    // DAO initialization
    static {
        try {
            elderDao = DaoManager.createDao(DBConnection.getConnectionSource(), Elder.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Elder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() { return this.id; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Talk getTalk() {
        return talk;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Congregation getCongregation() {
        return congregation;
    }

    public void setCongregation(Congregation congregation) {
        this.congregation = congregation;
    }

    @Override
    public String toString() {
        return firstName + " " + middleName + " " + lastName;
    }

    void save() {
        try {
            elderDao.createIfNotExists(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Dao<Elder, String> getElderDao () {
        return elderDao;
    }

}
