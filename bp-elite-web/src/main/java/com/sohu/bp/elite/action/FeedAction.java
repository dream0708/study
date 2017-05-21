package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
@Controller
//@RequestMapping(value = "ask/feed")
public class FeedAction {

    private Logger log = LoggerFactory.getLogger(FeedAction.class);

    @Resource
    private FeedService feedService;

    @ResponseBody
    @RequestMapping(value={"ask/feed/forward"}, produces = "application/json;charset=utf-8")
    public String getForwardFeeds(long latestTime, long oldestTime, HttpSession session, HttpServletRequest request){
    	return getForwardFeedsMethod(latestTime, oldestTime, session, request);
    }
    
    @ResponseBody
    @RequestMapping(value={"wx/ask/feed/forward"}, produces = "application/json;charset=utf-8")
    public String wxGetForwardFeeds(long latestTime, long oldestTime, HttpSession session, HttpServletRequest request){
    	return getForwardFeedsMethod(latestTime, oldestTime, session, request);
    }
    
    @ResponseBody
    @RequestMapping(value={"app/ask/feed/forward"}, produces = "application/json;charset=utf-8")
    public String appGetForwardFeeds(long latestTime, long oldestTime, HttpSession session, HttpServletRequest request){
        return getForwardFeedsMethod(latestTime, oldestTime, session, request);
    }
    
    public String getForwardFeedsMethod(long latestTime, Long oldestTime, HttpSession session, HttpServletRequest request){
        if(latestTime <= 0){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        log.info("get forward feed list for user <bpId={}, latestTime={}>", bpId, latestTime);
        //调用remove将feed流计数设为0
        feedService.removeNews(bpId);
        
        List<SimpleFeedItemBean> feedList = feedService.getForwardFeeds(latestTime, bpId, source);
        if(feedList != null){
            log.info("feed list size=" + feedList.size());
        }else {
            feedList = new ArrayList<>();
            log.info("no feed found");
        }
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        JSONObject dataJsonObj = new JSONObject();
        long[] times = getLatestAndOldTime(feedList, latestTime, oldestTime);
        dataJsonObj.put("latestTime", times[0]);
        dataJsonObj.put("oldestTime", times[1]);
        dataJsonObj.put("feedItemList", feedList);
        jsonObject.put("data", dataJsonObj);
        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value={"ask/feed/backward"}, produces = "application/json;charset=utf-8")
    public String getBackwardFeeds(long oldestTime, long latestTime, Boolean remove, HttpSession session, HttpServletRequest request){
    	return getBackwardFeedsMethod(oldestTime, latestTime, session, remove, request);
    }
    
    @ResponseBody
    @RequestMapping(value={"wx/ask/feed/backward"}, produces = "application/json;charset=utf-8")
    public String wxGetBackwardFeeds(long oldestTime, long latestTime, Boolean remove, HttpSession session, HttpServletRequest request){
    	return getBackwardFeedsMethod(oldestTime, latestTime, session, remove, request);
    }
    
    @ResponseBody
    @RequestMapping(value={"app/ask/feed/backward"}, produces = "application/json;charset=utf-8")
    public String appGetBackwardFeeds(long oldestTime, long latestTime, Boolean remove, HttpSession session, HttpServletRequest request){
        return getBackwardFeedsMethod(oldestTime, latestTime, session, remove, request);
    }
    
    public String getBackwardFeedsMethod(long oldestTime, long latestTime, HttpSession session, Boolean remove, HttpServletRequest request){
        if(oldestTime <= 0){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        log.info("get backward feed list for user <bpId={}, oldestTime={}>", bpId, oldestTime);
        
        if (null != remove && remove == true) feedService.removeNews(bpId);
        
        List<SimpleFeedItemBean> feedList = feedService.getBackwardFeeds(oldestTime, bpId, source);
        if(feedList != null){
            log.info("feed list size=" + feedList.size());
        }else {
            feedList = new ArrayList<>();
            log.info("no feed found");
        }
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        JSONObject dataJsonObj = new JSONObject();
        long[] times = getLatestAndOldTime(feedList, latestTime, oldestTime);
        dataJsonObj.put("latestTime", times[0]);
        dataJsonObj.put("oldestTime", times[1]);
        dataJsonObj.put("feedItemList", feedList);
        jsonObject.put("data", dataJsonObj);
        return jsonObject.toString();
    }
    
    @ResponseBody
    @RequestMapping(value = {"ask/feed/getNews"}, produces = "application/json;charset=utf-8")
    public String getNews(HttpSession session){
        return getNewsMethod(session);
    }
    @ResponseBody
    @RequestMapping(value = {"wx/ask/feed/getNews"}, produces = "application/json;charset=utf-8")
    public String wxGetNews(HttpSession session){
        return getNewsMethod(session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/feed/getNews"}, produces = "application/json;charset=utf-8")
    public String appGetNews(HttpSession session){
        return getNewsMethod(session);
    }
    
    private String getNewsMethod(HttpSession session){
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if(null == bpId || bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("num", feedService.getNews(bpId));
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    @ResponseBody
    @RequestMapping(value = {"ask/feed/removeNews"}, produces = "application/json;charset=utf-8")
    public String removeNews(HttpSession session){
        return removeNewsMethod(session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/feed/removeNews"}, produces = "application/json;charset=utf-8")
    public String wxRemoveNews(HttpSession session){
        return removeNewsMethod(session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/feed/removeNews"}, produces = "application/json;charset=utf-8")
    public String appRemoveNews(HttpSession session){
        return removeNewsMethod(session);
    }
    
    private String removeNewsMethod(HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if(null == bpId || bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        feedService.removeNews(bpId);
        return resJSON.toString();
    }

    private long[] getLatestAndOldTime(List<SimpleFeedItemBean> feedList, long latestTime, long oldTime){
        long[] times = new long[2];
        for(SimpleFeedItemBean feedItemBean : feedList){
            if(feedItemBean.getProduceTime() > latestTime)
                latestTime = feedItemBean.getProduceTime();
            if(feedItemBean.getProduceTime() < oldTime)
                oldTime = feedItemBean.getProduceTime();
        }
        times[0] = latestTime;
        times[1] = oldTime;
        return times;
    }
    
}
