package com.publictalkgenerator.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileGenerator {
    private HashMap<Integer, String> AMMonths;
    private Program program;
    private XSSFWorkbook schedule;
    private XSSFSheet scheduleSheet;

    private ExcelFileGenerator (Program program2) {
        this.program = program2;

        AMMonths = new HashMap<>();
        AMMonths.put(1, "ጥር");
        AMMonths.put(2, "የካቲት");
        AMMonths.put(3, "መጋቢት");
        AMMonths.put(4, "ሚያዝያ");
        AMMonths.put(5, "ግንቦት");
        AMMonths.put(6, "ሰኔ");
        AMMonths.put(7, "ሐምሌ");
        AMMonths.put(8, "ነሐሴ");
        AMMonths.put(9, "መስከረም");
        AMMonths.put(10, "ጥቅምት");
        AMMonths.put(11, "ህዳር");
        AMMonths.put(12, "ታህሳሥ");
        // create the excel workbook
        schedule = new XSSFWorkbook();
        // create a sheet named after the congregation
        scheduleSheet = schedule.createSheet(program.getCongregation().getName());
        // creating table-header row
        Row row1 = scheduleSheet.createRow(0);
        /* the first row has two columns. One has the name of the congregation to which
         * the program belongs and the second has the text "ከጉባኤ የሚሄዱ". The first column
         * spans 6 cells. The span of the second column depends on the number of elders
         * being sent from the congregation. To make merging cells easy, the number of elders
         * that are going from that congregation must first be fetched from the database.
         */
        List<Elder> elderList = null;
        try {
            elderList = Elder.getElderDao()
                                           .queryBuilder()
                                           .where()
                                           .eq("congregation_id", program.getCongregation().getId())
                                           .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int totalHorizontalCells = 6 + elderList.size();

        for (int i = 0 ; i < totalHorizontalCells; i++) {
            row1.createCell(i);
        }
        // writing the title of the first column (row 0) and merging the first 6 cells
        row1.getCell(0).setCellValue(program.getCongregation().getName());
        scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        // writing the title of the second column (row 0) and merging the next n cells
        row1.getCell(6).setCellValue("ከጉባኤ የሚሄዱ");
        scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 6, totalHorizontalCells));
        // give titles to the first 6 columns (cells) of the next row
        Row row2 = scheduleSheet.createRow(1);
        row2.createCell(0).setCellValue("ሳምንት");
        row2.createCell(1).setCellValue("ቀን");
        row2.createCell(2).setCellValue("ተናጋሪ");
        row2.createCell(3).setCellValue("የንግግር ቁጥር");
        row2.createCell(4).setCellValue("የሞባይል ስ.ቁ.");
        row2.createCell(5).setCellValue("የቤት ስ.ቁ.");
        // fill the next next n cells with the names of the elders to be sent from this congregation
        for (int i = 0; i < elderList.size(); i++) {
            row2.createCell(6 + i).setCellValue(
                    elderList.get(i).getFirstName() + " " + elderList.get(i).getMiddleName()
            );
        }
    }

    boolean createExcel () {
        /* TODO
        *
        * populate the schedule grid here
        *
        * */
        try {
            FileOutputStream out = new FileOutputStream(new File("/home/miki/Desktop/" + program.getCongregation().getName() + ".xlsx"));
            schedule.write(out);
            System.out.println("excel file generated...");
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
