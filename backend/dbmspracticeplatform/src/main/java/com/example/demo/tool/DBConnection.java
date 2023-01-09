package com.example.demo.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.core.BaseConnection;

import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {
	
    private String url;
	
	private String username;
	
	private String password;
	
	private String driverClassName;
	
	private HikariDataSource dataSource;
	
	private Connection connection;
	
	private Pattern urlPattern = Pattern.compile("jdbc\\s*:\\s*(?<type>[a-z]+)\\s*:\\s*//"
			+ "\\s*?(?<host>[a-zA-Z0-9-//.]+)\\s*?(:\\s*?(?<port>[0-9]+))?+\\s*?/"
			+ "\\s*?(?<database>[a-zA-Z0-9_]+)?+\\s*?(\\?(?<params>(\\s|\\S)+))?");
	
	public DBConnection(DBDatasource dbDatasource) {
		this.url = dbDatasource.getUrl();
		this.username = dbDatasource.getUsername();
		this.password = dbDatasource.getPassword();
		this.driverClassName = dbDatasource.getDriverClassName();
		this.dataSource = null;
		this.connection = null;
	}
	
	public void setDatabase(String dbName) {
		//采用正则表达式获取配置文件中数据库名称和后续配置参数，将其替换为传入数据库名称
		Matcher urlMatcher = urlPattern.matcher(this.url);
		urlMatcher.matches();
		String replaceString = this.url.substring(urlMatcher.start("database"));
		this.url = this.url.replace(replaceString, dbName);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setIP(String IP) {
		Matcher urlMatcher = urlPattern.matcher(this.url);
		urlMatcher.matches();
		String urlIP = urlMatcher.group("host");
		this.url = this.url.replace(urlIP, IP);
	}
	
	public void setPort(int port) {
		Matcher urlMatcher = urlPattern.matcher(this.url);
		urlMatcher.matches();
		String urlPort = urlMatcher.group("port");
		//原url中存在端口，直接替换；不存在端口，将端口追加到IP后
		if (urlPort != null)
			this.url = this.url.replaceFirst(urlPort, String.valueOf(port));
		else {
			String urlIP = urlMatcher.group("host");
			this.url = this.url.replace(urlIP, urlIP + ":" + port);
		}
	}
	
	private void getDataSource() throws SQLException {
		HikariDataSource internalDataSource = new HikariDataSource();
		
		internalDataSource.setJdbcUrl(this.url);
		internalDataSource.setUsername(this.username);
		internalDataSource.setPassword(this.password);
		internalDataSource.setDriverClassName(this.driverClassName);
		internalDataSource.setMaximumPoolSize(5);
		internalDataSource.setMinimumIdle(0);
		internalDataSource.setConnectionTimeout(30000);
		
		this.dataSource = internalDataSource;
	}
	
	public Connection getConnect() throws SQLException {
		if (this.dataSource == null)
		    getDataSource();
		if (this.connection == null || !this.connection.isValid(500))
			this.connection = this.dataSource.getConnection();
		
		return this.connection;
	}
	
	public void close() throws SQLException {
		if (connection.isValid(500) || !connection.isClosed())
		    this.connection.close();
		
		this.dataSource.close();
	}
	
	public BaseConnection getPgConnect() throws SQLException, ClassNotFoundException {
		Class.forName(this.driverClassName);
		Connection pgConnection = DriverManager.getConnection(this.url, this.username, this.password);
		if (pgConnection instanceof BaseConnection)
		    return (BaseConnection)pgConnection;
		else {
			if (pgConnection != null && !pgConnection.isClosed())
				pgConnection.close();
			throw new ClassNotFoundException();
		}
	}
	
}
