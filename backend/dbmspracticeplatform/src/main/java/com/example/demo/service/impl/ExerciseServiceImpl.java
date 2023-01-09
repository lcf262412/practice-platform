package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.ExerInClass;
import com.example.demo.entity.Exercise;
import com.example.demo.entity.QuestInExer;
import com.example.demo.mapper.ExerInClassMapper;
import com.example.demo.mapper.ExerciseMapper;
import com.example.demo.mapper.QuestInExerMapper;
import com.example.demo.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements ExerciseService {
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private ExerInClassMapper exerInClassMapper;
    @Autowired
    private QuestInExerMapper questInExerMapper;

    /*增加试卷*/
    public Map<String,Object> addExercise(String teacherId, String name, String describe) {
        Exercise exercise=new Exercise();
        exercise.setDeleteflag(false);
        exercise.setPublic(false);
        exercise.setTeacherId(teacherId);
        exercise.setName(name);
        exercise.setDescribe(describe);
        int mes=exerciseMapper.insert(exercise);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;
    }

    /*逻辑删除试卷*/
    public Map<String,Object> delExercise(int id){
        Exercise exercise=exerciseMapper.selectById(id);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if (exercise==null) {
        	modelMap.put("state", "error");
            modelMap.put("errmessage", "练习不存在");
        	return modelMap;

        };
        exercise.setDeleteflag(true);
        UpdateWrapper<Exercise> exerciseUpdateWrapper=new UpdateWrapper<>();
        exerciseUpdateWrapper.eq("id",id);
        int mes=exerciseMapper.update(exercise,exerciseUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;

    }

    /*修改试卷信息*/
    public Map<String,Object> modifyExercise(int id, String name, String describe){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
    	Exercise exercise=exerciseMapper.selectById(id);
        if (exercise==null) {
        	modelMap.put("state","error");
        	modelMap.put("errmessage","试卷不存在");
        	return modelMap;
        }
        int id_old=exercise.getId();
        exercise.setId(id);
        exercise.setName(name);
        exercise.setDescribe(describe);
        UpdateWrapper<Exercise> exerciseUpdateWrapper=new UpdateWrapper<>();
        exerciseUpdateWrapper.eq("id",id_old);
        int mes=exerciseMapper.update(exercise,exerciseUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;

    }

    /*查询全部可使用试卷*/
    public List<Map<String,Object>> getAllExercise(String id){
        List<Exercise> result=exerciseMapper.getALLExercise(id);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(Exercise e:result){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("e_id",e.getId());
            modelMap.put("e_name",e.getName());
            modelMap.put("t_id", e.getTeacherId());
            modelMap.put("t_name",e.getTeacher().getName());
            modelMap.put("describe",e.getDescribe());
            modelMap.put("isPublic",e.isPublic());
            list.add(modelMap);
        }

        return list;

    }

    /*设置试卷开放*/
    public   Map<String,Object> setExercisePublic(int id){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
        Exercise exercise=exerciseMapper.selectById(id);
        if (exercise==null) {
        	modelMap.put("state","error");
        	modelMap.put("errmesage","试卷不存在");
        	return modelMap;
        }
        exercise.setPublic(true);
        UpdateWrapper<Exercise> exerciseUpdateWrapper=new UpdateWrapper<>();
        exerciseUpdateWrapper.eq("id",id);
        int mes=exerciseMapper.update(exercise,exerciseUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;
    }

   /*设置试卷私用*/
    public Map<String,Object> setExercisePrivate( int id){
    	Map<String,Object> modelMap = new HashMap<String, Object>();
        Exercise exercise=exerciseMapper.selectById(id);
        if (exercise==null) {
        	modelMap.put("state","error");
        	modelMap.put("errmesage","试卷不存在");
        	return modelMap;
        }
        
        //判断是否存在其他教师使用试卷情况
        int count = exerciseMapper.getOtherTeacherUseCount(id);
        if (count > 0) {
        	modelMap.put("state","error");
        	modelMap.put("errmesage","存在其他教师使用试卷情况");
        	return modelMap;
        }
        
        exercise.setPublic(false);
        UpdateWrapper<Exercise> exerciseUpdateWrapper=new UpdateWrapper<>();
        exerciseUpdateWrapper.eq("id",id);
        int mes=exerciseMapper.update(exercise,exerciseUpdateWrapper);
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;
    }

    /* 物理删除试卷 */
    @Override
    @Transactional
    public Map<String, Object> delExercisePhysically( int id) {
    	Exercise exercise=exerciseMapper.selectById(id);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        /* 判断是否存在 */
        if (exercise==null) {
        	modelMap.put("state", "error");
        	modelMap.put("errmessage", "试卷不存在");
        	return modelMap;
        }
        
        //判断试卷是否已经发布，如果发布无法删除试卷 
        QueryWrapper<ExerInClass> exerInClassWrapper = new QueryWrapper<>();
        exerInClassWrapper.eq("exerciseid", id);
        int exerInClassCount = exerInClassMapper.selectCount(exerInClassWrapper);
        if (exerInClassCount > 0) {
        	modelMap.put("state", "error");
        	modelMap.put("errmessage", "试卷已被发布到相关班级，无法删除");
        	return modelMap;
        }
        
        //删除试卷题目表中的相关记录
        QueryWrapper<QuestInExer> questInExerWrapper = new QueryWrapper<>();
        questInExerWrapper.eq("exerciseid", id);
        questInExerMapper.delete(questInExerWrapper);
        
        //物理删除试卷
        int mes = exerciseMapper.deleteById(id);
        
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
            modelMap.put("errmessage","删除失败，请联系数据库管理员解决");
        }

        return modelMap;
    }
    
    /* 查询全部试卷（包括已设为删除状态的试卷） */
    public List<Map<String, Object>> getExercises() {
    	List<Exercise> results = exerciseMapper.getExercises();
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(Exercise e:results){
            Map<String,Object> modelMap = new HashMap<String, Object>();
            modelMap.put("e_id",e.getId());
            modelMap.put("e_name",e.getName());
            modelMap.put("t_id", e.getTeacherId());
            modelMap.put("t_name",e.getTeacher().getName());
            modelMap.put("describe",e.getDescribe());
            modelMap.put("isPublic",e.isPublic());
            modelMap.put("deleteflag", e.isDeleteflag());
            list.add(modelMap);
        }

        return list;
    }
}
