package com.sohu.bp.elite.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.EliteResponseJSON;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 用于问答广场
 * @author zhijungou
 * 2016年10月27日
 */
@Controller
//@RequestMapping("ask/square")
public class SquareAction {

	private static final Logger log = LoggerFactory.getLogger(SquareAction.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	@Resource
	private SquareService squareService;
	
	@RequestMapping(value = {"ask/square/num"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getNum(@RequestParam(value = "lastestTime", required = true) Long latestTime){
		if(null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
		JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
		resJSON.put("data", getNumMethod(latestTime));
		return resJSON.toString();	
	}
	
	@RequestMapping(value = {"wx/ask/square/num"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxGetNum(@RequestParam(value = "lastestTime", required = true) Long latestTime){
		if(null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
		JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
 		resJSON.put("data", getNumMethod(latestTime));
		return resJSON.toString();
	}
	
	@RequestMapping(value = {"app/ask/square/num"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetNum(@RequestParam(value = "lastestTime", required = true) Long latestTime){
        if(null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
        JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
        resJSON.put("data", getNumMethod(latestTime));
        return resJSON.toString();
    }
	
	public JSONObject getNumMethod(Long latestTime){
		Long num = squareService.getNewEliteNum(latestTime);
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("num", num);
		return dataJSON;
	}
	
	
	@RequestMapping(value = {"ask/square/initial"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getInitial(HttpSession session, HttpServletRequest request){
		return getInitialMethod(session, request);
	}
	
	@RequestMapping(value = {"wx/ask/square/initial"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxGetInitial(HttpSession session, HttpServletRequest request){
		return getInitialMethod(session, request);
	}
	
    @RequestMapping(value = {"app/ask/square/initial"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetInitial(HttpSession session, HttpServletRequest request){
        return getInitialMethod(session, request);
    }
	
	public String getInitialMethod(HttpSession session, HttpServletRequest request){
		JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TEliteSourceType source = AgentUtil.getFeedSourceType(request);
		List<SimpleFeedItemBean> items = squareService.getInitialEliteList(bpId, source);
		long latestTime = new Date().getTime();
		if (null != items && items.size() > 0) {
		    latestTime = items.get(0).getUpdateTime();
		}
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("feedItemList", items);
		dataJSON.put("lastTime", latestTime);
		resJSON.put("data", dataJSON);
		return resJSON.toString();
	}
	
	@RequestMapping(value = {"ask/square/list"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
		return getListMethod(latestTime, session, request);
	}
	
	@RequestMapping(value = {"wx/ask/square/list"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxGetList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
		return getListMethod(latestTime, session, request);
	}
	
	@RequestMapping(value = {"app/ask/square/list"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
        return getListMethod(latestTime, session, request);
    }
	
    public String getListMethod(Long latestTime, HttpSession session, HttpServletRequest request){
        if (null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        List<SimpleFeedItemBean> items = squareService.getNewEliteList(latestTime, bpId, source);
        JSONObject dataJSON = new JSONObject();
        latestTime = new Date().getTime();
        if (null != items && items.size() > 0) {
            latestTime = items.get(0).getUpdateTime();
        }
        dataJSON.put("feedItemList", items);
        dataJSON.put("lastTime", latestTime);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
	
	@RequestMapping(value = {"ask/square/backward"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String squareBackward(@RequestParam(value = "oldestId", required = true) String oldestId, 
	        @RequestParam(value = "feedType", required = true) Integer feedType, HttpSession session, HttpServletRequest request) {
		return squareBackwardMethod(oldestId, feedType, session, request);
	}
	
	@RequestMapping(value = {"wx/ask/square/backward"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxSquareBackward(@RequestParam(value = "oldestId", required = true) String oldestId, 
	        @RequestParam(value = "feedType", required = true) Integer feedType, HttpSession session, HttpServletRequest request) {
		return squareBackwardMethod(oldestId, feedType, session, request);
	}
	
	@RequestMapping(value = {"app/ask/square/backward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appSquareBackward(@RequestParam(value = "oldestId", required = true) String oldestId, 
            @RequestParam(value = "feedType", required = true) Integer feedType, HttpSession session, HttpServletRequest request) {
        return squareBackwardMethod(oldestId, feedType, session, request);
    }
	
	public String squareBackwardMethod(String oldestId, Integer feedType, HttpSession session, HttpServletRequest request) {
	    Long id = IDUtil.decodeId(oldestId);
		if (null == id || id <= 0 ) return ResponseJSON.getErrorParamsJSON().toString();
		JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TEliteSourceType source = AgentUtil.getFeedSourceType(request);
		List<SimpleFeedItemBean> items = squareService.getBackward(id, feedType, bpId, source);	
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("feedItemList", items);
		resJSON.put("data", dataJSON);
		return resJSON.toString();
	}
	
	@RequestMapping(value = {"ask/square/selected/num"}, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getSelectedNum(@RequestParam(value = "latestTime", required = true) Long latestTime){
	    return getSelectedNumMethod(latestTime);
    }
	
    @RequestMapping(value = {"wx/ask/square/selected/num"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetSelectedNum(@RequestParam(value = "latestTime", required = true) Long latestTime){
        return getSelectedNumMethod(latestTime); 
    }
    
    @RequestMapping(value = {"app/ask/square/selected/num"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetSelectedNum(@RequestParam(value = "latestTime", required = true) Long latestTime){
        return getSelectedNumMethod(latestTime);  
    }
    
    public String getSelectedNumMethod(Long latestTime){
        if(null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
        JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
        Long num = squareService.getNewSelectedNum(latestTime);
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("num", num);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    @RequestMapping(value = {"ask/square/selected/list"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSelectedList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
        return getSelectedListMethod(latestTime, session, request);
    }
    
    @RequestMapping(value = {"wx/ask/square/selected/list"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetSelectedList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
        return getSelectedListMethod(latestTime, session, request);
    }
    
    @RequestMapping(value = {"app/ask/square/selected/list"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetSelectedList(@RequestParam(value = "latestTime", required = true) Long latestTime, HttpSession session, HttpServletRequest request){
        return getSelectedListMethod(latestTime, session, request);
    }
    
    public String getSelectedListMethod(Long latestTime, HttpSession session, HttpServletRequest request){
        if (null == latestTime || latestTime <= 0 ) return EliteResponseJSON.getErrorParamsJSON().toString();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        List<SimpleFeedItemBean> items = squareService.getNewSelectedList(latestTime, bpId, source);
        JSONObject dataJSON = new JSONObject();
        latestTime = new Date().getTime();
        if (null != items && items.size() > 0) {
            latestTime = items.get(0).getUpdateTime();
        }
        dataJSON.put("feedItemList", items);
        dataJSON.put("latestTime", latestTime);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
	
}
