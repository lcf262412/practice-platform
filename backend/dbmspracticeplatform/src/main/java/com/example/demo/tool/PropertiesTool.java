package com.example.demo.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesTool {
	
	private Properties properties;
	
	private String filepath; 
	
	private Pattern urlPattern = Pattern.compile("jdbc\\s*:\\s*(?<type>[a-z]+)\\s*:\\s*//"
			+ "\\s*?(?<host>[a-zA-Z0-9-//.]+)\\s*?(:\\s*?(?<port>[0-9]+))?+\\s*?/"
			+ "\\s*?(?<database>[a-zA-Z0-9_]+)?+\\s*?(\\?(?<params>(\\s|\\S)+))?");
	
	public PropertiesTool() {
		this.properties = new Properties();
		
		/* 判断jar包外的配置文件是否存在, 存在则存入filepath中，不存在则获取jar包内配置文件，存入filepath中*/
		File testFile = new File("application.properties");
		if (testFile.exists()) 
			try {
				this.filepath = testFile.getCanonicalPath();
			} catch (IOException e) {
				this.filepath = this.getClass().getResource("/application.properties").getPath().replaceFirst("^file:", "");
			}
		else
			this.filepath = this.getClass().getResource("/application.properties").getPath().replaceFirst("^file:", "");
	}
	
	public Map<String, Object> getDatabaseConnectProperties () throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		loadProperties();
		
		String url = this.properties.getProperty("spring.datasource.jdbcurl");
		Matcher urlMatcher = this.urlPattern.matcher(url);
		//匹配失败，返回空
		if(!urlMatcher.matches()) return modelMap;
		modelMap.put("url", url);
		modelMap.put("IP", urlMatcher.group("host"));
		modelMap.put("port", urlMatcher.group("port"));
		modelMap.put("dbName", urlMatcher.group("database"));
		modelMap.put("params", urlMatcher.group("params"));
		modelMap.put("username", this.properties.getProperty("spring.datasource.username"));
		modelMap.put("password", this.properties.getProperty("spring.datasource.password"));

		return modelMap;
	}
	
	public boolean setDatabaseConnectProperties (String IP, int port, String dbName, String username, String password) throws IOException {
		loadProperties();
		
		String url = "jdbc:postgresql://"+IP;
		if (port != 0) {
			url = url + ":" +port;
		}
		url = url + "/" + dbName;
		
		this.properties.put("spring.datasource.jdbcurl", url);
		this.properties.put("spring.datasource.username", username);
		this.properties.put("spring.datasource.password", password);
		OutputStream outputStream = new FileOutputStream(new File(this.filepath));
		this.properties.store(outputStream, "");
		outputStream.flush();
		outputStream.close();
		
		return true;
	}
	
	private void loadProperties() throws IOException {
		InputStream inputStream = new FileInputStream(new File(this.filepath));
		this.properties.load(inputStream);
		inputStream.close();
	}

}
