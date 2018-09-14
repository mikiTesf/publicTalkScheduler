package com.publictalkgenerator;

import com.publictalkgenerator.view.GeneratorUI;

import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException {
        GeneratorUI UI = new GeneratorUI();
        UI.constructUI();
    }
}
