package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.service.StuAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StuAnswerServiceImpl extends ServiceImpl<StuAnswerMapper, StuAnswer> implements StuAnswerService {
    @Autowired
    private StuAnswerMapper stuAnswerMapper;
    @Autowired
    private QuestInExerMapper questInExerMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private ExerInClassMapper exerInClassMapper;
    @Autowired
    private StudentMapper studentMapper;
    /*添加作答结果*/
    public Map<String,Object> addStuAnswer(String studentId, int exerciseId, int questionId, String answer, boolean isRight, String idea, int score){
        QueryWrapper<StuAnswer> stuAnswerQueryWrapper=new QueryWrapper<>();
        stuAnswerQueryWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        StuAnswer stuAnswer=stuAnswerMapper.selectOne(stuAnswerQueryWrapper);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(stuAnswer!=null){
            modelMap.put("state","error:already exists");
            return modelMap;
        }
        StuAnswer stuAnswer1=new StuAnswer();
        stuAnswer1.setStudentId(studentId);
        stuAnswer1.setExerciseId(exerciseId);
        stuAnswer1.setQuestionId(questionId);
        stuAnswer1.setAnswer(answer);
        stuAnswer1.setIdea(idea);
        stuAnswer1.setRight(isRight);
        stuAnswer1.setScore(score);
        int mes=stuAnswerMapper.insert(stuAnswer1);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error:insert failed");
        }

        return modelMap;
    }

    /*修改作答结果*/
    public Map<String,Object> modifyStuAnswer(String studentId, int exerciseId, int questionId, String answer, boolean isRight, String idea, int score){
        QueryWrapper<StuAnswer> stuAnswerQueryWrapper=new QueryWrapper<>();
        stuAnswerQueryWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        StuAnswer stuAnswer=stuAnswerMapper.selectOne(stuAnswerQueryWrapper);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(stuAnswer==null){
            modelMap.put("state","error:not exists");
            return modelMap;
        }
        stuAnswer.setAnswer(answer);
        stuAnswer.setIdea(idea);
        stuAnswer.setRight(isRight);
        stuAnswer.setScore(score);
        UpdateWrapper<StuAnswer> stuAnswerUpdateWrapper=new UpdateWrapper<>();
        stuAnswerUpdateWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        int mes=stuAnswerMapper.update(stuAnswer,stuAnswerUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error:update failed");
        }

        return modelMap;


    }
    /*修改作答成绩*/
    public Map<String,Object> modifyStuScore(String studentId, int exerciseId, int questionId,  int score){
        QueryWrapper<StuAnswer> stuAnswerQueryWrapper=new QueryWrapper<>();
        stuAnswerQueryWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        StuAnswer stuAnswer=stuAnswerMapper.selectOne(stuAnswerQueryWrapper);

        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(stuAnswer==null){
            modelMap.put("state","error:not exists");
            return modelMap;
        }

        stuAnswer.setScore(score);
        UpdateWrapper<StuAnswer> stuAnswerUpdateWrapper=new UpdateWrapper<>();
        stuAnswerUpdateWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        int mes=stuAnswerMapper.update(stuAnswer,stuAnswerUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error:update failed");
        }

        return modelMap;
    }

    /*查询某学生某道题作答结果*/
    public Map<String,Object> getStuAnswer(String studentId, int exerciseId, int questionId){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	QueryWrapper<StuAnswer> stuAnswerQueryWrapper=new QueryWrapper<>();
        stuAnswerQueryWrapper.eq("studentId",studentId).eq("exerciseId",exerciseId).eq("questionId",questionId);
        StuAnswer stuAnswer=stuAnswerMapper.selectOne(stuAnswerQueryWrapper);
        if (stuAnswer==null) return modelMap;
        modelMap.put("answer",stuAnswer.getAnswer());
        modelMap.put("isRight",stuAnswer.isRight());
        modelMap.put("idea",stuAnswer.getIdea());
        modelMap.put("score",stuAnswer.getScore());
        return modelMap;

    }
    /*查询某班级全部学生在某套试卷上作答结果*/
    public List<Map<String,Object>> getClassExerScore(String classId,int exerciseId){
        List<StuAnswer> stuAnswers=stuAnswerMapper.getStuExerScore(classId,exerciseId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(StuAnswer s:stuAnswers){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("name",s.getName());
            modelMap.put("sumscore",s.getScore());

        }
        return result;
    }

    /*查询某班级在某套试卷上每一题的平均分*/
    public List<Map<String,Object>> getClassExerAve(String classId, int exerciseId){
    	List<Map<String,Object>> results =new ArrayList<>();
    	List<SQLResult> sqlResults=stuAnswerMapper.getClassExerEachQuestAvg(exerciseId,classId);
        if(sqlResults.isEmpty())return results;
        for(SQLResult s:sqlResults){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("questionId",s.getQuestInExer().getQuestionId());
            modelMap.put("orderId",s.getQuestInExer().getOrderId());
            modelMap.put("dbName",s.getQuestion().getDbName());
            modelMap.put("title",s.getQuestion().getTitle());
            modelMap.put("questionClass",s.getQuestion().getQuestionClass());
            modelMap.put("avg",s.getStuAnswer().getAvgscore());
            results.add(modelMap);
        }
        return results;
    }
    /*查询某班级在某套试卷上某一题的得分*/
    public List<Map<String,Object>> getClassQuestScore(String classId, int exerciseId,int questionId){
        List<StuAnswer> stuAnswers=stuAnswerMapper.getClassExerQuestScore(classId, exerciseId, questionId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(StuAnswer s:stuAnswers){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("name",s.getName());
            modelMap.put("answer",s.getAnswer());
            modelMap.put("isRight" ,s.isRight());
            modelMap.put("idea",s.getIdea());
            modelMap.put("score",s.getScore());
            result.add(modelMap);
        }

        return result;
    }

    /*显示某学生在某套试卷上的作答结果*/
    public List<Map<String,Object>> getStuTheExerAnswer(String studentId,int exerciseId){
        List<SQLResult> sqlResults=stuAnswerMapper.getStuOneExerAnswer(studentId,exerciseId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        boolean isEmpty;
        for (SQLResult sqlResult : sqlResults) {
        	Map<String,Object> modelMap = new HashMap<String, Object>();
        	isEmpty = (sqlResult.getQuestion() == null);
            modelMap.put("questionId", isEmpty ? null : sqlResult.getQuestion().getId());
            modelMap.put("orderId",isEmpty ? null : sqlResult.getQuestion().getOrderId());
            modelMap.put("dbName",isEmpty ? null : sqlResult.getQuestion().getDbName());
            modelMap.put("questionClass",isEmpty ? null : sqlResult.getQuestion().getQuestionClass());
            modelMap.put("title",isEmpty ? null : sqlResult.getQuestion().getTitle());
            modelMap.put("refanswer",isEmpty ? null : sqlResult.getQuestion().getAnswer());
            isEmpty = (sqlResult.getStuAnswer() == null);
            modelMap.put("answer",isEmpty ? null : sqlResult.getStuAnswer().getAnswer());
            modelMap.put("isRight",isEmpty ? null : sqlResult.getStuAnswer().isRight());
            modelMap.put("idea",isEmpty ? null : sqlResult.getStuAnswer().getIdea());
            modelMap.put("score",isEmpty ? null : sqlResult.getStuAnswer().getScore());
            result.add(modelMap);
        }
        return  result;
    }

    /*显示某学生在每套试卷上的作答结果*/
    public List<Map<String,Object>> getStuAllExerAnswer(String studentId){
        List<SQLResult> results=stuAnswerMapper.getStudentAllAnswer(studentId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(SQLResult sq:results){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("exerciseId",sq.getExercise().getId());
            modelMap.put("name",sq.getExercise().getName());
            modelMap.put("startTime",sq.getExerInClass().getStartTime());
            modelMap.put("endTime",sq.getExerInClass().getEndTime());
            modelMap.put("sum",sq.getScoreSum());
            modelMap.put("isfinish", sq.isFinish());
            modelMap.put("isfull", sq.isFull());
            result.add(modelMap);

        }
        return result;
    }

}
