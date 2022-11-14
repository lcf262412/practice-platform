package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.StuClass;

public interface StuClassService extends IService<StuClass> {
	/* 增加班级 */
	Map<String, Object> insertStuClass(String id, String teacherId, String semester);
	
	/* 删除班级 */
	Map<String, Object> deleteStuClass(String id);
	
	/* 查询单个班级 */
	StuClass getStuClassById(String id);
	
	/* 查询教师用户的全部班级 */
	List<Map<String, Object>> getStuClassByteacherId(String teacherId);

}
