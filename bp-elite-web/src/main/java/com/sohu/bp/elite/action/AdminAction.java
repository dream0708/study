package com.sohu.bp.elite.action;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.util.HttpUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.bp.utils.StringUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.THEAD;
import org.beetl.ext.fn.Print;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholastang on 2017/3/14.
 */
@Controller
@RequestMapping({"admin"})
public class AdminAction {
    private static final Logger logger = LoggerFactory.getLogger(AdminAction.class);
    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private static final String ELITE_REJECT_URI = "/audit/innerapi/elite-audit/reject";
    private static final int AUDIT_CONTENT = 1;
    private static final int AUDIT_USER = 2;
    private static final int AUDIT_ALL = 3;

    @ResponseBody
    @RequestMapping(value = "reject-content", produces = "application/json;charset=utf-8", method= RequestMethod.POST)
    public String rejectContent(String contextId, int type, String reason, HttpSession session){
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long contextIdNum = null;
        try {
        	contextIdNum = IDUtil.decodeId(contextId);
		} catch (Exception e) {
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
        
        if (null == contextIdNum || contextIdNum <= 0 || (type != FeedType.QUESTION.getValue() && type != FeedType.ANSWER.getValue())
                || StringUtils.isBlank(reason)) {
            resJSON = ResponseJSON.getErrorParamsJSON();
            return resJSON.toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (!this.checkUserAuth(bpId)) {
           return ResponseJSON.getErrorParamsJSON().toString();
        }

        StringBuilder questionIdSb = new StringBuilder("");
        StringBuilder answerIdSb = new StringBuilder("");
        StringBuilder bpIdSb = new StringBuilder("");
        StringBuilder rejectReasonSb = new StringBuilder("");

        resJSON = this.fillAuditParams(contextIdNum, type, reason, questionIdSb, answerIdSb, bpIdSb, rejectReasonSb, AUDIT_CONTENT, bpId);
        if(resJSON.getInt("code") != ResponseJSON.CODE_SUC) {
            return resJSON.toString();
        }
        resJSON = this.getAuditResponse(questionIdSb.toString(), answerIdSb.toString(), bpIdSb.toString(), rejectReasonSb.toString());
        
        if (ResponseJSON.CODE_SUC == resJSON.getInt("code")) {
        	logger.info("[ADMIN]" + bpId + " reject the context " + contextIdNum + " and the type of context is " + type + " the reason is " + reason);
        } 
        return resJSON.toString();
    }

    @ResponseBody
    @RequestMapping(value = "freeze-user", produces = "application/json;charset=utf-8", method= RequestMethod.POST)
    public String freezeUser(String contextId, int type, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long contextIdNum = null;
		try {
			contextIdNum = IDUtil.decodeId(contextId);
		} catch (Exception e) {
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
        if (null == contextIdNum || contextIdNum <= 0 || (type != FeedType.QUESTION.getValue() && type != FeedType.ANSWER.getValue())) {
            resJSON = ResponseJSON.getErrorParamsJSON();
            return resJSON.toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (!this.checkUserAuth(bpId)) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        StringBuilder questionIdSb = new StringBuilder("");
        StringBuilder answerIdSb = new StringBuilder("");
        StringBuilder bpIdSb = new StringBuilder("");
        StringBuilder rejectReasonSb = new StringBuilder("");

        resJSON = this.fillAuditParams(contextIdNum, type, "", questionIdSb, answerIdSb, bpIdSb, rejectReasonSb, AUDIT_USER, bpId);
        if (resJSON.getInt("code") != ResponseJSON.CODE_SUC) {
            return resJSON.toString();
        }
        resJSON = this.getAuditResponse(questionIdSb.toString(), answerIdSb.toString(), bpIdSb.toString(), rejectReasonSb.toString());
        if (ResponseJSON.CODE_SUC == resJSON.getInt("code")) {
        	 logger.info("[ADMIN]" + bpId + " freeze the user " + bpIdSb + " the contextid is " + contextIdNum + " the type is " + type);
        }
        return resJSON.toString();
    }

    @ResponseBody
    @RequestMapping(value = "reject-all", produces = "application/json;charset=utf-8", method= RequestMethod.POST)
    public String rejectContentAndFreezeUser(String contextId, int type, String reason, HttpSession session){
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long contextIdNum = null;
		try {
			contextIdNum = IDUtil.decodeId(contextId);
		} catch (Exception e) {
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
        if (contextIdNum <= 0 || (type != FeedType.QUESTION.getValue() && type != FeedType.ANSWER.getValue())
                || StringUtils.isBlank(reason)) {
            resJSON = ResponseJSON.getErrorParamsJSON();
            return resJSON.toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (!this.checkUserAuth(bpId)) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        StringBuilder questionIdSb = new StringBuilder("");
        StringBuilder answerIdSb = new StringBuilder("");
        StringBuilder bpIdSb = new StringBuilder("");
        StringBuilder rejectReasonSb = new StringBuilder("");

        resJSON = this.fillAuditParams(contextIdNum, type, "", questionIdSb, answerIdSb, bpIdSb, rejectReasonSb, AUDIT_ALL, bpId);
        if(resJSON.getInt("code") != ResponseJSON.CODE_SUC) {
            return resJSON.toString();
        }
        resJSON = this.getAuditResponse(questionIdSb.toString(), answerIdSb.toString(), bpIdSb.toString(), rejectReasonSb.toString());
      
        if (ResponseJSON.CODE_SUC == resJSON.getInt("code")) {
        	logger.info("[ADMIN]" + bpId + " reject the user " + bpIdSb + " and reject the context " + contextIdNum + " , and the type is " + type + " the reason is " + reason);
        }
        
        return resJSON.toString();
    }
    
    @ResponseBody
    @RequestMapping(value = "delete-freeze", produces = "application/json;charset=utf-8", method= RequestMethod.POST)
    public String deleteCommentAndFreezeUser(String commentId, String freezeId, HttpSession session){
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        if (StringUtils.isBlank(commentId) || StringUtils.isBlank(freezeId)) {
        	return ResponseJSON.getErrorParamsJSON().toString();
        }
        Long commentIdNum = null;
        Long freezeIdNum = null;
        try {
        	 commentIdNum = IDUtil.decodeId(commentId);
        	 freezeIdNum = IDUtil.decodeId(freezeId);
		} catch (Exception e) {
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
	    if (null == commentIdNum || null == freezeIdNum || commentIdNum <= 0 || freezeIdNum <= 0) {
	    	return ResponseJSON.getErrorParamsJSON().toString();
	    }
        CodeMsgData responseData = extendServiceAdapter.deleteBpInteraction(commentIdNum);
        if (ResponseConstants.OK != responseData.getCode()) {
        	logger.error("delete comment failure");
        	return ResponseJSON.getErrorInternalJSON().toString();
        }
        long bpId = (long) session.getAttribute(SessionConstants.USER_ID);
        if (!this.checkUserAuth(bpId)) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        
        StringBuilder rejectReasonSb = new StringBuilder("");        
        rejectReasonSb = rejectReasonSb.append(bpId).append(Constants.ID_SPLIT_CHAR).append("");
        resJSON = this.getAuditResponse("", "", freezeIdNum + "", rejectReasonSb.toString());
        
        if (ResponseJSON.CODE_SUC == resJSON.getInt("code")) {
        	logger.info("[ADMIN]" + bpId + " deleteCommentAndFreezeUser, the comment is " + commentIdNum + ", the user is " + freezeIdNum);
        }
        
        return resJSON.toString();
    }
    
    public boolean checkUserAuth(Long bpId) {
        if (null == bpId || bpId.longValue() <= 0) {
            return false;
        }
        TEliteUser eliteUser = null;
        try {
            eliteUser = eliteAdapter.getUserByBpId(bpId);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
        try {
            if (null == eliteUser || !eliteAdapter.superAdmin(bpId)) {
                return false;
            }
        }catch (Exception e) {
            logger.error("", e);
            return false;
        }
        return true;
    }

    public JSONObject getAuditResponse(String questionIds, String answerIds, String bpIds, String rejectReason) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionIds", questionIds);
        params.put("answerIds", answerIds);
        params.put("bpIds", bpIds);
        try {
            params.put("rejectReason", URLEncoder.encode(rejectReason,"UTF-8"));
        }catch (Exception e) {
            logger.error("", e);
        }

        String response = HttpUtil.auditAuthenPost(ELITE_REJECT_URI, params, "http://"+OverallDataFilter.innerDomain);
        if (StringUtils.isBlank(response) || response.contains("error")) {
            return ResponseJSON.getErrorInternalJSON();
        }
        logger.info("[ADMIN AUDIT]" + response);
        JSONObject auditResultJSON = JSONObject.fromObject(response);
        if (null == auditResultJSON || auditResultJSON.getInt("code") != ResponseJSON.CODE_SUC) {
            return ResponseJSON.getErrorInternalJSON();
        }

        return ResponseJSON.getSucJSON();
    }

    public JSONObject fillAuditParams(long contextId, int type, String reason, StringBuilder questionIdSb,
                                StringBuilder answerIdSb, StringBuilder bpIdSb, StringBuilder rejectReasonSb,
                                int auditType, long bpId) {
        rejectReasonSb.append(bpId).append(Constants.ID_SPLIT_CHAR).append(reason);
        FeedType feedType = FeedType.getFeedTypeByValue(type);
        try {
            switch (feedType) {
                case QUESTION:
                    TEliteQuestion question = eliteAdapter.getQuestionById(contextId);
                    if (null == question || question.getId() <= 0) {
                        break;
                    }
                    if (auditType != AUDIT_USER) {
                        questionIdSb.append(contextId);
                        List<TEliteAnswer> answerList = eliteAdapter.getAnswersByQuestionId(contextId);
                        if (null != answerList) {
                            answerList.forEach(answer -> answerIdSb.append(answer.getBpId()).append(Constants.ID_SPLIT_CHAR));
                        }
                    }
                    if (auditType != AUDIT_CONTENT){
                        bpIdSb.append(question.getBpId());
                    }
                    break;
                case ANSWER:
                    TEliteAnswer answer = eliteAdapter.getAnswerById(contextId);
                    if (null == answer || answer.getId() <= 0) {
                        break;
                    }
                    if (auditType != AUDIT_USER) {
                        answerIdSb.append(contextId);
                    }
                    if(auditType != AUDIT_CONTENT) {
                        bpIdSb.append(answer.getBpId());
                    }
                    break;
            }
        }catch (Exception e) {
            logger.error("", e);
            return ResponseJSON.getErrorInternalJSON();
        }
        return ResponseJSON.getSucJSON();
    }
}
