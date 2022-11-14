package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Database;

public interface DatabaseService extends IService<Database> {
	
	/* 插入新的数据库信息 */
	Map<String, Object> insertDatabase(String name, String userId, String IP, int port, short dbclass, String username, String password, boolean useForStu,String database_info);
	
	/* 通过名称获取数据库 */
	Database getDatabaseByName(String name);
	
	/* 修改数据库dbclass值 */
	Map<String, Object> modifyDatabaseClass(String name, short dbclass);
	
	/* 查询教师用户的全部数据库 */
	List<Map<String, Object>> getDatabasesByteacherId(String userId);
	
	/* 查询用于学生实践的实践数据库 */
	List<Map<String, Object>> getUseForStuDatabases();
	
	/* 删除数据库信息 */
	Map<String, Object> deleteDatabaseByName(String name);
	

}
