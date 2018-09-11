package com.publictalkgenerator.controller;

import com.publictalkgenerator.Constants;
import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;
import org.apache.commons.collections4.list.UnmodifiableList;

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
    private List <Elder> allElders;
    private  List<Congregation> allCongregations;


    public ProgramGenerator(LocalDate startDate) throws SQLException {
        this.startDate = startDate;
        this.endDate = startDate.plusDays(364);
        allElders = new UnmodifiableList<>(Elder.getElderDao().queryForAll());
        allCongregations =  new UnmodifiableList<>(Congregation.getCongregationDao().queryForAll());
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
            programsForWeek.add(program);
        }
        return programsForWeek;
    }

    private List<Elder> getEldersWhoDidntGiveTalkInACongregation (Congregation congregation){

        List <Elder> elders = new ArrayList<>();
        try {
            List<Elder> eldersWhoGaveTalkInThisCongregation = new ArrayList<>();
            Program.getProgramDao().queryBuilder()
                    .where().eq("congregation", congregation).query().forEach(item ->
                    eldersWhoGaveTalkInThisCongregation.add(item.getElder())
            );

            allElders.removeAll(eldersWhoGaveTalkInThisCongregation);

            elders = allElders;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elders;
    }

    private Map<Elder,Double> getEldersRank(Congregation congregation) {

        Map <Elder, Double> eldersRank = new HashMap<>();

        List<Elder> viableElders = getEldersWhoDidntGiveTalkInACongregation(congregation);
        viableElders = removeEldersWhoGaveTalkIn_N_Weeks (viableElders, Constants.MINIMUM_FREE_WEEKS);
        viableElders = removeEldersWithLeftEldersInCongregationBelowMinimum (viableElders, Constants.MINIMUM_ELDERS_LEFT_IN_CONG);
        for (Elder elder : viableElders){

            double elderRank = calculateElderRank (elder, congregation);
            eldersRank.put(elder, elderRank);
        }
        return eldersRank;
    }

    // TODO implement removeEldersWithLeftEldersInCongregationBelowMinimum
    private List<Elder> removeEldersWithLeftEldersInCongregationBelowMinimum(List<Elder> viableElders, int minimumEldersLeftInCong) {
        return viableElders;
    }

    // TODO implement removeEldersWhoGaveTalkIn_N_Weeks
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

        for (LocalDate week : programDates){
            generateProgramForWeek(week, allCongregations);
        }
    }

    public List<LocalDate> getProgramDates() {

        List<LocalDate> programDates = new ArrayList<>();
        // TODO check for backward selection of sunday for a year which starts in between a week...
        LocalDate sunday = startDate.with(DayOfWeek.SUNDAY);

        while (sunday.isBefore(endDate) || sunday.equals(endDate)){

            programDates.add(sunday);
            sunday.plusWeeks(1);
        }

        return programDates;
    }
}
