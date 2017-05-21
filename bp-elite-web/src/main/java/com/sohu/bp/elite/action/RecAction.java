package com.sohu.bp.elite.action;

import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.rec.EliteRecListDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TEliteSquareItem;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.CookieUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import cn.focus.rec.api.adapter.RecApiServiceAdapter;
import cn.focus.rec.api.adapter.RecApiServiceAdapterFactory;
import cn.focus.rec.model.RecItem;
import net.sf.json.JSONObject;

/**
 * 推荐
 * @author zhijungou
 * 2017年4月25日
 */
@Controller
public class RecAction {
    private static final Logger log = LoggerFactory.getLogger(RecAction.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final RecApiServiceAdapter recAdapter = RecApiServiceAdapterFactory.getRecApiServiceAdapter();
    
    @Resource 
    private SquareService squareService;
    
    
    @RequestMapping(value = {"rec/square"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecSquare(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecSquareMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"wx/rec/square"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecSquare(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecSquareMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"app/rec/square"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecSquare(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecSquareMethod(bean, request, session);
    }
    
    private String getRecSquareMethod(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        String fuv = "";
        if (null == bpId || bpId <= 0) {
            fuv = CookieUtil.getCookieValue(request, Constants.FUV);
            if (StringUtils.isBlank(fuv)) return ResponseJSON.getErrorParamsJSON().toString();
        } else {
            fuv = String.valueOf(bpId);
        }
        
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        int pageSize = 14;
        int start = (bean.getCurrPageNo() - 1) * pageSize;
        int count = pageSize;
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        Long feedId = bean.getFeedId();
        Integer feedType = bean.getFeedType();
        try {
            List<RecItem> recItemList= recAdapter.barSquareRec(fuv, start, count + 1);
            if (null != recItemList && recItemList.size() == (count + 1)) {
                bean.setNextPageNo(bean.getCurrPageNo() + 1);
                recItemList.remove(count);
            }
            List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertRecItemList(recItemList, bpId, source);
            if (null != feedItemList && feedItemList.size() > 0) {
                try {
                    List<TEliteSquareItem> items = eliteAdapter.getSelectedSquareBackward(feedId, feedType, 6);
                    List<SimpleFeedItemBean> selectedSquareList = ConvertUtil.convertSquareItemList(items, bpId, source);
                    if (null != selectedSquareList && selectedSquareList.size() > 0) {
                        Random rand = new Random();
                        for (int i = 0; i < selectedSquareList.size(); i++) {
                            int index = rand.nextInt(pageSize + i + 1);
                            feedItemList.add(index, selectedSquareList.get(i));
                        }
                        SimpleFeedItemBean lastItem = selectedSquareList.get(selectedSquareList.size() - 1);
                        bean.setFeedId(IDUtil.decodeId(lastItem.getItemId())).setFeedType(lastItem.getFeedType());
                    }
                    if (bean.getCurrPageNo() == 1) {
                        if (null != selectedSquareList && selectedSquareList.size() > 0) {
                            bean.setLatestTime(selectedSquareList.iterator().next().getUpdateTime());
                        } else {
                            bean.setLatestTime(new Date().getTime());
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            
            bean.setFeedItemList(feedItemList);
        } catch (Exception e) {
            log.error("", e);
        }
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    @RequestMapping(value = {"rec/backward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecBackward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecBackwardMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"wx/rec/backward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecBackward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecBackwardMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"app/rec/backward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecBackward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecBackwardMethod(bean, request, session);
    }
    
    private String getRecBackwardMethod(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        String fuv = "";
        if (null == bpId || bpId <= 0) {
            fuv = CookieUtil.getCookieValue(request, Constants.FUV);
            if (StringUtils.isBlank(fuv)) return ResponseJSON.getErrorParamsJSON().toString();
        } else {
            fuv = String.valueOf(bpId);
        }
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        int count = Constants.DEFAULT_REC_BACKWARD_NUM;
        Long oldestTime = bean.getOldestTime();
        if (null == oldestTime || oldestTime <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        try {
            List<RecItem> recItemList= recAdapter.doBarSquareRec(fuv, count + 1);
            if (null != recItemList && recItemList.size() == (count + 1)) {
                bean.setHasMore(true);
            }
            List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertRecItemList(recItemList, bpId, source);
            
            if (null != feedItemList && feedItemList.size() > 0) {
                if (isDateValid(oldestTime)) {
                    List<SimpleFeedItemBean> squareBackwardList = squareService.getSelectedBackward(oldestTime, Constants.DEFAULT_REC_SELECTED_BACKWARD_NUM, bpId, source);
                    for (SimpleFeedItemBean item : squareBackwardList) {
                        if (isDateValid(item.getUpdateTime())) {
                            feedItemList.add(item);
                        }
                    }
                    if (squareBackwardList.size() > 0) bean.setOldestTime(squareBackwardList.get(squareBackwardList.size() - 1).getUpdateTime());
                    Collections.shuffle(feedItemList);
                }
            }
            
            bean.setFeedItemList(feedItemList);
        } catch (Exception e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    public static void main(String[] args) {
        try {
            List<RecItem> list = recAdapter.barSquareRec("7298258", 154, 14);
            System.out.println("result :" + list.size());
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
    @RequestMapping(value = {"rec/forward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecForward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecForwardMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"wx/rec/forward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecForward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecForwardMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"app/rec/forward"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecForward(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecForwardMethod(bean, request, session);
    }
    
    public String getRecForwardMethod(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        String fuv = "";
        if (null == bpId || bpId <= 0) {
            fuv = CookieUtil.getCookieValue(request, Constants.FUV);
        } else {
            fuv = bpId.toString();
        }
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        Long latestTime = bean.getLatestTime();
        Long oldestTime = bean.getOldestTime();
        if (null == latestTime || latestTime <= 0 || null == oldestTime || oldestTime <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        try {
            List<RecItem> recItems = recAdapter.doBarSquareRec(fuv, Constants.DEFAULT_REC_FORWARD_NUM);
            List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertRecItemList(recItems, bpId, source);
            List<SimpleFeedItemBean> squareForwardList = squareService.getSelectedForward(latestTime, Constants.DEFAULT_REC_FORWARD_SELECTED_FORWARD_NUM, bpId, source);
            if (null !=  squareForwardList && squareForwardList.size() > 0) {
                feedItemList.addAll(squareForwardList);
                bean.setLatestTime(squareForwardList.get(0).getUpdateTime());
            }
            if (isDateValid(oldestTime)) {
                List<SimpleFeedItemBean> squareBackwardList = squareService.getSelectedBackward(oldestTime, Constants.DEFAULT_REC_FORWARD_SELECTED_BACKWARD_NUM, bpId, source);
                for (SimpleFeedItemBean item : squareBackwardList) {
                    if (isDateValid(item.getUpdateTime())) {
                        feedItemList.add(item);
                    }
                }
                if (squareBackwardList.size() > 0) bean.setOldestTime(squareBackwardList.get(squareBackwardList.size() - 1).getUpdateTime());
            }
            Collections.shuffle(feedItemList);
            bean.setFeedItemList(feedItemList);
        } catch (Exception e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        JSONObject resJSON = ResponseJSON.getSucJSON();
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    @RequestMapping(value = {"rec/top"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecTop(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecTopMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"wx/rec/top"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecTop(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecTopMethod(bean, request, session);
    }
    
    @RequestMapping(value = {"app/rec/top"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecTop(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        return getRecTopMethod(bean, request, session);
    }
    
    private String getRecTopMethod(EliteRecListDisplayBean bean, HttpServletRequest request, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
        int count = bean.getPageSize();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        try {
            List<RecItem> recItemList = recAdapter.barAllTop(start, count);
            List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertRecItemList(recItemList, bpId, source);
            bean.setFeedItemList(feedItemList);
        } catch (Exception e) {
            log.error("", e);
        }
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    /**
     * 用于获取selectedSquareBackward，为了以防与推荐流发生冲突，设置backward时间不超过当时凌晨
     */
    private boolean isDateValid(Long time) {
        if (null == time || time <= 0) return false;
        Calendar calendar = Calendar.getInstance();
        int currDate = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(time);
        int date = calendar.get(Calendar.DATE);
        return Objects.equals(date, currDate);
    }
     
}
