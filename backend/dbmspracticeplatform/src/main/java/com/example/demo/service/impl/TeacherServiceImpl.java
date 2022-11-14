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
import com.example.demo.entity.Exercise;
import com.example.demo.entity.Question;
import com.example.demo.entity.StuClass;
import com.example.demo.entity.Teacher;
import com.example.demo.mapper.DatabaseMapper;
import com.example.demo.mapper.ExerciseMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.StuClassMapper;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.service.TeacherService;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private DatabaseMapper databaseMapper;
    @Autowired
    private StuClassMapper stuClassMapper;
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private QuestionMapper questionMapper;
    
    /* 增加教师用户 */
    @Override
	public Map<String, Object> insertTeacher(String id, String name, String username, String password) {
    	Map<String, Object> modelMap = new HashMap<>();
    	
    	QueryWrapper<Teacher> teacherWrapper = new QueryWrapper<>();
    	teacherWrapper.eq("id", id);
    	Teacher teacher = teacherMapper.selectOne(teacherWrapper);
    	if (teacher != null) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "教师已存在");
    		return modelMap;
    	}
    	
    	teacher = new Teacher();
    	teacher.setId(id);
    	teacher.setName(name);
    	teacher.setUsername(username);
    	teacher.setPassword(password);
    	
    	int mes = teacherMapper.insert(teacher);
    	if (mes == 1) {
    		modelMap.put("state", "success");
    	} else {
    		modelMap.put("state", "error");
    	}
    	
    	return modelMap;
    }
    
    /* 删除教师用户  */
    @Override
	public Map<String, Object> deleteTeacher(String id) {
        Map<String, Object> modelMap = new HashMap<>();
    	
    	QueryWrapper<Teacher> teacherWrapper = new QueryWrapper<>();
    	teacherWrapper.eq("id", id);
    	Teacher teacher = teacherMapper.selectOne(teacherWrapper);
    	if (teacher == null) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "教师用户不存在");
    		return modelMap;
    	}
    	
    	QueryWrapper<StuClass> stuClassWrapper = new QueryWrapper<>();
    	stuClassWrapper.eq("teacherid", id);
    	int stuClassCount = stuClassMapper.selectCount(stuClassWrapper);
    	if (stuClassCount > 0) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "存在教师用户创建的班级，无法删除教师用户");
    		return modelMap;
    	}
    	
    	QueryWrapper<Exercise> exerciseWrapper = new QueryWrapper<>();
    	exerciseWrapper.eq("teacherid", id);
    	int exerciseCount = exerciseMapper.selectCount(exerciseWrapper);
    	if (exerciseCount > 0) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "存在教师用户创建的试卷，无法删除教师用户");
    		return modelMap;
    	}
    	
    	QueryWrapper<Question> questionWrapper = new QueryWrapper<>();
    	questionWrapper.eq("teacherid", id);
    	int questionCount = questionMapper.selectCount(questionWrapper);
    	if (questionCount > 0) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "存在教师用户创建的题目，无法删除教师用户");
    		return modelMap;
    	}
    	
    	QueryWrapper<Database> databaseWrapper = new QueryWrapper<>();
    	databaseWrapper.eq("userid", id);
    	int databaseCount = databaseMapper.selectCount(databaseWrapper);
    	if (databaseCount > 0) {
    		modelMap.put("state", "error");
    		modelMap.put("errmessage", "存在教师用户创建的数据库，无法删除教师用户");
    		return modelMap;
    	}
    	
    	int mes = teacherMapper.delete(teacherWrapper);
    	if (mes == 1) {
    		modelMap.put("state", "success");
    	} else {
    		modelMap.put("state", "error");
    	}
    	
    	return modelMap;
    }

    /* 获取指定教师对象 */
    public Teacher getTeacher(String id) {
    	QueryWrapper<Teacher> teacherWrapper = new QueryWrapper<>();
    	teacherWrapper.eq("id", id);
    	return teacherMapper.selectOne(teacherWrapper);
    }
    
    /* 查询全部教师用户 */
    @Override
	public List<Map<String, Object>> getAllTeachers() {
    	List<Teacher> teachers = teacherMapper.getAllTeachers();
    	List<Map<String, Object>> result = new ArrayList<>();
    	
    	for (Teacher teacher : teachers) {
    		Map<String, Object> modelMap = new HashMap<>();
    		modelMap.put("id", teacher.getId());
    		modelMap.put("name", teacher.getName());
    		
    		result.add(modelMap);
    	}
    	
    	return result;
    }
	
}
