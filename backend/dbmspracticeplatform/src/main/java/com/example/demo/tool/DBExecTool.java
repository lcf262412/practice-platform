package com.example.demo.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.util.RemoveComment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBExecTool {
	
	Connection connection;
	
	Map<String, Object> resultMap;
	
	public DBExecTool(Connection connection) {
		this.connection = connection;
	}
	
	public Map<String, Object> execute(String singleSQL) {
		this.resultMap = new LinkedHashMap<>();
		PreparedStatement preparedStatement = null;
		boolean isQuery;
		
		this.resultMap.put("code", singleSQL);
		if (RemoveComment.forbiddenSQL(singleSQL)) {
			this.resultMap.put("ResultType", "ERROR");
			this.resultMap.put("errmsg", "不允许执行涉及database、schema的语句");
			return resultMap;
		}

		try {
			preparedStatement = this.connection.prepareStatement(singleSQL);
			isQuery = preparedStatement.execute();
			if (isQuery) {
				getQueryResult(preparedStatement.getResultSet());
			} else {
				getUpdateResult(preparedStatement.getUpdateCount());
			}
			preparedStatement.close();
		} catch (SQLException e) {
			this.resultMap.put("ResultType", "ERROR");
			this.resultMap.put("errmsg", e.getMessage());
		} finally {
			if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}

		if(RemoveComment.judgesql(singleSQL)){
			this.resultMap.put("Isrefresh", "true");
		}
		else {
			this.resultMap.put("Isrefresh", "false");
		}
		return resultMap;
	}
	
	private void getUpdateResult(int updateCount) throws SQLException {
		if (updateCount == 0)
			this.resultMap.put("ResultType", "OTHER");
		else
			this.resultMap.put("ResultType", "DML");
		this.resultMap.put("nRowsAffected", updateCount);
	}
	
	private void getQueryResult(ResultSet rs) throws SQLException {
		this.resultMap.put("ResultType", "QUERY");
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		this.resultMap.put("columnCount", columnCount);
		List <Map<String, Object>> metaDateList = new ArrayList<>();
		for (int i = 1; i <= columnCount; i++) {
			Map<String, Object> metaData = new LinkedHashMap<>();
			metaData.put("SchemaName", md.getSchemaName(i));
			metaData.put("TableName", md.getTableName(i));
			metaData.put("columnLabel", md.getColumnLabel(i));
			metaData.put("columnName", md.getColumnName(i));
			metaData.put("columnTypeName", md.getColumnTypeName(i));
			metaData.put("columnPrecision", md.getPrecision(i));
			metaData.put("isReadOnly", md.isReadOnly(i));
			metaDateList.add(metaData);
		}
		this.resultMap.put("metaDataList", metaDateList);
		
		List <Map<String, Object>> rowList = new ArrayList<>();
		while (rs.next()) {
			Map<String, Object> rowData = new LinkedHashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnLabel(i), rs.getObject(i));
			}
			rowList.add(rowData);
		}
		this.resultMap.put("nRowsAffected", rowList.size());
		this.resultMap.put("rowList", rowList);
	}
}
