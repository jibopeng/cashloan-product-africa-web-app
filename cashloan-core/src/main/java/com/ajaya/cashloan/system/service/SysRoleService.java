package com.ajaya.cashloan.system.service;

import java.util.List;
import java.util.Map;

import com.ajaya.cashloan.system.domain.SysRole;
import com.github.pagehelper.Page;
import com.ajaya.cashloan.core.common.exception.BussinessException;
import com.ajaya.cashloan.core.common.exception.ServiceException;

/**
 * 
 * 系统角色服务类
 * 
 * @version 1.0
 * @author 吴国成
 * @created 2014年9月23日 上午9:55:16
 */
public interface SysRoleService {

	/**
	 * 查询查询用户所拥有的角色
	 * @param userId
	 *            用户ID
	 * @return 角色List
	 */
	List<SysRole> getRoleListByUserId(long userId) throws ServiceException;

	/**
	 * 查询角色
	 * @param id
	 *            主键ID
	 * @return 角色
	 */
	SysRole getRoleById(long id)throws ServiceException ;

	/**
	 * 角色查询
	 * @return 角色List
	 */
	List<SysRole> getList(Map<String, Object> paramMap);

	/**
	 * 角色删除
	 * @param id
	 *            主键ID
	 */
	int deleteRole(long id)throws ServiceException;
	
	int getRolecount(Map<String, Object> mapdata)throws ServiceException;

	Page<SysRole> getRolePageList(int currentPage,int pageSize,Map<String, Object> mapdata) throws ServiceException;

	long addRole(SysRole role) throws BussinessException;
	
	long insertByMap(Map<String, Object> data) throws ServiceException;
	
	int  updateRole(Map<String, Object> arg) throws BussinessException;
	
	List<Map<String,Object>> getByUserPassRolesList(String username,String password) throws ServiceException;
	
	/**
	 * 根据标识nid查询
	 * @param nid
	 * @return
	 */
	SysRole findByNid(String nid);
}
