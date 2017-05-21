package com.sohu.bp.elite.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.action.bean.IndexDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAdTypeBySite;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.AdDisplayService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.FirstFollowService;
import com.sohu.bp.elite.service.web.UserRestrictService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-12 15:11:05
 * TODO
 */
@Controller
//@RequestMapping("/")
public class IndexAction
{
	private static final Logger log = LoggerFactory.getLogger(IndexAction.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();


	@Resource
	private FeedService feedService;
	@Resource
	private WrapperPageService wrapperPageService;
	@Resource
	private BpUserService bpUserService;
	@Resource
	private EliteCacheService eliteCacheService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private FirstFollowService firstFollowService;
	@Resource
	private UserRestrictService userRestrictService;
	@Resource
	private AdDisplayService adDisplayService;

	@RequestMapping(value={"ask/index.html", "/"})
	public String index(@ModelAttribute("bean")IndexDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		if(bean.getLatestTime() <= 0){
			bean.setLatestTime(System.currentTimeMillis());
		}
		if(bean.getOldestTime() <= 0){
			bean.setOldestTime(System.currentTimeMillis());
		}

		//填充精选问答

		//填充feed流
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);

		if(null != bpId && bpId.longValue() > 0)
		{
			//用户进入主页后默认让其关注一批用户,将该部分操作移到了snafilter来进行。
//			firstFollowService.firstLoginFollow(bpId, request);


			log.info("get feed list for user <bpId={}>", bpId);
//			feedService.fillFeeds(bpId, bean);
//			long[] times = getLatestAndOldTime(bean.getFeedItemList(), bean.getLatestTime(), bean.getOldestTime());
//			bean.setLatestTime(times[0]);
//			bean.setOldestTime(times[1]);

			//查看用户是否问题达到每日限制
			bean.setIsQuestionRestrict(userRestrictService.isQuestionRestrict(bpId));

//			if(bean.getFeedItemList().size() > 0){
//				log.info("feed list size=" + bean.getFeedItemList().size());
//
//			}else {
//				log.info("no feed found");
//			}
			bean.setUser(bpUserService.getUserDetailByBpId(bpId, null));
			bean.setHasLogin(true);
			bean.setHasPhoneNo(bpUserService.checkHasPhoneNo(bpId));
		}else {
			bean.setTotalAnswerNum(eliteCacheService.getTotalAnswerNum());
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
//			if(null != bpId && bpId.longValue() > 0)
//				return "mobile/home";
//			else
//				return Constants.PAGE_LOGIN + "?jumpUrl=/ask/index.html";
			return "mobile/home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			Map<Integer, String> adMap = new HashMap<Integer, String>();
			adMap.put(EliteAdTypeBySite.PC_BOTTOM_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_BOTTOM_1));
			adMap.put(EliteAdTypeBySite.PC_RIGHTSIDE_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_RIGHTSIDE_1));
			adMap.put(EliteAdTypeBySite.PC_HORIZONTAL_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_HORIZONTAL_1));
			bean.setAdMap(adMap);

			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, true));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home";
		}else {
			return Constants.PAGE_404;
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = "wx")
	public String getIndex(IndexDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(bean.getLatestTime() <= 0){
			bean.setLatestTime(System.currentTimeMillis());
		}
		if(bean.getOldestTime() <= 0){
			bean.setOldestTime(System.currentTimeMillis());
		}
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null != bpId && bpId.longValue() > 0){
			//查看用户是否问题达到每日限制
			bean.setIsQuestionRestrict(userRestrictService.isQuestionRestrict(bpId));
			bean.setUser(bpUserService.getUserDetailByBpId(bpId, null));
			bean.setHasLogin(true);
			bean.setHasPhoneNo(bpUserService.checkHasPhoneNo(bpId));
		}else {
			bean.setTotalAnswerNum(eliteCacheService.getTotalAnswerNum());
		}
		resJSON.put("data", JSONObject.fromObject(bean));
		return resJSON.toString();
	}

	//对一条feed流不感兴趣
	@ResponseBody
	@RequestMapping(value = {"ask/ignoreFeed"}, produces = "application/json;charset=utf-8")
	public String ignoreFeed(String id, int feedType,HttpSession session) {
		return ignoreFeedMethod(id, feedType, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/ignoreFeed"}, produces = "application/json;charset=utf-8")
	public String wxIgnoreFeed(String id, int feedType,HttpSession session) {
		return ignoreFeedMethod(id, feedType, session);
	}
	
    @ResponseBody
    @RequestMapping(value = {"app/ask/ignoreFeed"}, produces = "application/json;charset=utf-8")
    public String appIgnoreFeed(String id, int feedType,HttpSession session) {
        return ignoreFeedMethod(id, feedType, session);
    }
	
	public String ignoreFeedMethod(String id, int feedType, HttpSession session){
		if(StringUtils.isBlank(id)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedId = -1;
		try{
			decodedId = IDUtil.decodeId(id);
		}catch (Exception e){
			log.error("", e);
		}
		if(decodedId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
		long unitId;
		if(FeedType.ANSWER.getValue() == feedType) {
			unitId = TimeLineUtil.getComplexId(decodedId, BpType.Answer.getValue());
			feedService.ignoreFeed(accountId, unitId);
		}else if(FeedType.QUESTION.getValue() == feedType){
			unitId = TimeLineUtil.getComplexId(decodedId, BpType.Question.getValue());
			feedService.ignoreFeed(accountId, unitId);

			TSearchAnswerCondition condition = new TSearchAnswerCondition();
			condition.setQuestionId(decodedId);
			condition.setFrom(0);
			condition.setCount(Integer.MAX_VALUE);
			try {
				TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
				if(listResult != null && listResult.getTotal() > 0){
					for(TEliteAnswer answer : listResult.getAnswers()){
						unitId = TimeLineUtil.getComplexId(answer.getId(), BpType.Answer.getValue());
						feedService.ignoreFeed(accountId, unitId);
					}
				}
			} catch (TException e) {
				log.error("", e);
			}
		}


		return ResponseJSON.getSucJSON().toString();
	}

	@ResponseBody
	@RequestMapping(value = {"ad/info"}, produces = "application/json;charset=utf-8")
	public String getAdFragment(Integer posId) {
		return getAdFragmentMethod(posId);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ad/info"}, produces = "application/json;charset=utf-8")
	public String wxGetAdFragment(Integer posId) {
		return getAdFragmentMethod(posId);
	}
	
	public String getAdFragmentMethod(Integer posId){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(null == posId || posId.intValue() <= 0){
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}

		try {
			String adInfo = adDisplayService.getAd(EliteAdTypeBySite.posCodeMap.get(posId));
			resJSON.put("data", adInfo);
		}catch(Exception e){
			log.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		return resJSON.toString();
	}
	
	/**
	 * 用于跳出错页
	 * @param errorUrl
	 * @return
	 */
	@RequestMapping(value = "common/error")
	public String getErrorPage(@RequestParam(value = "errorUrl", required = false) String errorUrl, HttpServletRequest request) {
	    AgentSource source = AgentUtil.getSource(request);
	    if (StringUtils.isNotBlank(errorUrl)) {
	        log.error("get common/error, error url = {}", new Object[]{errorUrl});
	    }
	    return source == AgentSource.APP ? Constants.APP_PAGE_404 : Constants.PAGE_404;
	    
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