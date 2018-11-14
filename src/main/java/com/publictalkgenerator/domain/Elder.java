package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Objects;

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
    @DatabaseField (canBeNull = false, defaultValue = "true")
    private boolean enabled;

    private static Dao<Elder, Integer> elderDaoDisk;
    private static Dao<Elder, Integer> elderDaoMem;
    
    // this no-argument constructor is required by ORMLite
    public Elder () { }

    // DAO initialization
    static {
        try {
            elderDaoDisk = DaoManager.createDao(DBConnection.getConnectionSourceDisk(), Elder.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSourceDisk(), Elder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setId (int id) {
        this.id = id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static Dao<Elder, Integer> getElderDaoDisk() {
        return elderDaoDisk;
    }

    public static Dao<Elder, Integer> getElderDaoMem() {
        return elderDaoMem;
    }

    static void setElderDaoMem(Dao<Elder, Integer> elderDaoMem) {
        Elder.elderDaoMem = elderDaoMem;
    }

    @Override
    public String toString() {
        return firstName + " " + middleName + " " + lastName;
    }

    public void save() {
        try {
            elderDaoDisk.createIfNotExists(this);
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
        Elder elder = (Elder) o;
        return id == elder.getId();
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, toString());
    }

}
