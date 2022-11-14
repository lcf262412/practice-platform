package com.example.demo.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Database;
import com.example.demo.entity.Teacher;
import com.example.demo.service.DatabaseService;
import com.example.demo.service.JudgeDatabaseService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBControlTool;
import com.example.demo.tool.DBDatasource;
import com.example.demo.util.SetDBConnection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/database")
@CrossOrigin
@RestController
public class DatabaseController {
	@Autowired
	private DatabaseService databaseService;
	
	@Autowired
	private TeacherService teacherService;
	
	@Autowired
	private JudgeDatabaseService judgeDatabaseService;
	
	@Autowired
	private DBDatasource dbDatasource;
	
	/* 
	 * 增加新的数据库 
	 * 注：目前只支持在DBMS中创建新的数据库，并不支持增加非本地的数据库
	 *    IP、port、username、password无用（这些参数用于增加非本地DBMS的数据库）
	 */
	@PostMapping(value = "/addDatabase")
	@Transactional
	public BaseResponse<Map<String, Object>> addDatabase(@RequestParam(value = "name") String name,
			                                             @RequestParam(value = "teacherId") String userId,
			                                             @RequestParam(value = "IP", required = false) String IP,
			                                             @RequestParam(value = "port", required = false, defaultValue = "0") int port,
			                                             @RequestParam(value = "username", required = false) String username,
			                                             @RequestParam(value = "password", required = false) String password,
			                                             @RequestParam(value = "useForStu", required = false, defaultValue = "false") boolean useForStu,
			                                             @RequestParam(value = "database_info") String database_info) {
		Map<String, Object> modelMap = new HashMap<>();
		
		/* 判断数据库是否已经存在 */
		log.info("-----------------------");
		Database database = databaseService.getDatabaseByName(name);
		if (database != null) {
			modelMap.put("errmessage", "数据库已存在");
			log.info("1");
			return BaseResponse.fail(modelMap);
		}
		
		/* 判断对应教师用户是否存在 */
		Teacher teacher = teacherService.getTeacher(userId);
		if (teacher == null) {
			log.info("2");
			modelMap.put("errmessage", "教师编号不存在");
			return BaseResponse.fail(modelMap);
		}
		
		/* 将数据库信息存入表中，先存入后创建，目的是当创建失败时可以回滚插入语句 */
		Map<String, Object> result = databaseService.insertDatabase(name, userId, IP, port, (short)1, username, password, useForStu,database_info);
		if (result.get("state").equals("error")){
			log.info("3");
            return BaseResponse.fail(result);
		}
		
		/* IP为空，表明该数据库非外部已有数据库，需创建相应的数据库 */
		if (IP == null) {
			DBConnection dbConnection = new DBConnection(dbDatasource);
			
			/* username为空，表明数据库为系统内部创建的数据库，username应为教师用户的username */
			if (username == null) {
				log.info("4");
				username = teacher.getUsername();
			}
			
			try {
				DBControlTool dbControlTool = new DBControlTool(dbConnection.getConnect());
				dbControlTool.createDatabase("\""+name+"\"", username);
			} catch (SQLException e) {
				log.info("5");
				log.error("Exception :", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				log.info("---------------------------");
				log.info(e.toString());
				String ee = e.toString();
				if(ee.contains("already exists"))
					modelMap.put("errmessage", "数据库已存在");
				else
					modelMap.put("errmessage", "无法创建数据库 "+ name);
				modelMap.put("errdetail", e.getMessage());
				return BaseResponse.fail(modelMap);
			} finally {
				try {
					dbConnection.close();
				} catch(SQLException e) {
					log.error("Exception :", e);
				}
			}
		}
		
		return BaseResponse.success(result);
	}
	
	/* 实践数据库转答题数据库 */
	@PostMapping(value = "/transferJudge")
	@Transactional
	public BaseResponse<Map<String, Object>> transferJudge(@RequestParam(value = "name") String name,
			                                                @RequestParam(value = "describe") String describe) {
		Map<String, Object> modelMap = new HashMap<>();
		Map<String, Object> result;
		
		/* 获取相应的数据库信息，判断其是否为实践数据库 */
		Database database = databaseService.getDatabaseByName(name);
		if (database == null) {
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		
		if (database.getDbClass() == 2) {
			modelMap.put("errmessage", "数据库已是答题数据库");
			return BaseResponse.fail(modelMap);
		}
		
		if (database.isUseForStu()) {
			modelMap.put("errmessage", "数据库为学生实践数据库，无法转为答题数据库");
			return BaseResponse.fail(modelMap);
		}
		
		/* 向答题数据库表中插入该条记录 */
		result = judgeDatabaseService.insertJudgeDatabase(name, describe);
		if (result.get("state").equals("error")){
            return BaseResponse.fail(result);
		}
		
		/* 修改数据库信息表中数据库类型信息 ， 失败回滚前面的操作*/
		result = databaseService.modifyDatabaseClass(name, (short)2);
		if (result.get("state").equals("error")){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return BaseResponse.fail(result);
		}
		
		/* 获取数据库对应教师用户信息 */
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		
		/* 向该数据库中增加相应的存储过程和表格， 失败回滚前面的操作 */
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		try {
			DBControlTool dbControlTool = new DBControlTool(dbConnection.getConnect());
			if (!dbControlTool.transferJudge()) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "无法转换数据库 "+ name);
				return BaseResponse.fail(modelMap);
			}
		} catch (SQLException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "无法连接数据库 "+ name);
			return BaseResponse.fail(modelMap);
		} finally {
			try {
				dbConnection.close();
			} catch(SQLException e) {
				log.error("Exception :", e);
			}
		}
		
		return BaseResponse.success(result);
	}
	
	/* 将答题数据库转为实践数据库 ，暂不支持具体功能 */
	@PostMapping(value = "/transferDatabase")
    @Transactional
	public BaseResponse<Map<String, Object>> transferDatabase(@RequestParam(value = "name") String name) {
		Map<String, Object> modelMap = new HashMap<>();
		
		/* 获取数据库信息，判断数据库是否存在且是否为答题数据库，并为删除数据库做准备 */
		Database database = databaseService.getDatabaseByName(name);
		if (database == null) {
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		
		if (database.getDbClass() != 2) {
			modelMap.put("errmessage", "数据库不是答题数据库");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除答题数据库中的数据 */
		Map<String, Object> result = judgeDatabaseService.deleteJudgeDatabaseByName(name);
		if (result.get("state").equals("error")){
            return BaseResponse.fail(result);
		}
		
		/* 修改数据库信息表中数据库类型信息 ， 失败回滚前面的操作*/
		result = databaseService.modifyDatabaseClass(name, (short)1);
		if (result.get("state").equals("error")){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return BaseResponse.fail(result);
		}
		
		/* 获取数据库对应教师用户信息 */
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		
		/* 从该数据库中删除相应的存储过程和表格， 失败回滚前面的操作 */
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		try {
			DBControlTool dbControlTool = new DBControlTool(dbConnection.getConnect());
			if (!dbControlTool.transferDatabase()) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "无法转换数据库 "+ name);
				return BaseResponse.fail(modelMap);
			}
		} catch (SQLException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "无法连接数据库 "+ name);
			return BaseResponse.fail(modelMap);
		} finally {
			try {
				dbConnection.close();
			} catch(SQLException e) {
				log.error("Exception :", e);
			}
		}
		
		return BaseResponse.success(result);
    }
	
	/* 删除数据库信息，只支持删除实践数据库 */
	@PostMapping(value = "/deleteDatabaseByName")
	@Transactional
	public BaseResponse<Map<String, Object>> deleteDatabaseByName(@RequestParam(value = "name") String name) {
		Map<String, Object> modelMap = new HashMap<>();
		log.info("----------------------------------------");
		log.info("It is practice database");
		/* 获取数据库信息，判断数据库是否存在，并为删除数据库做准备 */
		Database database = databaseService.getDatabaseByName(name);
		if (database == null) {
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		
		/* 判断其是否为实践数据库 */
		if (database.getDbClass() == 2) {
			modelMap.put("errmessage", "数据库是答题数据库，无法通过该接口删除");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除数据库表内数据信息 */
		Map<String, Object> result = databaseService.deleteDatabaseByName(name);
		if (result.get("state").equals("error")){
            return BaseResponse.fail(result);
		}
		
		/* IP为空，表明该数据库是本地数据库，删除该数据库 */
		if (database.getIP() == null) {
			DBConnection dbConnection = new DBConnection(dbDatasource);
			try {
				DBControlTool dbControlTool = new DBControlTool(dbConnection.getConnect());
				dbControlTool.dropDatabase("\""+name+"\"");
			} catch (SQLException e) {
				log.error("Exception :", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "无法删除数据库 "+ name);
				modelMap.put("errdetail", e.getMessage());
				return BaseResponse.fail(modelMap);
			} finally {
				try {
					dbConnection.close();
				} catch(SQLException e) {
					log.error("Exception :", e);
				}
			}
		}
		
		return BaseResponse.success(result);
	}
	
	/* 删除答题数据库信息，暂不支持具体功能 */
	@PostMapping(value = "/deleteJudgeDatabaseByName")
	@Transactional
	public BaseResponse<Map<String, Object>> deleteJudgeDatabaseByName(@RequestParam(value = "name") String name) {
		Map<String, Object> modelMap = new HashMap<>();
		log.info("----------------------------------------");
		log.info("It is judge database");
		/* 获取数据库信息，判断数据库是否存在且是否为答题数据库，并为删除数据库做准备 */
		Database database = databaseService.getDatabaseByName(name);
		if (database == null) {
			log.info("1");
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		
		if (database.getDbClass() != 2) {
			log.info("2");
			modelMap.put("errmessage", "数据库不是答题数据库");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除答题数据库表中的数据 */
		Map<String, Object> result = judgeDatabaseService.deleteJudgeDatabaseByName(name);
		if (result.get("state").equals("error")){
			log.info("3");
            return BaseResponse.fail(result);
		}
		
		/* 删除数据库表内数据信息 */
		result = databaseService.deleteDatabaseByName(name);
		if (result.get("state").equals("error")){
			log.info("4");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return BaseResponse.fail(result);
		}
		
		/* IP为空，表明该数据库是本地数据库，删除该数据库 */
		if (database.getIP() == null) {
			DBConnection dbConnection = new DBConnection(dbDatasource);
			try {
				DBControlTool dbControlTool = new DBControlTool(dbConnection.getConnect());
				dbControlTool.dropDatabase("\""+name+"\"");
			} catch (SQLException e) {
				log.info("5");
				log.error("Exception :", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "无法删除数据库 "+ name);
				modelMap.put("errdetail", e.getMessage());
				return BaseResponse.fail(modelMap);
			} finally {
				try {
					dbConnection.close();
				} catch(SQLException e) {
					log.info("6");
					log.error("Exception :", e);
				}
			}
		}
		
		return BaseResponse.success(result);
	}
	
	/* 修改指定答题数据库元数据说明 */
	@PostMapping(value = "/modifyJudgeDatabase")
	public BaseResponse<Map<String, Object>> modifyJudgeDatabase(@RequestParam(value = "name") String name,
			                                                     @RequestParam(value = "describe") String describe) {
		Map<String, Object> result = judgeDatabaseService.modifyJudgeDatabase(name, describe);
		if ("success".equals(result.get("state"))){
            return BaseResponse.success(result);
		}
		return BaseResponse.fail(result);
	}
	
	/* 根据名称查询指定答题数据库的元数据说明 */
	@GetMapping(value = "/getDatabaseByName")
	public BaseResponse<Map<String, Object>> getDatabaseByName(@RequestParam(value = "name") String name) {
		Map<String, Object> result = judgeDatabaseService.getJudgeDatabaseByName(name);
		if (result == null || result.isEmpty())
			return BaseResponse.success(null);
		else
		    return BaseResponse.success(result);
	}
	
	/* 查询教师用户的全部数据库 */
	@GetMapping(value = "/getDatabasesByteacherId")
	public BaseResponse<List<Map<String, Object>>> getDatabasesByteacherId(@RequestParam(value = "teacherId") String teacherId) {
		return BaseResponse.success(databaseService.getDatabasesByteacherId(teacherId));
	}
	
	/* 查询用于学生实践的实践数据库 */
	@GetMapping(value = "/getUseForStuDatabases")
	public BaseResponse<List<Map<String, Object>>> getUseForStuDatabases() {
		return BaseResponse.success(databaseService.getUseForStuDatabases());
	}
	
	/* 查询教师用户的全部答题数据库信息 */
	@GetMapping(value = "/getJudgeDatabasesByteacherId")
	public BaseResponse<List<Map<String, Object>>> getJudgeDatabasesByteacherId(@RequestParam(value = "teacherId") String teacherId) {
		return BaseResponse.success(judgeDatabaseService.getJudgeDatabasesByteacherId(teacherId));
	}

}
