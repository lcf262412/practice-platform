package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.ExerInClass;
import com.example.demo.entity.ExerInClass1;
import com.example.demo.entity.SQLResult;
import com.example.demo.mapper.ExerInClassMapper;
import com.example.demo.mapper.QuestInExerMapper;
import com.example.demo.service.ExerInClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExerInClassServiceImpl extends ServiceImpl<ExerInClassMapper, ExerInClass> implements ExerInClassService {
    @Autowired
    private ExerInClassMapper exerInClassMapper;
    @Autowired
    private  QuestInExerMapper questInExerMapper;
    /*增加班级与试卷关系*/
    public Map<String, Object> addExerInClass(String stuClassId, int exerciseId, boolean isTest, Date startTime, Date endTime) {
        QueryWrapper<ExerInClass> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("stuClassID",stuClassId).eq("exerciseId",exerciseId);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        ExerInClass exerInClass=exerInClassMapper.selectOne(questInExerQueryWrapper);
        if(exerInClass!=null){
            modelMap.put("state","error:already exists");
            modelMap.put("detail","已经存在");
        }
        else{
            ExerInClass exerInClass_new=new ExerInClass();
            exerInClass_new.setStuClassId(stuClassId);
            exerInClass_new.setExerciseId(exerciseId);
            exerInClass_new.setTest(isTest);
            exerInClass_new.setStartTime(startTime);
            exerInClass_new.setEndTime(endTime);
            int mes=exerInClassMapper.insert(exerInClass_new);
            if(mes==1){
                modelMap.put("state","success");
                modelMap.put("detail","成功");
            }
            else {
                modelMap.put("state","error:insert failed");
                modelMap.put("detail","插入失败");
            }
        }
        return modelMap;
    }
    
    /*修改班级与试卷关系*/
    public Map<String, Object> modifyExerInClass(String stuClassId, int exerciseId, boolean isTest, Date startTime, Date endTime) {
        QueryWrapper<ExerInClass> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("stuClassID",stuClassId).eq("exerciseId",exerciseId);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        ExerInClass exerInClass=exerInClassMapper.selectOne(questInExerQueryWrapper);
        if(exerInClass==null){
            modelMap.put("state","error");
            modelMap.put("errmessage", "已经存在");
        }
        else{
            exerInClass.setTest(isTest);
            exerInClass.setStartTime(startTime);
            exerInClass.setEndTime(endTime);
            int mes=exerInClassMapper.update(exerInClass, questInExerQueryWrapper);
            if(mes==1){
                modelMap.put("state","success");
            }
            else {
                modelMap.put("state","error");
                modelMap.put("errmessage", "更新失败");
            }
        }
        return modelMap;
    }

    /*删除班级与试卷关系*/
    public Map<String,Object> delExerInClass(String stuClassId, int exerciseId){
        QueryWrapper<ExerInClass> questInExerQueryWrapper=new QueryWrapper<>();
        questInExerQueryWrapper.eq("stuClassID",stuClassId).eq("exerciseId",exerciseId);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        ExerInClass exerInClass=exerInClassMapper.selectOne(questInExerQueryWrapper);
        if(exerInClass==null){
            modelMap.put("state","error");
            modelMap.put("errmessage", "不存在");

        }
        else{
            int mes=exerInClassMapper.delete(questInExerQueryWrapper);
            if(mes==1){
                modelMap.put("state","success");
            }
            else {
                modelMap.put("state","error");
                modelMap.put("errmessage", "删除失败");
            }
        }
        return modelMap;
    }
    
    /*删除指定班级的全部试卷*/
    @Override
    public Map<String,Object> delExersByClassId(String stuClassId) {
    	QueryWrapper<ExerInClass> exerInClassWrapper = new QueryWrapper<>();
		exerInClassWrapper.eq("stuclassid", stuClassId);
		exerInClassMapper.delete(exerInClassWrapper);
		
		Map<String,Object> modelMap = new HashMap<String, Object>();
		modelMap.put("state","success");
		return modelMap;
    }

    /*查询某班级全部试卷*/
    public List<Map<String,Object>> getClassAllExer(String stuClassId, boolean showAll){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        List<ExerInClass> exerInClasses= showAll ? exerInClassMapper.getClassALLExer(stuClassId) : exerInClassMapper.getClassALLExerInTime(stuClassId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(ExerInClass e:exerInClasses){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("exerciseId",e.getExerciseId());
            modelMap.put("name",e.getExercise().getName());
            modelMap.put("describe",e.getExercise().getDescribe());
            modelMap.put("isTest",e.isTest());
            modelMap.put("startTime",e.getStartTime()==null? null : sdf.format(e.getStartTime()));
            modelMap.put("endTime",e.getEndTime()==null? null : sdf.format(e.getEndTime()));
            result.add(modelMap);
        }
        return result;
    }

    /*查询某班级全部试卷包括得分*/
    public List<Map<String,Object>> getClassAllExers(String stuClassId,String stuId, boolean showAll){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        List<ExerInClass1> exerInClasses= showAll ? exerInClassMapper.getClassALLExers(stuClassId,stuId) : exerInClassMapper.getClassALLExersInTime(stuClassId,stuId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for(ExerInClass1 e:exerInClasses){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("exerciseId",e.getExerciseId());
            modelMap.put("name",e.getExercise().getName());
            modelMap.put("describe",e.getExercise().getDescribe());
            modelMap.put("isTest",e.isTest());
            modelMap.put("startTime",e.getStartTime()==null? null : sdf.format(e.getStartTime()));
            modelMap.put("endTime",e.getEndTime()==null? null : sdf.format(e.getEndTime()));
            modelMap.put("get_score",e.getTotal_score());
            modelMap.put("total_score", questInExerMapper.exerSumScore(e.getExerciseId()));
            result.add(modelMap);
        }
        return result;
    }

    /*查询使用某试卷的班级*/
    public List<Map<String,Object>> getExerUsedClass(int exerciseId, String teacherId){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        List<SQLResult> sqlResults = exerInClassMapper.getClassByExerciseId(exerciseId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        for (SQLResult sqlResult : sqlResults) {
        	Map<String, Object> modelMap = new HashMap<String, Object>();
        	modelMap.put("stuClassId", sqlResult.getStuClass().getId());
        	modelMap.put("semester", sqlResult.getStuClass().getSemester());
            modelMap.put("isTest",sqlResult.getExerInClass().isTest());
            modelMap.put("startTime",sqlResult.getExerInClass().getStartTime()==null ? null : sdf.format(sqlResult.getExerInClass().getStartTime()));
            modelMap.put("endTime",sqlResult.getExerInClass().getEndTime()==null ? null : sdf.format(sqlResult.getExerInClass().getEndTime()));
            modelMap.put("teacherId",sqlResult.getTeacher().getId());
            modelMap.put("teacherName",sqlResult.getTeacher().getName());
            modelMap.put("cancelFlag", teacherId.equals(sqlResult.getTeacher().getId()));
            
            result.add(modelMap);
        }
        
        return result;
    }

    /*查询教师发布的所有班级试卷关系*/
    @Override
    public List<Map<String,Object>> getALLExerInClassByTeacherId(String teacherId) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        List<SQLResult> sqlResults = exerInClassMapper.getALLExerInClassByTeacherId(teacherId);
        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();
        
        for (SQLResult sqlResult : sqlResults) {
        	Map<String, Object> modelMap = new HashMap<String, Object>();
        	modelMap.put("stuClassId", sqlResult.getStuClass().getId());
        	modelMap.put("semester", sqlResult.getStuClass().getSemester());
            modelMap.put("isTest",sqlResult.getExerInClass().isTest());
            modelMap.put("startTime",sqlResult.getExerInClass().getStartTime()==null ? null : sdf.format(sqlResult.getExerInClass().getStartTime()));
            modelMap.put("endTime",sqlResult.getExerInClass().getStartTime()==null ? null : sdf.format(sqlResult.getExerInClass().getStartTime()));
            modelMap.put("exerciseId", sqlResult.getExercise().getId());
            modelMap.put("name", sqlResult.getExercise().getName());
            modelMap.put("describe", sqlResult.getExercise().getDescribe());
            
            result.add(modelMap);
        }
        
        return result;
    }
    
    /*查询指定试卷是否被使用*/
    public boolean exerIsUsed(int exerciseId){
        QueryWrapper  exerInClassQueryWrapper=new QueryWrapper<>();
        exerInClassQueryWrapper.eq("exerciseId",exerciseId);
        int count=exerInClassMapper.selectCount(exerInClassQueryWrapper);
        if(count>0){
            return true;
        }
        return false;
    }
}
