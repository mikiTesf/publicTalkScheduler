package com.publictalkgenerator.controller;

import java.util.Date;

/**
 * created by nati
 * sep 1 2018
 * **/

public class ProgramDate {

    private Date startDate;

    ProgramDate (Date startDate){
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getDateAfterWeeks (int weekOffset){
        //TODO implement
        return null;
    }

    public String getWeekLocalName (int weekOffset){
        //TODO implement
        return null;
    }
}
