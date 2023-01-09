package com.example.demo.util;

import com.example.demo.entity.Database;
import com.example.demo.entity.Teacher;
import com.example.demo.tool.DBConnection;

public class SetDBConnection {
	
	public static void setDBConnection (DBConnection dbConnection, Database database, Teacher teacher) {
		
		dbConnection.setDatabase(database.getName());
		
		/* 数据库表中用户名和密码为空，表明数据库为内部数据库，用户名和密码为教师表中教师的用户名和密码 */
		if (database.getUsername() != null) {
			dbConnection.setUsername(database.getUsername());
			dbConnection.setPassword(database.getPassword());
		} else {
			dbConnection.setUsername(teacher.getUsername());
			dbConnection.setPassword(teacher.getPassword());
		}
	}

}
