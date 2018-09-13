package com.publictalkgenerator;

import com.j256.ormlite.logger.LocalLog;
import com.publictalkgenerator.controller.ProgramGenerator;
import com.publictalkgenerator.view.ConsoleView;
import com.publictalkgenerator.view.GeneratorUI;

import java.io.Console;
import java.sql.SQLException;
import java.time.LocalDate;

public class App {

    public static void main(String[] args) {
//        GeneratorUI UI = new GeneratorUI();
//        UI.constructUI();

        LocalDate startDate = LocalDate.now();

        ProgramGenerator programGenerator = null;

//        try {
//            programGenerator = new ProgramGenerator(startDate);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            programGenerator.doGenerate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

        try {
            ConsoleView view = new ConsoleView();
            view.showProgram();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
