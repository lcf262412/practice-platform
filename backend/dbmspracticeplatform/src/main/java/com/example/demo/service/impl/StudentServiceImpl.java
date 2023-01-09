package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.SQLResult;
import com.example.demo.entity.Student;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.service.StudentService;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService{
    @Autowired
    StudentMapper studentMapper;
	
	/* 增加学生信息 */
	@Override
	public Map<String, Object> insertStudent(String id, String name, String grade, String classId, String dbName,
			                          String username, String password, boolean isactive) {
		Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("id", id);
		Student student = studentMapper.selectOne(studentWrapper);
		if (student != null) {
			modelMap.put("state", "error");
			return modelMap;
		}
		
		student = new Student();
		student.setId(id);
		student.setName(name);
		student.setGrade(grade);
		student.setClassId(classId);
		student.setDbName(dbName);
		student.setUsername(username);
		student.setPassword(password);
		student.setIsactive(isactive);
		
		int mes = studentMapper.insert(student);
		if (mes == 1) {
			modelMap.put("state", "success");
		} else {
			modelMap.put("state", "error");
		}
		
		return modelMap;
	}
	
	/* 禁用学生信息 */
	@Override
	public Map<String, Object> forbidStudent(String id) {
		Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("id", id);
		Student student = studentMapper.selectOne(studentWrapper);
		if (student == null) {
			modelMap.put("state", "error");
			return modelMap;
		}
		
		UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
		studentUpdateWrapper.eq("id", id).set("isactive", false);
		int mes = studentMapper.update(null, studentUpdateWrapper);
		if (mes == 1) {
			modelMap.put("state", "success");
		} else {
			modelMap.put("state", "error");
		}
		
		return modelMap;
	}
	
	/* 禁用某个班级学生信息 */
	@Override
	public Map<String, Object> forbidClassStudent(String classId) {
		Map<String, Object> modelMap = new HashMap<>();
		UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
		studentUpdateWrapper.eq("classid", classId).set("isactive", false);
		studentMapper.update(null, studentUpdateWrapper);
		modelMap.put("state", "success");
		return modelMap;
	}
	
    /* 激活学生信息 */
	public Map<String, Object> activateStudent(String id) {
        Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("id", id);
		Student student = studentMapper.selectOne(studentWrapper);
		if (student == null) {
			modelMap.put("state", "error");
			return modelMap;
		}
		
		UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
		studentUpdateWrapper.eq("id", id).set("isactive", true);
		int mes = studentMapper.update(null, studentUpdateWrapper);
		if (mes == 1) {
			modelMap.put("state", "success");
		} else {
			modelMap.put("state", "error");
		}
		
		return modelMap;
	}
	
	/* 激活某个班级学生信息 */
	public Map<String, Object> activateClassStudent(String classId) {
		Map<String, Object> modelMap = new HashMap<>();
		UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
		studentUpdateWrapper.eq("classid", classId).set("isactive", true);
		studentMapper.update(null, studentUpdateWrapper);
		modelMap.put("state", "success");
		return modelMap;
	}
	
	/* 删除指定学生信息 */
	public Map<String, Object> deleteStudent(String id) {
        Map<String, Object> modelMap = new HashMap<>();
    	
    	QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
    	studentWrapper.eq("id", id);
    	Student student = studentMapper.selectOne(studentWrapper);
    	if (student == null) {
    		modelMap.put("state", "error");
			modelMap.put("detail", "学生不存在");
    		return modelMap;
    	}
    	
    	int mes = studentMapper.delete(studentWrapper);
    	if (mes == 1) {
    		modelMap.put("state", "success");
    	} else {
    		modelMap.put("state", "error");
    	}
    	
    	return modelMap;
	}
	
	/* 获取指定班级学生信息 */
	@Override
	public List<Map<String, Object>> getStudentByClassId(String classId) {
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("classid", classId);
		List<Student> studentList = studentMapper.selectList(studentWrapper);
		List<Map<String, Object>> result = new ArrayList<>();
		
		for (Student student : studentList) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", student.getId());
			modelMap.put("name", student.getName());
			modelMap.put("grade", student.getGrade());
			modelMap.put("isactive", student.isIsactive());
			
			result.add(modelMap);
		}
		
		return result;
	}
	
	/* 获取指定教师的全部学生信息 */
	@Override
	public List<Map<String, Object>> getStudentByTeacherId(String teacherId) {
		List<Student> studentList = studentMapper.selectListByTeacherId(teacherId);
		List<Map<String, Object>> result = new ArrayList<>();
		
		for (Student student : studentList) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", student.getId());
			modelMap.put("name", student.getName());
			modelMap.put("grade", student.getGrade());
			modelMap.put("classId", student.getClassId());
			modelMap.put("isactive", student.isIsactive());
			
			result.add(modelMap);
		}
		
		return result;
	}
	
	@Override
	public Student getStudent(String id) {
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("id", id);
		return studentMapper.selectOne(studentWrapper);
	}
	
	/* 获取指定学生信息 */
	@Override
	public Map<String, Object> getStudentById(String id) {
		
		Map<String, Object> modelMap = new HashMap<>();
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("id", id);
		Student student = studentMapper.selectOne(studentWrapper);
		
		if (student == null) return modelMap;
		
		modelMap.put("id", student.getId());
		modelMap.put("name", student.getName());
		modelMap.put("grade", student.getGrade());
		modelMap.put("classId", student.getClassId());
		modelMap.put("isactive", student.isIsactive());
		
		return modelMap;
		
	}
	
	/* 获取指定学生信息 */
	@Override
	public List<Map<String, Object>> getStudentsById(String id, String teacherId, String classId) {
		List<SQLResult> sqlResults = studentMapper.getStudentById('%'+id+'%');
        List<Map<String, Object>> result = new ArrayList<>();
		
		for (SQLResult sqlResult : sqlResults) {
			if (classId != null && !classId.equals(sqlResult.getStudent().getClassId())) continue;
			else if (teacherId != null && !teacherId.equals(sqlResult.getTeacher().getId())) continue;
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", sqlResult.getStudent().getId());
			modelMap.put("name", sqlResult.getStudent().getName());
			modelMap.put("grade", sqlResult.getStudent().getGrade());
			modelMap.put("classId", sqlResult.getStudent().getClassId());
			modelMap.put("isactive", sqlResult.getStudent().isIsactive());
			modelMap.put("t_id", sqlResult.getTeacher().getId());
			modelMap.put("t_name", sqlResult.getTeacher().getName());
			modelMap.put("hasTeacher", sqlResult.getSysUser().getUserClass() == 5 || sqlResult.getSysUser().getUserClass() == 6);
			
			result.add(modelMap);
		}
		return result;
	}
	
	/* 通过名字获取指定学生信息 */
	@Override
	public List<Map<String, Object>> getStudentByName(String name, String teacherId, String classId) {
		List<SQLResult> sqlResults = studentMapper.getStudentByName('%'+name+'%');
        List<Map<String, Object>> result = new ArrayList<>();
		
		for (SQLResult sqlResult : sqlResults) {
			if (classId != null && !classId.equals(sqlResult.getStudent().getClassId())) continue;
			else if (teacherId != null && !teacherId.equals(sqlResult.getTeacher().getId())) continue;
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", sqlResult.getStudent().getId());
			modelMap.put("name", sqlResult.getStudent().getName());
			modelMap.put("grade", sqlResult.getStudent().getGrade());
			modelMap.put("classId", sqlResult.getStudent().getClassId());
			modelMap.put("isactive", sqlResult.getStudent().isIsactive());
			modelMap.put("t_id", sqlResult.getTeacher().getId());
			modelMap.put("t_name", sqlResult.getTeacher().getName());
			modelMap.put("hasTeacher", sqlResult.getSysUser().getUserClass() == 5 || sqlResult.getSysUser().getUserClass() == 6);
			
			result.add(modelMap);
		}
		
		return result;
	}

	/* 通过名字或id获取指定学生信息 */
	@Override
	public List<Map<String, Object>> getStudentByNameOrId(String nameorid,String teacherId, String classId) {
		List<SQLResult> sqlResults = studentMapper.getStudentByNameOrId('%'+nameorid+'%');
		List<Map<String, Object>> result = new ArrayList<>();

		for (SQLResult sqlResult : sqlResults) {
			if (classId != null && !classId.equals(sqlResult.getStudent().getClassId())) continue;
			else if (teacherId != null && !teacherId.equals(sqlResult.getTeacher().getId())) continue;
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", sqlResult.getStudent().getId());
			modelMap.put("name", sqlResult.getStudent().getName());
			modelMap.put("grade", sqlResult.getStudent().getGrade());
			modelMap.put("classId", sqlResult.getStudent().getClassId());
			modelMap.put("isactive", sqlResult.getStudent().isIsactive());
			modelMap.put("t_id", sqlResult.getTeacher().getId());
			modelMap.put("t_name", sqlResult.getTeacher().getName());
			modelMap.put("hasTeacher", sqlResult.getSysUser().getUserClass() == 5 || sqlResult.getSysUser().getUserClass() == 6);

			result.add(modelMap);
		}

		return result;
	}

	/* 获取全部学生用户 */
	@Override
	public List<Map<String, Object>> getAllStudents() {
		List<SQLResult> sqlResults = studentMapper.getAllStudents();
        List<Map<String, Object>> result = new ArrayList<>();
		
		for (SQLResult sqlResult : sqlResults) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", sqlResult.getStudent().getId());
			modelMap.put("name", sqlResult.getStudent().getName());
			modelMap.put("grade", sqlResult.getStudent().getGrade());
			modelMap.put("classId", sqlResult.getStudent().getClassId());
			modelMap.put("isactive", sqlResult.getStudent().isIsactive());
			modelMap.put("t_id", sqlResult.getTeacher().getId());
			modelMap.put("t_name", sqlResult.getTeacher().getName());
			modelMap.put("hasTeacher", sqlResult.getSysUser().getUserClass() == 5 || sqlResult.getSysUser().getUserClass() == 6);
			
			result.add(modelMap);
		}
		
		return result;
	}


	/* 根据学生id获得对应的教师id */
	@Override
	public Map<String, Object> useStuidgetTeaid(String id)
	{
		SQLResult sqlResult = studentMapper.useStuidgetTeaid(id);
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put("t_id", sqlResult.getTeacher().getId());
		return modelMap;

	}
}
