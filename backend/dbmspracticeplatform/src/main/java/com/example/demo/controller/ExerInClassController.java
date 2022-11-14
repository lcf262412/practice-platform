package com.example.demo.controller;

import com.example.demo.entity.BaseResponse;
import com.example.demo.service.ExerInClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RequestMapping("/exerinclass")
@CrossOrigin
@RestController
public class ExerInClassController {
    @Autowired
    private ExerInClassService exerInClassService;

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));        
    }
    
    /*增加班级与试卷关系*/
    @PostMapping(value = "/addExerInClass")
    private BaseResponse<Map<String,Object>> addExerInClass(@RequestParam(value = "stuClassId") String stuClassId,
                                                            @RequestParam(value = "exerciseId") int exerciseId,
                                                            @RequestParam(value = "isTest") boolean isTest,
                                                            @RequestParam(value = "startTime", required = false) Date startTime,
                                                            @RequestParam(value = "endTime", required = false) Date endTime)throws ParseException{
        Map<String, Object> result = exerInClassService.addExerInClass(stuClassId, exerciseId, isTest, startTime, endTime);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }
    
    /*增加班级与试卷关系*/
    @PostMapping(value = "/modifyExerInClass")
    private BaseResponse<Map<String,Object>> modifyExerInClass(@RequestParam(value = "stuClassId") String stuClassId,
                                                            @RequestParam(value = "exerciseId") int exerciseId,
                                                            @RequestParam(value = "isTest") boolean isTest,
                                                            @RequestParam(value = "startTime", required = false) Date startTime,
                                                            @RequestParam(value = "endTime", required = false) Date endTime)throws ParseException{
        Map<String, Object> result = exerInClassService.modifyExerInClass(stuClassId, exerciseId, isTest, startTime, endTime);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*删除班级与试卷关系*/
    @PostMapping(value = "/delExerInClass")
    private  BaseResponse<Map<String,Object>> delExerInClass(@RequestParam(value = "stuClassId") String stuClassId,
                                               @RequestParam(value = "exerciseId") int exerciseId){
        Map<String, Object> result = exerInClassService.delExerInClass(stuClassId, exerciseId);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }
    
    /*删除指定班级的全部试卷*/
    @PostMapping(value = "/delExersByClassId")
    private  BaseResponse<Map<String,Object>> delExersByClassId(@RequestParam(value = "stuClassId") String stuClassId){
        Map<String, Object> result = exerInClassService.delExersByClassId(stuClassId);
        if(!"success".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
    }

    /*查询某班级全部试卷*/
    @GetMapping(value = "/getClassAllExer")
    private BaseResponse<List<Map<String,Object>>> getClassAllExer(@RequestParam(value = "stuClassId") String stuClassId,
    		                                                       @RequestParam(value = "showAll", required = false, defaultValue = "false") boolean showAll)throws ParseException{
        List<Map<String, Object>> result = exerInClassService.getClassAllExer(stuClassId, showAll);
        
        return  BaseResponse.success(result);
    }

    @GetMapping(value = "/getClassAllExers")
    private BaseResponse<List<Map<String,Object>>> getClassAllExers(@RequestParam(value = "stuClassId") String stuClassId,
                                                                    @RequestParam(value = "stuId") String stuId,
                                                                   @RequestParam(value = "showAll", required = false, defaultValue = "false") boolean showAll)throws ParseException{
        List<Map<String, Object>> result = exerInClassService.getClassAllExers(stuClassId,stuId, showAll);

        return  BaseResponse.success(result);
    }

    /*查询使用某试卷的班级*/
    @GetMapping(value = "/getExerUsedClass")
    private  BaseResponse<List<Map<String,Object>>> getExerUsedClass(@RequestParam(value = "exerciseId") int exerciseId,
    		                                                         @RequestParam(value = "teacherId") String teacherId)throws ParseException{
        List<Map<String, Object>> result = exerInClassService.getExerUsedClass(exerciseId, teacherId);
        return  BaseResponse.success(result);
    }
    
    /*查询教师发布的所有班级试卷关系*/
    @GetMapping(value = "/getALLExerInClassByTeacherId")
    public BaseResponse<List<Map<String, Object>>> getALLExerInClassByTeacherId(@RequestParam(value = "teacherId") String teacherId) {
    	List<Map<String, Object>> result = exerInClassService.getALLExerInClassByTeacherId(teacherId);
    	return BaseResponse.success(result);
    }

    /*查询指定试卷是否被使用*/
    @GetMapping(value = "/exerIsUsed")
    private BaseResponse<Boolean> exerIsUsed(@RequestParam(value = "exerciseId") int exerciseId)throws ParseException{
        boolean result=exerInClassService.exerIsUsed(exerciseId);
        return  BaseResponse.success(result);
    }

}
