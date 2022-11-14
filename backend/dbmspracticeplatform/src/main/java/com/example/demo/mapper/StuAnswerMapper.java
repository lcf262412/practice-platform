package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StuAnswerMapper extends BaseMapper<StuAnswer> {
    /* 查询某班级全部学生*/
    List<Student> getClassStudent(String classId);
    /*查询某班级学生某套试卷的总分*/
    List<StuAnswer> getStuExerScore(String classId,int exerciseId);

    /*查询某试卷满分*/
    int getExerTotalScore(int exerciseId);

    /*查询某班级在某套试卷上每一题的平均分*/
    List<SQLResult> getClassExerEachQuestAvg(int exerciseId, String classId);

    /*查询某班级在某套试卷上某一题的得分*/
    List<StuAnswer> getClassExerQuestScore(String classId,int exerciseId,int questionId);
    /*查询某学生每套试卷的总分*/
    List<StuAnswer> getStuExerScores(String studentId);

    /*学生在某套试卷上的作答结果*/
    List<SQLResult> getStuOneExerAnswer(@Param("studentId")String studentId,@Param("exerciseId")int exerciseId);

    /*查询某套试卷全部题目信息*/
    List<Question> getExerAllQuestion(int exerciseId);

    /*查询某学生全部作答信息*/
    List<SQLResult> getStudentAllAnswer(String studentId);
}
