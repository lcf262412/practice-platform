package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Database;
import com.example.demo.entity.Student;
import com.example.demo.mapper.DatabaseMapper;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.service.DatabaseService;

@Service
public class DatabaseServiceImpl extends ServiceImpl<DatabaseMapper, Database> implements DatabaseService{
	@Autowired
	private DatabaseMapper databaseMapper;
	@Autowired
	private StudentMapper studentMapper;
	
	@Override
	/*插入新的数据库信息*/
	public Map<String, Object> insertDatabase(String name, String userId, String IP, int port, short dbClass, String username, String password, boolean useForStu,String database_info) {
		Database database = new Database();
		database.setName(name);
		database.setUserId(userId);
		database.setIP(IP);
		database.setPort(port);
		database.setDbClass(dbClass);
		database.setUsername(username);
		database.setPassword(password);
		database.setUseForStu(useForStu);
		database.setDatabase_info(database_info);
		
		int mes = databaseMapper.insert(database);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;

	}
	
	@Override
	/* 根据数据库名称查询数据库具体信息 */
	public Database getDatabaseByName(String name) {
		QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
		databaseWrapper.eq("name", name);
		return databaseMapper.selectOne(databaseWrapper);
	}

	@Override
	public Map<String, Object> modifyDatabaseClass(String name, short dbClass) {
		QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
		databaseWrapper.eq("name", name);
		Database database = databaseMapper.selectOne(databaseWrapper);
		
		database.setDbClass(dbClass);
		int mes = databaseMapper.update(database, databaseWrapper);
		Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }
		
		return modelMap;
	}

	/* 查询教师用户的全部数据库 */
	@Override
	public List<Map<String, Object>> getDatabasesByteacherId(String userId) {
		QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
		databaseWrapper.eq("userid", userId);
		List<Database> databaseList = databaseMapper.selectList(databaseWrapper);
		List<Map<String, Object>> result = new ArrayList<>();
		
		for(Database database:databaseList) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("name", database.getName());
			modelMap.put("dbClass", database.getDbClass());
			modelMap.put("db_info", database.getDatabase_info());
			result.add(modelMap);
		}
		
		return result;
	}

	/* 查询用于学生实践的实践数据库 */
	@Override
	public List<Map<String, Object>> getUseForStuDatabases() {
		QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
		databaseWrapper.eq("useforstu", true);
		List<Database> databaseList = databaseMapper.selectList(databaseWrapper);
		List<Map<String, Object>> result = new ArrayList<>();
		
		for(Database database:databaseList) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("name", database.getName());
			result.add(modelMap);
		}
		
		return result;
	}
	
	/* 删除数据库信息 */
	@Override
	public Map<String, Object> deleteDatabaseByName(String name) {
		Map<String,Object> modelMap = new HashMap<String, Object>();
		
		QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
		databaseWrapper.eq("name", name);
		Database database = databaseMapper.selectOne(databaseWrapper);
		
		if (database == null) {
			modelMap.put("state", "error");
        	modelMap.put("errmessage", "数据库不存在");
        	return modelMap;
		}
		
		if (database.isUseForStu()) {
			QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
			studentWrapper.eq("dbname", name);
			int studentCount = studentMapper.selectCount(studentWrapper);
			if (studentCount > 0) {
				modelMap.put("state", "error");
	        	modelMap.put("errmessage", "有学生正在使用该数据库");
	        	return modelMap;
			}
		}
		
		int mes = databaseMapper.delete(databaseWrapper);
		if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
            modelMap.put("errmessage","删除失败，请联系数据库管理员解决");
        }
		
		return modelMap;
	}
	
}
