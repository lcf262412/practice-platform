package com.example.demo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class FileTrans {
	
	public static void transUTF8(InputStream csvInput, OutputStream csvOutput, String charSet) throws IOException { 
		InputStreamReader inputStreamReader = new InputStreamReader(csvInput, charSet); 
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader); 
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(csvOutput, "UTF-8"); 
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter); 
		String line; 
		for(line = bufferedReader.readLine(); 
				line != null; line = bufferedReader.readLine()) { 
			bufferedWriter.write(line+System.getProperty("line.separator"));
		}
		bufferedWriter.flush(); 
	}
	
	public static void XLSX2CSV(InputStream xlsxInput, OutputStream csvOutput) throws IOException {
		//载入xlsx文件
		Workbook workbook = new XSSFWorkbook(xlsxInput);
		transCSV(workbook, csvOutput);
		workbook.close();
	}
	
	public static void XLS2CSV(InputStream xlsInput, OutputStream csvOutput) throws IOException {
		//载入xls文件
		Workbook workbook = new HSSFWorkbook(xlsInput);
        transCSV(workbook, csvOutput);
		workbook.close();
	}
	
	public static void CSV2XLS(InputStream csvInput, OutputStream xlsOutput) throws IOException, CsvException {
		CSVReader csvReader = new CSVReader(new InputStreamReader(csvInput, "UTF-8"));
		
		Workbook workbook =  new HSSFWorkbook();;
		
		transExecl(workbook, csvReader);
		
		workbook.write(xlsOutput);
		workbook.close();
		csvReader.close();
	}
	
	public static void CSV2XLSX(InputStream csvInput, OutputStream xlsxOutput) throws IOException, CsvException {
		CSVReader csvReader = new CSVReader(new InputStreamReader(csvInput, "UTF-8"));
		
		Workbook workbook =  new XSSFWorkbook();
		
		transExecl(workbook, csvReader);	
		
		workbook.write(xlsxOutput);
		workbook.close();
		csvReader.close();
	}
	
	private static void transCSV(Workbook workbook, OutputStream csvOutput) throws IOException {
		//配置输出csv文件格式
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(csvOutput, "UTF8"));
		
		//默认获取第一张sheet
		Sheet sheet = workbook.getSheetAt(0);
		//获取每一行的数据，并将其导入csv文件中
		for (int j = 0; j <= sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);
			
			if (row == null) {
				continue;
			} else {
				if (row.getLastCellNum() > 0) {
					if (row.getCell(0) != null) {
						String cellValue = row.getCell(0).getCellType() == CellType.BLANK ? null : getCellValue(row.getCell(0));
						bufferedWriter.write(cellValue);
					}
					for (int k = 1; k < row.getLastCellNum(); k++) {
						bufferedWriter.write(',');
						if (row.getCell(k) != null) {
							String cellValue = row.getCell(k).getCellType() == CellType.BLANK ? null : getCellValue(row.getCell(k));
							bufferedWriter.write(cellValue);
						}
					}
				}	
			}
			
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public static String getCellValue(Cell cell) {
		CellType cellType = cell.getCellType();
		
		switch(cellType) {
		    case NUMERIC:
		    	double numericValue = cell.getNumericCellValue();
		    	long intValue = (long) numericValue;
		    	if (intValue == numericValue) return String.valueOf(intValue);
		    	return String.valueOf(numericValue);
		    case STRING:
		    	return cell.getStringCellValue();
            case BLANK:
		    	return "";
		    case BOOLEAN:
		    	boolean booleanValue = cell.getBooleanCellValue();
		    	return String.valueOf(booleanValue);
		    case FORMULA:
		    	return cell.getCellFormula();
		    case ERROR:
		    	return "\"ERROR: " + cell.getErrorCellValue()+'"';
		    case _NONE:
		    default:
		    	return "\"ERROR: Unexpected type \"";
		}
	}
	
    private static void transExecl(Workbook workbook, CSVReader csvReader) throws CsvException, IOException {
        
		Sheet sheet = workbook.createSheet("Sheet1");
		
		String[] nextLine;
		int rowNum = 0;
		while ((nextLine = csvReader.readNext()) != null) {
			Row row = sheet.createRow(rowNum);
			
			for (int columnNum = 0; columnNum < nextLine.length; columnNum++) {
				Cell cell = row.createCell(columnNum);
				cell.setCellValue(nextLine[columnNum]);
			}
			
			rowNum++;
		}
	}
	
    /* 将传入文件转为Workbook格式 */
    public static Workbook getWorkbook(MultipartFile file) throws IOException {
		Workbook workbook = null;
		String fileName = file.getOriginalFilename();
		InputStream in = file.getInputStream();
		if(fileName.matches("^.+\\.(?i)(xls)$"))
			workbook = new HSSFWorkbook(in);
		else if(fileName.matches("^.+\\.(?i)(xlsx)$"))
			workbook = new XSSFWorkbook(in);
		
		return workbook;
	}

}
