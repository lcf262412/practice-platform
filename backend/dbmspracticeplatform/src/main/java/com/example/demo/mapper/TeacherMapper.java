package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Teacher;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
	//获取除管理员外的其他教师信息
	List<Teacher> getAllTeachers();
}
