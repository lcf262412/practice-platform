package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class Student {
	/* 学生编号,同班级编号 */
	private String id;
	
	/* 学生姓名 */
	private String name;
	
	/* 学生年级 */
	private String grade;
	
	/* 所在班级编号 */
	private String classId;
	
	/* 允许实践的数据库名称 */
	private String dbName;
	
	/* 对应数据库用户名 */
	private String username;
	
	/* 对应数据库密码 */
	private String password;
	
	/* 是否激活 */
	private boolean isactive;

}
