package com.publictalkgenerator.view;

import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.sql.SQLException;
import java.util.*;

public class ConsoleView {

    Set<Date> programDates;
    private List <Elder> allElders;
    private List<Congregation> allCongregations;
    static final int colWidth = 15;

    public ConsoleView() throws SQLException {
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        allElders        = new UnmodifiableList<>(Elder.getElderDao().queryForAll());
        allCongregations = new UnmodifiableList<>(Congregation.getCongregationDao().queryForAll());
    }

    private void setProgramDates () throws SQLException {

        List<Program> programsForCongregation = null;

        programsForCongregation = Program.getProgramDao().queryForAll();

        Set<Date> progDates = new TreeSet<>();

        for (Program program : programsForCongregation){

            progDates.add(program.getDate());
        }
        programDates = progDates;
    }

    public void showProgram() throws SQLException {

        List<String> names = new ArrayList<>();

        setProgramDates();

        for (Congregation congregation : allCongregations){
            names.add(congregation.getName());
        }

        drawTable(names);
        for (Date date : programDates){
            showProgramRow (date);
        }
    }

    private void showProgramRow(Date date) throws SQLException {

        List <Program> programsForWeek = Program.getProgramDao().queryBuilder()
                .where().eq("date", date).query();
        List<String> names = new ArrayList<>();

        for (Congregation congregation : allCongregations){

            for (Program p : programsForWeek){


                if (p.getCongregation().getId() == congregation.getId()){
                    if (p.isFree()){
                        names.add("");
                    }
                    else {

                        names.add(p.getElder().getFirstName());
                    }
                }
            }
        }

        drawTable (names);
    }

    private void drawTable(List<String> values) {

        for (int i = 0 ; i < values.size(); i++){

            System.out.print("+");
            for (int j = 0; j < colWidth; j++){
                System.out.print("-");
            }
        }
        System.out.print("\n");

        for (String s : values){
            System.out.print("|" + s );
            for (int j = s.length(); j < colWidth; j ++)
                System.out.print(" ");
        }
        System.out.print("|\n");
    }
}
