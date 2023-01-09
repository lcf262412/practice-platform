package com.example.demo.controller;

import com.example.demo.entity.BaseResponse;
import com.example.demo.service.StuAnswerService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/stuanswer")
@CrossOrigin
@RestController
public class StuAnswerController {
    @Autowired
    private StuAnswerService stuAnswerService;
    /*添加作答结果*/
    @PostMapping(value = "/addStuAnswer")
    private BaseResponse<Map<String,Object>> addStuAnswer(@RequestParam(value = "studentId") String studentId,
                                            @RequestParam(value = "exerciseId") int exerciseId,
                                            @RequestParam(value = "questionId") int questionId,
                                            @RequestParam(value = "answer") String answer,
                                            @RequestParam(value = "isRight") boolean isRight,
                                            @RequestParam(value = "idea") String idea,
                                            @RequestParam(value = "score") int score)throws ParseException{
       Map<String,Object> result=stuAnswerService.addStuAnswer(studentId, exerciseId, questionId, answer, isRight, idea, score);
       log.info("-------------------------------------");
       log.info(studentId+exerciseId+answer);
       if (result.get("state") instanceof String)
           log.info((String) result.get("state"));
       if(result.get("state")!="success"){
           return  BaseResponse.fail(result);
       }
       return BaseResponse.success(result);
    }

    /*修改作答结果*/
    @PostMapping(value = "/modifyStuAnswer")
    private BaseResponse<Map<String,Object>> modifyStuAnswer(@RequestParam(value = "studentId") String studentId,
                                            @RequestParam(value = "exerciseId") int exerciseId,
                                            @RequestParam(value = "questionId") int questionId,
                                            @RequestParam(value = "answer") String answer,
                                            @RequestParam(value = "isRight") boolean isRight,
                                            @RequestParam(value = "idea") String idea,
                                            @RequestParam(value = "score") int score)throws ParseException{
        Map<String,Object> result=stuAnswerService.modifyStuAnswer(studentId, exerciseId, questionId, answer, isRight, idea, score);
        if(result.get("state")!="success"){
            return  BaseResponse.fail(result);
        }
        return BaseResponse.success(result);
    }

    /*修改作答成绩*/
    @PostMapping(value = "/modifyStuScore")
    private BaseResponse<Map<String,Object>> modifyStuScore(@RequestParam(value = "studentId") String studentId,
                                               @RequestParam(value = "exerciseId") int exerciseId,
                                               @RequestParam(value = "questionId") int questionId,
                                               @RequestParam(value = "score") int score)throws ParseException{
        Map<String,Object> result=stuAnswerService.modifyStuScore(studentId, exerciseId, questionId, score);
        if(result.get("state")!="success"){
            return  BaseResponse.fail(result);
        }
        return BaseResponse.success(result);
    }

    /*查询某学生某道题作答结果*/
    @GetMapping(value = "/getStuAnswer")
    private BaseResponse<Map<String, Object>> getStuAnswer(@RequestParam(value = "studentId") String studentId,
                                              @RequestParam(value = "exerciseId") int exerciseId,
                                              @RequestParam(value = "questionId") int questionId
                                             )throws ParseException{
        Map<String, Object> result = stuAnswerService.getStuAnswer(studentId, exerciseId, questionId);
        if (result == null || result.isEmpty())
            return BaseResponse.success(null);
        else
            return BaseResponse.success(result);
    }

    /*查询某班级全部学生在某套试卷上作答结果*/
    @GetMapping(value = "/getClassExerScore")
    private BaseResponse<List<Map<String,Object>>> getClassExerScore(@RequestParam(value = "classId") String classId,
                                                       @RequestParam(value = "exerciseId") int exerciseId
                                                        )throws ParseException{
        List<Map<String,Object>> results=stuAnswerService.getClassExerScore(classId, exerciseId);
        if(results.size()==0){
            return BaseResponse.fail();
        }
        return BaseResponse.success(results);
    }

    /*查询某班级在某套试卷上每一题的平均分*/
    @GetMapping(value = "/getClassExerAve")
    public  BaseResponse<List<Map<String,Object>>> getClassExerAve(@RequestParam(value = "classId") String classId,
                                                    @RequestParam(value = "exerciseId") int exerciseId)throws ParseException{
        List<Map<String,Object>> results=stuAnswerService.getClassExerAve(classId,exerciseId);
        if(results == null || results.size()==0){
            return BaseResponse.fail();
        }
        return BaseResponse.success(results);
    }

    /*查询某班级在某套试卷上某一题的得分*/
    @GetMapping(value = "/getClassQuestScore")
    private BaseResponse<List<Map<String,Object>>> getClassQuestScore(@RequestParam(value = "classId") String classId,
                                                        @RequestParam(value = "exerciseId") int exerciseId,
                                                        @RequestParam(value = "questionId") int questionId)throws ParseException{
        List<Map<String,Object>> results=stuAnswerService.getClassQuestScore(classId, exerciseId, questionId);
        if(results.size()==0){
            return BaseResponse.fail();
        }
        return BaseResponse.success(results);
    }
    
    /*显示某学生在某套试卷上的作答结果*/
    @GetMapping(value = "/getStuTheExerAnswer")
    private BaseResponse<List<Map<String,Object>>> getStuTheExerAnswer(@RequestParam(value = "studentId") String studentId,
                                                         @RequestParam(value = "exerciseId") int exerciseId)throws  ParseException{
        log.info("-------------------------------------");
        log.info(studentId+"   "+exerciseId);
    	List<Map<String,Object>> results=stuAnswerService.getStuTheExerAnswer(studentId, exerciseId);

        if(results.size()==0){
            return BaseResponse.fail();
        }
        return BaseResponse.success(results);
    }

    /*显示某学生在每套试卷上的作答结果*/
    @GetMapping(value = "/getStuAllExerAnswer")
    private BaseResponse<List<Map<String,Object>>> getStuAllExerAnswer(@RequestParam(value = "studentId") String studentId)throws ParseException{
        List<Map<String,Object>> results=stuAnswerService.getStuAllExerAnswer(studentId);
        if(results.size()==0){
            return BaseResponse.fail();
        }
        return BaseResponse.success(results);
    }
}
