package com.example.demo.tool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBControlTool {
	
    private Connection connection;
	
	private Statement statement;
	
	private String createDatabaseSQL = "create database databasename OWNER username";
	
	private String dropDatabaseSQL = "drop database databasename";
	
	public DBControlTool(Connection connection) throws SQLException {
		this.connection = connection;
		this.statement = connection.createStatement();
	}
	
	public void createDatabase(String dbName, String username) throws SQLException {
		String internalCreateDatabaseSQL = this.createDatabaseSQL.replace("databasename", dbName).replace("username", username);
		
		statement.execute(internalCreateDatabaseSQL);
	}
	
	public void dropDatabase(String dbName) throws SQLException {
		String internalDropDatabaseSQL = this.dropDatabaseSQL.replace("databasename", dbName);
		
		statement.execute(internalDropDatabaseSQL);
	}
	
	public boolean transferJudge() {
		ClassPathResource classPathResource = new ClassPathResource("checkSQL"+File.separator+"check_procedure.sql");
		EncodedResource encodedResource = new EncodedResource(classPathResource, "UTF-8");
		String[] commentPrefixes = new String[1];
		commentPrefixes[0] = "--";
		String separator = "^^^separator^^^";
		String blockCommentStartDelimiter = "/*";
		String blockCommentEndDelimiter = "*/";
		try {
			connection.setAutoCommit(false);
			
			ScriptUtils.executeSqlScript(connection, encodedResource, false, false, commentPrefixes,
					                     separator, blockCommentStartDelimiter, blockCommentEndDelimiter);
			connection.commit();
			return true;
		} catch (SQLException | ScriptException e) {
			log.error("Exception :", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.error("Exception :", e1);
			}
		}
		
		return false;
	}
	
	public boolean transferDatabase() {
		ClassPathResource classPathResource = new ClassPathResource("checkSQL"+File.separator+"drop_procedure.sql");
		EncodedResource encodedResource = new EncodedResource(classPathResource, "UTF-8");
		try {
			connection.setAutoCommit(false);
			ScriptUtils.executeSqlScript(connection, encodedResource);
			connection.commit();
			return true;
		} catch (SQLException | ScriptException e) {
			log.error("Exception :", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.error("Exception :", e1);
			}
		}
		
		return false;
	}

}
