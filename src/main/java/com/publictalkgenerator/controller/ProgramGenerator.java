package com.publictalkgenerator.controller;

import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * **/

public class ProgramGenerator {

    private LocalDate startDate;
    private LocalDate endDate;

    private static final int MINIMUM_FREE_WEEKS = 3;

    public ProgramGenerator(LocalDate startDate) {
        this.startDate = startDate;
        this.endDate = startDate.plusDays(364);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    private List<Program> generateProgramForWeek(LocalDate week, List<Congregation> congregations) throws SQLException {

        List<Program> programsForWeek = new ArrayList<>();

        for (Congregation congregation : congregations){

            Map<Elder, Double> eldersRank = getEldersRank (congregation);

            List<Elder> rankingElders = new ArrayList<>();
            Double maxRank = Collections.max(eldersRank.values());

            for (Elder key : eldersRank.keySet()){

                if (eldersRank.get(key).equals(maxRank)){
                    rankingElders.add(key);
                }
            }

            // TODO find more ways to rank equally ranking elders...
            // TODO check if empty set...
            Elder elder = rankingElders.get(0);
            Program program = new Program(week, congregation, elder);
            program.save();
        }
        return null;
    }

    private List<Elder> getEldersWhoDidntGiveTalkInACongregation (Congregation congregation){

        try {
            List<Elder> eldersWhoGaveTalkInThisCongregation = new ArrayList<>();
            Program.getProgramDao().queryBuilder()
                    .where().eq("congregation", congregation).query().forEach(item ->
                    eldersWhoGaveTalkInThisCongregation.add(item.getElder())
            );
            List <Elder> allElders = Elder.getElderDao().queryForAll();

            allElders.removeAll(eldersWhoGaveTalkInThisCongregation);

            return allElders;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<Elder,Double> getEldersRank(Congregation congregation) {

        Map <Elder, Double> eldersRank = new HashMap<>();

        List<Elder> viableElders = getEldersWhoDidntGiveTalkInACongregation(congregation);
        viableElders = removeEldersWhoGaveTalkIn_N_Weeks (viableElders, MINIMUM_FREE_WEEKS);
        viableElders = removeEldersWithLeftEldersInCongregationBelowMinimum (viableElders);
        for (Elder elder : viableElders){

            double elderRank = calculateElderRank (elder, congregation);
            eldersRank.put(elder, elderRank);
        }
        return eldersRank;
    }

    // TODO implement
    private List<Elder> removeEldersWithLeftEldersInCongregationBelowMinimum(List<Elder> viableElders) {
        return viableElders;
    }

    // TODO implement
    private List<Elder> removeEldersWhoGaveTalkIn_N_Weeks(List<Elder> eldersWhoDidntGiveTalkInThisCongregation, int minimumFreeWeeks) {
        return eldersWhoDidntGiveTalkInThisCongregation;
    }

    private double calculateElderRank(Elder elder, Congregation congregation) {

        double rank = 0;
        // TODO implement
        return rank;
    }


    void doGenerate () throws SQLException {

        List<LocalDate> programDates = getProgramDates();
        List<Congregation> allCongregations = new ArrayList<>();
        try {
            allCongregations = Congregation.getCongregationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (LocalDate week : programDates){
            generateProgramForWeek(week, allCongregations);
        }
    }

    public List<LocalDate> getProgramDates() {

        List<LocalDate> programDates = new ArrayList<>();
        // TODO check for backward selection of sunday for a year which starts in between a week...
        LocalDate sundays = startDate.with(DayOfWeek.SUNDAY);

        while (sundays.isBefore(endDate) || sundays.equals(endDate)){

            programDates.add(sundays);
            sundays.plusWeeks(1);
        }

        return programDates;
    }
}
