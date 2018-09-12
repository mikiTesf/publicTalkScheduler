package com.publictalkgenerator;

import com.publictalkgenerator.controller.ProgramGenerator;
import com.publictalkgenerator.view.GeneratorUI;

import java.sql.SQLException;
import java.time.LocalDate;

public class App {

    public static void main(String[] args) {
//        GeneratorUI UI = new GeneratorUI();
//        UI.constructUI();

        LocalDate startDate = LocalDate.now();

        ProgramGenerator programGenerator = null;

        try {
            programGenerator = new ProgramGenerator(startDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            programGenerator.doGenerate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
