package com.publictalkgenerator.domain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
public class InstructionMessage {
    @DatabaseField (generatedId = true, canBeNull = false)
    private int id;
    @DatabaseField (canBeNull = false)
    private String message;

    public static Dao<InstructionMessage, Integer> messageIntegerDao;

    static {
        try {
            messageIntegerDao = DaoManager.createDao(DBConnection.getConnectionSource(), InstructionMessage.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), InstructionMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean save () {
        try {
            messageIntegerDao.createIfNotExists(this);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
