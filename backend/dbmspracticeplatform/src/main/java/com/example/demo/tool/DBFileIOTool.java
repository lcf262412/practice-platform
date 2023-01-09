package com.example.demo.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class DBFileIOTool {
	
	BaseConnection connection;
	
	public DBFileIOTool(BaseConnection connection) {
		this.connection = connection;
	}
	
	public void fileToDatabase(String tablename, List<String> colnames, InputStream in, boolean header) throws SQLException, IOException {
		
		CopyManager copyManager = new CopyManager(connection);
		
		String colSQL = null;
		
		if ((colnames == null || colnames.size() == 0) && header) {
			colSQL = getColnamesFormFile(in);
			header = false;
		}
		
		if (colnames != null && colnames.size()!=0) {
			colSQL = colnamesToSQL(colnames);
		}
			
		String sql = createCopyFromSQL(tablename, colSQL, header);
			
		copyManager.copyIn(sql, in);
	}
	
	public void fileFromDatabase(String tablename, List<String> colnames, OutputStream out, boolean header) throws SQLException, IOException {
		CopyManager copyManager = new CopyManager(connection);
			
		String sql = createCopyToSQL(tablename, colnames, header);
			
		copyManager.copyOut(sql, out);
	}
	
	private String createCopyFromSQL(String tablename, String colSQL, boolean header) {
		String sql = "COPY " + tablename;
		
		if (colSQL != null)
		    sql = sql + colSQL;
		
		sql = sql + " FROM STDIN CSV";
		
		if (header)
			sql = sql + " HEADER";
		
		return sql;
	}
	
	private String createCopyToSQL(String tablename, List<String> colnames, boolean header) {
		String sql = "COPY " + tablename;
		
		if (colnames != null && colnames.size()!=0) {
			sql += colnamesToSQL(colnames);
		}
		
		sql = sql + " TO STDOUT CSV";
		
		if (header) 
			sql = sql + " HEADER";
		
		return sql;
	}
	
	private String getColnamesFormFile(InputStream in) throws IOException {
		String colSQL = "(";
		
		//获取第一行数据并存入字节数组中
		List<Byte> readBytes = new ArrayList<>(); 
		int readByte = in.read();
		while(readByte != '\n') {
			if (readByte != '\r') 
				readBytes.add((byte)readByte);
			readByte = in.read();
		}
		byte[] bytes = new byte[readBytes.size()];
		for (int i = 0; i < readBytes.size(); i++) 
			bytes[i] = readBytes.get(i);
		//从字节数组中还原字字符串
		String colNames = new String(bytes, "UTF-8");

		colSQL += colNames;
			
        colSQL += ')';
		
		return colSQL;
	}
	
	private String colnamesToSQL(List<String> colnames) {
		String colSQL = "(";
		
		colSQL = colSQL + colnames.toString().substring(1, colnames.toString().length()-1);
		
		colSQL += ')';
		
		return colSQL;
	}
	
	public static byte[] getUTF8BytesFromGBKString(String gbkStr) {  
	   int n = gbkStr.length();  
	   byte[] utfBytes = new byte[3 * n];  
	   int k = 0;  
	   for (int i = 0; i < n; i++) {  
		   int m = gbkStr.charAt(i);  
		   if (m < 128 && m >= 0) {  
		       utfBytes[k++] = (byte) m;  
		       continue;  
		   }  
		   utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
		   utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
		   utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
		}  
		if (k < utfBytes.length) {  
		    byte[] tmp = new byte[k];  
		    System.arraycopy(utfBytes, 0, tmp, 0, k);  
		    return tmp;  
	    }  
	    return utfBytes;   
	}  

}
