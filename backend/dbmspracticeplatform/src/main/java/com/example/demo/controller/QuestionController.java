package com.example.demo.controller;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Database;
import com.example.demo.entity.Teacher;
import com.example.demo.service.DatabaseService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBDatasource;
import com.example.demo.tool.DBJudgeTool;
import com.example.demo.util.SetDBConnection;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class InsertQuestion {
	String dbName;
	String teacherId;
    short questionClass;
    String title;
    String content;
    String targetName;
    String answer;
    String analysis;
    String initSQL;
    List<String> testCases;
}

@Slf4j
@RequestMapping("/question")
@CrossOrigin
@RestController
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private DBDatasource dbDatasource;

    /*新增试题*/
    @PostMapping(value = "/addNewQuestion")
    @Transactional
    public BaseResponse<Map<String,Object>> insertQuestion(@RequestBody InsertQuestion question)throws ParseException {
    	String dbName = question.getDbName();
    	String teacherId = question.getTeacherId();
        short questionClass = question.getQuestionClass();
        String title = question.getTitle();
        String content = question.getContent();
        String targetName = question.getTargetName();
        String answer = question.getAnswer();
        String analysis = question.getAnalysis();
        String initSQL = question.getInitSQL();
        List<String> testCases =question.getTestCases();
    	
        Map<String,Object> result=questionService.insertQuestion(dbName, teacherId, questionClass, title, content, targetName, answer, analysis, initSQL);
    	if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        
        /* 非评判函数类题目，测试用例为null或为空，新增题目结束 */
        if (questionClass != 12 || testCases == null || testCases.size() == 0) {
        	return  BaseResponse.success(result);
        }
        /* 获取题目Id */
        int questionId = (int)result.get("questionId");
        
        /* 配置题目所在数据库连接信息 */
        Database database = databaseService.getDatabaseByName(dbName);
        Teacher teacher = teacherService.getTeacher(database.getUserId());
        DBConnection dbConnection = new DBConnection(dbDatasource);
        SetDBConnection.setDBConnection(dbConnection, database, teacher);
        
        Connection connection = null;
        /* 打开数据库连接并设置为非自动提交 */
        try {
        	connection = dbConnection.getConnect();
        	connection.setAutoCommit(false);
        
        	DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
        
        	/* 执行insert语句 */
        	result = dbJudgeTool.insertFuncTests(questionId, testCases);
        	if (!"success".equals(result.get("state"))) {
        		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        		try {
        			connection.rollback();
        			dbConnection.close();
        		} catch (SQLException e) {
        			log.error("Exception :", e);
        		}
        		return BaseResponse.fail(result);
        	}
        
        	/* 提交事务，并关闭数据库连接 */
        	try {
        		connection.commit();
        		dbConnection.close();
        	} catch (SQLException e) {
        		log.error("Exception :", e);
        		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        		Map<String, Object> modelMap = new HashMap<>();
        		modelMap.put("errmessage", "连接答题数据库失败");
        		modelMap.put("detail", e.getMessage());
        		return BaseResponse.fail(modelMap);
        	}
        
        	return  BaseResponse.success(result);
        } catch (SQLException e) {
        	log.error("Exception :", e);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	Map<String, Object> modelMap = new HashMap<>();
        	modelMap.put("errmessage", "连接答题数据库失败");
			modelMap.put("detail", e.getMessage());
			return BaseResponse.fail(modelMap);
        } finally {
			if (connection != null)
				try {
					if (!connection.isClosed()) connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
    }
    
    /*逻辑删除试题*/
    @PostMapping(value = "/delQuestion")
    public BaseResponse<Map<String,Object>> delQuestion(@RequestParam(value = "id") int id)throws ParseException{
    	if(questionService != null) 
    	    questionService.getAllQuestion();
    	Map<String,Object> result=questionService.delQuestion(id);
        if(result == null || result.get("state")!="success"){
            return BaseResponse.fail();
        }
        return  BaseResponse.success(result);

    }
    
    /*修改试题*/
    @PostMapping(value = "/modifyQuestion")
    public BaseResponse<Map<String,Object>> modifyQuestion(@RequestParam(value = "id") String id,
                                              @RequestParam(value = "dbName") String dbName,
                                              @RequestParam(value = "questionClass") short questionClass,
                                              @RequestParam(value = "title") String title,
                                              @RequestParam(value = "content") String content,
                                              @RequestParam(value = "targetName", required = false) String targetName,
                                              @RequestParam(value = "answer") String answer,
                                              @RequestParam(value = "analysis", required = false) String analysis,
                                              @RequestParam(value = "initSQL", required = false) String initSQL
    		                                  )throws ParseException{

        Map<String,Object> result=questionService.modifyQuestion(id, dbName, questionClass, title, content, targetName, answer, analysis, initSQL);

        if(result.get("state")!="success"){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);

    }
    
    /*查询指定试题内容（教师）*/
    @GetMapping(value = "/getQuestionTeacher")
    public BaseResponse<Map<String,Object>> getQuestionTeacher(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = questionService.getQuestionTeacher(id);
        
        if (result == null || result.isEmpty())
        	return BaseResponse.success(null);
        else
        	return BaseResponse.success(result);

    }
    
    /*查询指定试题内容（学生）*/
    @GetMapping(value = "/getQuestionStudent")
    public BaseResponse<Map<String,Object>> getQuestionStudent(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = questionService.getQuestionStudent(id);
        
        if (result == null || result.isEmpty())
        	return BaseResponse.success(null);
        else
        	return BaseResponse.success(result);
    }
    
    /*查询指定试题解析（学生）*/
    @GetMapping(value = "/getQuestionAnalyse")
    public BaseResponse<Map<String,Object>> getQuestionAnalyse(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = questionService.getQuestionAnalyse(id);
        
        if (result == null || result.isEmpty())
        	return BaseResponse.success(null);
        else
        	return BaseResponse.success(result);
    }
    
    /*查询所有试题*/
    @GetMapping(value = "/getAllQuestion")
    public BaseResponse<List<Map<String,Object>>>getAllQuestion(){
        List<Map<String,Object>> results=questionService.getAllQuestion();
        if (results == null || results.isEmpty())
        	return BaseResponse.success(null);
        else
            return BaseResponse.success(results);

    }

    /* 物理删除指定试题 */
    @PostMapping(value = "/deleteQuestionPhysically")
    @Transactional
    public BaseResponse<Map<String, Object>> deleteQuestionPhysically(@RequestParam(value = "id") int id) {
    	/* 删除试题表中的记录 */
    	Map<String, Object> result = questionService.deleteQuestionPhysically(id);
    	if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
    	
    	/* 判断题目类型，如果是函数/存储过程题目则执行下一步 */
    	short questionClass = (short)result.get("questionClass");
    	if (questionClass != 12) {
    		return BaseResponse.success(null);
    	}
    	String dbName;
		Object dbNameObj = result.get("dbName");
		if (dbNameObj instanceof String)
			dbName = (String) dbNameObj;
		else {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("errmessage", "所在数据库名称获取失败");
			return BaseResponse.fail(modelMap);
		}
    	
    	Database database = databaseService.getDatabaseByName(dbName);
    	Teacher teacher = teacherService.getTeacher(database.getUserId());
    	DBConnection dbConnection = new DBConnection(dbDatasource);
    	SetDBConnection.setDBConnection(dbConnection, database, teacher);
    	
    	/* 连接数据库 */
    	Connection connection = null;
    	try {
    		connection = dbConnection.getConnect();
    	
    		/* 删除记录 */
    		DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
    	
    		result = dbJudgeTool.deleteAllFuncTest(id);
    		if("error".equals(result.get("state"))){
    			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    			try {
    				dbConnection.close();
    			} catch (SQLException e) {
    				log.error("Exception :", e);
    			}
    			return BaseResponse.fail(result);
    		}
    	
    		/* 关闭数据库连接 */
    		try {
    			dbConnection.close();
    		} catch (SQLException e) {
    			log.error("Exception :", e);
    		}
    	
    		return BaseResponse.success(result);
    	} catch (SQLException e) {
    		log.error("Exception :", e);
    		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    		Map<String, Object> modelMap = new HashMap<>();
        	modelMap.put("errmessage", "连接答题数据库失败");
			modelMap.put("detail", e.getMessage());
			return BaseResponse.fail(modelMap);
    	} finally {
			if (connection != null)
				try {
					if (!connection.isClosed()) connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
    }
    
    /* 查询所有试题（包括已设为删除状态的题目） */
    @GetMapping(value = "/getQuestions")
    public BaseResponse<List<Map<String, Object>>> getQuestion() {
    	
    	return BaseResponse.success(questionService.getQuestions());
    }
}
