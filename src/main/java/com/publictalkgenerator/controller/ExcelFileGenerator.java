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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

public class ExcelFileGenerator {
    private XSSFWorkbook excelDoc;
    private ArrayList<Date> distinctDates;
    private List<Congregation> congregations;

    public ExcelFileGenerator() {
        excelDoc = new XSSFWorkbook();
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
            scheduleSheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            scheduleSheet.setMargin(Sheet.LeftMargin, 0.2);
            scheduleSheet.setMargin(Sheet.RightMargin, 0.2);
            // creating table-header row
            Row headerRow = scheduleSheet.createRow(0);
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

            //noinspection ConstantConditions
            int totalHorizontalCells = 5 + elderList.size();

            for (int i = 0 ; i < totalHorizontalCells; i++) {
                headerRow.createCell(i);
            }
            // writing the title of the first column (row 0) and merging the first 5 cells
            headerRow.getCell(headerRow.getFirstCellNum()).setCellValue(congregation.getName());
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            // writing the title of the second column (row 0) and merging the next n cells
            headerRow.getCell(5).setCellValue("ከጉባኤ የሚሄዱ");
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 5, totalHorizontalCells - 1));
            formatRow(headerRow);
            // give titles to the first 6 columns (cells) of the next row
            Row row2 = scheduleSheet.createRow(1);
            row2.createCell(0).setCellValue("ሳምንት");
            row2.createCell(1).setCellValue("ቀን");
            row2.createCell(2).setCellValue("ተናጋሪ");
            row2.createCell(3).setCellValue("የንግግር ቁ.");
            row2.createCell(4).setCellValue("የሞባይል ስ.ቁ.");
            // fill the next n cells with the names of the elders to be sent from this congregation
            for (int i = 0; i < elderList.size(); i++) {
                row2.createCell(5 + i).setCellValue(
                        elderList.get(i).getFirstName() + " " + elderList.get(i).getMiddleName() + "\n\r"
                                + "(ንግግር ቁ. " + elderList.get(i).getTalk().getTalkNumber() + ")"
                );
            }
            formatRow(row2);
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

                    //noinspection ConstantConditions
                    if (program.size() == 0) {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue("");
                    } else {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue(
                            program.get(0).getCongregation().getName()
                        );
                    }
                }

                formatRow(nonHeaderRow);
                ++nextRow;
                ++weekNumber;
            }
            // auto-size all columns
            for (int i = 0; i < totalHorizontalCells; i++) {
                scheduleSheet.autoSizeColumn(i);
            }
            scheduleSheet.setFitToPage(true);
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

    private XSSFCellStyle getCellStyle(
            boolean horizontalCenter,
            boolean boldText,
            boolean thickTopBorder,
            boolean thickBottomBorder,
            boolean thickRightBorder,
            boolean thickLeftBorder,
            boolean colored
    ) {
        XSSFCellStyle cellStyle = excelDoc.createCellStyle();

        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(horizontalCenter ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);

        if (boldText) {
            Font boldFont = excelDoc.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 10);
            cellStyle.setFont(boldFont);
        }
        if (thickTopBorder)     cellStyle.setBorderTop(BorderStyle.THICK);
        if (thickBottomBorder)  cellStyle.setBorderBottom(BorderStyle.THICK);
        if (thickLeftBorder)    cellStyle.setBorderLeft(BorderStyle.THICK);
        if (thickRightBorder)   cellStyle.setBorderRight(BorderStyle.THICK);
        if (colored) {
            cellStyle.setFillBackgroundColor(new XSSFColor(new java.awt.Color(190, 190, 190)));
            cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(190, 190, 190)));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return cellStyle;
    }

    private void formatRow(Row row) {
        final int HEADER_ROW = 0;
        final int SECOND_ROW = 1;
        final int LAST_ROW   = 58;
        
        switch (row.getRowNum()) {
            case HEADER_ROW:
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    row.getCell(i).setCellStyle(getCellStyle(true, true, true, false, true, true, true));
                }
                // the border between the two header cells must be normal
                row.getCell(4).getCellStyle().setBorderRight(BorderStyle.THIN);
                row.getCell(5).getCellStyle().setBorderLeft(BorderStyle.THIN);
                break;
            case SECOND_ROW:
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    // the first cell
                    if (i == 0) {
                        row.getCell(i).setCellStyle(getCellStyle(true, true, false, false, false, true, true));
                    } else if (i == row.getLastCellNum() - 1) { // the last cell
                        row.getCell(i).setCellStyle(getCellStyle(true, true, false, false, true, false, true));
                    } else {
                        // the cells in the middle are only bold, centered and have bottom borders
                        row.getCell(i).setCellStyle(getCellStyle(true, true, false, false, false, false, true));
                    }
                    row.getCell(i).getCellStyle().setBorderBottom(BorderStyle.MEDIUM);

                }
                break;
            case LAST_ROW:
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    if (i == 0) {
                        row.getCell(i).setCellStyle(getCellStyle(true, false, false, true, false, true, false));
                    } else if (i == 1 || i == 2 || i == 4) {
                        row.getCell(i).setCellStyle(getCellStyle(false, false, false ,true ,false ,false, false));
                    } else if (i == row.getLastCellNum() - 1) {
                        row.getCell(i).setCellStyle(getCellStyle(true, false, false, true, true, false, false));
                    } else {
                        row.getCell(i).setCellStyle(getCellStyle(true, false, false, true, false, false, false));
                    }
                }
                break;
            default:
                // if the value in the cell is "1" it means that it is a new-week row
                if (row.getCell(row.getFirstCellNum()).getNumericCellValue() == 1) {
                    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                        if (i == 0) {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, false, true, true));
                        } else if (i == 1 || i == 2 || i == 4) {
                            row.getCell(i).setCellStyle(getCellStyle(false, false, false ,false ,false ,false, true));
                        } else if (i == row.getLastCellNum() - 1) {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, true, false, true));
                        } else {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, false, false, true));
                        }
                        row.getCell(i).getCellStyle().setBorderBottom(BorderStyle.MEDIUM);
                    }
                } else { // else it is a normal row
                    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                        if (i == 0) {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, false, true, false));
                        } else if (i == 1 || i == 2 || i == 4) {
                            row.getCell(i).setCellStyle(getCellStyle(false, false, false ,false ,false ,false, false));
                        } else if (i == row.getLastCellNum() - 1) {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, true, false, false));
                        } else {
                            row.getCell(i).setCellStyle(getCellStyle(true, false, false, false, false, false, false));
                        }
                    }
                }
                break;
        }
    }

    public static void main(String[] args) {
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator();
        excelFileGenerator.createExcel();
    }
}
