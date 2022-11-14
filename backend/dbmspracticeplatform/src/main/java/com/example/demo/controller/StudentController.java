package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Database;
import com.example.demo.entity.Student;
import com.example.demo.entity.SysUser;
import com.example.demo.entity.Teacher;
import com.example.demo.service.DatabaseService;
import com.example.demo.service.StudentService;
import com.example.demo.service.SysUserService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBDatasource;
import com.example.demo.tool.DBUserTool;
import com.example.demo.util.FileTrans;
import com.example.demo.util.SetDBConnection;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
class delstudents {
    String stuId;
    String stuName;
}

class ImportStudentException extends Exception {
	private static final long serialVersionUID = 1L;
}

@Slf4j
@RequestMapping("/student")
@CrossOrigin
@RestController
public class StudentController {
	@Autowired
	private StudentService studentService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private DatabaseService databaseService;
	@Autowired
	private DBDatasource dbDatasource;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	DataSourceTransactionManager dataSourceTransactionManager;
	@Autowired
	TransactionDefinition transactionDefinition;
	
	/* 增加学生信息 */
	@PostMapping(value = "/addStudent")
	@Transactional
	public BaseResponse<Map<String, Object>> addStudent (@RequestParam(value = "id") String id, 
			                                             @RequestParam(value = "pwd", required = false, defaultValue = "12345678") String pwd,
			                                             @RequestParam(value = "name") String name,
			                                             @RequestParam(value = "grade") String grade,
			                                             @RequestParam(value = "classId") String classId,
			                                             @RequestParam(value = "dbName", required = false, defaultValue = "practicedb1") String dbName) {
		Map<String, Object> modelMap = new HashMap<>();
		
		/* 判断数据库是否存在，且是否用于学生实践 */
		Database database = databaseService.getDatabaseByName(dbName);
		if (database == null) {
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		if (database.getDbClass() != 1 && database.isUseForStu()) {
			modelMap.put("errmessage", "非学生可用数据库");
			return BaseResponse.fail(modelMap);
		}
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		
		/* 配置学生数据库账号 */
		String username = 's'+id;
		String password = "stu_"+id+"_123";
		
		/* 向数据表中增加学生信息 */
		Map<String, Object> result = insertStudentUser(id, pwd, name, grade, classId, dbName, username, password);
		if (result.get("state").equals("error")) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return BaseResponse.fail(result);
		}
		
		/* 连接指定的实践数据库，创建学生用户 */
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		try {
			Connection connection = dbConnection.getConnect();
			DBUserTool dbUserTool = new DBUserTool(connection);
			dbUserTool.createUser(username, password);
		} catch(SQLException e) {
			log.error("Exception :", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "不能创建数据库用户 ");
			modelMap.put("errdetail", e.getMessage());
			return BaseResponse.fail(modelMap);
	    }finally {
	    	try {
	    		dbConnection.close();
	    	} catch(SQLException e) {
	    		log.error("Exception :", e);
	    	}
		}
		
		return BaseResponse.success(result);
	}
	
	/* 删除指定学生账号 */
	@PostMapping(value = "/deleteStudent")
	@Transactional
	public BaseResponse<Map<String, Object>> deleteStudent(@RequestParam(value = "id") String studentId) {
        Map<String, Object> modelMap = new HashMap<>();
        
		/* 获取用户对象，为空表明不存在 */
		SysUser sysUser = sysUserService.getSysUserById(studentId);
		if (sysUser == null) {
			modelMap.put("errmessage", "删除学生编号为:"+studentId+"失败，用户不存在");
			return BaseResponse.fail(modelMap);
		}
	
		/* 获取用户类别 */
		short userClass = sysUser.getUserClass();
		if (userClass == 1 || userClass == 2) {
			modelMap.put("errmessage", "用户:"+studentId+"不具有学生权限");
			return BaseResponse.fail(modelMap);
		}
		Student student = studentService.getStudent(studentId);
		if (student == null) {
			modelMap.put("errmessage", "删除学生编号为"+studentId+"的学生出错:"+"用户不存在");
			return BaseResponse.fail(modelMap);
		}

		if (student.isIsactive()) {
			modelMap.put("errmessage", "删除学生x姓名为"+student.getName()+"，编号为"+studentId+"的学生出错:"+"学生用户处于激活状态");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除学生表中的记录 */
		Map<String, Object> resultMap = studentService.deleteStudent(studentId);
		if ("error".equals(resultMap.get("state"))) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "删除学生x姓名为"+student.getName()+"，编号为"+studentId+"的学生出错:"+"删除学生表记录失败");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除或修改用户表中的记录 */
		if (userClass == 3 || userClass == 4) {
			resultMap = sysUserService.deleteSysUser(studentId);
			if ("error".equals(resultMap.get("state"))) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "删除学生x姓名为"+student.getName()+"，编号为"+studentId+"的学生出错:"+"删除用户表记录失败");
				return BaseResponse.fail(modelMap);
			}
		} else {
			userClass = 2;
			resultMap = sysUserService.modifySysUserClass(studentId, userClass);
			if ("error".equals(resultMap.get("state"))) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage","删除学生x姓名为"+student.getName()+"，编号为"+studentId+"的学生出错:"+ "修改用户表记录失败");
				return BaseResponse.fail(modelMap);
			}
		}
		
		/* 删除学生用户的数据库账号 */
		String dbName = student.getDbName();
		String username = student.getUsername();
		Database database = databaseService.getDatabaseByName(dbName);
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		
		try {
			Connection connection = dbConnection.getConnect();
			DBUserTool dbUserTool = new DBUserTool(connection);
			dbUserTool.dropUser(username);			
		} catch(SQLException e) {
			log.error("Exception :", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "删除学生x姓名为"+student.getName()+"，编号为"+studentId+"的学生出错:"+"删除学生数据库帐号失败");
			modelMap.put("detail", e.getMessage());
			return BaseResponse.fail(modelMap);
		} finally {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				log.error("Exception :", e);
			}
		}
		
		return BaseResponse.success(null);
	}
	
	/* 删除指定班级全部学生信息 */
	@PostMapping(value = "/deleteClassStudent")
	public BaseResponse<Map<String, Object>> deleteClassStudent(@RequestParam(value = "classId") String classId) {
		Map<String, Object> modelMap = new HashMap<>();
		//获取班级内全部学生信息
		List<Map<String, Object>> studentList = studentService.getStudentByClassId(classId);
		if (studentList.size() == 0) {
			modelMap.put("errmessage", "删除班级名称为"+classId+"失败，班级内不存在学生用户");
			return BaseResponse.fail(modelMap);
		}
		
		//循环，依次删除学生账号
		for(Map<String, Object> studentMap : studentList) {
			//获取学生编号
			String studentId;
			Object studentIdObj = studentMap.get("id");
			if (studentIdObj instanceof String)
				studentId = (String) studentIdObj;
			else {
				modelMap.put("errmessage", "学生编号获取失败");
				return BaseResponse.fail(modelMap);
			}
			String studentName;
			Object studentNameObj = studentMap.get("name");
			if (studentNameObj == null || studentNameObj instanceof String)
				studentName = (String) studentNameObj;
			else {
				modelMap.put("errmessage", "删除学生编号为"+studentId+"的学生出错：学生姓名获取失败");
				return BaseResponse.fail(modelMap);
			}

			//开启事务
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
			
			//删除学生记录，并获取数据库和用户名信息
			Map<String, Object> resultMap = deleteStudentUser(studentId);
			if ("error".equals(resultMap.get("state"))) {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+resultMap.get("errmessage"));
				return BaseResponse.fail(modelMap);
			}
			
			/* 删除学生用户的数据库账号 */
			String dbName;
			Object dbNameObj = resultMap.get("dbName");
			if (dbNameObj instanceof String)
				dbName = (String) dbNameObj;
			else {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错：数据库名称获取失败");
				return BaseResponse.fail(modelMap);
			}
			String username;
			Object usernameObj = resultMap.get("username");
			if (usernameObj instanceof String)
				username = (String) usernameObj;
			else {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错：学生数据库用户名称获取失败");
				return BaseResponse.fail(modelMap);
			}
			Database database = databaseService.getDatabaseByName(dbName);
			Teacher teacher = teacherService.getTeacher(database.getUserId());
			DBConnection dbConnection = new DBConnection(dbDatasource);
			SetDBConnection.setDBConnection(dbConnection, database, teacher);
			
			try {
				Connection connection = dbConnection.getConnect();
				DBUserTool dbUserTool = new DBUserTool(connection);
				dbUserTool.dropUser(username);
				//事务提交
				dataSourceTransactionManager.commit(transactionStatus);
			} catch(SQLException e) {
				log.error("Exception :", e);
				//事务回滚
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+resultMap.get("errmessage"));
				modelMap.put("detail", e.getMessage());
				return BaseResponse.fail(modelMap);
			} finally {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
			}
		}
		
		return BaseResponse.success(null);
	}

	/*删除选中学生信息*/
	@PostMapping(value = "/deleteSomeStudents")
	public BaseResponse<Map<String, Object>> deleteSomeStudents(@RequestBody List<delstudents> stus) {
		Map<String, Object> modelMap = new HashMap<>();

		//前端去判断一下，不能不选。
		if(stus.size() == 0) {
			modelMap.put("errmessage", "未选择要删除的用户");
			return BaseResponse.fail(modelMap);
		}
		
		//循环，依次删除学生账号
		for(delstudents studentMap : stus) {
			//获取学生编号
			String studentId = studentMap.getStuId();
			String studentName = studentMap.getStuName();
			Student student = studentService.getStudent(studentId);
			if (student == null) {
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+"学生用户不存在");
				return BaseResponse.fail(modelMap);
			}
			if (student.isIsactive()) {
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+"学生用户处于激活状态");
				return BaseResponse.fail(modelMap);
			}
			//开启事务
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
			
			//删除学生记录，并获取数据库和用户名信息
			Map<String, Object> resultMap = deleteStudentUser(studentId);
			if ("error".equals(resultMap.get("state"))) {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+resultMap.get("errmessage"));
				return BaseResponse.fail(modelMap);
			}
			
			/* 删除学生用户的数据库账号 */
			String dbName;
			Object dbNameObj = resultMap.get("dbName");
			if (dbNameObj instanceof String)
				dbName = (String) dbNameObj;
			else {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错：数据库名称获取失败");
				return BaseResponse.fail(modelMap);
			}
			String username;
			Object usernameObj = resultMap.get("username");
			if (usernameObj instanceof String)
				username = (String) usernameObj;
			else {
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错：学生数据库用户名称获取失败");
				return BaseResponse.fail(modelMap);
			}
			Database database = databaseService.getDatabaseByName(dbName);
			Teacher teacher = teacherService.getTeacher(database.getUserId());
			DBConnection dbConnection = new DBConnection(dbDatasource);
			SetDBConnection.setDBConnection(dbConnection, database, teacher);
			
			try {
				Connection connection = dbConnection.getConnect();
				DBUserTool dbUserTool = new DBUserTool(connection);
				dbUserTool.dropUser(username);
				//事务提交
				dataSourceTransactionManager.commit(transactionStatus);
			} catch(SQLException e) {
				log.error("Exception :", e);
				//事务回滚
				dataSourceTransactionManager.rollback(transactionStatus);
				modelMap.put("errmessage", "删除学生姓名为"+studentName+"，编号为"+studentId+"的学生出错："+resultMap.get("errmessage"));
				modelMap.put("detail", e.getMessage());
				return BaseResponse.fail(modelMap);
			} finally {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
			}
		}
		
		return BaseResponse.success(null);
	}
	
	/* 禁用学生信息 */
	@PostMapping(value = "/forbidStudent")
	public BaseResponse<Map<String, Object>> forbidStudent(@RequestParam(value = "id") String id) {
		Map<String, Object> result = studentService.forbidStudent(id);
		if (!result.get("state").equals("success")) {
			return BaseResponse.fail(result);
		}
		
		return BaseResponse.success(result);
	}

	/*禁用部分学生新信息*/
	@PostMapping(value = "/forbidSomeStudents")
	public BaseResponse<Map<String, Object>> forbidSomeStudents(@RequestBody List<delstudents> stus) {
		for(delstudents studentMap : stus) {
			String studentId = studentMap.getStuId();
			String studentName = studentMap.getStuName();
			Map<String, Object> result = studentService.forbidStudent(studentId);
			if (!result.get("state").equals("success")) {
				result.put("errmessage", "禁用学生姓名为"+studentName+"，编号为"+studentId+"的学生出错");
				return BaseResponse.fail(result);
			}
		}
		Map<String, Object> result1 = new HashMap<>();
		result1.put("state", "success");
		return BaseResponse.success(result1);
	}
	
	/* 禁用某个班级学生信息 */
	@PostMapping(value = "/forbidClassStudent")
	public BaseResponse<Map<String, Object>> forbidClassStudent(@RequestParam(value = "classId") String classId) {
		Map<String, Object> result = studentService.forbidClassStudent(classId);
		if (!result.get("state").equals("success")) {
			return BaseResponse.fail(result);
		}
		
		return BaseResponse.success(result);
	}
	
	/* 激活学生信息 */
	@PostMapping(value = "/activateStudent")
	public BaseResponse<Map<String, Object>> activateStudent(@RequestParam(value = "id") String id) {
		Map<String, Object> result = studentService.activateStudent(id);
		if (!result.get("state").equals("success")) {
			return BaseResponse.fail(result);
		}
		
		return BaseResponse.success(result);
	}
	
	/* 激活某些学生信息 */
	@PostMapping(value = "/activateSomeStudents")
	public BaseResponse<Map<String, Object>> activateSomeStudents(@RequestBody List<delstudents> stus) {
		
		for(delstudents studentMap : stus) {
			String studentId = studentMap.getStuId();
			String studentName = studentMap.getStuName();
			Map<String, Object> result = studentService.activateStudent(studentId);
			if (!result.get("state").equals("success")) {
				result.put("errmessage", "激活学生姓名为"+studentName+"，编号为"+studentId+"的学生出错");
				return BaseResponse.fail(result);
			}
		}
		Map<String, Object> result1 = new HashMap<>();
		result1.put("state", "success");
		return BaseResponse.success(result1);
	}
	
	/* 激活某个班级学生信息 */
	@PostMapping(value = "/activateClassStudent")
	public BaseResponse<Map<String, Object>> activateClassStudent(@RequestParam(value = "classId") String classId) {
		Map<String, Object> result = studentService.activateClassStudent(classId);
		if (!result.get("state").equals("success")) {
			return BaseResponse.fail(result);
		}
		
		return BaseResponse.success(result);
	}
	
	/* 获取指定班级学生信息 */
	@GetMapping(value = "/getStudentByClassId")
	public BaseResponse<List<Map<String, Object>>> getStudentByClassId(@RequestParam(value = "classId") String classId) {
		
		return BaseResponse.success(studentService.getStudentByClassId(classId));
	}
	
	/* 获取指定教师的全部学生信息 */
	@GetMapping(value = "/getStudentByTeacherId")
	public BaseResponse<List<Map<String, Object>>> getStudentByTeacherId(@RequestParam(value = "teacherId") String teacherId) {

		return BaseResponse.success(studentService.getStudentByTeacherId(teacherId));
	}
	
	/* 通过id搜索指定学生信息 */
	@GetMapping(value = "/getStudentById")
	public BaseResponse<Map<String, Object>> getStudentById(@RequestParam(value = "id") String id) {
		Map<String, Object> result = studentService.getStudentById(id);
		if (result == null || result.isEmpty())
			return BaseResponse.success(null);
		else
		    return BaseResponse.success(result);
	}
	
	/* 通过id模糊搜索学生信息 */
	@GetMapping(value = "/getStudentsById")
	public BaseResponse<List<Map<String, Object>>> getStudentsById(@RequestParam(value = "id") String id,
			                                                @RequestParam(value = "teacherId", required = false) String teacherId,
			                                                @RequestParam(value = "classId", required = false) String classId) {
		
		return BaseResponse.success(studentService.getStudentsById(id, teacherId, classId));
	}
	
	/* 通过name模糊搜索学生信息 */
	@GetMapping(value = "/getStudentByName")
	public BaseResponse<List<Map<String, Object>>> getStudentByName(@RequestParam(value = "name") String name,
                                                                    @RequestParam(value = "teacherId", required = false) String teacherId,
                                                                    @RequestParam(value = "classId", required = false) String classId) {
		
		return BaseResponse.success(studentService.getStudentByName(name, teacherId, classId));
	}

	/* 通过nameorid模糊搜索学生信息 */
	@GetMapping(value = "/getStudentByNameOrId")
	public BaseResponse<List<Map<String, Object>>> getStudentByNameOrId(@RequestParam(value = "nameorid") String nameorid,
																	@RequestParam(value = "teacherId", required = false) String teacherId,
																	@RequestParam(value = "classId", required = false) String classId) {

		return BaseResponse.success(studentService.getStudentByNameOrId(nameorid,teacherId, classId));
	}
	
	/* 查询所有学生的信息 */
	@GetMapping(value = "/getAllStudents")
	public BaseResponse<List<Map<String, Object>>> getAllStudents() {
		return BaseResponse.success(studentService.getAllStudents());
	}
	
	/* 批量导入学生账号 */
	@PostMapping(value = "/importStudents")
	@Transactional
	public BaseResponse<Map<String, Object>> importStudents(@RequestParam(value = "file") MultipartFile file,
			                                                @RequestParam(value = "classId") String classId,
			                                                @RequestParam(value = "dbName", required = false, defaultValue = "practicedb1") String dbName) {
		Map<String, Object> modelMap = new HashMap<>();
		Workbook workbook = null;
		
		/* 判断文件格式是否正确 */
		if (file == null) {
			modelMap.put("errmessage", "file is null");
			return BaseResponse.fail(modelMap);
		}
		
		/* 根据文件创建Excel处理对象 */
		try {
			workbook = FileTrans.getWorkbook(file);
		} catch (IOException e) {
			log.error("Exception :", e);
			modelMap.put("errmessage", "无法读取文件");
			return BaseResponse.fail(modelMap);
		}
		
		if (workbook == null) {
			modelMap.put("errmessage", "文件格式错误，不是.xls或.xlsx文件");
			return BaseResponse.fail(modelMap);
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			modelMap.put("errmessage", "无法获取文件中第一张sheet");
			return BaseResponse.fail(modelMap);
		}
		
		/* 判断数据库是否存在，是否为学生实践数据库 */
		Database database = databaseService.getDatabaseByName(dbName);
		if (database == null) {
			modelMap.put("errmessage", "数据库不存在");
			return BaseResponse.fail(modelMap);
		}
		if (database.getDbClass() != 1 && database.isUseForStu()) {
			modelMap.put("errmessage", "数据库不能用于学生实践");
			return BaseResponse.fail(modelMap);
		}
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		
		try {
			/* 打开实践数据库连接，并设置其为非自动commit */
			Connection connection = dbConnection.getConnect();
			connection.setAutoCommit(false);
			DBUserTool dbUserTool = new DBUserTool(connection);
			
			/* 循环，依次读取每一行的记录 */
			for (int r = 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null) 
					continue;
				
				/* 从每一行中获取相应数据 */
				if (row.getCell(0) == null || 
						(row.getCell(0).getCellType() != CellType.STRING 
						&& row.getCell(0).getCellType() != CellType.NUMERIC)) {
					connection.rollback();
					modelMap.put("errmessage", "第" + r + "行第1列不存在或格式存在问题");
					throw new ImportStudentException();
				}

				String name = row.getCell(0).getCellType() == CellType.BLANK ? null : FileTrans.getCellValue(row.getCell(0));

				//密码初始化写死
                String pwd = "12345678";
                
                if (row.getCell(1) == null || 
						(row.getCell(1).getCellType() != CellType.STRING 
						&& row.getCell(1).getCellType() != CellType.NUMERIC)) {
					connection.rollback();
                	modelMap.put("errmessage", "第" + r + "行第2列格式存在问题");
					throw new ImportStudentException();
				}
                
                String id = row.getCell(1).getCellType() == CellType.BLANK ? null : FileTrans.getCellValue(row.getCell(1));
                
                if (row.getCell(2) == null || 
						(row.getCell(2).getCellType() != CellType.STRING 
						&& row.getCell(2).getCellType() != CellType.NUMERIC)) {
					connection.rollback();
                	modelMap.put("errmessage", "第" + r + "行第3列格式存在问题");
					throw new ImportStudentException();
				}
                
                String grade = row.getCell(2).getCellType() == CellType.BLANK ? null : FileTrans.getCellValue(row.getCell(2));
                
                /* 配置学生数据库账号 */
        		String username = 's'+id;
        		String password = "stu_"+id+"_123";
				
				/* 创建数据表中的用户信息 */
        		Map<String, Object> result = insertStudentUser(id, pwd, name, grade, classId, dbName, username, password);
        		if ("error".equals(result.get("state"))) {
					connection.rollback();
        			modelMap.put("errmessage", "增加学生"+name+"，学号"+id+" 失败");
        			if (result.get("detail") != null)
        				modelMap.put("detail", result.get("detail"));
        			throw new ImportStudentException();
        		}
				/* 创建实践数据库中的学生用户账号 */
        		try {
        			dbUserTool.createUser(username, password);
        		} catch (SQLException e) {
        			log.error("Exception :", e);
					connection.rollback();
					modelMap.put("errmessage", "创建学生"+name+"对应的数据库账号失败");
					modelMap.put("detail", e.getMessage());
					throw new ImportStudentException();
        		}
			}
			
			/* 循环结束后，实践数据库连接commit */
			connection.commit();
			
		} catch (SQLException e1) {
		    log.error("Exception :", e1);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "database error");
			return BaseResponse.fail(modelMap);
		} catch (ImportStudentException e2) {
			log.error("Exception :", e2);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			if (modelMap.get("errmessage") == null)
			    modelMap.put("errmessage", "未知错误，请联系管理员解决");
			return BaseResponse.fail(modelMap);
		} finally {
			try {
				dbConnection.close();
			} catch(SQLException e) {
				log.error("Exception :", e);
			}
		}
		
		return BaseResponse.success(null);
	}
	
	/* 下载导入学生模板 */
	@GetMapping(value = "/downloadTemplate")
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		
		try {
			/* 从resource中获取模板 */
			ClassPathResource classPathResource = new ClassPathResource("templates"+File.separator+"importStudentTemplates.xls");
			EncodedResource encodedResource = new EncodedResource(classPathResource, "UTF-8");
			InputStream inputStream = encodedResource.getInputStream();
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("importStudentTemplates.xls", "UTF-8"));
			inputStream.transferTo(response.getOutputStream());
			
			inputStream.close();
		} catch (IOException e) {
			log.error("Exception :", e);
			modelMap.put("errmessage", "can't download this file");
			modelMap.put("detail", e.getMessage());
			BaseResponse<Map<String, Object>> ret_Response = BaseResponse.fail(modelMap);
			PrintWriter out = response.getWriter();
			out.print(ret_Response);
			out.flush();
			out.close();
		}
	}
	
	/* 向用户表和学生表中插入数据 */
	private Map<String, Object> insertStudentUser(String id, String pwd, String name, String grade, String classId,
			                                      String dbName, String username, String password) {
		/* 向用户表中增加学生信息 */
		Map<String, Object> result = sysUserService.insertSysUser(id, pwd, (short)3);
		if (result.get("state").equals("error")) {
			return result;
		}
		
		/* 向学生表中增加学生信息 */
		result = studentService.insertStudent(id, name, grade, classId, dbName, username, password, true);		
		return result;
	}
	
	/* 从用户表和学生表中删除数据 */
	private Map<String, Object> deleteStudentUser(String id) {
        Map<String, Object> modelMap = new HashMap<>();
		
		/* 获取用户对象，为空表明不存在 */
		SysUser sysUser = sysUserService.getSysUserById(id);
		if (sysUser == null) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "用户不存在");
			return modelMap;
		}
		
		/* 获取用户类别 */
		short userClass = sysUser.getUserClass();
		if (userClass == 1 || userClass == 2) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "用户不具有学生权限");
			return modelMap;
		}
		Student student = studentService.getStudent(id);
		if (student == null) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "学生用户不存在");
			return modelMap;
		}
		
		if (student.isIsactive()) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "学生用户处于激活状态");
			return modelMap;
		}
		
		/* 删除学生表中的记录 */
		Map<String, Object> resultMap = studentService.deleteStudent(id);
		if ("error".equals(resultMap.get("state"))) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "删除学生表记录失败");
			return modelMap;
		}
		
		/* 删除或修改用户表中的记录 */
		if (userClass == 3 || userClass == 4) {
			resultMap = sysUserService.deleteSysUser(id);
			if ("error".equals(resultMap.get("state"))) {
				modelMap.put("state", "error");
				modelMap.put("errmessage", "删除用户表记录失败");
				return modelMap;
			}
		} else {
			userClass = 2;
			resultMap = sysUserService.modifySysUserClass(id, userClass);
			if ("error".equals(resultMap.get("state"))) {
				modelMap.put("state", "error");
				modelMap.put("errmessage", "修改用户表记录失败");
				return modelMap;
			}
		}
		
		modelMap.put("state", "success");
		modelMap.put("dbName", student.getDbName());
		modelMap.put("username", student.getUsername());
		return modelMap;
	}
}
