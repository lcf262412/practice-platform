package com.example.demo.controller;

import java.sql.Connection;
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
import com.example.demo.entity.SysUser;
import com.example.demo.entity.Teacher;
import com.example.demo.service.SysUserService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBDatasource;
import com.example.demo.tool.DBUserTool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/teacher")
@CrossOrigin
@RestController
public class TeacherController {
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private DBDatasource dbDatasource;
	
	/* 增加一名教师用户 */
	@PostMapping(value = "/addTeacher")
	@Transactional
	public BaseResponse<Map<String, Object>> addTeacher(@RequestParam(value = "id") String id,
			                                            @RequestParam(value = "pwd", required = false, defaultValue = "12345678") String pwd,
			                                            @RequestParam(value = "name") String name) {
		Map<String, Object> modelMap = new HashMap<>();
		/* 将数据插入用户表中 */
		Map<String, Object> result = sysUserService.insertSysUser(id, pwd, (short)2);
		if (result.get("state").equals("error")) {
			return BaseResponse.fail(result);
		}
		
		/* 配置相应的数据库用户账户 */
		String username = 't' + id;
		String password = "tea_" + id + "_123";
		
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
	
	/* 删除指定教师账号 */
	@PostMapping(value = "/deleteTeacher")
	@Transactional
	public BaseResponse<Map<String, Object>> deleteTeacher(@RequestParam(value = "id") String teacherId) {
		Map<String, Object> modelMap = new HashMap<>();
		
		/* 获取用户对象，为空表明不存在 */
		SysUser sysUser = sysUserService.getSysUserById(teacherId);
		if (sysUser == null) {
			modelMap.put("errmessage", "用户不存在");
			return BaseResponse.fail(modelMap);
		}
		
		/* 获取用户类别 */
		short userClass = sysUser.getUserClass();
		if (userClass == 1) {
			modelMap.put("errmessage", "该用户为管理员，禁止删除");
			return BaseResponse.fail(modelMap);
		}
		if (userClass != 2 && userClass != 5 && userClass != 6) {
			modelMap.put("errmessage", "用户不具有教师权限");
			return BaseResponse.fail(modelMap);
		}
		Teacher teacher = teacherService.getTeacher(teacherId);
		if (teacher == null) {
			modelMap.put("errmessage", "教师用户不存在");
			return BaseResponse.fail(modelMap);
		}
		
		/* 删除教师表中相应记录 */
		Map<String, Object> resultMap = teacherService.deleteTeacher(teacherId);
		if ("error".equals(resultMap.get("state"))) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "删除教师表记录失败");
			return BaseResponse.fail(modelMap);		
		}
		
		/* 删除或修改用户表中的相应记录 */
		if (userClass == 2) {
			resultMap = sysUserService.deleteSysUser(teacherId);
			if ("error".equals(resultMap.get("state"))) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "删除用户表记录失败");
				return BaseResponse.fail(modelMap);
			}
		} else {
			if (userClass == 5) userClass = 3;
			if (userClass == 6) userClass = 4;
			resultMap = sysUserService.modifySysUserClass(teacherId, userClass);
			if ("error".equals(resultMap.get("state"))) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				modelMap.put("errmessage", "修改用户表记录失败");
				return BaseResponse.fail(modelMap);
			}
		}
		
		/* 根据用户类别，删除数据库中相应用户 */
		DBConnection dbConnection = new DBConnection(dbDatasource);
		String username = teacher.getUsername();
		
		try {
			Connection connection = dbConnection.getConnect();
			DBUserTool dbUserTool = new DBUserTool(connection);
			dbUserTool.dropUser(username);			
		} catch(SQLException e) {
			log.error("Exception :", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			modelMap.put("errmessage", "删除教师用户失败");
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
	
	/* 查询所有教师的信息 */
	@GetMapping(value = "/getAllTeachers")
	public BaseResponse<List<Map<String, Object>>> getAllTeachers() {
		return BaseResponse.success(teacherService.getAllTeachers());
	}
}
