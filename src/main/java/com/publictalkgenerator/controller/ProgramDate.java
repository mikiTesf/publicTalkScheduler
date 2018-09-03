package com.publictalkgenerator.controller;

import java.util.Date;

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
