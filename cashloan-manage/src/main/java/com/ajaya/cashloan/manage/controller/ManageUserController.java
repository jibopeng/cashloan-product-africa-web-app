package com.ajaya.cashloan.manage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.Null;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ajaya.cashloan.cl.domain.BankAccount;
import com.ajaya.cashloan.cl.domain.BorrowRepay;
import com.ajaya.cashloan.cl.domain.Channel;
import com.ajaya.cashloan.cl.domain.UserAuth;
import com.ajaya.cashloan.cl.domain.UserEmerContacts;
import com.ajaya.cashloan.cl.domain.UserVirtualAccount;
import com.ajaya.cashloan.cl.mapper.BankAccountMapper;
import com.ajaya.cashloan.cl.mapper.ClBorrowMapper;
import com.ajaya.cashloan.cl.mapper.UserVirtualAccountMapper;
import com.ajaya.cashloan.cl.model.UserAuthModel;
import com.ajaya.cashloan.cl.service.BankAccountService;
import com.ajaya.cashloan.cl.service.BorrowRepayService;
import com.ajaya.cashloan.cl.service.ChannelService;
import com.ajaya.cashloan.cl.service.ClBorrowService;
import com.ajaya.cashloan.cl.service.NameCheckService;
import com.ajaya.cashloan.cl.service.UserAuthService;
import com.ajaya.cashloan.cl.service.UserEmerContactsService;
import com.ajaya.cashloan.cl.service.UserMessagesService;
import com.ajaya.cashloan.cl.service.UserRemarkService;
import com.ajaya.cashloan.cl.service.UserVirtualAccountService;
import com.ajaya.cashloan.core.common.context.Constant;
import com.ajaya.cashloan.core.common.context.Global;
import com.ajaya.cashloan.core.common.util.JsonUtil;
import com.ajaya.cashloan.core.common.util.RdPage;
import com.ajaya.cashloan.core.common.util.ServletUtils;
import com.ajaya.cashloan.core.domain.Borrow;
import com.ajaya.cashloan.core.domain.User;
import com.ajaya.cashloan.core.model.CloanUserModel;
import com.ajaya.cashloan.core.model.ManagerUserModel;
import com.ajaya.cashloan.core.model.UserBaseInfoModel;
import com.ajaya.cashloan.core.service.CloanUserService;
import com.ajaya.cashloan.core.service.UserBaseInfoService;
import com.ajaya.cashloan.system.permission.annotation.RequiresPermission;
import com.github.pagehelper.Page;

import tool.util.StringUtil;

 /**
 * ????????????Controller
 * 
 * @author jdd
 * @version 1.0.0
 * @date 2017-02-21 13:39:06

 */
@Controller
@Scope("prototype")
public class ManageUserController extends ManageBaseController{
	private static final Logger logger = LoggerFactory.getLogger(ManageUserController.class);
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private UserAuthService authService;
	@Resource
	private UserBaseInfoService userBaseInfoService;
	@Resource
	private UserEmerContactsService userEmerContactsService;
	@Resource
	private ChannelService channelService;
	@Resource 
	private BankAccountMapper bankAccountMapper;
	@Resource
	private UserMessagesService userMessagesService;
	@Resource
	private UserRemarkService userRemarkServiceImpl;
	@Resource
    private UserVirtualAccountMapper userVirtualAccountMapper;
	@Resource
	private UserVirtualAccountService userVirtualAccountService;
	@Resource
	private BorrowRepayService borrowRepayService;
	@Resource
	private ClBorrowMapper clBorrowMappper;

	 /**
      *??????????????????
      * @param searchParams
      * @param currentPage
      * @param pageSize
      */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/cl/cluser/list.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:cl:cluser:list",name = "??????????????????")
	public void list(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		if(params != null && params.containsKey("orderNo")){
			//??????????????????????????????
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("orderNo", params.get("orderNo").toString());
			Borrow borrow = clBorrowMappper.findSelective(paramMap);
			if(borrow != null){
				params.put("userId", borrow.getUserId());
			}
		}
		Page<CloanUserModel> page = cloanUserService.listUser(params,currentPage,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "????????????");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * ??????????????????
	 * @param userId ??????id
	 */
	@RequestMapping(value="/modules/manage/cl/cluser/detail.htm",method={RequestMethod.GET,RequestMethod.POST})   
	@RequiresPermission(code = "modules:manage:cl:cluser:detail",name = "??????????????????")
	public void detail(@RequestParam(value = "userId") Long userId){
		String serverHost = Global.getValue("manage_host");
		HashMap<String, Object> map = new HashMap<String,Object>();
		User user = cloanUserService.getById(userId);
		if (user != null && user.getId() != null) {
			//??????????????????
			ManagerUserModel model = userBaseInfoService.getBaseModelByUserId(userId);
			//model.setLivingImg(model.getLivingImg()!=null?serverHost +"/readFile.htm?path="+ model.getLivingImg():"");
			//model.setFrontImg(model.getFrontImg()!=null?serverHost +"/readFile.htm?path="+ model.getFrontImg():"");
			//model.setBackImg(model.getBackImg()!=null?serverHost +"/readFile.htm?path="+ model.getBackImg():"");
			//model.setOcrImg(model.getPanImg()!=null?serverHost +"/readFile.htm?path="+ model.getPanImg():"");
			
//			if (StringUtil.isNotBlank(model.getWorkingImg())) {
//				String workImgStr = model.getWorkingImg();
//				List<String> workImgList = Arrays.asList(workImgStr.split(";"));
//				for (int i = 0; i < workImgList.size(); i++) {
//					String workImg = workImgList.get(i);
//					workImgList.set(i, serverHost +"/readFile.htm?path="+ workImg);
//				}
//				map.put("workImgArr", workImgList);
//			}
			
			
			Channel cl = channelService.getById(user.getChannelId());
			if (cl!=null) {
				model.setChannleName(cl.getName());
			} else {
				model.setChannleName("organic traffic");
			}
			
		
			//??????????????????
			HashMap<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("userId",user.getId());
			paramMap.put("status", 1);
			List<BankAccount> bankAccounts = bankAccountMapper.listSelective(paramMap);
			
			String bankName = null;
			String accountNo = null;
			String accountName = null;
			String accountNo1 = null;
			String accountNo2 = null;
			String accountNo3 = null;
			String virtualAccountBank = null;
			String virtualAccountNo = null;
			String virtualAccountName = null;
			
			if(bankAccounts != null && bankAccounts.size() > 0){
				bankName = bankAccounts.get(0).getBankName();
				accountName = bankAccounts.get(0).getHolderName();
				accountNo = bankAccounts.get(0).getAccountNumber();
				accountNo1 = bankAccounts.size() > 1 ? bankAccounts.get(1).getAccountNumber() : null;
				accountNo2 = bankAccounts.size() > 2 ? bankAccounts.get(2).getAccountNumber() : null;
				accountNo3 = bankAccounts.size() > 3 ? bankAccounts.get(3).getAccountNumber() : null;
			}
			model.setBankName(bankName);
			model.setAccountNo(accountNo);
			model.setAccountName(accountName);
			model.setAccountNo1(accountNo1);
			model.setAccountNo2(accountNo2);
			model.setAccountNo3(accountNo3);
			
			//??????????????????
			//???????????????????????????????????????
			paramMap.put("state", "20");
			List<BorrowRepay> list = borrowRepayService.listSelective(paramMap);
			if(list != null && list.size() > 0){
				userVirtualAccountService.createBankTransferInfo(user.getId(),Global.getValue("title"));
			}
			
			//??????????????????
			UserVirtualAccount userVirtualAccount = userVirtualAccountMapper.findByUserId(userId);
			if(userVirtualAccount != null){
				virtualAccountName = userVirtualAccount.getAccountName();
				virtualAccountNo = userVirtualAccount.getAccountNumber();
				virtualAccountBank = userVirtualAccount.getBankName();
			}
			
			model.setVirtualAccountNo(virtualAccountNo);
			model.setVirtualAccountName(virtualAccountName);
			model.setVirtualAccountBank(virtualAccountBank);
			map.put("userbase", model);
			
			// ????????????
			UserAuth authModel = authService.getUserAuth(paramMap);
			map.put("userAuth", authModel);
			
			// ???????????????
			List<UserEmerContacts> infoModel = userEmerContactsService.getUserEmerContactsByUserId(paramMap);
			if(infoModel != null && infoModel.size() == 1){
				UserEmerContacts userEmerContacts = new UserEmerContacts();
				userEmerContacts.setName(infoModel.get(0).getName());
				userEmerContacts.setNameInContact("");
				userEmerContacts.setPhone(infoModel.get(0).getPhone());
				userEmerContacts.setRelation(infoModel.get(0).getRelation());
				userEmerContacts.setType(infoModel.get(0).getType().equals("10") ? "10" : "20");
				userEmerContacts.setUserId(userId);
				infoModel.add(userEmerContacts);
			}
			map.put("userContactInfo", infoModel);
		
			Map<String, Object> messageInfoModel = userMessagesService.getMessageInfo(paramMap);
			map.put("messageInfo", messageInfoModel);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, map);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "????????????");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * ????????????????????????
	 * @param searchParams ????????????
	 * @param currentPage ?????????
	 * @param pageSize ????????????
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/cl/cluser/authlist.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:cl:cluser:authlist",name = "????????????????????????")
	public void authlist(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<UserAuthModel> page = authService.listUserAuth(params, currentPage, pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "????????????");
		ServletUtils.writeToResponse(response,result);
	}

	
	/**
	 * ????????????????????????
	 * @param id
	 * @param state
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/user/updateState.htm")
	public void updateState(
			@RequestParam(value="id") long id,
			@RequestParam(value="state") String state) throws Exception {
		//int msg = userBaseInfoService.updateState(id,state);
		int msg = -1;
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg<0) {
			result.put(Constant.RESPONSE_CODE_MSG, "????????????");
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "????????????");
			
		}
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * ????????????????????????
	 * @param id id
	 * @param state ??????
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/user/updateWhiteState.htm")
	public void updateWhiteState(
			@RequestParam(value="id") long id,
			@RequestParam(value="state") String state) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		if(StringUtil.isNotBlank(state) && (UserBaseInfoModel.USER_STATE_NOBLACK.equals(state) || UserBaseInfoModel.USER_STATE_WHITE.equals(state+""))){
			//int msg = userBaseInfoService.updateState(id,state);
			int msg = -1;
			if (msg<0) {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "????????????");
			}else {
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "????????????");
			}
		}else{
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "????????????");
		}
		
		ServletUtils.writeToResponse(response,result);
	}
	


	@RequestMapping(value = "/modules/manage/user/saveRemark.htm")
	public void saveRemark(@RequestParam(value = "userId") Long userId, String remark) throws Exception {
		int msg = userRemarkServiceImpl.saveOrUpdateRemark(userId, remark);
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg<0) {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "????????????");
		}else {
			result.put(Constant.RESPONSE_CODE_MSG, "????????????");
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		}
		ServletUtils.writeToResponse(response,result);
	}
}
