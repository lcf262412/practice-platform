package com.example.demo.tool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUserTool {

	private Connection connection;
	
	private Statement statement;
	
	private String createUserSQL = "CREATE USER user_name LOGIN PASSWORD 'password';";
	
	private String createSuperUserSQL = "CREATE USER user_name WITH SYSADMIN PASSWORD 'password';";
	
	private String dropUserSQL = "DROP USER user_name CASCADE";
	
	public DBUserTool(Connection connection) throws SQLException {
		this.connection = connection;
		this.statement = this.connection.createStatement();
	}
	
	public void createUser(String username, String password) throws SQLException {
		String internalCreateUserSQL = new String(this.createUserSQL);
		internalCreateUserSQL = internalCreateUserSQL.replace("user_name", username);
		internalCreateUserSQL = internalCreateUserSQL.replace("password", password);
		
		statement.execute(internalCreateUserSQL);	
	}
	
	public void dropUser(String username) throws SQLException {
		String internalDropUserSQL = new String(this.dropUserSQL);
		internalDropUserSQL = internalDropUserSQL.replace("user_name", username);
		
		statement.execute(internalDropUserSQL);
	}
	
	public void createSuperUser(String username, String password) throws SQLException {
		String internalCreateSuperUserSQL = new String(this.createSuperUserSQL);
		internalCreateSuperUserSQL = internalCreateSuperUserSQL.replace("user_name", username).replace("password", password);
		
		statement.execute(internalCreateSuperUserSQL);
	}
}
