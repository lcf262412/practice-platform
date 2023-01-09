package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.ExerInClass;
import com.example.demo.entity.StuClass;
import com.example.demo.entity.Student;
import com.example.demo.mapper.ExerInClassMapper;
import com.example.demo.mapper.StuClassMapper;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.service.StuClassService;

@Service
public class StuClassServiceImpl extends ServiceImpl<StuClassMapper, StuClass> implements StuClassService {
    @Autowired
    private StuClassMapper stuClassMapper;
    @Autowired
    private ExerInClassMapper exerInClassMapper;
    @Autowired
    private StudentMapper studentMapper;
	
    /* 增加班级 */
	@Override
	public Map<String, Object> insertStuClass(String id, String teacherId, String semester) {
		Map<String, Object> modelMap = new HashMap<>();
		QueryWrapper<StuClass> stuClassWrapper = new QueryWrapper<>();
		stuClassWrapper.eq("id", id);
		StuClass stuClass = stuClassMapper.selectOne(stuClassWrapper);
		if (stuClass != null) {
			modelMap.put("state", "error");
			modelMap.put("detail", "班级名称已被其他老师使用");
			return modelMap;
		}
		
		stuClass = new StuClass();
		stuClass.setId(id);
		stuClass.setTeacherId(teacherId);
		stuClass.setSemester(semester);
		int mes = stuClassMapper.insert(stuClass);
		if (mes == 1) {
			modelMap.put("state", "success");
		} else {
			modelMap.put("state", "error");
		}
		
		return modelMap;
	}

	/* 删除班级 */
	@Override
	@Transactional
	public Map<String, Object> deleteStuClass(String id) {
		Map<String, Object> modelMap = new HashMap<>();
		//判断班级是否存在
		QueryWrapper<StuClass> stuClassWrapper = new QueryWrapper<>();
		stuClassWrapper.eq("id", id);
		StuClass stuClass = stuClassMapper.selectOne(stuClassWrapper);
		if (stuClass == null) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "班级不存在");
			return modelMap;
		}
		
		//判断班级内是否存在学生，存在则不删除
		QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
		studentWrapper.eq("classid", id);
		int studentCount = studentMapper.selectCount(studentWrapper);
		if (studentCount > 0) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "班级内存在学生，请手动删除学生后再删除班级");
			return modelMap;
		}
		
		//删除班级试卷表内记录
		QueryWrapper<ExerInClass> exerInClassWrapper = new QueryWrapper<>();
		exerInClassWrapper.eq("stuclassid", id);
		exerInClassMapper.delete(exerInClassWrapper);
		
		//删除班级表内记录
		int mes = stuClassMapper.delete(stuClassWrapper);
		if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
            modelMap.put("errmessage","删除失败，请联系数据库管理员解决");
        }
			
		return modelMap;
	}

	/* 查询单个班级 */
	@Override
	public StuClass getStuClassById(String id) {
		QueryWrapper<StuClass> stuClassWrapper = new QueryWrapper<>();
		stuClassWrapper.eq("id", id);
		return stuClassMapper.selectOne(stuClassWrapper);
	}

	/* 查询教师用户的全部班级 */
	@Override
	public List<Map<String, Object>> getStuClassByteacherId(String teacherId) {
		QueryWrapper<StuClass> stuClassWrapper = new QueryWrapper<>();
		stuClassWrapper.eq("teacherId", teacherId);
		List<StuClass> stuClassList = stuClassMapper.selectList(stuClassWrapper);
		List<Map<String, Object>> result = new ArrayList<>();
		for (StuClass stuClass : stuClassList) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("id", stuClass.getId());
			modelMap.put("semester", stuClass.getSemester());
			
			result.add(modelMap);
		}
		
		return result;
	}

}
