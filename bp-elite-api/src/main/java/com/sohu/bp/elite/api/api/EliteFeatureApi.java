package com.sohu.bp.elite.api.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.adapter.BpMediaArticleServiceAdapter;
import com.sohu.bp.decoration.model.PageData;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.service.EliteFeatureService;
import com.sohu.bp.elite.api.service.IdentityService;
import com.sohu.bp.elite.api.service.impl.UserInfoServiceImpl;
import com.sohu.bp.elite.api.util.IDUtil;
import com.sohu.bp.elite.api.util.SpringUtil;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author zhijungou
 * 2016年8月27日
 */
@Controller
@RequestMapping("innerapi/feature")
public class EliteFeatureApi {
	
	private static final BpMediaArticleServiceAdapter articleAdapter= BpDecorationServiceAdapterFactory.getBpMediaArticleServiceAdapter();
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private static final Logger log = LoggerFactory.getLogger(EliteFeatureApi.class);
	@Resource
	private EliteFeatureService eliteFeatureService;
	@Resource
	private IdentityService identityService;
	@Resource
	private UserInfoServiceImpl userInfoService;
	
	@ResponseBody
	@RequestMapping(value = "updateCache", produces = "application/json;charset=utf-8")
	public String updateCache(@RequestParam(value = "key", required = true)String key)
	{
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(null == key && key != Constants.ELITE_FRAGMENT_KEY && key != Constants.ELITE_SUBJECT_KEY 
				&& key != Constants.ELITE_TAGSQUARE_KEY && key != Constants.ELITE_TOPIC_KEY && key != Constants.ELITE_COLUMN_KEY)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		if(!eliteFeatureService.updateCache(key)) resJSON = ResponseJSON.getErrorInternalJSON();
		return resJSON.toString();
	}
	
	@RequestMapping(value = "getAllUser", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getAllUser(){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		String allUser = eliteFeatureService.getAllUser();
		resJSON.put("data", allUser);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "getInvited", produces = "appliction/json;charset=utf-8")
	@ResponseBody
	public String getInvited(){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		List<Long> ids = eliteFeatureService.getInvitedList();
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("ids", ids);
		resJSON.put("data", dataJSON);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "addInvited", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String addInvited(@RequestParam(value = "id", required = true) Long id){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Boolean data = eliteFeatureService.addInvited(id);
		resJSON.put("data", data);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "removeInvited", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String removeInvited(@RequestParam(value = "id", required = true) Long id){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Boolean data = eliteFeatureService.removeInvited(id);
		resJSON.put("data", data);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "getEdit", produces = "appliction/json;charset=utf-8")
	@ResponseBody
	public String getEdit(){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		List<Long> ids = eliteFeatureService.getEditList();
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("ids", ids);
		resJSON.put("data", dataJSON);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "addEdit", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String addEdit(@RequestParam(value = "id", required = true) Long id){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Boolean data = eliteFeatureService.addEditHistory(id);
		resJSON.put("data", data);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "addIdentity", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String addIdentity(@RequestParam(value = "id", required = true)Long id){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		identityService.insert(id);
		sendRecomExperts();
		return resJSON.toString();
	}
	
	@RequestMapping(value = "removeIdentity", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String removeIdentity(@RequestParam(value = "id", required = true)Long id){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		identityService.remove(id);
		sendRecomExperts();
		return resJSON.toString();
	}
	
	@RequestMapping(value = "getExperts", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getExperts(@RequestParam(value = "start", required = true) Integer start, @RequestParam(value = "count", required = true) Integer count){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		List<Long> ids = identityService.getExpertsList(start, count);
		resJSON.put("data", ids);
		return resJSON.toString();
	}
	
	@RequestMapping(value = "getExpertsNum", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getExpertsNum(){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		resJSON.put("data", identityService.getNum());
		return resJSON.toString();
	}
	
	//保存焦点图顺序
	@RequestMapping(value = "saveFocusOrder", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String saveFocusOrder(@RequestParam(value = "objectId") Long objectId, @RequestParam(value = "objectType") Integer type,
	        @RequestParam(value = "order") Integer order) {
	    JSONObject resJSON = ResponseJSON.getDefaultResJSON();
	    if (null == objectId || null == type || null == order || objectId <= 0 || type <= 0) return ResponseJSON.getErrorParamsJSON().toString();
	    if (!eliteFeatureService.saveFocusOrder(objectId, type, order)) return ResponseJSON.getErrorInternalJSON().toString();
	    return resJSON.toString();
	}
	
	@RequestMapping(value = "getFocusOrders", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getFocusOrders(@RequestParam(value = "objectIds") String idString, @RequestParam(value = "bpType") Integer bpType) {
	    JSONObject resJSON = ResponseJSON.getDefaultResJSON();
	    net.sf.json.JSONArray objectIds = net.sf.json.JSONArray.fromObject(idString);
	    if (null == objectIds || objectIds.size() <= 0) return ResponseJSON.getErrorParamsJSON().toString();
	    JSONObject dataJSON = new JSONObject();
	    List<Long> ids = new ArrayList<Long>();
	    for (int i = 0; i < objectIds.size(); i++) {
	        Long id = objectIds.getLong(i);
	        ids.add(id);
	    }
	    List<Integer> orders = eliteFeatureService.getOrdersByIds(ids, bpType);
	    dataJSON.put("list", orders);
	    resJSON.put("data", dataJSON);
	    return resJSON.toString();
	}
	
	
	//每次更新新的专家时,推到主站碎片
	public void sendRecomExperts(){
		try{
			PageData pageData = articleAdapter.getEditAreaPageData(Constants.RECOM_EXPERT_POSID, Constants.RECOM_EXPERT_AREACODE);
			if(pageData == null){
				log.error("push recommand expert fragment failed!");
			}else{
				JSONArray experts = new JSONArray();
				List<Long> ids = identityService.getExpertsList(0, Constants.RECOM_EXPERT_COUNT);
				if(ids.size() < Constants.RECOM_EXPERT_COUNT){
					log.info("recom experts size is little than " + Constants.RECOM_EXPERT_COUNT);
					return;
				}
				for(Long bpId : ids){
					JSONObject expert = new JSONObject();
					UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
					expert.put("name", userInfo.getNick());
					expert.put("avatar", userInfo.getAvatar());
					TEliteUser user = eliteAdapter.getUserByBpId(bpId);
					expert.put("brief", user.getDescription());
					Configuration configuration = (Configuration) SpringUtil.getBean("configuration");
					String host = configuration.get("frontDomain");
					String askurl = host + Constants.RECOM_EXPERT_URL + IDUtil.encodeId(bpId);
					expert.put("askurl", askurl);
					experts.add(expert);
				}
				pageData.setContent(experts.toJSONString());
				log.info("send recom experts to index, content = " + experts);
				if(articleAdapter.updatePageData(pageData)){
					log.info("send recom experts succeed!");
				}else{
					log.info("send recom experts failed!");
				}
			}
		} catch (Exception e){
			log.error("", e);
		}
	}
}
