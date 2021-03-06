package com.ajaya.cashloan.rule.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ajaya.cashloan.rule.model.BorrowRuleConfigModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.ajaya.cashloan.core.common.context.Constant;
import com.ajaya.cashloan.core.common.exception.ServiceException;
import com.ajaya.cashloan.core.common.util.RdPage;
import com.ajaya.cashloan.core.common.util.ServletUtils;
import com.ajaya.cashloan.core.common.web.controller.BaseController;
import com.ajaya.cashloan.rule.domain.BorrowRuleConfig;
import com.ajaya.cashloan.rule.domain.BorrowRuleEngine;
import com.ajaya.cashloan.rule.domain.RuleEngine;
import com.ajaya.cashloan.rule.domain.RuleEngineConfig;
import com.ajaya.cashloan.rule.service.BorrowRuleConfigService;
import com.ajaya.cashloan.rule.service.BorrowRuleEngineService;
import com.ajaya.cashloan.rule.service.RuleEngineConfigService;
import com.ajaya.cashloan.rule.service.RuleEngineService;
import com.ajaya.cashloan.system.service.SysDictService;

 /**
 * 借款规则管理Controller
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2016-12-20 10:22:30

 */
@Scope("prototype")
@Controller
public class BorrowRuleEngineController extends BaseController {

	
	@Resource
	private BorrowRuleEngineService borrowRuleEngineService;
	@Resource
	private RuleEngineService ruleEngineService;
	@Resource
	private SysDictService sysDictService;
	@Resource
	private RuleEngineConfigService ruleEngineConfigService;
	@Resource
	private BorrowRuleConfigService borrowRuleConfigService;
	/**
	 * 查询借款规则列表
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/page.htm")
	public void page(
			@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JSONObject.parseObject(searchParams);
		Page<BorrowRuleEngine> page = borrowRuleEngineService.page(params,currentPage,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 借款规则设置下拉列表选项
	 */
	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/findRuleList.htm")
	public void findRuleList()  throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", RuleEngine.STATE_ENABLE);
		List<RuleEngine> engineList = ruleEngineService.selectList(params);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (RuleEngine rule : engineList) {
			Map<String, Object> search = new HashMap<String, Object>();
			search.put("ruleEnginId", rule.getId());
			List<RuleEngineConfig> configs = ruleEngineConfigService.findByMap(search);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rule", rule);
			map.put("configList", configs);
			list.add(map);
		}

		List<Map<String, Object>> borrowList = new ArrayList<Map<String, Object>>();
		// 获取借款类型数据字典
		List<Map<String, Object>> dicList = sysDictService.getDictsCache("PRODUCT_TYPE");
		for (Map<String, Object> dic : dicList) {
			Map<String, Object> borrows = new HashMap<String, Object>();
			borrows.put("borrowType", dic.get("value"));
			borrows.put("borrowTypeName", dic.get("text"));
			borrowList.add(borrows);
		}

		List<Map<String, Object>> adaptedList = new ArrayList<Map<String, Object>>();
		Map<String, Object> adapted = new HashMap<String, Object>();
		adapted.put("adaptedId", BorrowRuleEngine.ADAPTED_BEFORE);
		adapted.put("adaptedName", "贷前");
		adaptedList.add(adapted);
		adapted = new HashMap<String, Object>();
		adapted.put("adaptedId", BorrowRuleEngine.ADAPTED_AFTER);
		adapted.put("adaptedName", "贷后");
		adaptedList.add(adapted);

		params = new HashMap<String, Object>();
		params.put("adaptedList", adaptedList);
		params.put("rules", list);
		params.put("borrows", borrowList);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, params);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}
	/**
	 * 新增借款规则
	 * @param ruleId
	 * @param ruleName
	 * @param borrowId
	 * @param borrowName
	 */
	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/save.htm")
//	@RequiresPermission(code = "/modules/manage/borrow/borrowRuleEngine/save.htm",name = "新增借款规则")
	public void save(BorrowRuleEngine bre){
		bre.setAddTime(new Date());
		bre.setReqExt(""); 
		bre.setRuleCount(0);
		int msg = borrowRuleEngineService.insert(bre);
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg>0) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "新增成功");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "新增失败");
		}
		ServletUtils.writeToResponse(response,result);
	}
	 
	/**
	 * 查询借款配置详情
	 * @param id
	 */
	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/detail.htm")
	public void detail(@RequestParam(value = "id") long id){
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		params.put("id", id);
		BorrowRuleEngine  bre = borrowRuleEngineService.getById(id);
		params = new HashMap<String, Object>();
		params.put("borrowRuleId", id);
		List<BorrowRuleConfigModel> configs=borrowRuleConfigService.findConfig(params);
		list.put("borrowRuleConfig", configs);
		list.put("borrowRule", bre);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, list);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	
	
	
	/**
	 * 编辑借款规则
	 * @throws ServiceException 
	 */
	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/updateRuleConfig.htm")
	public void update(BorrowRuleEngine brc,@RequestParam(value = "ruleConfigList") String ruleConfigList) throws ServiceException{
		List<BorrowRuleConfigModel> list = JSONObject.parseArray(ruleConfigList,BorrowRuleConfigModel.class);
		List<BorrowRuleConfig> configlist = new ArrayList<BorrowRuleConfig>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				BorrowRuleConfigModel allList = list.get(i);
				if (allList == null) {
					break;
				}
				BorrowRuleConfig rule = allList.getRule();
				for (BorrowRuleConfig config : allList.getConfigList()) {
					BorrowRuleConfig brconfig = new BorrowRuleConfig();
					brconfig.setRuleId(rule.getRuleId());
					brconfig.setRuleSort(rule.getRuleSort());

					brconfig.setConfigId(config.getConfigId());
					brconfig.setConfigSort(config.getConfigSort());

					brconfig.setId(config.getId()); // 修改是配置的id不可少
					configlist.add(brconfig);
				}
			}
		}
		if (brc != null && brc.getBorrowType() != null
				&& brc.getBorrowType() != "") {
			// 获取借款类型数据字典
			List<Map<String, Object>> dicList = sysDictService.getDictsCache("PRODUCT_TYPE");
			for (Map<String, Object> dic : dicList) {
				if (brc.getBorrowType().equals(String.valueOf(dic.get("value")))) {
					brc.setBorrowTypeName(String.valueOf(dic.get("text")));
					break;
				}
			}
		}
		// logger.info("==="+JSON.toJSONString(configlist));
		int msg = borrowRuleEngineService.update(brc, configlist);
		Map<String, Object> result = new HashMap<String, Object>();
		if (msg > 0) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "编辑成功");
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "编辑失败");
		}
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 删除借款规则
	 * @param id
	 */
	//刘晓亮 ：目前 将 删除规则的代码
/*	@RequestMapping(value = "/modules/manage/borrow/borrowRuleEngine/delete.htm")
	public void delete(
			@RequestParam(value = "id") long id){
		int msg = borrowRuleEngineService.deleteById(id);
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg>0) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "删除成功");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "删除失败");
		}
		ServletUtils.writeToResponse(response,result);
	}*/
}
