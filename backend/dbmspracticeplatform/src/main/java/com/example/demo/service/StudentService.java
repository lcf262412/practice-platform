package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Student;

public interface StudentService extends IService<Student> {
	
	/* 增加学生信息 */
	Map<String, Object> insertStudent(String id, String name, String grade, String classId, String dbName,
			                          String username, String password, boolean isactive);
	
	/* 禁用学生信息 */
	Map<String, Object> forbidStudent(String id);
	
	/* 禁用某个班级学生信息 */
	Map<String, Object> forbidClassStudent(String classId);
	
	/* 激活学生信息 */
	Map<String, Object> activateStudent(String id);
	
	/* 激活某个班级学生信息 */
	Map<String, Object> activateClassStudent(String classId);
	
	/* 删除指定学生信息 */
	Map<String, Object> deleteStudent(String id);
	
	/* 获取指定班级学生信息 */
	List<Map<String, Object>> getStudentByClassId(String classId);
	
	/* 获取指定教师的全部学生信息 */
	List<Map<String, Object>> getStudentByTeacherId(String teacherId);
	
	/* 获取指定学生对象 */
	Student getStudent(String id);
	
	/* 获取指定学生信息-return Map */
	Map<String, Object> getStudentById(String id);
	
	/* 模糊搜索指定学生信息-return Map */
	List<Map<String, Object>> getStudentsById(String id, String teacherId, String classId);
	
	/* 通过名字模糊搜索指定学生信息 */
	List<Map<String, Object>> getStudentByName(String name, String teacherId, String classId);

	/* 通过名字模糊搜索指定学生信息 */
	List<Map<String, Object>> getStudentByNameOrId(String nameorid,String teacherId, String classId);
	
	/* 获取全部学生信息 */
	List<Map<String, Object>> getAllStudents();

	/* 获取全部学生信息 */
	Map<String, Object> useStuidgetTeaid(String id);
}
