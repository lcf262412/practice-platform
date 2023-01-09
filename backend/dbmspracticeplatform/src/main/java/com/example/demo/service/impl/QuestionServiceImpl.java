package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.QuestInExer;
import com.example.demo.entity.Question;
import com.example.demo.entity.SQLResult;
import com.example.demo.mapper.JudgeDatabaseMapper;
import com.example.demo.mapper.QuestInExerMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestInExerMapper questInExerMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private JudgeDatabaseMapper judgeDatabaseMapper;

    /*新增试题*/
    public Map<String,Object> insertQuestion( String dbName, String teacherId, short questionClass, String title, String content,
                                               String targetName, String answer, String analysis, String initSQL) {
            Question question=new Question();
            question.setDbName(dbName);
            question.setTeacherId(teacherId);
            question.setQuestionClass(questionClass);
            question.setTitle(title);
            question.setContent(content);
            question.setTargetName(targetName);
            question.setAnswer(answer);
            question.setAnalysis(analysis);
            question.setInitSQL(initSQL);
            question.setDeleteflag(false);
            int mes=questionMapper.insert(question);
            Map<String,Object> modelMap = new HashMap<String, Object>();
            if(mes==1){
                modelMap.put("state","success");
                modelMap.put("questionId", question.getId());
            }
            else {
                modelMap.put("state","error");
            }

            return modelMap;

    }

    /*逻辑删除试题*/
    public Map<String,Object> delQuestion(int id){
    	Question question=questionMapper.selectById(id);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if (question==null) {
        	modelMap.put("state", "error");
            modelMap.put("errmessage", "题目不存在");
        	return modelMap;
        }
        question.setDeleteflag(true);
        UpdateWrapper<Question> questionUpdateWrapper=new UpdateWrapper<>();
        questionUpdateWrapper.eq("id",id);
        int mes=questionMapper.update(question,questionUpdateWrapper);
        
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;

    }

    /*修改试题*/
    public Map<String,Object> modifyQuestion(String id, String dbName, short questionClass, String title, String content, String targetName, String answer,String analysis, String initSQL){

        Question question=questionMapper.selectById(id);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(question==null||question.isDeleteflag()){
            modelMap.put("state","error:not exists");
        }
        else{

            question.setDbName(dbName);
            question.setQuestionClass(questionClass);
            question.setTitle(title);
            question.setContent(content);
            question.setTargetName(targetName);
            question.setAnswer(answer);
            question.setAnalysis(analysis);
            question.setInitSQL(initSQL);
            UpdateWrapper<Question> questionUpdateWrapper=new UpdateWrapper<>();
            questionUpdateWrapper.eq("id",id);
            int mes=questionMapper.update(question,questionUpdateWrapper);
            if(mes==1){
                modelMap.put("state","success");
            }
            else {
                modelMap.put("state","error:update failed");
            }
        }
        return modelMap;
    }
    
    /*查询指定试题内容（教师）*/
    public Map<String ,Object> getQuestionTeacher(int id){

    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	List<SQLResult> results = questionMapper.getTeacherQuestion(id);
        if(results.isEmpty()) {
            return modelMap;
        }
        SQLResult sqlResult = results.get(0);
 
        modelMap.put("dbName",sqlResult.getQuestion().getDbName());
        modelMap.put("describe",sqlResult.getJudgeDatabase().getDescribe());
        modelMap.put("teacherId", sqlResult.getQuestion().getTeacherId());
        modelMap.put("teacherName",sqlResult.getTeacher().getName());
        modelMap.put("questionClass",sqlResult.getQuestion().getQuestionClass());
        modelMap.put("title",sqlResult.getQuestion().getTitle());
        modelMap.put("content",sqlResult.getQuestion().getContent());
        modelMap.put("analysis",sqlResult.getQuestion().getAnalysis());
        modelMap.put("initSQL", sqlResult.getQuestion().getInitSQL());
        modelMap.put("targetName",sqlResult.getQuestion().getTargetName());
        modelMap.put("answer",sqlResult.getQuestion().getAnswer());

        return modelMap;

    }

    /*查询指定试题内容（学生）*/
    public Map<String,Object> getQuestionStudent(int id){

    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	List<SQLResult> results = questionMapper.getStudentQuestion(id);
    	if(results.isEmpty()) {
    		return modelMap;
    	}
    	SQLResult sqlResult = results.get(0);

        modelMap.put("dbName",sqlResult.getQuestion().getDbName());
        modelMap.put("describe",sqlResult.getJudgeDatabase().getDescribe());
        modelMap.put("questionClass",sqlResult.getQuestion().getQuestionClass());
        modelMap.put("title",sqlResult.getQuestion().getTitle());
        modelMap.put("content",sqlResult.getQuestion().getContent());
        modelMap.put("analysis",sqlResult.getQuestion().getAnalysis());

        return modelMap;
    }

    /*查询指定试题解析（学生）*/
    public Map<String,Object> getQuestionAnalyse(int id){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	Question question=questionMapper.selectById(id);
        if(question==null||question.isDeleteflag()){
            return modelMap;
        }
        modelMap.put("analysis",question.getAnalysis());
        return modelMap;
    }

    /*查询所有试题*/
    public List<Map<String,Object>> getAllQuestion(){

    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	List<Question> AllQuestion=questionMapper.getALLQuestion();
        if(AllQuestion.size()==0){
            return list;
        }
        for(Question q:AllQuestion){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("dbName",q.getDbName());
            modelMap.put("id",q.getId());
            modelMap.put("questionClass",q.getQuestionClass());
            modelMap.put("title",q.getTitle());
            modelMap.put("t_id", q.getTeacherId());
            modelMap.put("name",q.getTeacher().getName());
            list.add(modelMap);
        }

        return list;

    }

    /* 物理删除指定试题 */
    @Override
    public Map<String, Object> deleteQuestionPhysically(int id){
    	Question question=questionMapper.selectById(id);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        /* 判断试题是否存在 */
        if (question==null) {
        	modelMap.put("state", "error");
        	modelMap.put("errmessage", "题目不存在");
        	return modelMap;
        }
        
        //判断试卷题目表中是否存在题目，存在则不能删除
        QueryWrapper<QuestInExer> questInExerWrapper = new QueryWrapper<>();
        questInExerWrapper.eq("questionid", id);
        int questInExerCount = questInExerMapper.selectCount(questInExerWrapper);
        if (questInExerCount > 0) {
        	modelMap.put("state","error");
            modelMap.put("errmessage","题目已被试卷使用，无法被删除");
            return modelMap;
        }
        
        int mes = questionMapper.deleteById(id);
        
        if(mes==1){
            modelMap.put("state","success");
            modelMap.put("questionClass", question.getQuestionClass()); //获取题目类型，便于后续操作
            modelMap.put("dbName", question.getDbName()); //获取题目所属数据库名称，便于后续操作
        }
        else {
            modelMap.put("state","error");
            modelMap.put("errmessage","删除失败，请联系数据库管理员解决");
        }

        return modelMap;
    }
    
    /* 查询所有试题（包括已设为删除状态的题目） */
    @Override
	public List<Map<String,Object>> getQuestions(){
    	List<Question> AllQuestion=questionMapper.getQuestions();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(Question q:AllQuestion){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("dbName",q.getDbName());
            modelMap.put("id",q.getId());
            modelMap.put("questionClass",q.getQuestionClass());
            modelMap.put("title",q.getTitle());
            modelMap.put("t_id", q.getTeacherId());
            modelMap.put("name",q.getTeacher().getName());
            modelMap.put("deleteFlag", q.isDeleteflag());
            list.add(modelMap);
        }

        return list;
    }
}
