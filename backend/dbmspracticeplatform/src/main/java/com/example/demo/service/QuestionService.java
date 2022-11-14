package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Question;

import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<Question> {
    /*增加试题*/
    Map<String,Object> insertQuestion(String dbName, String teacherId, short questionClass, String title, String content,
                                      String targetName, String answer, String analysis, String initSQL);
    /*逻辑删除试题*/
    Map<String,Object> delQuestion(int id);

    /*修改试题*/
    Map<String,Object> modifyQuestion(String id, String dbName, short questionClass, String title, String content, String targetName, String answer,String analysis, String initSQL);

    /*查询指定试题内容（教师）*/
     Map<String ,Object> getQuestionTeacher(int id);

    /*查询指定试题内容（学生）*/
    Map<String,Object> getQuestionStudent(int id);

    /*查询指定试题解析（学生）*/
    Map<String,Object> getQuestionAnalyse(int id);

    /*查询所有试题*/
    List<Map<String,Object>> getAllQuestion();
    
    /* 物理删除指定试题 */
    Map<String, Object> deleteQuestionPhysically(int id);
    
    /* 查询所有试题（包括已设为删除状态的题目） */
    List<Map<String,Object>> getQuestions();
    
}
