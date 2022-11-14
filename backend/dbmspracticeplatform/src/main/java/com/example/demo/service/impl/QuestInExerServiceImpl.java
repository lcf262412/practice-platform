package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.QuestInExer;
import com.example.demo.entity.SQLResult;
import com.example.demo.mapper.ExerInClassMapper;
import com.example.demo.mapper.QuestInExerMapper;
import com.example.demo.service.QuestInExerService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QuestInExerServiceImpl extends ServiceImpl<QuestInExerMapper, QuestInExer> implements QuestInExerService {
    @Autowired
    private  QuestInExerMapper questInExerMapper;
    @Autowired
    private ExerInClassMapper exerInClassMapper;

    /*增加题目*/
    public Map<String,Object> addQuestion(int id_question, int id_exercise, int orderId, int score)  {
        QuestInExer questInExer_t=questInExerMapper.selectById(id_question);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(questInExer_t!=null){
            modelMap.put("state","error:already exists");

        }
        else{
            QuestInExer questInExer=new QuestInExer();
            questInExer.setQuestionId(id_question);
            questInExer.setExerciseId(id_exercise);
            questInExer.setOrderId(orderId);
            questInExer.setScore(score);
            int mes=questInExerMapper.insert(questInExer);
            if(mes==1){
                modelMap.put("state","success");
            }
            else {
                modelMap.put("state","error:insert failed");
            }
        }
        return modelMap;
    }
    
    /* 获取当前最大orderId */
    public int getMaxOrderIdInExer(int exerciseId) {
    	Integer maxOrderId = questInExerMapper.getMaxOrderIdInExer(exerciseId);
    	if (maxOrderId == null) return 0;
    	return maxOrderId;
    }

    /*删除题目*/
    public Map<String,Object> delQuestion(int exerciseId, int questionId){
        QueryWrapper<QuestInExer> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("exerciseId",exerciseId).eq("questionId",questionId);
        int mes=questInExerMapper.delete(questInExerQueryWrapper);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }
        return modelMap;
    }
    
    /* 更新其余题目orderId */
    public void updateOtherQuestionOrderId(int exerciseId, int questionId) {
    	QueryWrapper<QuestInExer> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("exerciseId",exerciseId).eq("questionId",questionId);
        QuestInExer questInExer = questInExerMapper.selectOne(questInExerQueryWrapper);
        if (questInExer == null) return;
        int delQuestionOrderId = questInExer.getOrderId();
        questInExerMapper.updateOtherQuestionOrderId(exerciseId, delQuestionOrderId);
    }

    /*修改题目分数*/
    public Map<String,Object> modifyExerScore(int exerciseId,int questionId,int score){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	QueryWrapper<QuestInExer> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("exerciseId",exerciseId).eq("questionId",questionId);
        QuestInExer questInExer=questInExerMapper.selectOne(questInExerQueryWrapper);
        if (questInExer==null) {
        	modelMap.put("state","error");
        	modelMap.put("errmessage", "题目不存在");
        	return modelMap;
        }
        questInExer.setScore(score);
        UpdateWrapper<QuestInExer> questInExerUpdateWrapper=new UpdateWrapper<>();
        questInExerUpdateWrapper.eq("exerciseId",exerciseId).eq("questionId",questionId);
        int mes=questInExerMapper.update(questInExer,questInExerUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }
        return modelMap;

    }

    /*修改题目序号*/
    public boolean modifyExerOrder(int exerciseId,int questionId,int orderId){
        UpdateWrapper<QuestInExer> questInExerUpdateWrapper=new UpdateWrapper<>();
        questInExerUpdateWrapper.eq("exerciseId",exerciseId).eq("questionId",questionId);
        QuestInExer questInExer=questInExerMapper.selectOne(questInExerUpdateWrapper);
        if (questInExer==null) return false;
        questInExer.setOrderId(orderId);

        int mes=questInExerMapper.update(questInExer,questInExerUpdateWrapper);

        if(mes==1){
            return true;
        }
        else {
           return  false;
        }

    }
    
    /*删除指定试卷全部题目*/
    public Map<String,Object> delExerAllQuestion(int exerciseId){
        QueryWrapper<QuestInExer> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("exerciseId",exerciseId);

        int mes=questInExerMapper.delete(questInExerQueryWrapper);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }
        return modelMap;

    }

    /*查询指定试卷总分*/
    public int exerSumScore(int exerciseId){
        return questInExerMapper.exerSumScore(exerciseId);
    }

    /*查询指定试卷内全部题目标题*/
    public List<Map<String,Object>> getExerALLContent(int exerciseId){
        List<QuestInExer> questInExers=questInExerMapper.getExerAllQuestContent(exerciseId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(QuestInExer q:questInExers){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("questionId",q.getQuestionId());
            modelMap.put("orderId",q.getOrderId());
            modelMap.put("score",q.getScore());
            modelMap.put("dbName",q.getQuestion().getDbName());
            modelMap.put("questionClass",q.getQuestion().getQuestionClass());
            modelMap.put("title",q.getQuestion().getTitle());
            result.add(modelMap);
        }
        return result;
    }
    /*查询指定试卷内全部题目标题*/
    public List<Map<String,Object>> getExerALLContents(int exerciseId,String stuId){
        List<SQLResult> questInExers=questInExerMapper.getExerAllQuestContents(exerciseId,stuId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        log.info("111111111111111111111111111111111111111");
        log.info("questions In this Exercise: {}", questInExers);

        for(SQLResult q:questInExers){
            log.info("22222222222222222222222222222222222222222222222");
            log.info("questionId:{}", q.getQuestInExer().getQuestionId());
            log.info("orderId:{}", q.getQuestInExer().getOrderId());
            log.info("zscore:{}", q.getQuestInExer().getScore());
            log.info("dbName:{}", q.getQuestion().getDbName());
            log.info("questionClass:{}", q.getQuestion().getQuestionClass());
            log.info("title:{}", q.getQuestion().getTitle());
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("questionId",q.getQuestInExer().getQuestionId());
            modelMap.put("orderId",q.getQuestInExer().getOrderId());
            modelMap.put("zscore",q.getQuestInExer().getScore());
            Integer tscore=0;
            if(q.getStuAnswer()==null)
                tscore=null;
            modelMap.put("sscore",(tscore== null) ? 0:q.getStuAnswer().getScore());
            modelMap.put("dbName",q.getQuestion().getDbName());
            modelMap.put("questionClass",q.getQuestion().getQuestionClass());
            modelMap.put("title",q.getQuestion().getTitle());
            result.add(modelMap);
        }
        return result;
    }

    /*查询指定题目所在试卷是否被使用*/
    public boolean ExerIsUsed(int questionId){
        int count =questInExerMapper.exerIsUsed(questionId);
        if(count>0) return true;
        return false;
    }
    
    /*查询指定试卷指定试题的标准答案、分数和对应数据库连接属性，用于评判学生作答结果是否正确*/
    public Map<String, Object> getJudgeQuestion(int exerciseId, int questionId) {
    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	List<SQLResult> results = questInExerMapper.getJudgeQuestion(exerciseId, questionId);
    	if(results.isEmpty()) {
    		return modelMap;
    	}
    	SQLResult sqlResult = results.get(0);
    	
        modelMap.put("questionClass",sqlResult.getQuestion().getQuestionClass());
        modelMap.put("targetName",sqlResult.getQuestion().getTargetName());
        modelMap.put("dbName",sqlResult.getQuestion().getDbName());
        modelMap.put("initSQL", sqlResult.getQuestion().getInitSQL());
        modelMap.put("answer",sqlResult.getQuestion().getAnswer());
        modelMap.put("score", sqlResult.getQuestInExer().getScore());
        
        return modelMap;
    }
    
    /* 查询指定试卷指定序号题目详情 (学生) */
    public Map<String,Object> getQuestionStudent(int exerciseId, int orderId){

    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	List<SQLResult> results = questInExerMapper.getStudentQuestion(exerciseId, orderId);
    	if(results.isEmpty()) {
    		return modelMap;
    	}
    	SQLResult sqlResult = results.get(0);
    	
        modelMap.put("questionId", sqlResult.getQuestion().getId());
        modelMap.put("dbName",sqlResult.getQuestion().getDbName());
        modelMap.put("describe",sqlResult.getJudgeDatabase().getDescribe());
        modelMap.put("questionClass",sqlResult.getQuestion().getQuestionClass());
        modelMap.put("title",sqlResult.getQuestion().getTitle());
        modelMap.put("content",sqlResult.getQuestion().getContent());
        modelMap.put("analysis",sqlResult.getQuestion().getAnalysis());

        return modelMap;
    }
    
    /* 根据题目编号查询其所在试卷信息 */
    public List<Map<String, Object>> getExerciseByQuestionId(int questionId) {
    	List<SQLResult> sqlResults = questInExerMapper.getExerciseByQuestionId(questionId);
    	List<Map<String, Object>> results = new ArrayList<>();
    	boolean isEmpty;
    	
    	for(SQLResult sqlResult : sqlResults) {
    		Map<String,Object> modelMap = new HashMap<String, Object>();
        	isEmpty = (sqlResult.getExercise() == null);
        	modelMap.put("e_id",isEmpty ? null : sqlResult.getExercise().getId());
            modelMap.put("e_name",isEmpty ? null : sqlResult.getExercise().getName());
            modelMap.put("describe",isEmpty ? null : sqlResult.getExercise().getDescribe());
            isEmpty = (sqlResult.getTeacher() == null);
            modelMap.put("t_id", isEmpty ? null : sqlResult.getTeacher().getId());
            modelMap.put("t_name",isEmpty ? null : sqlResult.getTeacher().getName());
            results.add(modelMap);
    	}
    	
    	return results;
    }

}
