package com.example.demo.controller;

import com.example.demo.entity.BaseResponse;
import com.example.demo.service.QuestInExerService;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Data
class QuestOrder {
	int questionId;
	int orderId;
}

@Data
class ExerQuestOrder {
	int exerciseId;
	List<QuestOrder> questOrders;
}

@RestController
@CrossOrigin
@RequestMapping("/questinexer")
public class QuestInExerController {
    @Autowired
    private QuestInExerService questInExerService;

    /*增加题目*/
    @PostMapping(value = "/addQuestion")
    private BaseResponse<Map<String,Object>> addQuestion(@RequestParam(value = "questionId") int id_question,
                                           @RequestParam(value = "exerciseId") int id_exercise,
                                           @RequestParam(value = "score") int score) throws ParseException{
        int orderId = questInExerService.getMaxOrderIdInExer(id_exercise) + 1;
    	Map<String, Object> result = questInExerService.addQuestion(id_question, id_exercise, orderId, score);

        if(result.get("state")!="success"){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*删除题目*/
    @PostMapping(value = "/delQuestion")
    @Transactional
    private BaseResponse<Map<String,Object>> delQuestion(@RequestParam(value = "exerciseId") int exerciseId,
                                           @RequestParam(value = "questionId") int questionId)throws ParseException{
        questInExerService.updateOtherQuestionOrderId(exerciseId, questionId);
    	Map<String, Object> result = questInExerService.delQuestion(exerciseId,questionId);

        if(result.get("state")!="success"){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*修改题目分数*/
    @PostMapping(value = "/modifyExerScore")
    private  BaseResponse<Map<String,Object>> modifyExerScore(@RequestParam(value = "exerciseId") int exerciseId,
                                                @RequestParam(value = "questionId") int questionId,
                                                @RequestParam(value = "score") int score)throws ParseException{
        Map<String, Object> result = questInExerService.modifyExerScore(exerciseId, questionId, score);

        if(result.get("state")!="success"){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*修改题目序号*/
    @Transactional
    @PostMapping(value = "/modifyExerOrder")
    private BaseResponse<Map<String,Object>> modifyExerOrder(@RequestBody ExerQuestOrder exerQuestOrder){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        int exerciseId = exerQuestOrder.getExerciseId();
        List<QuestOrder> questOrders = exerQuestOrder.getQuestOrders();
        for(QuestOrder questOrder : questOrders){
            boolean flag=questInExerService.modifyExerOrder(exerciseId,questOrder.getQuestionId(),questOrder.getOrderId());
            if(!flag){
                modelMap.put("state","error");
                return BaseResponse.fail(modelMap);
            }
        }
        modelMap.put("state","success");
        return BaseResponse.success(modelMap);
    }
    
    /*删除指定试卷全部题目*/
    @PostMapping(value = "/delExerAllQuestion")
    private BaseResponse<Map<String,Object>> delExerAllQuestion(@RequestParam(value = "exerciseId") int exerciseId)throws ParseException{
        Map<String, Object> result = questInExerService.delExerAllQuestion(exerciseId);

        if(result.get("state")!="success"){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*查询指定试卷总分*/
    @GetMapping(value = "/exerSumScore")
    private  BaseResponse<Integer> exerSumScore(@RequestParam(value = "exerciseId") int exerciseId)throws ParseException{
        int result = questInExerService.exerSumScore(exerciseId);

        if(result<0){
            return BaseResponse.fail();
        }
        return  BaseResponse.success(result);
    }

    /*查询指定试卷内全部题目标题*/
    @GetMapping(value = "/getExerAllContent")
    private  BaseResponse<List<Map<String,Object>>> getExerAllContent(@RequestParam(value = "exerciseId") int exerciseId)throws ParseException{
        List<Map<String,Object>> result=questInExerService.getExerALLContent(exerciseId);
        return  BaseResponse.success(result);
    }

    @GetMapping(value = "/getExerAllContents")
    private  BaseResponse<List<Map<String,Object>>> getExerAllContents(@RequestParam(value = "exerciseId") int exerciseId,
                                                                               @RequestParam(value = "stuId") String stuId)throws ParseException{
        List<Map<String,Object>> result=questInExerService.getExerALLContents(exerciseId,stuId);
        return  BaseResponse.success(result);
    }

    /*查询指定题目所在试卷是否被使用*/
    @GetMapping(value = "/exerIsUsed")
    private  BaseResponse<Boolean> exerIsUsed(@RequestParam(value = "questionId") int questionId){
        boolean result=questInExerService.ExerIsUsed(questionId);
        return BaseResponse.success(result);
    }
    
    /* 查询指定试卷指定序号题目详情 (学生) */
    @GetMapping(value = "/getQuestionStudentByExerciseIdAndOrderId")
    private BaseResponse<Map<String,Object>> getQuestionStudent(@RequestParam(value = "exerciseId") int exerciseId,
    		                                                    @RequestParam(value = "orderId") int orderId) {
    	Map<String, Object> result = questInExerService.getQuestionStudent(exerciseId, orderId);
    	if (result == null || result.isEmpty())
    		return BaseResponse.success(null);
    	else
    	    return BaseResponse.success(result);
    }
    
    /* 根据题目编号查询其所在试卷信息 */
    @GetMapping(value = "/getExercisesByQuestionId")
    private BaseResponse<List<Map<String, Object>>> getExerciseByQuestionId(@RequestParam(value = "questionId") int questionId) {
    	
    	return BaseResponse.success(questInExerService.getExerciseByQuestionId(questionId));
    }
}
