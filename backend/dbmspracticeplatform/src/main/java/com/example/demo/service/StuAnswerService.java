package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.StuAnswer;

import java.util.List;
import java.util.Map;

public interface StuAnswerService extends IService<StuAnswer> {

    /*添加作答结果*/
    Map<String,Object> addStuAnswer(String studentId, int exerciseId, int questionId, String answer, boolean isRight, String idea, int score);

    /*修改作答结果*/
    Map<String,Object> modifyStuAnswer(String studentId, int exerciseId, int questionId, String answer, boolean isRight, String idea, int score);

    /*修改作答成绩*/
    Map<String,Object> modifyStuScore(String studentId, int exerciseId, int questionId,  int score);

    /*查询某学生某道题作答结果*/
    Map<String,Object> getStuAnswer(String studentId, int exerciseId, int questionId);

    /*查询某班级全部学生在某套试卷上作答结果*/
    List<Map<String,Object>> getClassExerScore(String classId, int exerciseId);

     /*查询某班级在某套试卷上每一题的平均分*/
     List<Map<String,Object>> getClassExerAve(String classId, int exerciseId);

    /*查询某班级在某套试卷上某一题的得分*/
    List<Map<String,Object>> getClassQuestScore(String classId, int exerciseId,int questionId);

    /*显示某学生在某套试卷上的作答结果*/
    List<Map<String,Object>> getStuTheExerAnswer(String studentId,int exerciseId);

    /*显示某学生在每套试卷上的作答结果*/
    List<Map<String,Object>> getStuAllExerAnswer(String studentId);

}
