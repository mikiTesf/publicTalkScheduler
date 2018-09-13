package com.publictalkgenerator.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileGenerator {
    private XSSFWorkbook excelDoc;

    public ExcelFileGenerator() {
        excelDoc = new XSSFWorkbook();
        // cell styles to be used later
        XSSFCellStyle centerStyle = excelDoc.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        // List of all congregations to prepare a excelDoc for (that's all congregations)
        List<Congregation> congregations = null;
        try {
            congregations = Congregation.getCongregationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // the main loop that populates the sheet for each congregation
        for (Congregation congregation : congregations) {
            // create a sheet named after the congregation
            XSSFSheet scheduleSheet = excelDoc.createSheet(congregation.getName());
            // creating table-header row
            Row row1 = scheduleSheet.createRow(0);
            /* the first row has two columns. One has the name of the congregation to which
             the program belongs and the second has the text "ከጉባኤ የሚሄዱ". The first column
             spans 6 cells. The span of the second column depends on the number of elders
             being sent from the congregation. To make merging the cells "ከጉባኤ የሚሄዱ" is written on
             easy, the number of elders that are going from that congregation must first be fetched
             from the database. */
            List<Elder> elderList = null;
            try {
                elderList = Elder.getElderDao()
                        .queryBuilder()
                        .where()
                        .eq("congregation_id", congregation)
                        .query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int totalHorizontalCells = 5 + elderList.size();

            for (int i = 0 ; i < totalHorizontalCells; i++) {
                row1.createCell(i);
            }
            // writing the title of the first column (row 0) and merging the first 5 cells
            row1.getCell(0).setCellValue(congregation.getName());
            row1.getCell(0).setCellStyle(centerStyle);
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            // give titles to the first 6 columns (cells) of the next row
            Row row2 = scheduleSheet.createRow(1);
            row2.createCell(0).setCellValue("ሳምንት");
            row2.createCell(1).setCellValue("ቀን");
            row2.createCell(2).setCellValue("ተናጋሪ");
            row2.createCell(3).setCellValue("የንግግር ቁጥር");
            row2.createCell(4).setCellValue("የሞባይል ስ.ቁ.");
            // writing the title of the second column (row 0) and merging the next n cells
            row1.getCell(5).setCellValue("ከጉባኤ የሚሄዱ");
            row1.getCell(5).setCellStyle(centerStyle);
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 5, totalHorizontalCells - 1));
            // fill the next n cells with the names of the elders to be sent from this congregation
            for (int i = 0; i < elderList.size(); i++) {
                row2.createCell(5 + i).setCellValue(
                        elderList.get(i).getFirstName() + " " + elderList.get(i).getMiddleName()
                );
            }
            /* TODO: Iterate through each date and fill the details of the elders that come to this congregation
               TODO: Iterate through each date and fill the names of the elders that go from this congregation
            * */
            // auto-size all columns
            for (int i = 0; i < totalHorizontalCells; i++) {
                scheduleSheet.autoSizeColumn(i);
            }
        }
    }

    public boolean createExcel () {
        /* TODO
        *
        * populate the excelDoc grid here
        *
        * */
        try {
            FileOutputStream out = new FileOutputStream(new File("/home/miki/Desktop/" + "sociopath" + ".xlsx"));
            excelDoc.write(out);
            System.out.println("excel file generated...");
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
