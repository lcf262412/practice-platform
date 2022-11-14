package com.example.demo.tool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBJudgeTool {
	
	private Connection connection;
	
	private String checkDQLSQL = "call check_select(?, ?, ?)";
	
	private String checkDMLSQL = "call check_other(?, ?, ?, ?)";
	
	private String checkCreateTableSQL = "call check_createtable(?, ?, ?, ?)";
	
	private String checkAlterTableSQL = "call check_altertable(?, ?, ?, ?)";
	
	private String checkCreateViewSQL = "call check_createview(?, ?, ?)";
	
	private String checkCreateIndexSQL = "call check_createindex(?, ?, ?, ?)";
	
	private String checkCreateUserSQL = "call check_createUser(?, ?, ?)";
	
	private String checkGrantTableSQL = "call check_grantTable(?, ?, ?, ?)";
	
	private String checkRevokeTableSQL = "call check_revokeTable(?, ?, ?, ?)";
	
	private String checkGrantUserSQL = "call check_grantUser(?, ?, ?, ?)";
	
	private String checkRevokeUserSQL = "call check_revokeUser(?, ?, ?, ?)";
	
	private String checkCreateFuncSQL = "call check_createFunc(?, ?, ?, ?)";
	
	private String insertFuncTestSQL = "insert into test_case(questionId, test_case) values(?, ?);";
	
	private String updateFuncTestSQL = "update test_case set test_case = ? where caseId = ? and questionId = ?;";
	
	private String deleteFuncTestSQL = "delete from test_case where caseId = ? and questionId = ?;";
	
	private String deleteAllFuncTestSQL = "delete from test_case where questionId = ?;";
	
	private String selectFuncTestByQuestionSQL = "select caseId, questionId, test_case from test_case where questionId = ?;";
	
	public DBJudgeTool(Connection connection) {
		this.connection = connection;
	}
	
	public boolean checkDQL(String answer, String correct) {
		answer = answer.replaceAll("(?i)select", "select");
		correct = correct.replaceAll("(?i)select", "select");
		
		try {
			int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkDQLSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, correct);
			callableStatement.setInt(3, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
			
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkDML(String answer, String correct, String targetName) {
		answer = answer.replaceAll("(?i)"+targetName, targetName);
		correct = correct.replaceAll("(?i)"+targetName, targetName);
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkDMLSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, correct);
			callableStatement.setString(3, targetName);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkCreateTable(String answer, String targetName, String initSQL) {
		answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		answer = answer.replaceFirst("(?i)table", "table");
		
		initSQL = initSQL.replaceAll("(?i)table", "table");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkCreateTableSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
		
	}
	
	public boolean checkAlterTable(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		answer = answer.replaceFirst("(?i)table", "table");
		
		initSQL = initSQL.replaceAll("(?i)table", "table");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkAlterTableSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkCreateView(String answer, String targetName) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		answer = answer.replaceFirst("(?i)view", "view");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkCreateViewSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setInt(3, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkCreateIndex(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		initSQL = initSQL.replaceAll("(?i)table", "table");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkCreateIndexSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkCreateUser(String answer, String targetName) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkCreateUserSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setInt(3, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkGrantTable(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		initSQL = initSQL.replaceAll("(?i)table", "table");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkGrantTableSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkRevokeTable(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		initSQL = initSQL.replaceAll("(?i)table", "table");
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkRevokeTableSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkGrantUser(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		initSQL = initSQL.replaceAll("(?i)"+targetName, targetName);
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkGrantUserSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkRevokeUser(String answer, String targetName, String initSQL) {
        answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		initSQL = initSQL.replaceAll("(?i)"+targetName, targetName);
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkRevokeUserSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setString(3, initSQL);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
	
	public boolean checkCreateFunc(String answer, String targetName, int questionId) {
		answer = answer.replaceFirst("(?i)function", "function");
		answer = answer.replaceFirst("(?i)procedure", "procedure");
		answer = answer.replaceFirst("(?i)begin", "begin");
		answer = answer.replaceAll("(?i)"+targetName, targetName);
		
		try {
            int result = 3;
			
			CallableStatement callableStatement = connection.prepareCall(checkCreateFuncSQL);
			callableStatement.setString(1, answer);
			callableStatement.setString(2, targetName);
			callableStatement.setInt(3, questionId);
			callableStatement.setInt(4, result);
			
			callableStatement.execute();
			
			ResultSet rSet = callableStatement.getResultSet();
			rSet.next();
			result = rSet.getInt(1);
			if (result != 1)
				return false;
			return true;
		} catch (SQLException e) {
			log.error("Exception :", e);
			return false;
		}
	}
			
    public Map<String, Object> insertFuncTest(int questionId, String test_case) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(insertFuncTestSQL);
			
			preparedStatement.setInt(1, questionId);
			preparedStatement.setString(2, test_case);
			
			preparedStatement.execute();
			
			modelMap.put("state", "success");
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("state", "error");
			modelMap.put("detail", e.getMessage());
		}
    	
    	return modelMap;
    }
    
    public Map<String, Object> insertFuncTests(int questionId, List<String> test_cases) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(insertFuncTestSQL);
			
			for (String test_case : test_cases) {
				preparedStatement.setInt(1, questionId);
				preparedStatement.setString(2, test_case);
				preparedStatement.addBatch();
			}
			
			preparedStatement.executeBatch();
			
			modelMap.put("state", "success");
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("state", "error");
			modelMap.put("detail", e.getMessage());
		}
    	
    	return modelMap;
    }
    
    public Map<String, Object> updateFuncTest(int caseId, int questionId, String test_case) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(updateFuncTestSQL);
			
			preparedStatement.setString(1, test_case);
			preparedStatement.setInt(2, caseId);
			preparedStatement.setInt(3, questionId);
			
			preparedStatement.execute();
			
			modelMap.put("state", "success");
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("state", "error");
			modelMap.put("detail", e.getMessage());
		}
    	
    	return modelMap;
    }
    
    public Map<String, Object> deleteFuncTest(int caseId, int questionId) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(deleteFuncTestSQL);
			
			preparedStatement.setInt(1, caseId);
			preparedStatement.setInt(2, questionId);
			
			preparedStatement.execute();
			
			modelMap.put("state", "success");
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("state", "error");
			modelMap.put("detail", e.getMessage());
		}
    	
    	return modelMap;
    }
    
    public Map<String, Object> deleteAllFuncTest(int questionId) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(deleteAllFuncTestSQL);
			
			preparedStatement.setInt(1, questionId);
			
			preparedStatement.execute();
			
			modelMap.put("state", "success");
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("state", "error");
			modelMap.put("detail", e.getMessage());
		}
    	
    	return modelMap;
    }
    
    public List<Map<String, Object>> selectFuncTest(int questionId) {
    	List<Map<String, Object>> results = new ArrayList<>();
    	
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(selectFuncTestByQuestionSQL);
			
			preparedStatement.setInt(1, questionId);
			
			preparedStatement.execute();
			
			ResultSet rs = preparedStatement.getResultSet();
			
			while (rs.next()) {
				Map<String, Object> rowDate = new HashMap<>();
				
				rowDate.put("caseId", rs.getObject("caseid"));
				rowDate.put("questionId", rs.getObject("questionid"));
				rowDate.put("test_case", rs.getObject("test_case"));
				
				results.add(rowDate);
			}
			
		} catch (SQLException e) {
			log.error("Exception :", e);
		}
    	
    	return results;
    }
}
