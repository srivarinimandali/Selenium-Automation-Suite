package com.sqcm.group4.Selenium.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelDataProvider {
    private Sheet sheet;
    private Map<String, Integer> columns;

    public ExcelDataProvider(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            sheet = workbook.getSheet(sheetName);
            columns = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    columns.put(cell.getStringCellValue(), i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCellData(int rowNum, String columnName) {
        if (!columns.containsKey(columnName)) {
            return "";
        }
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(columns.get(columnName));
        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    public String getCellData(int rowNum, String columnName, String sheetName) {
        try (FileInputStream fis = new FileInputStream("src/test/resources/test_data.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet targetSheet = workbook.getSheet(sheetName);
            if (targetSheet == null) return "";

            Row headerRow = targetSheet.getRow(0);
            Map<String, Integer> tempColumns = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    tempColumns.put(cell.getStringCellValue(), i);
                }
            }

            if (!tempColumns.containsKey(columnName)) return "";
            Row dataRow = targetSheet.getRow(rowNum);
            if (dataRow == null) return "";
            Cell cell = dataRow.getCell(tempColumns.get(columnName));
            return getCellValueAsString(cell);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public int getRowCount(String sheetName) {
        try (FileInputStream fis = new FileInputStream("src/test/resources/test_data.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet targetSheet = workbook.getSheet(sheetName);
            if (targetSheet == null) {
                throw new IllegalArgumentException("❌ Sheet '" + sheetName + "' not found in Excel file.");
            }
            return targetSheet.getLastRowNum();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int getRowCount() {
        return sheet.getLastRowNum();
    }
    public Map<String, String> getRowData(String sheetName, int rowNum) {
        Map<String, String> rowData = new HashMap<>();
        try (FileInputStream fis = new FileInputStream("src/test/resources/test_data.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);
            Row dataRow = sheet.getRow(rowNum);

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String key = headerRow.getCell(i).getStringCellValue();
                String value = dataRow.getCell(i).getStringCellValue();
                rowData.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowData;
    }
}