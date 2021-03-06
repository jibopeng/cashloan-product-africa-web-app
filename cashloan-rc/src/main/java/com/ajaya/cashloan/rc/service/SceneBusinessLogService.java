package com.ajaya.cashloan.rc.service;

import com.ajaya.cashloan.core.common.service.BaseService;
import com.ajaya.cashloan.rc.domain.SceneBusinessLog;

/**
 * 场景与第三方征信接口执行记录
 * @author caitt
 * @version 1.0
 * @date 2017年4月11日上午11:51:09
 */
public interface SceneBusinessLogService extends BaseService<SceneBusinessLog, Long> {

	
	/**
	 * 是否有未执行完的接口
	 * @return
	 */
	boolean haveNeedExcuteService(Long borrowId);
	
	/**
	 * 是否需要执行
	 * @param userId
	 * @param info
	 * @return true  有未完成的接口，false 没有未完成的接口
	 * @throws Exception
	 */
	boolean needExcute(Long userId,Long busId,String getWay,String period);

	/**
	 * 
	 * @param borrowId
	 * @param busNid
	 * @return
	 */
	SceneBusinessLog findByBorrowIdAndBusNid(long borrowId, String busNid);
}
