package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Teacher;

public interface TeacherService extends IService<Teacher> {
    
	/* 增加教师用户 */
	Map<String, Object> insertTeacher(String id, String name, String username, String password);
    
    /* 删除教师用户 */
	Map<String, Object> deleteTeacher(String id);
	
	/* 获取指定教师对象 */
	Teacher getTeacher(String id);
    
    /* 查询全部教师用户 */
	List<Map<String, Object>> getAllTeachers();

}
