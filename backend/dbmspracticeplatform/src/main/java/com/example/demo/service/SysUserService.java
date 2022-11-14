package com.example.demo.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.SysUser;

public interface SysUserService extends IService<SysUser>  {
	/* 增加用户 */
	Map<String, Object> insertSysUser(String id, String pwd, short userClass);
	
	/* 删除用户 */
	Map<String, Object> deleteSysUser(String id);
	
	/* 根据编号查询指定用户信息 */
	SysUser getSysUserById(String id);
	
	/* 修改用户类型 */
	Map<String, Object> modifySysUserClass(String id, short userClass);
	
	/* 修改用户密码 */
	Map<String, Object> modifySysUserPwd(String id, String old_pwd, String new_pwd);
	
	/* 初始化用户密码 */
	Map<String, Object> initializeSysUserPwd(String id);
	
	/* 获取用户密码 */
	Map<String, Object> getSysUserPwd(String id);
}
