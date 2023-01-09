package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class Teacher {
	/* 教师编号,同用户编号 */
	private String id;

	/* 教师姓名 */
	private String name;
	
	/* 对应数据库用户名 */
	private String username;
	
	/* 对应数据库密码 */
	private String password;
}
