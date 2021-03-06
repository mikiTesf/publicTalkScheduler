package com.publictalkgenerator.controller;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.publictalkgenerator.Constants;
import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Elder;
//import com.publictalkgenerator.domain.InstructionMessage;
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
            for (Program program : Program.getProgramDaoMem().queryBuilder().distinct().selectColumns("date").orderBy("date", true).query()) {
                distinctDates.add(program.getDate());
            }
            // List of all congregations to prepare a excelDoc for (that's all congregations)
            congregations = Congregation.getCongregationDaoMem().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createExcel () {
        final int WEEK_NUMBER = 0, DATE = 1, SPEAKER = 2, TALK_NUMBER = 3, PHONE_NUMBER = 4;
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
                elderList = Elder.getElderDaoMem()
                        .queryBuilder()
                        .where()
                        .eq("congregation_id", congregation)
                        .and()
                        .eq("enabled", true)
                        .query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //noinspection ConstantConditions
            int totalColumnsInSheet = 5 + elderList.size();

            for (int column = 0 ; column < totalColumnsInSheet; column++) {
                headerRow.createCell(column);
            }
            // writing the title of the first column (row 0) and merging the first 5 cells
            headerRow.getCell(headerRow.getFirstCellNum()).setCellValue(congregation.getName());
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            // writing the title of the second column (row 0) and merging the next n cells
            headerRow.getCell(5).setCellValue("ከጉባኤ የሚሄዱ");
            scheduleSheet.addMergedRegion(new CellRangeAddress(0, 0, 5, totalColumnsInSheet - 1));
            formatRow(headerRow);
            // give titles to the first 6 columns (cells) of the next row
            Row row2 = scheduleSheet.createRow(1);
            row2.createCell(WEEK_NUMBER).setCellValue("ሳምንት");
            row2.createCell(DATE).setCellValue("ቀን");
            row2.createCell(SPEAKER).setCellValue("ተናጋሪ");
            row2.createCell(TALK_NUMBER).setCellValue("የንግግር ቁ.");
            row2.createCell(PHONE_NUMBER).setCellValue("የሞባይል ስ.ቁ.");
            // fill the next n cells with the names of the elders to be sent from this congregation
            for (int i = 0; i < elderList.size(); i++) {
                row2.createCell(5 + i).setCellValue(
                        elderList.get(i).getFirstName() + " " + elderList.get(i).getMiddleName() + "\n\r"
                        + "(ንግግር ቁ. " + elderList.get(i).getTalk().getTalkNumber() + ")"
                );
            }
            formatRow(row2);
            // Iterate through each date and fill the details of the elders that come to this congregation
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

                Row nonHeaderRow = scheduleSheet.createRow(scheduleSheet.getLastRowNum() + 1);

                nonHeaderRow.createCell(WEEK_NUMBER).setCellValue(weekNumber);
                nonHeaderRow.createCell(DATE).setCellValue(
                        Constants.monthName.get(thisWeek.get(Calendar.MONTH)) + " " +
                        thisWeek.get(Calendar.DAY_OF_MONTH) + " " +
                        thisWeek.get(Calendar.YEAR)
                );
                // check if the congregation has no elder assigned for it today
                Elder assignedToThisCongToday = null;
                try {
                    assignedToThisCongToday = Program.getProgramDaoMem().queryBuilder().where()
                            .eq("date", weekDate)
                            .and()
                            .eq("congregation_id", congregation)
                            .query().get(0).getElder();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (assignedToThisCongToday == null) {
                    nonHeaderRow.createCell(SPEAKER).setCellValue("");
                    nonHeaderRow.createCell(TALK_NUMBER).setCellValue("");
                    nonHeaderRow.createCell(PHONE_NUMBER).setCellValue("");
                } else {
                    nonHeaderRow.createCell(SPEAKER).setCellValue(assignedToThisCongToday.getFirstName() + " " + assignedToThisCongToday.getMiddleName());
                    nonHeaderRow.createCell(TALK_NUMBER).setCellValue(assignedToThisCongToday.getTalk().getTalkNumber());
                    nonHeaderRow.createCell(PHONE_NUMBER).setCellValue(assignedToThisCongToday.getPhoneNumber());
                }

                for (Elder elder : elderList) {
                    List<Program> program = null;
                    try {
                        program = Program.getProgramDaoMem().queryBuilder().where()
                                .eq("date", weekDate)
                                .and()
                                .eq("elder_id", elder).query();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //noinspection ConstantConditions
                    if (program.isEmpty()) {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue("");
                    } else {
                        nonHeaderRow.createCell(nonHeaderRow.getLastCellNum()).setCellValue(
                            program.get(0).getCongregation().getName()
                        );
                    }
                }

                formatRow(nonHeaderRow);
                ++weekNumber;
            }

            insertInstructionMessage(scheduleSheet);

            for (int column = 0; column < totalColumnsInSheet; column++) {
                scheduleSheet.autoSizeColumn(column);
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
        }
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
        if (thickTopBorder)    cellStyle.setBorderTop(BorderStyle.THICK);
        if (thickBottomBorder) cellStyle.setBorderBottom(BorderStyle.THICK);
        if (thickLeftBorder)   cellStyle.setBorderLeft(BorderStyle.THICK);
        if (thickRightBorder)  cellStyle.setBorderRight(BorderStyle.THICK);
        if (colored) {
            cellStyle.setFillBackgroundColor(new XSSFColor(new Color(190, 190, 190)));
            cellStyle.setFillForegroundColor(new XSSFColor(new Color(190, 190, 190)));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return cellStyle;
    }

    private void insertInstructionMessage (XSSFSheet sheet) {
        XSSFRichTextString richTextString = new XSSFRichTextString();
        XSSFFont font = excelDoc.createFont();

        font.setBold(true);
        font.setUnderline(Font.U_SINGLE);

        richTextString.setString("ለአስተባባሪው");
        richTextString.applyFont(font);
        sheet.createRow(sheet.getLastRowNum() + 2).createCell(0).setCellValue(richTextString);
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));

        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue("እባክህ ተጋባዥ ተናጋሪውን እጅግ ቢዘገይ እስከ ማክሰኞ ደውለህ አስታውሰው።");
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));
        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue("ይህን ፕሮግራም እንደደረሰህ እባክህ ከጉባኤህ ውስጥ ለተመደቡት ተናጋሪዎች ፎቶ ኮፒ አድርገህ መስጠትህን አትዘንጋ።");
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));

        richTextString.setString("ለተናጋሪዎች");
        richTextString.applyFont(font);
        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue(richTextString);
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));

        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue("1. ንግግሩ እንደደረሰህ ዝግጅትህን በማጠናቀቅ በፕሮግራምህ መሠረት ንግግርህን መስጠት ይጠበቅብሃል፡፡");
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));
        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue("2. ዝግጅት ስታደርግ ማስተካከያ የተደረገበትን ትምህርት ለመጠቀምህ እርግጠኛ ሁን።");
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));
        sheet.createRow(sheet.getLastRowNum() + 1).createCell(0).setCellValue("3. ንግግር በምትሰጥበት ቀን የተለየ ፕሮግራም ካለህ እባክህ አስቀድመህ ንግግር ለምታቀርብበት ጉባኤ አስተባባሪ ንገር።");
        sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 0, 7));
    }

    private void formatRow(Row row) {
        final int HEADER_ROW = 0;
        final int SECOND_ROW = 1;
        final int LAST_ROW   = 58;
        
        switch (row.getRowNum()) {
            case HEADER_ROW:
                for (int column = 0; column < row.getPhysicalNumberOfCells(); column++) {
                    row.getCell(column).setCellStyle(getCellStyle(true, true, true, false, true, true, true));
                }
                // the border between the two header cells must be normal
                row.getCell(4).getCellStyle().setBorderRight(BorderStyle.THIN);
                row.getCell(5).getCellStyle().setBorderLeft(BorderStyle.THIN);
                break;
            case SECOND_ROW:
                for (int column = 0; column < row.getPhysicalNumberOfCells(); column++) {
                    // the first cell
                    if (column == 0) {
                        row.getCell(column).setCellStyle(getCellStyle(true, true, false, false, false, true, true));
                    } else if (column == row.getLastCellNum() - 1) { // the last cell
                        row.getCell(column).setCellStyle(getCellStyle(true, true, false, false, true, false, true));
                    } else {
                        // the cells in the middle are only bold, centered and have bottom borders
                        row.getCell(column).setCellStyle(getCellStyle(true, true, false, false, false, false, true));
                    }
                    row.getCell(column).getCellStyle().setBorderBottom(BorderStyle.MEDIUM);

                }
                break;
            case LAST_ROW:
                for (int column = 0; column < row.getPhysicalNumberOfCells(); column++) {
                    if (column == 0) {
                        row.getCell(column).setCellStyle(getCellStyle(true, false, false, true, false, true, false));
                    } else if (column == 1 || column == 2 || column == 4) {
                        row.getCell(column).setCellStyle(getCellStyle(false, false, false ,true ,false ,false, false));
                    } else if (column == row.getLastCellNum() - 1) {
                        row.getCell(column).setCellStyle(getCellStyle(true, false, false, true, true, false, false));
                    } else {
                        row.getCell(column).setCellStyle(getCellStyle(true, false, false, true, false, false, false));
                    }
                }
                break;
            default:
                // if the value in the cell is "1" it means that it is a new-week row
                if (row.getCell(row.getFirstCellNum()).getNumericCellValue() == 1) {
                    for (int column = 0; column < row.getPhysicalNumberOfCells(); column++) {
                        if (column == 0) {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, false, true, true));
                        } else if (column == 1 || column == 2 || column == 4) {
                            row.getCell(column).setCellStyle(getCellStyle(false, false, false ,false ,false ,false, true));
                        } else if (column == row.getLastCellNum() - 1) {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, true, false, true));
                        } else {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, false, false, true));
                        }
                        row.getCell(column).getCellStyle().setBorderBottom(BorderStyle.MEDIUM);
                    }
                } else { // else it is a normal (non-header) row
                    for (int column = 0; column < row.getPhysicalNumberOfCells(); column++) {
                        if (column == 0) {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, false, true, false));
                        } else if (column == 1 || column == 2 || column == 4) {
                            row.getCell(column).setCellStyle(getCellStyle(false, false, false ,false ,false ,false, false));
                        } else if (column == row.getLastCellNum() - 1) {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, true, false, false));
                        } else {
                            row.getCell(column).setCellStyle(getCellStyle(true, false, false, false, false, false, false));
                        }
                    }
                }
                break;
        }
    }

    public int getNumberOfSheets () {
        return excelDoc.getNumberOfSheets();
    }

//    private void parseInstructionMessageAndPutInSheet (XSSFSheet sheet) {
//        InstructionMessage instructionMessage = null;
//        try {
//            /* TODO: change "queryForId(1)" in the next line
//            * You should make the user either choose from a previously used instruction
//            * or use the last saved instruction
//            * */
//            instructionMessage = InstructionMessage.messageIntegerDao.queryForId(1);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        String[] instructionLines = instructionMessage.getMessage().split("\n");
//        for (String instruction : instructionLines) {
//            XSSFRichTextString formattedInstruction = new XSSFRichTextString();
//            int boldStartIndex;
//            int boldEndIndex;
//            int italicStartIndex;
//            int italicEndIndex;
//            int underlineStartIndex;
//            int underlineEndIndex;
//
//            int firstOfAngleBrace = instruction.indexOf("[");
//            switch (instruction.substring(firstOfAngleBrace + 1, firstOfAngleBrace + 2)) {
//                case "b":
//                    boldStartIndex = firstOfAngleBrace + 3;
//                    instruction = instruction.replaceFirst("\\[", "");
//                    instruction = instruction.replaceFirst("b", "");
//                    instruction = instruction.replaceFirst("]", "");
//                    boldEndIndex = instruction.indexOf("[/b]") - 1;
//                    instruction = instruction.replaceFirst("\\[", "");
//                    instruction = instruction.replaceFirst("/", "");
//                    instruction = instruction.replaceFirst("b", "");
//                    instruction = instruction.replaceFirst("]", "");
//                    break;
//                case "i":
//                    break;
//                case "u":
//                    break;
//                default:
//                    break;
//            }
//            formattedInstruction.setString(instruction);
//            XSSFFont font = excelDoc.createFont()
//        }
//    }

    public static void main(String[] args) {
        //System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator();
        excelFileGenerator.createExcel();
    }
}
