package com.example.demo.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Student;
import com.example.demo.entity.SysUser;
import com.example.demo.service.StudentService;
import com.example.demo.service.SysUserService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBDatasource;
import com.example.demo.tool.DBUserTool;
import com.example.demo.tool.PropertiesTool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/system")
@CrossOrigin
@RestController
public class SystemController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private DBDatasource dbDatasource;
	@Autowired
	private ContextRefresher contextRefresher;
	
	/* 授予学生或访客教师用户身份 */
	@PostMapping(value = "/grantTeacher")
	@Transactional
	public BaseResponse<Map<String, Object>> grantTeacher(@RequestParam(value = "id") String id) {
		Map<String, Object> modelMap = new HashMap<>();
		
		SysUser sysUser = sysUserService.getSysUserById(id);
		if (sysUser == null) {
			modelMap.put("errmessage", "用户不存在");
			return BaseResponse.fail(modelMap);
		}
		
		if (sysUser.getUserClass() != 3 && sysUser.getUserClass() != 4 && sysUser.getUserClass() != 5 && sysUser.getUserClass() != 6) {
			modelMap.put("errmessage", "用户已具有教师用户权限");
			return BaseResponse.fail(modelMap);
		}
		
		short userClass = sysUser.getUserClass();
		if (userClass == 3) userClass = 5;
		if (userClass == 4) userClass = 6;
		
		/* 修改用户表中用户的身份信息 */
		Map<String, Object> result = sysUserService.modifySysUserClass(id, userClass);
		if (result.get("state").equals("error")) {
			return BaseResponse.fail(result);
		}
		
		/* 配置相应的数据库用户账户 */
		String username = 't' + id;
		String password = "tea_" + id + "_123";
		
		/* 从学生表中获取学生姓名 */
		Student student = studentService.getStudent(id);
		String name = student.getName();
		
		/* 将数据插入教师用户表中 */
		result = teacherService.insertTeacher(id, name, username, password);
		if (result.get("state").equals("error")) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return BaseResponse.fail(result);
		}
		
		/* 在数据库中创建相应用户 */
		DBConnection dbConnection = new DBConnection(dbDatasource);
		try {
			Connection connection = dbConnection.getConnect();
			DBUserTool dbUserTool = new DBUserTool(connection);
			dbUserTool.createSuperUser(username, password);
		} catch(SQLException e) {
			log.error("Exception :", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "无法创建相应数据库用户");
			modelMap.put("detail", e.getMessage());
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

	/* 查看当前数据库连接信息 */
	@GetMapping(value = "/getConnectProperties")
	public BaseResponse<Map<String, Object>> getConnectProperties() {
		try {
		    PropertiesTool propertiesTool = new PropertiesTool();
		    
		    Map<String, Object> resultMap = propertiesTool.getDatabaseConnectProperties();
		    
		    if (resultMap == null || resultMap.isEmpty())
		    	return BaseResponse.success(null);
		    else
		        return BaseResponse.success(resultMap);
		} catch (IOException e) {
		    log.error("Exception :", e);
		    Map<String, Object> modelMap = new HashMap<>();
		    modelMap.put("errmessage", e.getMessage());
		    return BaseResponse.fail(modelMap);
		}
	}
	
	/* 修改当前数据库连接信息 */
	@PostMapping(value = "/setConnectProperties")
	public BaseResponse<Map<String, Object>> setConnectProperties(@RequestParam(value = "IP") String IP,
			                                                      @RequestParam(value = "port", required = false, defaultValue = "0" ) int port,
			                                                      @RequestParam(value = "dbName") String dbName,
			                                                      @RequestParam(value = "username") String username,
			                                                      @RequestParam(value = "password") String password
			                                                      ) {
		try {
            PropertiesTool propertiesTool = new PropertiesTool();
		    
		    boolean result = propertiesTool.setDatabaseConnectProperties(IP, port, dbName, username, password);
		    
		    if (result)
		    	return BaseResponse.success(null);
		    else
		    	return BaseResponse.fail(null);
		} catch (IOException e) {
		    log.error("Exception :", e);
		    Map<String, Object> modelMap = new HashMap<>();
		    modelMap.put("errmessage", e.getMessage());
		    return BaseResponse.fail(modelMap);
		}
	}
	
	/* 刷新上下文缓存内容 */
	@PostMapping(value = "/refresh")
	public BaseResponse<Map<String, Object>> refresh() {
		contextRefresher.refresh();
			
		return BaseResponse.success(null);
	}
}
