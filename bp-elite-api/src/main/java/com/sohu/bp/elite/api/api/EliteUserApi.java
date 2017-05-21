package com.sohu.bp.elite.api.api;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.api.bean.IndexUserBean;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.service.UserInfoService;
import com.sohu.bp.elite.api.util.PropertyFilterUtil;
import com.sohu.bp.elite.api.util.TagIdsUtil;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
@Controller
@RequestMapping("/innerapi/user")
public class EliteUserApi {

    private Logger log = LoggerFactory.getLogger(EliteQuestionApi.class);

    private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();

    @Resource
    UserInfoService userInfoService;

    @ResponseBody
    @RequestMapping(value = "index-data", produces = "application/json;charset=utf-8")
    public String getById(long bpId){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        try {
//            CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
//            if(codeMsgData.getCode() == ResponseConstants.OK){
//                log.info("get userInfo for api, <bpId={}>", bpId);
//                String infoJSONStr = codeMsgData.getData();
//                if(StringUtils.isNotBlank(infoJSONStr)) {
//                    JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
//                    if (null != infoJSON) {
//                        TEliteUser user = new TEliteUser();
//                        user.setBpId(bpId);
//                        user.setNick(infoJSON.getString("nick"));
//                        JSONObject responseJson = ResponseJSON.getSucJSON();
//                        responseJson.put("data", JSON.toJSONString(user, PropertyFilterUtil.getFilter(TEliteUser.class)));
//                        return responseJson.toString();
//                    }
//                }
//            }
        	
        	
        	if(bpId <= 0)
        	{
        		resJSON = ResponseJSON.getErrorParamsJSON();
        		return resJSON.toString();
        	}
        	
        	TEliteUser eliteUser = eliteThriftServiceAdapter.getUserByBpId(bpId);
        	if(null == eliteUser || eliteUser.getBpId() <= 0)
        	{
        		resJSON = ResponseJSON.getErrorParamsJSON();
        		return resJSON.toString();
        	}
        	
        	
        	IndexUserBean indexUserBean = new IndexUserBean();
        	indexUserBean.setBpId(bpId);
        	indexUserBean.setBirthday(eliteUser.getBirthday());
        	indexUserBean.setGender(eliteUser.getGender());
        	indexUserBean.setDescription(eliteUser.getDescription());
        	indexUserBean.setGold((int)eliteUser.getGold());
        	indexUserBean.setGrade(eliteUser.getGender());
        	indexUserBean.setIdentity(eliteUser.getIdentity());
        	indexUserBean.setStatus(eliteUser.getStatus());
        	indexUserBean.setTagIds(eliteUser.getTagIds());
        	indexUserBean.setLastLoginTime(eliteUser.getLastLoginTime());
        	indexUserBean.setFirstLoginTime(eliteUser.getFirstLoginTime());
        	indexUserBean.setFirstLogin(eliteUser.getFirstLogin());
        	indexUserBean.setAreaCode(eliteUser.getAreaCode());

			List<Integer> validQuestionStatus = new ArrayList<>();
			validQuestionStatus.add(EliteQuestionStatus.AUDITING.getValue());
			validQuestionStatus.add(EliteQuestionStatus.PASSED.getValue());
			validQuestionStatus.add(EliteQuestionStatus.PUBLISHED.getValue());
			indexUserBean.setQuestionNum(eliteThriftServiceAdapter.getUserQuestionNumByStatus(bpId, validQuestionStatus));

			List<Integer> validAnswerStatus = new ArrayList<>();
			validAnswerStatus.add(EliteAnswerStatus.AUDITING.getValue());
			validAnswerStatus.add(EliteAnswerStatus.PASSED.getValue());
			validAnswerStatus.add(EliteAnswerStatus.PUBLISHED.getValue());
			indexUserBean.setAnswerNum(eliteThriftServiceAdapter.getUserAnswerNumByStatus(bpId, validAnswerStatus));

        	UserInfo userInfo = userInfoService.getDecorateUserInfoByBpid(bpId);
        	if(null != userInfo)
        	{
        		indexUserBean.setNick(userInfo.getNick());
        	}
        	
        	JSONObject dataJSON = JSONObject.fromObject(indexUserBean);
        	resJSON.put("data", dataJSON);
        	log.info("index user : " + dataJSON.toString());
        } catch (Exception e) {
            log.error("", e);
            resJSON = ResponseJSON.getErrorInternalJSON();
        }
        return resJSON.toString();
    }

    @ResponseBody
    @RequestMapping(value = "answers/index-data", produces = "application/json;charset=utf-8")
    public String getAnswersByBpId(Long bpId){
        JSONObject json = ResponseJSON.getDefaultResJSON();
        try {
            List<TEliteAnswer> tEliteAnswers = eliteThriftServiceAdapter.getAnswersByBpId(bpId);
            TagIdsUtil.addTagIds(tEliteAnswers);
            if(tEliteAnswers != null && tEliteAnswers.size() > 0){
                json.put("data", JSON.toJSONString(tEliteAnswers, PropertyFilterUtil.getFilter(TEliteAnswer.class)));
            }else {
                json = ResponseJSON.getErrorParamsJSON();
            }
        } catch (TException e) {
            log.error("", e);
            json = ResponseJSON.getErrorInternalJSON();
        }

        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "questions/index-data", produces = "application/json;charset=utf-8")
    public String getQuestionsByBpId(Long bpId){
        JSONObject json = ResponseJSON.getDefaultResJSON();
        try {
            List<TEliteQuestion> tEliteQuestions = eliteThriftServiceAdapter.getQuestionsByBpId(bpId);
            if(tEliteQuestions != null && tEliteQuestions.size() > 0){
                json.put("data", JSON.toJSONString(tEliteQuestions, PropertyFilterUtil.getFilter(TEliteQuestion.class)));
            }else {
                json = ResponseJSON.getErrorParamsJSON();
            }
        }catch (TException e){
            log.error("", e);
            json = ResponseJSON.getErrorInternalJSON();
        }
        return json.toString();
    }
    
    //以下是将原始ssdb中的用户信息插入到数据库的操作
    @RequestMapping(value = "insert")
    @ResponseBody
    public String insertUserInfo(@RequestParam(value = "ids", required = true) String ids){
    	if (null == ids) return ResponseJSON.getErrorInternalJSON().toString();
    	String[] idArray = ids.split(Constants.DEFAULT_SPLIT_CHAR);
    	try{
	    	for(String id : idArray){
	        	Long bpId = Long.valueOf(id);
	    		TEliteUser userElite = new TEliteUser();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		userElite.setFirstLoginTime(sdf.parse("2016-10-01").getTime());
	    		userElite.setLastLoginTime(new Date().getTime());
	    		userElite.setBpId(bpId);
	    		userElite.setStatus(1);
	    		userElite.setFirstLogin(0);
	    		Long userId = eliteThriftServiceAdapter.insertUser(userElite);
	    		if(null != userId && userId >0){
	    			log.info("insert user bpId = {} suceed!", new Object[]{userId});
	    		}
	    		else{
	    			return ResponseJSON.getErrorInternalJSON().toString();
	    		}
	    	}
    	} catch (Exception e){
    		log.error("", e);
    	}
    	return ResponseJSON.getSucJSON().toString();
    }

}
