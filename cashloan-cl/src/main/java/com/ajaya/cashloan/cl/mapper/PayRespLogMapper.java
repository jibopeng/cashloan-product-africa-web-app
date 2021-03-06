package com.ajaya.cashloan.cl.mapper;

import java.util.List;
import java.util.Map;

import com.ajaya.cashloan.cl.domain.PayRespLog;
import com.ajaya.cashloan.cl.model.ManagePayRespLogModel;
import com.ajaya.cashloan.core.common.mapper.BaseMapper;
import com.ajaya.cashloan.core.common.mapper.RDBatisDao;

/**
 * 支付响应记录Mapper
 * 
 * @author gc
 * @version 1.0.0
 * @date 2017-03-07 16:18:10
 */
@RDBatisDao
public interface PayRespLogMapper extends BaseMapper<PayRespLog,Long> {

	/**
	 * 分页查询
	 * 
	 * @param searchMap
	 * @return
	 */
	List<ManagePayRespLogModel> page(Map<String, Object> searchMap);

	/**
	 * 查询详情
	 * 
	 * @param id
	 * @return
	 */
	ManagePayRespLogModel findDetail(Long id);
}
