package com.ajaya.cashloan.manage.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.ajaya.cashloan.manage.domain.QuartzInfo;
import com.ajaya.cashloan.manage.model.QuartzInfoModel;
import com.ajaya.cashloan.manage.model.QuartzManager;
import com.ajaya.cashloan.manage.service.QuartzInfoService;
import org.apache.log4j.Logger;

import tool.util.BeanUtil;

public class QuartzListener implements ServletContextListener,HttpSessionAttributeListener{

	private static Logger logger=Logger.getLogger(QuartzListener.class);
	
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {

		
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {

		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {

		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("【启动所有任务】开始...");
		try {
			QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil.getBean("quartzInfoService");
		
			// 查询启用状态的定时任务信息
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("state", QuartzInfoModel.STATE_ENABLE);
			List<QuartzInfo> list = quartzInfoService.list(paramMap);
			
			// 循环添加任务
			for (QuartzInfo quartzInfo : list) {
				String clName = quartzInfo.getClassName();
				Object cl = Class.forName(clName).newInstance();
				QuartzManager.addJob(quartzInfo.getCode(), cl.getClass(),quartzInfo.getCycle());
			}
			
			// 启动所有定时任务			
			QuartzManager.startJobs();

		} catch (Exception e) {
			logger.error("启动定时任务异常--->" + e.getMessage(), e);
		}
		logger.info("【启动所有任务】结束...");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		
	}

}
