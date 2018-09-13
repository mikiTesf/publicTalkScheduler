package com.publictalkgenerator;

import com.j256.ormlite.logger.LocalLog;
import com.publictalkgenerator.controller.ExcelFileGenerator;
import com.publictalkgenerator.controller.ProgramGenerator;
import com.publictalkgenerator.view.ConsoleView;

import java.sql.SQLException;
import java.time.LocalDate;

public class App {

    public static void main(String[] args) throws SQLException {
        LocalDate startDate = LocalDate.now();

        ProgramGenerator generator = new ProgramGenerator(startDate);
        generator.doGenerate();

        ExcelFileGenerator fileGenerator = new ExcelFileGenerator();
        fileGenerator.createExcel();
    }
}
