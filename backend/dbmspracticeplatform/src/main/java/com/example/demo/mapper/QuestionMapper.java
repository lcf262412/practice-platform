package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Question;
import com.example.demo.entity.SQLResult;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /*查询指定试题内容（教师)*/
    List<SQLResult> getTeacherQuestion(int questionId);
	/*查询指定试题内容（学生） */
	List<SQLResult> getStudentQuestion(int questionId);

    /*查询全部试题*/
    List<Question> getALLQuestion();
    
    /* 查询全部试题（包含标记删除的试题） */
    List<Question> getQuestions();

}
