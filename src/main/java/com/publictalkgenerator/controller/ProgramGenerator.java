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
    private float totalFreeWeeksForCongregation;
    private Map<Congregation, Integer> totalEldersInCong;


    public ProgramGenerator(LocalDate startDate, LocalDate endDate) throws SQLException {
        this.startDate   = startDate;
        this.endDate     = endDate;
        allElders        = new UnmodifiableList<>(Elder.getElderDao().queryForAll());
        allCongregations = new UnmodifiableList<>(Congregation.getCongregationDao().queryForAll());
        totalEldersInCong = totalEldersInCongregation();
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



    private Map<Congregation, Integer> totalEldersInCongregation() throws SQLException {

        Map <Congregation, Integer> totalEldersInCong = new HashMap<>();

        for (Congregation c: allCongregations){
            int count = Elder.getElderDao().queryBuilder().where().eq("congregation_id", c).query().size();
            totalEldersInCong.put(c, count);
        }

        return totalEldersInCong;
    }

    private void generateProgramForWeek(LocalDate week, List<Congregation> congregations) throws SQLException {

        Date programDate = ProgramDate.localDateToDate(week);

        for (Congregation congregation : congregations){

            Program program;

            if (weekShouldBeFree(week, congregation)){

                program = new Program(programDate, congregation, true);
            }
            else {

                Map<Elder, Double> eldersRank = getEldersRank(week, congregation);
                List<Elder> rankingElders = new ArrayList<>();

                if (eldersRank.size() > 0){

                    Double maxRank = Collections.max(eldersRank.values());
                    for (Elder key : eldersRank.keySet()) {

                        if (eldersRank.get(key).equals(maxRank)) {
                            rankingElders.add(key);
                        }
                    }

                    Random rand = new Random();
                    Elder elder = rankingElders.get(rand.nextInt(rankingElders.size()));
                    program = new Program(programDate, congregation, elder);
                }
                else {
                    // if there are no candidate elders or the maxRank is zero, not fit, just make it free.
                    program = new Program(programDate, congregation, true);
                }
            }
            program.save();
        }
    }

    private boolean weekShouldBeFree(LocalDate week, Congregation congregation) throws SQLException {

        double currentWeekNumber = weeksBetweenTwoDates(startDate, week) + 1;
        double expectedNumberOfFrees = (totalFreeWeeksForCongregation * currentWeekNumber) / programDates.size();
        double actualNumberOfFrees = getNumberOfFreesForCongregation(congregation);

        if (actualNumberOfFrees < expectedNumberOfFrees && percentageOfFreeCongregationsInAWeek(week) <= Constants.PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK){

            // prevent too many free congregations on the same week
            //return percentageOfFreeCongregationsInAWeek(week) < Constants.PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK;
            return true;
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
        //List<Elder> viableElders = Elder.getElderDao().queryForAll();
        List<Elder> viableElders = getEldersWhoDidntGiveTalkInACongregation(congregation);
        viableElders = removeEldersWithLeftEldersInCongregationBelowMinimum (viableElders, week);
        viableElders = removeEldersWhoGaveTalkIn_N_Weeks (viableElders, week);

        for (Elder elder : viableElders){

            double elderRank  = calculateElderRank (week, elder, congregation);
            eldersRank.put(elder, elderRank);
        }
        return eldersRank;
    }

    private List<Elder> removeEldersWithLeftEldersInCongregationBelowMinimum(List<Elder> elders, LocalDate week) throws SQLException {

        List<Elder> viableElders = new ArrayList<>();

        for (Elder e : elders){

            if (eldersRemainingInCongregation(week, e) > Constants.MINIMUM_ELDERS_LEFT_IN_CONG){
                viableElders.add(e);
            }
        }
        return viableElders;
    }

    private List<Elder> removeEldersWhoGaveTalkIn_N_Weeks(List<Elder> elders, LocalDate week) throws SQLException {

        List<Elder> viableElders = new ArrayList<>();

        for (Elder e : elders){

            if (distanceFromLastTalk(e, week) >= Constants.MINIMUM_FREE_WEEKS){
                viableElders.add(e);
            }
        }
        return viableElders;
    }

    private double calculateElderRank(LocalDate week, Elder elder, Congregation congregation) throws SQLException {

        // TODO make sure no two elders get same ranking as much as possible
        double dist = distanceFromLastTalk(elder, week);
        //double rept = elderRepeatingInCongregationFactor(elder, congregation);
        double elrm = eldersRemainingInCongregation (week, elder);
        double totk = totalTalksGivenByElder(elder);
        double toel = totalEldersInTheElderCongregation(elder);

        return ( dist/10 - totk/allCongregations.size()) * (elrm - 1)/toel;
    }

    private double totalEldersInTheElderCongregation(Elder elder) throws SQLException {

        return totalEldersInCong.get(elder.getCongregation());
    }

    private double totalTalksGivenByElder(Elder elder) throws SQLException {

        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("elder_id", elder).query();
        return programs.size();
    }

    private double eldersRemainingInCongregation(LocalDate week, Elder elder) throws SQLException {
        Date programDate = ProgramDate.localDateToDate(week);
        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("date", programDate).and().eq("isFree",false).query();

        int count = 0;
        for (Program program : programs){
            if (program.getElder().getCongregation().equals(elder.getCongregation())) {
                count++;
            }
        }

        return totalEldersInTheElderCongregation(elder) - count;
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

    private double weeksBetweenTwoDates(LocalDate oldest, LocalDate latest){

        if (oldest.isBefore(latest)){

            return (double) (ChronoUnit.WEEKS.between(oldest, latest));
        }
        return 0;
    }

    private double distanceFromLastTalk(Elder elder, LocalDate week) throws SQLException {

        List<Program> programs = Program.getProgramDao().queryBuilder()
                .where().eq("elder_id", elder).query();

        List<LocalDate> progDates = new ArrayList<>();

        for (Program p: programs){
            progDates.add(ProgramDate.dateToLocalDate(p.getDate()));
        }

        LocalDate lastTalkDate = week.minusWeeks(allCongregations.size());
        if (progDates.size() > 0){
            lastTalkDate = Collections.max(progDates);
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
