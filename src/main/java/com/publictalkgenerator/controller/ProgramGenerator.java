package com.publictalkgenerator.controller;

import com.publictalkgenerator.Constants;
import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ProgramGenerator {

    private LocalDate startDate;
    private LocalDate endDate;
    private List <Elder> allElders;
    private List<Congregation> allCongregations;
    private List<LocalDate> programDates;
    float totalFreeWeeksForCongregation;


    public ProgramGenerator(LocalDate startDate) throws SQLException {
        this.startDate   = startDate;
        this.endDate     = startDate.plusYears(1);
        allElders        = new UnmodifiableList<>(Elder.getElderDao().queryForAll());
        allCongregations = new UnmodifiableList<>(Congregation.getCongregationDao().queryForAll());
        generateProgramDates();
        totalFreeWeeksForCongregation = programDates.size() - allElders.size();
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
        Date programDate = ProgramDate.localDateToDate(week);

        for (Congregation congregation : congregations){

            Program program;

            if (weekShouldBeFree(week, congregation)){

                program = new Program(programDate, congregation, true);
            }
            else {

                Map<Elder, Double> eldersRank = getEldersRank(week, congregation);
                List<Elder> rankingElders = new ArrayList<>();
                Double maxRank = Collections.max(eldersRank.values());

                for (Elder key : eldersRank.keySet()) {

                    if (eldersRank.get(key).equals(maxRank)) {
                        rankingElders.add(key);
                    }
                }

                // if there are no candidate elders or the maxRank is zero, not fit, just make it free.
                if (rankingElders.size() == 0 ){
                    program = new Program(programDate, congregation, true);
                }
                else {

                    Elder elder = rankingElders.get(0);
                    program = new Program(programDate, congregation, elder);
                }
            }
            program.save();
            programsForWeek.add(program);
        }
        return programsForWeek;
    }

    private boolean weekShouldBeFree(LocalDate week, Congregation congregation) throws SQLException {

        double currentWeekNumber = distanceBetweenTwoDates(startDate, week);
        double expectedNumberOfFrees = (totalFreeWeeksForCongregation * currentWeekNumber) / programDates.size();
        double actualNumberOfFrees = getNumberOfFreesForCongregation(congregation);

        if (actualNumberOfFrees < expectedNumberOfFrees){

            // prevent too many free congregations on the same week
            return percentageOfFreeCongregationsInAWeek(week) < Constants.PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK;
        }
        return false;
    }

    private double percentageOfFreeCongregationsInAWeek(LocalDate week) throws SQLException {
        Date programDate = ProgramDate.localDateToDate(week);
        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("date", programDate).and().eq("isFree", true).query();
        return ((double) programs.size()) / ((double) allCongregations.size());
    }

    private double getNumberOfFreesForCongregation(Congregation congregation) throws SQLException {

        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("isFree", true).and().eq("congregation_id", congregation).query();
        return programs.size();
    }

    private List<Elder> getEldersWhoDidntGiveTalkInACongregation (Congregation congregation) throws SQLException {

        List <Elder> elders = new ArrayList<>();
        List <Elder> allElders = Elder.getElderDao().queryForAll();

        try {

            List<Program> programsForCongregation = Program.getProgramDao().queryBuilder()
                    .where().eq("congregation_id", congregation).query();
            List<Elder> eldersWhoGaveTalkInThisCongregation = new ArrayList<>();

            for (Program program : programsForCongregation){

                Elder.getElderDao().refresh(program.getElder());
                eldersWhoGaveTalkInThisCongregation.add(program.getElder());
            }

            allElders.removeAll(eldersWhoGaveTalkInThisCongregation);
            elders = allElders;
        }
        catch (SQLException e) {

            e.printStackTrace();
        }
        return elders;
    }

    private Map<Elder,Double> getEldersRank(LocalDate week, Congregation congregation) throws SQLException {

        Map <Elder, Double> eldersRank = new HashMap<>();
        List<Elder> viableElders = getEldersWhoDidntGiveTalkInACongregation(congregation);
        viableElders = removeEldersWhoGaveTalkIn_N_Weeks (viableElders, Constants.MINIMUM_FREE_WEEKS);
        viableElders = removeEldersWithLeftEldersInCongregationBelowMinimum (viableElders, Constants.MINIMUM_ELDERS_LEFT_IN_CONG);

        for (Elder elder : viableElders){

            double elderRank  = calculateElderRank (week, elder, congregation);
            eldersRank.put(elder, elderRank);
        }
        return eldersRank;
    }

    // TODO implement removeEldersWithLeftEldersInCongregationBelowMinimum --> not needed for now but maybe it will improve program quality
    private List<Elder> removeEldersWithLeftEldersInCongregationBelowMinimum(List<Elder> viableElders, int minimumEldersLeftInCong) {
        return viableElders;
    }

    // TODO implement removeEldersWhoGaveTalkIn_N_Weeks --> not needed for now but maybe it will improve program quality
    private List<Elder> removeEldersWhoGaveTalkIn_N_Weeks(List<Elder> eldersWhoDidntGiveTalkInThisCongregation, int minimumFreeWeeks) {
        return eldersWhoDidntGiveTalkInThisCongregation;
    }

    private double calculateElderRank(LocalDate week, Elder elder, Congregation congregation) throws SQLException {

        // TODO make sure no two elders get same ranking as much as possible
        double rank = (
                    distanceFromLastTalk(elder, week) * elderRepeatingInCongregationFactor(elder, congregation) * eldersRemainingInCongregation (week, elder) ) /
                    ((totalTalksGivenByElder(elder) + 1) * totalEldersInTheElderCongregation(elder)
                );
        return rank;
    }

    private double totalEldersInTheElderCongregation(Elder elder) throws SQLException {

        Congregation.getCongregationDao().refresh(elder.getCongregation());
        List<Elder> elders = Elder.getElderDao().queryBuilder()
                .where().eq("congregation_id", elder.getCongregation()).query();
        return elders.size();
    }

    private double totalTalksGivenByElder(Elder elder) throws SQLException {

        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("elder_id", elder).query();
        return programs.size();
    }

    private double eldersRemainingInCongregation(LocalDate week, Elder elder) throws SQLException {
        Date programDate = ProgramDate.localDateToDate(week);
        Congregation.getCongregationDao().refresh(elder.getCongregation());
        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("congregation_id", elder.getCongregation()).and().eq("date", programDate).query();

        return totalEldersInTheElderCongregation(elder) - programs.size();
    }

    private double elderRepeatingInCongregationFactor(Elder elder, Congregation congregation) throws SQLException {

        //Congregation.getCongregationDao().refresh(congregation);
        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("congregation_id", congregation).and().eq("elder_id", elder).query();
        if (programs.size() > 0){
            return 0;
        }
        return 1;
    }

    private double distanceBetweenTwoDates (LocalDate oldest, LocalDate latest){

        if (oldest.isBefore(latest)){

            return (double) (ChronoUnit.WEEKS.between(oldest, latest));
        }
        return 0;
    }

    private double distanceFromLastTalk(Elder elder, LocalDate week) throws SQLException {

        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("elder_id", elder).query();

        LocalDate lastTalkDate = startDate;
        if (programs.size() > 0){
            lastTalkDate = ProgramDate.dateToLocalDate(programs.get(programs.size() - 1).getDate());
        }

        return (double) ( ChronoUnit.WEEKS.between(lastTalkDate, week));
    }

    public void doGenerate () throws SQLException {

        for (LocalDate week : programDates){
            generateProgramForWeek(week, allCongregations);
        }
    }

    public List<Elder> getAllElders() {
        return allElders;
    }

    public List<Congregation> getAllCongregations() {
        return allCongregations;
    }

    public void generateProgramDates() {

        programDates = new ArrayList<>();
        // TODO check for backward selection of sunday for a year which starts in between a week...
        LocalDate sunday = startDate.with(DayOfWeek.SUNDAY);

        while (sunday.isBefore(endDate) || sunday.equals(endDate)) {

            programDates.add(sunday);
            sunday = sunday.plusWeeks(1);
        }
    }
}
