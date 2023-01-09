package com.example.demo.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Database;
import com.example.demo.entity.Teacher;
import com.example.demo.service.DatabaseService;
import com.example.demo.service.QuestInExerService;
import com.example.demo.service.StuAnswerService;
import com.example.demo.service.TeacherService;
import com.example.demo.tool.DBConnection;
import com.example.demo.tool.DBDatasource;
import com.example.demo.tool.DBJudgeTool;
import com.example.demo.util.LocalCacheUtil;
import com.example.demo.util.SetDBConnection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/judge")
@CrossOrigin
@RestController
public class JudgeController {
	@Autowired
	private QuestInExerService questInExerService;
	@Autowired
	private StuAnswerService stuAnswerService;
	@Autowired
	private DatabaseService databaseService;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private DBDatasource dbDatasource;
	
	/* 评判功能入口 */
	@PostMapping(value="/judge")
	public BaseResponse<Map<String, Object>> judgeSQL(@RequestParam(value = "studentId") String studentId,
                                                      @RequestParam(value = "exerciseId") int exerciseId,
                                                      @RequestParam(value = "questionId") int questionId,
                                                      @RequestParam(value = "answer") String answer,
                                                      @RequestParam(value = "idea", required=false) String idea) {
		Map<String, Object> modelMap = new HashMap<>();
		
		if (studentId == null) {
			modelMap.put("errmessage", "学生id不存在");
			return BaseResponse.fail(modelMap);
		}
		
		Map<String, Object> judgeQuestionMap = questInExerService.getJudgeQuestion(exerciseId, questionId);
		if (judgeQuestionMap == null || judgeQuestionMap.isEmpty()) {
			modelMap.put("errmessage", "题目不存在");
			return BaseResponse.fail(modelMap);
		}
		String correct;
		Object correctObj = judgeQuestionMap.get("answer");
		if (correctObj == null || correctObj instanceof String)
			correct = (String) correctObj;
		else {
			modelMap.put("errmessage", "标准答案获取失败");
			return BaseResponse.fail(modelMap);
		}
		short questionClass = (short)judgeQuestionMap.get("questionClass");
		String targetName;
		Object targetNameObj = judgeQuestionMap.get("targetName");
		if (targetNameObj == null || targetNameObj instanceof String)
			targetName = (String) targetNameObj;
		else {
			modelMap.put("errmessage", "涉及对象获取失败");
			return BaseResponse.fail(modelMap);
		}
		String initSQL;
		Object initSQLObj = judgeQuestionMap.get("initSQL");
		if (initSQLObj == null || initSQLObj instanceof String)
			initSQL = (String) initSQLObj;
		else {
			modelMap.put("errmessage", "评判环境初始化语句获取失败");
			return BaseResponse.fail(modelMap);
		}
		int score = (int)judgeQuestionMap.get("score");
		String dbName;
		Object dbNameObj = judgeQuestionMap.get("dbName");
		if (dbNameObj instanceof String)
			dbName = (String) dbNameObj;
		else {
			modelMap.put("errmessage", "所在数据库名称获取失败");
			return BaseResponse.fail(modelMap);
		}
		//获取数据库信息和相应教师信息，由于存在约束，因此不需要验证是否存在
		Database database = databaseService.getDatabaseByName(dbName);
		Teacher teacher = teacherService.getTeacher(database.getUserId());
		
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		
		Connection connection = null;
		try {
			connection = dbConnection.getConnect();

			DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
			boolean result=false;
		
			String answerHead;
			String correctHead;
			if (answer.trim().length() > 6) answerHead = answer.trim().substring(0, 6);
			else answerHead = answer.trim();
		
			if (correct.trim().length() > 6) correctHead = correct.trim().substring(0, 6);
			else correctHead = correct.trim();
		
			String check_answer = new String(answer.trim());	
			if (!answerHead.equalsIgnoreCase(correctHead))
				result = false;
			else {
				switch (questionClass) {
				case 1:
					result = dbJudgeTool.checkDQL(check_answer, correct);
					break;
				case 2:
					result = dbJudgeTool.checkDML(check_answer, correct, targetName);
					break;
				case 3:
					result = dbJudgeTool.checkCreateTable(check_answer, targetName, initSQL);
					break;
				case 4:
					result = dbJudgeTool.checkAlterTable(check_answer, targetName, initSQL);
					break;
				case 5:
					result = dbJudgeTool.checkCreateView(check_answer, targetName);
					break;
				case 6:
					result = dbJudgeTool.checkCreateIndex(check_answer, targetName, initSQL);
					break;
				case 7:
					result = dbJudgeTool.checkCreateUser(check_answer, targetName);
					break;
				case 8:
					result = dbJudgeTool.checkGrantTable(check_answer, targetName, initSQL);
					break;
				case 9:
					result = dbJudgeTool.checkRevokeTable(check_answer, targetName, initSQL);
					break;
				case 10:
					result = dbJudgeTool.checkGrantUser(check_answer, targetName, initSQL);
					break;
				case 11:
					result = dbJudgeTool.checkRevokeUser(check_answer, targetName, initSQL);
					break;
				case 12:
					result = dbJudgeTool.checkCreateFunc(check_answer, targetName, questionId);
					break;
				default:
					modelMap.put("errmessage", "无法评判该数据类型");

					return BaseResponse.fail(modelMap);
				}
			}
		
			try {
				dbConnection.close();
			} catch(SQLException e) {
				log.error("Exception :", e);
			}
		
			modelMap.put("judge result", result);
			if (!result)
				score = 0;
		
			Map<String, Object> updateResultMap =
					stuAnswerService.modifyStuAnswer(studentId, exerciseId, questionId, answer, result, idea, score);
			if ("error:not exists".equals(updateResultMap.get("state"))) {
				updateResultMap = stuAnswerService.addStuAnswer(studentId, exerciseId, questionId, answer, result, idea, score);
			}
		
			Object updateStatus = updateResultMap.get("state");
			modelMap.put("insert state", updateStatus);
		
			return BaseResponse.success(modelMap);
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("errmessage", "数据库连接错误");
			return BaseResponse.fail(modelMap);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
	}

	/* 连接评判数据库 ,只用于修改test_case表格 */
	@PostMapping(value = "/connectJudgeDatabase")
	public BaseResponse<Map<String, Object>> connectJudgeDatabase(@RequestParam(value = "teacherId") String teacherId,
																  @RequestParam(value = "zjId",required = false,defaultValue = "#")String zjId,
			                                                      @RequestParam(value = "dbName") String dbName) {
		Map<String, Object> modelMap = new HashMap<>();
		
		//获取数据库连接信息
		Database database = databaseService.getDatabaseByName(dbName);
		if (database == null) {
			modelMap.put("errmessage", dbName + " 数据库不存在");
			return BaseResponse.fail(modelMap);

		}
		if (!database.getUserId().equals(teacherId)) {
			modelMap.put("errmessage", dbName + " 数据库不属于  " + teacherId);
			return BaseResponse.fail(modelMap);
		}
		
		//获取数据库对应的教师用户信息
		Teacher teacher = teacherService.getTeacher(teacherId);
		
		//建立数据库连接
		DBConnection dbConnection = new DBConnection(dbDatasource);
		SetDBConnection.setDBConnection(dbConnection, database, teacher);
		try {
			dbConnection.getConnect();
		} catch (SQLException e) {
			log.error("Exception :", e);
			modelMap.put("errmessage", "数据库连接错误");
			return BaseResponse.fail(modelMap);
		}
		
		//如果存在旧连接，关闭旧连接，将新连接存入缓存中
		Optional<Object> oldDbConnectObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object oldDbConnectionObj = null;
		if (oldDbConnectObjOptional.isPresent())
			oldDbConnectionObj = oldDbConnectObjOptional.get();

		if (oldDbConnectionObj != null && oldDbConnectionObj instanceof DBConnection) {
			DBConnection oldDbConnection = (DBConnection) oldDbConnectionObj;
			try {
				oldDbConnection.close();
			} catch (SQLException e) {
				log.error("Exception :", e);
			}
			LocalCacheUtil.remove(setKeyForJudgeConnect(teacherId));
		}
		if(zjId.equals("#")) {
			log.info("---------------------------------------");
			log.info(teacherId);
			LocalCacheUtil.set(setKeyForJudgeConnect(teacherId), dbConnection, 4 * 60 * 60 * 1000);
		}
		else
		{
			log.info("---------------------------------------");
			log.info(zjId);
			LocalCacheUtil.set(setKeyForJudgeConnect(zjId), dbConnection, 4 * 60 * 60 * 1000);
		}


		modelMap.put("notice", "数据库连接成功");
		return BaseResponse.success(modelMap);
	}
	
	/* 断开数据库连接 */
	@PostMapping(value = "/disconnectJudgeConnect")
	public BaseResponse<Object> disconnectJudgeConnect(@RequestParam(value = "teacherId") String teacherId) {
		Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object dbConnectionObj = null;
		if (dbConnectionObjOptional.isPresent())
			dbConnectionObj = dbConnectionObjOptional.get();

		if (dbConnectionObj != null && dbConnectionObj instanceof DBConnection) {
			DBConnection dbConnection = (DBConnection) dbConnectionObj;
			try {
				dbConnection.close();
			} catch (SQLException e) {
				log.error("Exception :", e);
			}
			LocalCacheUtil.remove(setKeyForJudgeConnect(teacherId));
		}
		
		return BaseResponse.success(null);
	}
	
	/* 新增单条测试用例 */
	@PostMapping(value = "/addOneTestCase")
	public BaseResponse<Map<String, Object>> addOneTestCase(@RequestParam(value = "teacherId") String teacherId,
			                                                @RequestParam(value = "questionId") int questionId,
			                                                @RequestParam(value = "testCase") String test_case) {
		
		Map<String, Object> modelMap = new HashMap<>();
		Map<String, Object> resultMap;
		DBConnection dbConnection;
		Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object dbConnectionObj = null;
		if (dbConnectionObjOptional.isPresent())
			dbConnectionObj = dbConnectionObjOptional.get();

		if (dbConnectionObj instanceof DBConnection)
			dbConnection = (DBConnection) dbConnectionObj;
		else
			dbConnection = null;

		if (dbConnection == null) {
			modelMap.put("errmessage", "连接超时, 请重连数据库");
			return BaseResponse.fail(modelMap);
		}
		
		Connection connection = null;
		try {
			connection = dbConnection.getConnect();
			DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
			resultMap = dbJudgeTool.insertFuncTest(questionId, test_case);
		
			if (!"success".equals(resultMap.get("state"))) {
				return BaseResponse.fail(resultMap);
			}
		
			return BaseResponse.success(resultMap);
		} catch (SQLException e) {
			modelMap.put("errmessage", "无法连接数据库");
			return BaseResponse.fail(modelMap);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
	}
	
	/* 删除单条测试用例 */
	@PostMapping(value = "/deleteOneTestCase")
	public BaseResponse<Map<String, Object>> deleteOneTestCase(@RequestParam(value = "teacherId") String teacherId,
			                                                @RequestParam(value = "caseId") int caseId,
			                                                @RequestParam(value = "questionId") int questionId) {
		
		Map<String, Object> modelMap = new HashMap<>();
		Map<String, Object> resultMap;
		DBConnection dbConnection;
		Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object dbConnectionObj = null;
		if (dbConnectionObjOptional.isPresent())
			dbConnectionObj = dbConnectionObjOptional.get();

		if (dbConnectionObj instanceof DBConnection)
			dbConnection = (DBConnection) dbConnectionObj;
		else
			dbConnection = null;

		if (dbConnection == null) {
			modelMap.put("errmessage", "连接超时, 请重连数据库");
			return BaseResponse.fail(modelMap);
		}
		
		Connection connection = null;
		try {
			connection = dbConnection.getConnect();
		
			DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
			resultMap = dbJudgeTool.deleteFuncTest(caseId, questionId);
		
			if (!"success".equals(resultMap.get("state"))) {
				return BaseResponse.fail(resultMap);
			}
		
			return BaseResponse.success(resultMap);
		} catch (SQLException e) {
			modelMap.put("errmessage", "无法连接数据库");
			return BaseResponse.fail(modelMap);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
	}
	
	/* 删除指定题目全部测试用例 */
	@PostMapping(value = "/deleteAllTestCase")
	public BaseResponse<Map<String, Object>> deleteAllTestCase(@RequestParam(value = "teacherId") String teacherId,
			                                                   @RequestParam(value = "questionId") int questionId) {
		
		Map<String, Object> modelMap = new HashMap<>();
		Map<String, Object> resultMap;
		DBConnection dbConnection;
		Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object dbConnectionObj = null;
		if (dbConnectionObjOptional.isPresent())
			dbConnectionObj = dbConnectionObjOptional.get();

		if (dbConnectionObj instanceof DBConnection)
			dbConnection = (DBConnection) dbConnectionObj;
		else
			dbConnection = null;

		if (dbConnection == null) {
			modelMap.put("errmessage", "连接超时, 请重连数据库");
			return BaseResponse.fail(modelMap);
		}
		
		Connection connection = null;
		try {
			connection = dbConnection.getConnect();
		
			DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
			resultMap = dbJudgeTool.deleteAllFuncTest(questionId);
		
			if (!"success".equals(resultMap.get("state"))) {
				return BaseResponse.fail(resultMap);
			}
		
			return BaseResponse.success(resultMap);
		} catch (SQLException e) {
			modelMap.put("errmessage", "无法连接数据库");
			return BaseResponse.fail(modelMap);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
	}
	
	/* 修改单条测试用例 */
	@PostMapping(value = "/updateOneTestCase")
	public BaseResponse<Map<String, Object>> updateOneTestCase(@RequestParam(value = "teacherId") String teacherId,
			                                                   @RequestParam(value = "caseId") int caseId,
			                                                   @RequestParam(value = "questionId") int questionId,
			                                                   @RequestParam(value = "testCase") String test_case) {
		
		Map<String, Object> modelMap = new HashMap<>();
		Map<String, Object> resultMap;
		DBConnection dbConnection;
		Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		Object dbConnectionObj = null;
		if (dbConnectionObjOptional.isPresent())
			dbConnectionObj = dbConnectionObjOptional.get();

		if (dbConnectionObj instanceof DBConnection)
			dbConnection = (DBConnection) dbConnectionObj;
		else
			dbConnection = null;

		if (dbConnection == null) {
			modelMap.put("errmessage", "连接超时, 请重连数据库");
			return BaseResponse.fail(modelMap);
		}
		
		Connection connection = null;
		try {
			connection = dbConnection.getConnect();
		
			DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
			resultMap = dbJudgeTool.updateFuncTest(caseId, questionId, test_case);
		
			if (!"success".equals(resultMap.get("state"))) {
				return BaseResponse.fail(resultMap);
			}
		
			return BaseResponse.success(resultMap);
		} catch (SQLException e) {
			modelMap.put("errmessage", "无法连接数据库");
			return BaseResponse.fail(modelMap);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
	}
	
	/* 查询指定题目全部测试用例 */
	@PostMapping(value = "/selectTestCase")
	public BaseResponse<Map<String, Object>> selectTestCase(@RequestParam(value = "teacherId") String teacherId,
                                                               @RequestParam(value = "questionId") int questionId) {

         Map<String, Object> modelMap = new HashMap<>();
         List<Map<String, Object>> caseList;
         DBConnection dbConnection;
 		 Optional<Object> dbConnectionObjOptional = LocalCacheUtil.get(setKeyForJudgeConnect(teacherId));
		 Object dbConnectionObj = null;
		 if (dbConnectionObjOptional.isPresent())
			 dbConnectionObj = dbConnectionObjOptional.get();

 		 if (dbConnectionObj instanceof DBConnection)
 			 dbConnection = (DBConnection) dbConnectionObj;
 		 else
 			 dbConnection = null;

		 log.info("11111111111111111111111111111111111");
		 log.info("dbConnection: {}", dbConnection);
         if (dbConnection == null) {
			 modelMap.put("errmessage", "连接超时, 请重连数据库");
             return BaseResponse.fail(modelMap);
         }

         Connection connection = null;
         try {
             connection = dbConnection.getConnect();

             DBJudgeTool dbJudgeTool = new DBJudgeTool(connection);
             caseList = dbJudgeTool.selectFuncTest(questionId);
             modelMap.put("caseList", caseList);
         
             return BaseResponse.success(modelMap);
         } catch (SQLException e) {
			 modelMap.put("errmessage", "无法连接数据库");
             return BaseResponse.fail(modelMap);
         } finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("Exception :", e);
				}
		}
    } 
	
	/* 设置评判数据库缓存关键字 */
	private String setKeyForJudgeConnect(String teacherId) {
		return teacherId+"_Judge";
	}

}
