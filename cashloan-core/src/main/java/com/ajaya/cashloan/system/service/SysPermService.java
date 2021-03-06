package com.ajaya.cashloan.system.service;

import java.util.List;
import java.util.Map;

import com.ajaya.cashloan.core.common.service.BaseService;
import com.ajaya.cashloan.system.domain.SysPerm;

public interface SysPermService extends BaseService<SysPerm, Long>{

	int updateByPrimaryKeySelective(SysPerm record);
	    
	List<SysPerm> listByUserName(String userName);
	
	List<SysPerm> listByRoleId(Long roleId);
	
	List<Map<String, Object>> fetchAll();
}
