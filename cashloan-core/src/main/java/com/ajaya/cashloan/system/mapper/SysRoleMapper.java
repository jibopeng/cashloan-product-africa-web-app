package com.ajaya.cashloan.system.mapper;

import java.util.List;
import java.util.Map;

import com.ajaya.cashloan.core.common.mapper.BaseMapper;
import com.ajaya.cashloan.core.common.mapper.RDBatisDao;
import com.ajaya.cashloan.system.domain.SysRole;

/**
 * 
 * 角色DAO
 * 
 * @version 1.0
 * @author 吴国成
 * @created 2014年9月22日 下午2:51:25
 */
@RDBatisDao
public interface SysRoleMapper extends BaseMapper<SysRole,Long> {
	List<? extends SysRole> getRolePageList(Map<String, Object> data);

	List<SysRole> getRoleListByUserId(Long userId);

	List<SysRole> getListByMap(Map<String, Object> param);

	int deleteById(long id);

	int updateByMap(Map<String, Object> arg);
	
	List<Map<String, Object>> getByUserPassRolesList(Map<String, Object> data);

	int getRolecount(Map<String, Object> mapdata);

	long insertByMap(Map<String, Object> data);

}
