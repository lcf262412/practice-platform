package com.example.demo.tool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "spring.datasource")
@Component
@Data
public class DBDatasource {
	
	private String url;
	
	private String username;
	
	private String password;
	
	private String driverClassName;

}
