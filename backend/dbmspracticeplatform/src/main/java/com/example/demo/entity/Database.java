package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class Database {
	/* 数据库名称 */
	private String name;
	
	/* 创建者编号 */
	private String userId;
	
	/* IP */
	private String IP;
	
	/* 端口 */
	private int port;
	
	/* 类型 */
	private short dbClass;
	
	/* 数据库用户名 */
	private String username;
	
	/* 数据库密码 */
	private String password;
	
	/* UseForStu */
	private boolean useForStu;
	
	/*database_info*/
	private String database_info;
	
	
	
}
