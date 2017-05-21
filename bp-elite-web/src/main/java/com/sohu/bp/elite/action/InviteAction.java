package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.person.UserInviteDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.InviteStatus;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.external.WechatService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.InviteService;
import com.sohu.bp.elite.util.EliteMessageUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author zhijungou
 * 2016年10月24日
 * 问答邀请相关内容
 */
@Controller
//@RequestMapping("ask/invite")
public class InviteAction {
	private static final Logger log = LoggerFactory.getLogger(InviteAction.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	@Resource
	private InviteService inviteService;
	@Resource
	private BpUserService bpUserService;
	@Resource
	private WechatService wechatService;
	
	//在缓存保存邀请的转态
	@RequestMapping(value = {"ask/invite/save"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String saveInvite(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
							 @RequestParam(value = "invitedUserId", required = true) String encodedInvitedId, HttpSession session, HttpServletRequest request){
		return saveInviteMethod(encodedQuestionId, encodedInvitedId, session, request);
	}
	
	@RequestMapping(value = {"wx/ask/invite/save"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxSaveInvite(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
							 @RequestParam(value = "invitedUserId", required = true) String encodedInvitedId, HttpSession session, HttpServletRequest request){
		return saveInviteMethod(encodedQuestionId, encodedInvitedId, session, request);
	}
	
    @RequestMapping(value = {"app/ask/invite/save"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appSaveInvite(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
                             @RequestParam(value = "invitedUserId", required = true) String encodedInvitedId, HttpSession session, HttpServletRequest request){
        return saveInviteMethod(encodedQuestionId, encodedInvitedId, session, request);
    }
	
	public String saveInviteMethod(String encodedQuestionId, String encodedInvitedId, HttpSession session, HttpServletRequest request){
		Long questionId = IDUtil.decodeId(encodedQuestionId);
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long inviteUserId = (long) session.getAttribute(SessionConstants.USER_ID);
		if(null == inviteUserId || inviteUserId <=0 ) return ResponseJSON.getErrorParamsJSON().toString();
		Long invitedUserId = IDUtil.decodeId(encodedInvitedId);
		if (null == questionId || null == invitedUserId || null == invitedUserId ||
				questionId <= 0 || inviteUserId <= 0 || invitedUserId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
		Integer saveResult = inviteService.saveInviteStatus(questionId, inviteUserId, invitedUserId);
		//1代表已邀请，2代表未邀请
		if(saveResult.equals(InviteStatus.INVITE_ALREADY.getValue())) resJSON.put("data", true);
		else if(saveResult.equals(InviteStatus.INVITE_NOT_YET.getValue())) {
			resJSON.put("data", false);		
			UserDetailDisplayBean user = bpUserService.getUserDetailByBpId(inviteUserId, null);
			EliteMessageUtil.postInviteMessage(user.getNick(), invitedUserId, questionId);
		}
		return (saveResult > 0) ? resJSON.toString() : ResponseJSON.getErrorInternalJSON().toString();
	}
	
	//获取用户的邀请列表
	@RequestMapping(value = {"ask/invite/list"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getInviteList(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
								PageBean page, HttpSession session){
		return getInviteListMethod(encodedQuestionId, page, session);
	}
	
	@RequestMapping(value = {"wx/ask/invite/list"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxGetInviteList(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
								PageBean page, HttpSession session){
		return getInviteListMethod(encodedQuestionId, page, session);
	}
	
	@RequestMapping(value = {"app/ask/invite/list"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetInviteList(@RequestParam(value = "questionId", required = true) String encodedQuestionId,
                                PageBean page, HttpSession session){
        return getInviteListMethod(encodedQuestionId, page, session);
    }
	
	public String getInviteListMethod(String encodedQuestionId, PageBean page, HttpSession session){
		Long questionId = IDUtil.decodeId(encodedQuestionId);
		Long inviteUserId = (long) session.getAttribute(SessionConstants.USER_ID);
		if(null == inviteUserId || inviteUserId <=0 ) return ResponseJSON.getErrorParamsJSON().toString();
		if(null == questionId || null == inviteUserId || questionId <= 0 || inviteUserId<= 0) return ResponseJSON.getErrorParamsJSON().toString();
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Integer total = inviteService.getRecomNum();
		int pageNo = page.getCurrPageNo();
		page.setTotal(total);
		Integer totalPage = page.getTotalPage();
		Integer startPage = (pageNo - 1) % totalPage;
		Integer start = startPage * page.getPageSize();
		List<Long> idRecomList = inviteService.getRecomInviteList(start, page.getPageSize());
		List<Long> idUserList = inviteService.getUserInviteList(questionId, inviteUserId);
		List<UserInviteDisplayBean> userList = new ArrayList<>();
		try{
			userList = fill2InviteList(idRecomList, idUserList);
		} catch (Exception e) {
			log.error("", e);
		}
		resJSON.put("data", userList);
		return resJSON.toString();
	}
	
	//wap端跳转邀请页
	@RequestMapping(value = {"ask/invite/{questionId}/go.html", "inv/{questionId}/go"})
	public String go(@PathVariable(value = "questionId") String questionId, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		model.put("questionId", questionId);
		return "mobile/invite-answer";
	}
	
	public List<UserInviteDisplayBean> fill2InviteList(List<Long> idRecomList, List<Long> idUserList) throws TException{
		List<UserInviteDisplayBean> userList = new ArrayList<>();
		for(Long id : idRecomList){
		UserInviteDisplayBean userBean = new UserInviteDisplayBean();
		userBean.setBpId(IDUtil.encodeId(id));
		TEliteUser user = eliteAdapter.getUserByBpId(id);
		UserDetailDisplayBean userDetail = bpUserService.getUserSimpleByBpId(id);
		userBean.setIdentityString(userDetail.getIdentityString());
		userBean.setAuthenticated(userDetail.isAuthenticated());
		userBean.setAvatar(userDetail.getAvatar());
		userBean.setDescription(user.getDescription());
		userBean.setNick(userDetail.getNick());
		userBean.setInvited(idUserList.contains(id));
		userList.add(userBean);
		}
		return userList;
	}
}
