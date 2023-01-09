package com.example.demo.controller;

import com.example.demo.entity.BaseResponse;
import com.example.demo.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.text.ParseException;
import java.util.Map;

@RequestMapping("/exercise")
@CrossOrigin
@RestController
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    /*增加试卷*/
    @PostMapping(value = "/addExercise")
    public BaseResponse<Map<String,Object>> addExercise(@RequestParam(value = "teacherId") String teacherId,
                                                         @RequestParam(value = "name") String name,
                                                         @RequestParam(value = "describe") String describe)throws ParseException{
        Map<String, Object> result = exerciseService.addExercise(teacherId, name, describe);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*逻辑删除试卷*/
    @PostMapping(value = "/delExercise")
    public BaseResponse<Map<String,Object>> delExercise(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = exerciseService.delExercise(id);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);

    }

    /*修改试卷信息*/
    @PostMapping(value = "/modifyExercise")
    public BaseResponse<Map<String,Object>> modifyExercise(@RequestParam(value = "id") int id,
                                              @RequestParam(value = "name") String name,
                                              @RequestParam(value = "describe") String describe)throws ParseException{
        Map<String, Object> result = exerciseService.modifyExercise(id, name, describe);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);

    }

    /*查询全部可使用试卷*/
    @GetMapping(value = "/getAllExercise")
    public BaseResponse<List<Map<String,Object>>> getAllExercise(@RequestParam(value = "id") String id)throws ParseException{
        List<Map<String, Object>> result = exerciseService.getAllExercise(id);
        
        return  BaseResponse.success(result);
    }

    /*设置试卷开放*/
    @PostMapping(value = "/setExercisePublic")
    public  BaseResponse<Map<String,Object>> setExercisePublic(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = exerciseService.setExercisePublic(id);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*设置试卷私用*/
    @PostMapping(value = "/setExercisePrivate")
    public  BaseResponse<Map<String,Object>> setExercisePrivate(@RequestParam(value = "id") int id)throws ParseException{
        Map<String, Object> result = exerciseService.setExercisePrivate(id);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }
    
    /* 物理删除试卷 */
    @PostMapping(value = "/delExercisePhysically")
    public BaseResponse<Map<String, Object>> delExercisePhysically(@RequestParam(value = "id") int id) {
    	Map<String, Object> result = exerciseService.delExercisePhysically(id);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }
    
    /* 查询全部试卷（包括已被删除试卷） */
    @GetMapping(value = "/getExercises")
    public BaseResponse<List<Map<String, Object>>> getExercises() {
    	
    	return BaseResponse.success(exerciseService.getExercises());
    }
}
