package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.SysUserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
	@Autowired
	private SysUserMapper sysUserMapper;
	
	/* 增加用户 */
	@Override
	public Map<String, Object> insertSysUser(String id, String pwd, short userClass) {
		Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		if (sysUser != null) {
			modelMap.put("state", "error");
			modelMap.put("detail", "用户已存在");
    		return modelMap;
		}
		
		sysUser = new SysUser();
		sysUser.setId(id);
		sysUser.setPwd(pwd);
		sysUser.setUserClass(userClass);
		
		int mes = sysUserMapper.insert(sysUser);
		if (mes == 1) {
			modelMap.put("state", "success");
    	} else {
    		modelMap.put("state", "error");
    	}
		
		return modelMap;
	}

	/* 删除用户 */
	@Override
	public Map<String, Object> deleteSysUser(String id) {
        Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		if (sysUser == null) {
			modelMap.put("state", "error");
			modelMap.put("detail", "用户不存在");
    		return modelMap;
		}
		
		int mes = sysUserMapper.delete(sysUserWrapper);
		if (mes == 1) {
    		modelMap.put("state", "success");
    	} else {
    		modelMap.put("state", "error");
    	}
		
		return modelMap;
	}
	
	/* 根据编号查询指定用户信息 */
	@Override
	public SysUser getSysUserById(String id) {
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		return sysUserMapper.selectOne(sysUserWrapper);
	}
	
	/* 修改用户类型 */
	@Override
	public Map<String, Object> modifySysUserClass(String id, short userClass) {
		Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		
		if (sysUser == null) {
			modelMap.put("state", "error");
			modelMap.put("detail", "用户不存在");
			return modelMap;
		}
		
		sysUser.setUserClass(userClass);
		int result = sysUserMapper.update(sysUser, sysUserWrapper);
		if (result != 1)
			modelMap.put("state", "error");
		else
			modelMap.put("state", "success");
		
		return modelMap;
	}

	/* 修改用户密码 */
	@Override
	public Map<String, Object> modifySysUserPwd(String id, String old_pwd, String new_pwd) {
        Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		
		if (sysUser == null) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "用户不存在");
			return modelMap;
		}
		
		if (!old_pwd.equals(sysUser.getPwd())) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "旧密码不一致");
			return modelMap;
		}

		sysUser.setPwd(new_pwd);

		int result = sysUserMapper.update(sysUser, sysUserWrapper);
		if (result != 1)
			modelMap.put("state", "error");
		else
			modelMap.put("state", "success");
		
		return modelMap;
	}
	
	
	/* 初始化用户密码 */
	@Override
	public Map<String, Object> initializeSysUserPwd(String id) {
        Map<String, Object> modelMap = new HashMap<>();
		
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		
		if (sysUser == null) {
			modelMap.put("state", "error");
			modelMap.put("errmessage", "用户不存在");
			return modelMap;
		}

		sysUser.setPwd("12345678");

		int result = sysUserMapper.update(sysUser, sysUserWrapper);
		if (result != 1)
			modelMap.put("state", "error");
		else
			modelMap.put("state", "success");
		
		return modelMap;
	}
	
	/* 获取用户密码 */
	@Override
	public Map<String, Object> getSysUserPwd(String id) {
		Map<String, Object> modelMap = new HashMap<>();
		QueryWrapper<SysUser> sysUserWrapper = new QueryWrapper<>();
		sysUserWrapper.eq("id", id);
		SysUser sysUser = sysUserMapper.selectOne(sysUserWrapper);
		
		if (sysUser == null) {
			return modelMap;
		}
		
		modelMap.put("id", sysUser.getId());
		modelMap.put("pwd", sysUser.getPwd());
		
		return modelMap;
	}
}
