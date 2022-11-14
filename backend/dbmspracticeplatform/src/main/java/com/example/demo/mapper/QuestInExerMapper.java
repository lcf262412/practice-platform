package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.QuestInExer;
import com.example.demo.entity.SQLResult;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestInExerMapper extends BaseMapper<QuestInExer> {
    /*查询指定试卷总分*/
    int exerSumScore(int exerciseId);

    /*查询指定题目所在试卷是否被使用*/
    int exerIsUsed(int questionId);

    /*查询指定试卷内全部题目标题*/
    List<QuestInExer> getExerAllQuestContent(int exerciseId);

	/*查询指定试卷内全部题目标题包括分数*/
	List<SQLResult> getExerAllQuestContents(int exerciseId,String stuId);
    
    /*查询指定试卷指定试题的标准答案、分数和对应数据库连接属性，用于评判学生作答结果是否正确*/
	List<SQLResult> getJudgeQuestion(@Param("exerciseId")int exerciseId, @Param("questionId")int questionId);

	/* 获取当前最大orderId */
	Integer getMaxOrderIdInExer(int exerciseId);
	
	/* 将大于该orderId的题目orderId减一 */
	int updateOtherQuestionOrderId(@Param("exerciseId") int exerciseId, @Param("orderId") int orderId);
	
	/* 查询指定试卷指定序号题目详情 (学生) */
	List<SQLResult> getStudentQuestion(@Param("exerciseId") int exerciseId, @Param("orderId") int orderId);
	
	/* 查询指定试题所在试卷信息 */
	List<SQLResult> getExerciseByQuestionId(int questionId);
}
