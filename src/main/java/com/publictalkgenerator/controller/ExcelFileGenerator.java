package com.publictalkgenerator.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.publictalkgenerator.Constants;
import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
import com.publictalkgenerator.domain.Program;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileGenerator {
    private XSSFWorkbook excelDoc;
    private ArrayList<Date> distinctDates;
    private List<Congregation> congregations;
    private XSSFCellStyle centerStyle;
    private XSSFCellStyle centerBoldStyle;

    public ExcelFileGenerator() {
        excelDoc = new XSSFWorkbook();
        // cell styles to be used later
        // horizontally center only style
        centerStyle = excelDoc.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        // horizontally and vertically center and bold style
        centerBoldStyle = excelDoc.createCellStyle();
        centerBoldStyle.setAlignment(HorizontalAlignment.CENTER);
        centerBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font boldFont = excelDoc.createFont();
        boldFont.setBold(true);
        centerBoldStyle.setFont(boldFont);
        // A distinct list of Dates from the 'program' table
        distinctDates = new ArrayList<>();
        try {
            for (Program program : Program.getProgramDao().queryBuilder().distinct().selectColumns("date").query()) {
                distinctDates.add(program.getDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // List of all congregations to prepare a excelDoc for (that's all congregations)
        congregations = null;
        try {
            congregations = Congregation.getCongregationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createExcel () {
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
            row1.getCell(0).setCellStyle(centerBoldStyle);
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            // writing the title of the second column (row 0) and merging the next n cells
            row1.getCell(5).setCellValue("ከጉባኤ የሚሄዱ");
            row1.getCell(5).setCellStyle(centerBoldStyle);
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 5, totalHorizontalCells - 1));
            // give titles to the first 6 columns (cells) of the next row
            Row row2 = scheduleSheet.createRow(1);
            row2.createCell(0).setCellValue("ሳምንት");
            row2.getCell(0).setCellStyle(centerBoldStyle);
            row2.createCell(1).setCellValue("ቀን");
            row2.getCell(1).setCellStyle(centerBoldStyle);
            row2.createCell(2).setCellValue("ተናጋሪ");
            row2.getCell(2).setCellStyle(centerBoldStyle);
            row2.createCell(3).setCellValue("የንግግር ቁ.");
            row2.getCell(3).setCellStyle(centerBoldStyle);
            row2.createCell(4).setCellValue("የሞባይል ስ.ቁ.");
            row2.getCell(4).setCellStyle(centerBoldStyle);
            // fill the next n cells with the names of the elders to be sent from this congregation
            for (int i = 0; i < elderList.size(); i++) {
                row2.createCell(5 + i).setCellValue(
                        elderList.get(i).getFirstName() + " " + elderList.get(i).getMiddleName() + "\n"
                        + "(" + elderList.get(i).getTalk().getTalkNumber() + ")"
                );
                row2.getCell(5 + i).setCellStyle(centerBoldStyle);
            }
            // Iterate through each date and fill the details of the elders that come to this congregation
            int nextRow = 2;
            int weekNumber = 1;
            for (Date weekDate : distinctDates) {
                Calendar thisWeek = Calendar.getInstance();
                thisWeek.setTime(weekDate);
                Calendar lastWeek = Calendar.getInstance();
                lastWeek.setTime(weekDate);
                lastWeek.add(Calendar.WEEK_OF_MONTH, -1);
                // reset week count every time a month is changed
                if (thisWeek.get(Calendar.MONTH) != lastWeek.get(Calendar.MONTH))
                    weekNumber = 1;

                Row nonHeaderRow = scheduleSheet.createRow(nextRow);
                nonHeaderRow.createCell(0).setCellValue(weekNumber);
                nonHeaderRow.getCell(0).setCellStyle(centerStyle);
                nonHeaderRow.createCell(1).setCellValue(
                        Constants.monthName.get(thisWeek.get(Calendar.MONTH)) + " " +
                        thisWeek.get(Calendar.DAY_OF_MONTH) + " " +
                        thisWeek.get(Calendar.YEAR)
                );
                // check if the congregation has no elder assigned for it today
                Elder assignedToThisCongToday = null;
                try {
                    assignedToThisCongToday = Program.getProgramDao().queryBuilder().where()
                            .eq("date", weekDate)
                            .and()
                            .eq("congregation_id", congregation)
                            .query().get(0).getElder();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (assignedToThisCongToday == null) {
                    nonHeaderRow.createCell(2).setCellValue("");
                    nonHeaderRow.createCell(3).setCellValue("");
                    nonHeaderRow.createCell(4).setCellValue("");
                } else {
                    nonHeaderRow.createCell(2).setCellValue(assignedToThisCongToday.getFirstName() + " " + assignedToThisCongToday.getMiddleName());
                    // fill the elders talk number
                    nonHeaderRow.createCell(3).setCellValue(assignedToThisCongToday.getTalk().getTalkNumber());
                    nonHeaderRow.getCell(3).setCellStyle(centerStyle);
                    // fill the elder's phone number
                    nonHeaderRow.createCell(4).setCellValue(assignedToThisCongToday.getPhoneNumber());
                }

                for (Elder elder : elderList) {
                    List<Program> program = null;
                    try {
                        program = Program.getProgramDao().queryBuilder().where()
                                .eq("date", weekDate)
                                .and()
                                .eq("elder_id", elder).query();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (program.size() == 0) {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue("");
                    } else {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue(
                                        program.get(0).getCongregation().getName()
                                );
                        nonHeaderRow.getCell(nonHeaderRow.getLastCellNum() - 1).setCellStyle(centerStyle);
                    }
                }
                ++nextRow;
                ++weekNumber;
            }
            // auto-size all columns
            for (int i = 0; i < totalHorizontalCells; i++) {
                scheduleSheet.autoSizeColumn(i);
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(new File("schedule" + ".xlsx"));
            excelDoc.write(out);
            System.out.println("excel file generated...");
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator();
        excelFileGenerator.createExcel();
    }
}
