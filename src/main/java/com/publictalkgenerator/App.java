package com.publictalkgenerator;

import com.publictalkgenerator.view.GeneratorUI;

import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        GeneratorUI UI = new GeneratorUI();
        UI.constructUI();
    }
}
