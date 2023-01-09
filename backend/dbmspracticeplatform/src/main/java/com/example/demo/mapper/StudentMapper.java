package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.SQLResult;
import com.example.demo.entity.Student;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
	/* 获取指定老师的全部学生信息 */
	List<Student> selectListByTeacherId(String teacherId);
	
	/* 获取全部学生信息 */
	List<SQLResult> getAllStudents();
	
	/* 根据id获取指定学生信息 */
	List<SQLResult> getStudentById(String id);
	
	/* 根据name获取指定学生信息 */
	List<SQLResult> getStudentByName(String Name);

	/* 根据name或id获取指定学生信息 */
	List<SQLResult> getStudentByNameOrId(String Name);

	/* 根据学生id获得对应的教师id */
	SQLResult useStuidgetTeaid(String id);

}
