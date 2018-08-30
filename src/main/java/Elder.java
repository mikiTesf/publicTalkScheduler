import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@DatabaseTable
class Elder {
    @DatabaseField
    private String name;
    @DatabaseField (generatedId = true, canBeNull = false)
    private String phoneNumber;
    @DatabaseField
    private int talkId;
    @DatabaseField
    private boolean hasTalkedHere;
    @DatabaseField
    private boolean been3WeeksSinceLastTalk;
    @DatabaseField
    private boolean validNoOfEldersLeft;

    private static Dao<Elder, String> elderDao;
    
    // this no-argument constructor is required by ORMLite
    Elder () { }

    // DAO construction
    static {
        try {
            elderDao = DaoManager.createDao(DBConnection.getConnectionSource(), Elder.class);
            TableUtils.createTableIfNotExists(DBConnection.getConnectionSource(), Elder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setTalkId(int talkId) {
        this.talkId = talkId;
    }

    public void setHasTalkedHere(boolean hasTalkedHere) {
        this.hasTalkedHere = hasTalkedHere;
    }

    public void setBeen3WeeksSinceLastTalk(boolean been3WeeksSinceLastTalk) {
        this.been3WeeksSinceLastTalk = been3WeeksSinceLastTalk;
    }

    public void setValidNoOfEldersLeft(boolean validNoOfEldersLeft) {
        this.validNoOfEldersLeft = validNoOfEldersLeft;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getTalkId() {
        return talkId;
    }

    public boolean isHasTalkedHere() {
        return hasTalkedHere;
    }

    public boolean isBeen3WeeksSinceLastTalk() {
        return been3WeeksSinceLastTalk;
    }

    public boolean isValidNoOfEldersLeft() {
        return validNoOfEldersLeft;
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
