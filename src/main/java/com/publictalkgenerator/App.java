package com.publictalkgenerator;

import com.publictalkgenerator.view.ConsoleView;
import com.publictalkgenerator.view.GeneratorUI;

import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException {
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        GeneratorUI UI = new GeneratorUI();
        UI.constructUI();

        /*ConsoleView view = new ConsoleView();
        view.showProgram();*/
    }
}
