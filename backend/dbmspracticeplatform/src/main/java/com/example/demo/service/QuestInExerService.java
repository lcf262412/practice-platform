package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.QuestInExer;

import java.util.List;
import java.util.Map;

public interface QuestInExerService extends IService<QuestInExer> {
    /*增加题目*/
    Map<String,Object> addQuestion( int id_question, int id_exercise, int orderId, int score);

    /* 获取当前最大orderId */
    int getMaxOrderIdInExer(int exerciseId);
    
    /*删除题目*/
    Map<String,Object> delQuestion(int exerciseId, int questionId);
    
    /* 更新其余题目orderId */
    void updateOtherQuestionOrderId(int exerciseId, int questionId);

    /*修改题目分数*/
    Map<String,Object> modifyExerScore(int exerciseId,int questionId,int score);

    /*修改题目序号*/
    boolean modifyExerOrder(int exerciseId,int questionId,int orderId);

    /*删除指定试卷全部题目*/
    Map<String,Object> delExerAllQuestion(int exerciseId);

    /*查询指定试卷总分*/
    int exerSumScore(int exerciseId);

    /*查询指定试卷内全部题目标题*/
    List<Map<String,Object>> getExerALLContent(int exerciseId);

    /*查询指定试卷内全部题目包括得分*/
    List<Map<String,Object>> getExerALLContents(int exerciseId,String stuId);
    /*查询指定题目所在试卷是否被使用*/
    boolean ExerIsUsed(int questionId);
    
    /*查询指定试卷指定试题的标准答案、分数和对应数据库连接属性，用于评判学生作答结果是否正确*/
    Map<String, Object> getJudgeQuestion(int exerciseId, int questionId);
    
    /* 查询指定试卷指定序号题目详情 (学生) */
    Map<String,Object> getQuestionStudent(int exerciseId, int orderId);
    
    /* 根据题目编号查询其所在试卷信息 */
    List<Map<String, Object>> getExerciseByQuestionId(int questionId);

}
