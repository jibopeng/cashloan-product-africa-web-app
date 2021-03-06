package com.ajaya.cashloan.manage.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ajaya.cashloan.core.common.context.Constant;
import com.ajaya.cashloan.core.common.exception.ImgCodeException;
import com.ajaya.cashloan.core.common.exception.ServiceException;
import com.ajaya.cashloan.core.common.exception.SysAccessCodeException;
import com.ajaya.cashloan.core.common.util.ServletUtils;
import com.ajaya.cashloan.core.common.util.StringUtil;
import com.ajaya.cashloan.core.common.web.controller.BaseController;
import com.ajaya.cashloan.core.domain.SysUserLoginLog;
import com.ajaya.cashloan.core.mapper.SysUserLoginLogMapper;
import com.ajaya.cashloan.system.domain.SysRole;
import com.ajaya.cashloan.system.domain.SysUser;
import com.ajaya.cashloan.system.domain.SysUserRole;
import com.ajaya.cashloan.system.security.authentication.encoding.PasswordEncoder;
import com.ajaya.cashloan.system.service.SysRoleService;
import com.ajaya.cashloan.system.service.SysUserRoleService;
import com.ajaya.cashloan.system.service.SysUserService;

/**
 * 
 * ????????????Action, ????????????????????????Spring Security??????, ???Action????????????????????????
 * 
 * @version 1.0
 * @author ?????????
 * @created 2014???9???23??? ??????2:15:42
 */
@Scope("prototype")
@Controller
public class SysLoginController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(SysLoginController.class);

	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private AuthenticationManager authenticationManager;
	@Resource
	private PasswordEncoder passwordEncoder;// ????????????
	@Resource
	private SysUserRoleService sysUserRoleService;

	/**
	 * ????????????
	 * 
	 * @param error
	 * @param model
	 */
	@RequestMapping("/login.htm")
	public void login(HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.sendRedirect("/dev/index.html");
	}

	@RequestMapping("/index.htm")
	public String index() {
		return "index";
	}
	@Resource
	private SysUserLoginLogMapper sysUserLoginLogMapper;
	@RequestMapping(value = "/system/user/login.htm")
	public void loginAjax(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "accessCode", required = false) String accessCode,
			HttpServletResponse response,
			HttpServletRequest request, HttpSession session) throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			//shiro
			Subject user = SecurityUtils.getSubject();
			password = passwordEncoder.encodePassword(String.valueOf(password));
			UsernamePasswordToken token = new UsernamePasswordToken(username,password);
			token.setRememberMe(true);
			user.login(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT",SecurityContextHolder.getContext());
			SysUser sysUser = (SysUser) user.getSession().getAttribute("SysUser");
			
			//?????????????????????
			checkImgCode(request.getParameter("code"),session.getAttribute("code"));
			
			session.setAttribute("SysUser", sysUser);
			
			List<SysUserRole> list = sysUserRoleService.getSysUserRoleList(sysUser.getId());
			if(list != null && list.size() > 0) {
				session.setAttribute(Constant.ROLEID, list.get(0).getRoleId());
			} else {
				throw new UnknownAccountException("?????????????????????????????????");
			}
			SysUserLoginLog sysUserLoginLog = new SysUserLoginLog();
			sysUserLoginLog.setUserId(sysUser.getId());
			sysUserLoginLog.setUserName(username);
			sysUserLoginLog.setCreateTime(new Date());
			int save = sysUserLoginLogMapper.save(sysUserLoginLog);
			logger.info("userName:" + username + ",???????????????????????????:" + save);
			res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		} catch (SysAccessCodeException ex){
			logger.error("???????????????", ex);
			res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "Logon failure, access code invalid");
		} catch (IncorrectCredentialsException ex){
			logger.error("????????????", ex);
			res.put(Constant.RESPONSE_CODE, Constant.OTHER_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "wrong password");
		} catch (LockedAccountException ex) {
			logger.error("??????????????????", ex);
			res.put(Constant.RESPONSE_CODE, Constant.OTHER_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "This user has been locked, please contact the administrator!");
		} catch (AuthenticationException ex) {
			logger.error("????????????", ex);
			res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "Login failed, please try again");
		} catch (ExpiredCredentialsException ex) {
			logger.error(ex.getMessage(), ex);
			res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, ex.getMessage());
		} catch (UnknownAccountException ex){
			logger.error(ex.getMessage(), ex);
			res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "The account does not exist");
		} catch (ImgCodeException ex){
			logger.error(ex.getMessage(), ex);
			res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			res.put(Constant.RESPONSE_CODE_MSG, "Image verification code error");
		}  
		
		ServletUtils.writeToResponse(response, res);

	}
	
	private void checkImgCode(String code, Object sessionCode) {
		if (StringUtil.isBlank(code)||code.length()!=4||!code.equals(sessionCode)) {
			throw new ImgCodeException("?????????????????????");
		}
	}

	/**
	 * ?????????????????????
	 * @param search
	 * @throws Exception
	 */
	@RequestMapping(value = "/system/user/imgCode/generate.htm")
	public void generate() throws Exception {
		super.generateImgCode();
	}
	
	/**
	 * ????????????
	 * @description
	 * @param role
	 * @param response
	 * @param request
	 * @author wgc
	 * @return void
	 * @throws IOException
	 * @since 1.0.0
	 */
	@RequestMapping(value = "/system/user/switch/role.htm")
	public void changeLoginajax(@RequestParam(value = "role", required = false) String role,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();
		HttpSession session = request.getSession(true);
		session.setAttribute(Constant.ROLEID, Long.valueOf(role.trim()));
		res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		ServletUtils.writeToResponse(response, res);
	}

	public void validateRole(SysUser user, Long roleid) throws ServiceException {
		List<SysUserRole> list = sysUserRoleService.getSysUserRoleList(user.getId());
		for (SysUserRole role : list) {
			if (role.getRoleId().equals(roleid))
				return;
		}
		SysRole role = sysRoleService.getRoleById(roleid);
		throw new ServiceException(user.getName() + "?????????[" + role.getName() + "]????????????");
	}

	@RequestMapping(value = "/login2.htm")
	public void sessionout(HttpServletResponse response) {

		Map<String, Object> res = new HashMap<String, Object>();
		res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
		res.put(Constant.RESPONSE_CODE_MSG, "????????????");

		ServletUtils.writeToResponse(response, res);
	}
	
	@RequestMapping(value = "/system/user/logout.htm")
	public void logout() {
	/*	
	 * session.removeAttribute("SysUser");
		session.removeAttribute(Constant.ROLEID);
		session.removeAttribute("SPRING_SECURITY_CONTEXT");
	    session.invalidate();
	    */
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		res.put(Constant.RESPONSE_CODE_MSG, "??????");
		ServletUtils.writeToResponse(response, res);
	}
	
	
}
