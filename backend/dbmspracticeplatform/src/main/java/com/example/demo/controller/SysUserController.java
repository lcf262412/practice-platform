package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BaseResponse;
import com.example.demo.entity.Student;
import com.example.demo.entity.SysUser;
import com.example.demo.service.StudentService;
import com.example.demo.service.SysUserService;
import com.example.demo.util.LoginCacheUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/user")
@CrossOrigin
@RestController
public class SysUserController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private StudentService studentService;
	
	@PostMapping(value = "/login")
	private BaseResponse<Map<String, Object>> Login(@RequestParam(value = "id") String id,
			                                        @RequestParam(value = "pwd") String pwd) {
		Map<String, Object> modelMap = new HashMap<>();
		SysUser sysUser = sysUserService.getSysUserById(id);
		
		if(sysUser == null) {
			modelMap.put("errmessage", "用户不存在");
			return BaseResponse.fail(modelMap);	
		}
		
		if(!sysUser.getPwd().equals(pwd)) {
			modelMap.put("errmessage", "密码错误");
			return BaseResponse.fail(modelMap);
		}
		//计划用于避免重复登录，暂未考虑实现。
		if(LoginCacheUtil.get(id).isPresent())
		{
			modelMap.put("errmessage", "用户已登录，请稍后重试");
			return BaseResponse.fail(modelMap);
		}
		log.info("-------------------------------");
		log.info(sysUser.getId()+' '+sysUser.getUserClass());
		if (sysUser.getUserClass() >= 3) {
			if(sysUser.getUserClass()==5)
			{
				Map<String, Object> ttt = new HashMap<>();
				ttt = studentService.useStuidgetTeaid(id);
				modelMap.put("teacherid",ttt.get("t_id"));
			}
			log.info(sysUser.getId()+' '+sysUser.getUserClass());
			Student student = studentService.getStudent(id);
			if (student == null) {
				modelMap.put("errmessage", "学生不存在");
			}
			modelMap.put("isactive", student.isIsactive());
		}
		
		modelMap.put("userClass", sysUser.getUserClass());
		LoginCacheUtil.set(id, id,2*60*1000);
		//在此处添加
		return BaseResponse.success(modelMap);
	}
	
	@GetMapping(value = "/logout")
	private BaseResponse<Map<String, Object>> Logout(@RequestParam(value = "id") String id){
		Map<String, Object> modelMap = new HashMap<>();
		LoginCacheUtil.remove(id);
		if(LoginCacheUtil.get(id).isPresent())
		{
			modelMap.put("fail logout", id);
		}
		modelMap.put("succeed logout", id);
		return BaseResponse.success(modelMap);
	}
	
	@GetMapping(value = "/logupdate")
	private BaseResponse<Map<String, Object>> Logupdate(@RequestParam(value = "id") String id){
		Map<String, Object> modelMap = new HashMap<>();
		SysUser sysUser = sysUserService.getSysUserById(id);
		log.info("sysUser: {}", sysUser);
		if(sysUser == null) {
			modelMap.put("errmessage", "用户不存在");
			return BaseResponse.fail(modelMap);
		}

		if(LoginCacheUtil.get(id).isEmpty())
		{
			modelMap.put("errmessage", "用户未登录，无法更新过期时间");
			return BaseResponse.fail(modelMap);
		}

		if(LoginCacheUtil.update(id).isPresent())
			modelMap.put("更新过期时间成功",id);
		else
			modelMap.put("更新过期时间失败",id);
		return BaseResponse.success(modelMap);
	}
	
	@PostMapping(value = "/changePwd")
	public BaseResponse<Map<String, Object>> changePwd(@RequestParam(value = "id") String id,
                                                       @RequestParam(value = "oldPwd") String oldPwd,
                                                       @RequestParam(value = "newPwd") String newPwd) {
		Map<String, Object> resultMap = sysUserService.modifySysUserPwd(id, oldPwd, newPwd);
		if (!"success".equals(resultMap.get("state"))) {
			return BaseResponse.fail(resultMap);
		}
		
		return BaseResponse.success(null);
	}
	@GetMapping(value = "/initializePwd")
	public BaseResponse<Map<String, Object>> initializePwd(@RequestParam(value = "id") String id) {
		log.info(id);
		Map<String, Object> resultMap = sysUserService.initializeSysUserPwd(id);
		if (!"success".equals(resultMap.get("state"))) {
			return BaseResponse.fail(resultMap);
		}
		
		return BaseResponse.success(null);
	}
}
