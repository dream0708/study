package com.sohu.bp.elite.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.FeedListDisplayBean;
import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureAppWrapperBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureItemBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureListDisplayBean;
import com.sohu.bp.elite.action.bean.feature.EliteFocusBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.subject.SubjectBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.bean.ContentBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.FeatureService;
import com.sohu.bp.elite.service.web.IdentityService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteResponseJSON;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.ImageUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用于专题，话题，标签导航，标签广场, 专家推荐
 * 
 * @author zhijungou 2016年8月26日
 */
@Controller
// @RequestMapping({"feature", "/"})
public class FeatureAction {
    private static Logger log = LoggerFactory.getLogger(FeatureAction.class);

    private static final EliteThriftServiceAdapter adapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpDecorationServiceAdapter tagAdapter = BpDecorationServiceAdapterFactory
            .getBpDecorationServiceAdapter();
    private static final Integer pageSize = Constants.DEFAULT_PAGE_SIZE;

    @Resource
    private FeatureService featureService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private BpUserService bpUserService;

    @Resource
    private WrapperPageService wrapperPageService;

    @Resource
    private IdentityService identityService;

    public FeatureAction() {
    }

    public FeatureAction(FeatureService featureService, UserInfoService userInfoService, BpUserService bpUserService) {
        this.featureService = featureService;
        this.userInfoService = userInfoService;
        this.bpUserService = bpUserService;
    }

    @RequestMapping(value = { "ask/feature/subject/index.json",
            "fs/index.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSubjectIndex() {
        return getSubjectIndexMethod();
    }

    @RequestMapping(value = { "wx/fs/index.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetSubjectIndex() {
        return getSubjectIndexMethod();
    }

    public String getSubjectIndexMethod() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        TEliteSubject tEliteSubject = new TEliteSubject();

        List<TEliteSubject> tEliteSubjects = featureService.getEliteSubject(0, 1);
        if (null != tEliteSubjects && tEliteSubjects.size() > 0) {
            tEliteSubject = tEliteSubjects.get(0);
        } else {
            resJSON = ResponseJSON.getErrorInternalJSON();
        }

        JSONObject data = new JSONObject();
        data.put("id", IDUtil.encodeId(tEliteSubject.getId()));
        data.put("name", HtmlUtils.htmlUnescape(tEliteSubject.getName()));
        data.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
        data.put("wapCover", ImageUtil.getWapFocusCover(tEliteSubject.getCover()));
        data.put("brief", HtmlUtils.htmlUnescape(tEliteSubject.getBrief()));
        data.put("detail", HtmlUtils.htmlUnescape(tEliteSubject.getDetail()));
        data.put("update_time", tEliteSubject.getUpdateTime());
        resJSON.put("data", data);

        return resJSON.toString();
    }

    @RequestMapping({ "ask/feature/subject/list.html", "fs" })
    public String getSubjectList(@RequestParam(value = "currPageNo", required = false) Integer currentPage,
            ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        if (null == currentPage || currentPage < 0)
            currentPage = 1;
        Integer start = (currentPage - 1) * pageSize;
        Integer count = pageSize;
        List<TEliteSubject> tEliteSubjects = featureService.getEliteSubject(start, count);
        Long totalNum = featureService.getEliteCount();
        PageBean page = new PageBean();
        page.setPageSize(pageSize);
        page.setCurrPageNo(currentPage);
        page.setTotal(totalNum);
        model.putAll(JSONObject.fromObject(page));
        if (null != tEliteSubjects && tEliteSubjects.size() > 0) {
            JSONArray subjectList = new JSONArray();
            Iterator<TEliteSubject> it = tEliteSubjects.iterator();
            while (it.hasNext()) {
                JSONObject subjectJSON = new JSONObject();
                TEliteSubject tEliteSubject = (TEliteSubject) it.next();
                subjectJSON.put("id", IDUtil.encodeId(tEliteSubject.getId()));
                subjectJSON.put("name", tEliteSubject.getName());
                subjectJSON.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
                subjectJSON.put("smallCover", ImageUtil.getSmallImage(tEliteSubject.getCover()));
                subjectJSON.put("brief", tEliteSubject.getBrief());
                Date date = new Date(tEliteSubject.getUpdateTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String dateStr = sdf.format(date);
                subjectJSON.put("date", dateStr);
                String detail = tEliteSubject.getDetail();
                Integer num = ContentUtil.getSubjectNum(detail);
                subjectJSON.put("num", num);
                subjectList.add(subjectJSON);
            }
            model.put("subject_list", subjectList);

            PageWrapperBean pageWrapper = new PageWrapperBean();
            UserInfo userInfo = null;
            Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
            if (null != bpId && bpId > 0)
                userInfo = userInfoService.getUserInfoByBpid(bpId);
            pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
            pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
            model.put("pageWrapper", pageWrapper);
        } else {
            return Constants.PAGE_404;
        }
        // TODO 要返回专题页

        return AgentUtil.getSource(request) == AgentSource.PC ? "pc/special-topic/all-special"
                : "mobile/special/special";
    }

    @RequestMapping(value = { "ask/feature/subject/list.json", "fs.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSubjectList(EliteFeatureListDisplayBean bean,
            HttpSession session) {
        return getSubjectListMethod(bean, session);
    }

    @RequestMapping(value = { "wx/fs.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetSubjectList(EliteFeatureListDisplayBean bean,
            HttpSession session) {
        return getSubjectListMethod(bean, session);

    }

    @RequestMapping(value = { "app/fs.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetSubjectList(EliteFeatureListDisplayBean bean,
            HttpSession session) {
        return getSubjectListMethod(bean, session);
    }

    public String getSubjectListMethod(EliteFeatureListDisplayBean bean, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Integer start = (bean.getCurrPageNo() - 1) * pageSize;
        Integer count = pageSize;
        JSONObject data = new JSONObject();
        List<TEliteSubject> subjects = featureService.getEliteSubject(start, count);
        Long totalNum = featureService.getEliteCount();
        bean.setTotal(totalNum);
        List<EliteFeatureItemBean> subjectList = new ArrayList<>();
        if (null != subjects && subjects.size() > 0) {
            subjectList = subjects.stream().map(ConvertUtil::convertToFeatureItemBean).collect(Collectors.toList());
        }
        data.put("subject_list", subjectList);
        bean.setFeatureList(subjectList);
        data.putAll(JSONObject.fromObject(bean));
        resJSON.put("data", data);
        return resJSON.toString();
    }

    @RequestMapping({ "ask/feature/subject/{subjectId}.html", "fs/{subjectId}" })
    public String getSubjectById(@PathVariable("subjectId") String subjectId, ModelMap model, HttpSession session,
            HttpServletRequest request, HttpServletResponse response) {
        TEliteSubject tEliteSubject = new TEliteSubject();
        Long decodedId = IDUtil.decodeId(subjectId);
        if (null == decodedId || decodedId <= 0)
            return Constants.PAGE_404;
        tEliteSubject = featureService.getEliteSubjectById(decodedId);
        if (null != tEliteSubject && tEliteSubject.getId() > 0) {
            model.put("id", subjectId);
            model.put("name", tEliteSubject.getName());
            model.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
            model.put("cover_small", ImageUtil.getSmallImage(tEliteSubject.getCover()));
            model.put("cover_medium",
                    ImageUtil.getSmallImage(tEliteSubject.getCover(), null, Constants.MEDIUM_IMAGE_RATIO));
            model.put("brief", tEliteSubject.getBrief());
            model.put("num", ContentUtil.getSubjectNum(tEliteSubject.getDetail()));
            // TODO 解析subject属性detail的json串，可以写成一个方法保存在util中
            JSONArray detail = new JSONArray();
            detail = resolveSubjectDetail(tEliteSubject.getDetail());
            model.put("detail", detail);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            model.put("update_time", sdf.format(new Date(tEliteSubject.getUpdateTime())));
            JSONArray recentSubject = resolveRecentSubject();
            model.put("recentSubject", recentSubject);

            SubjectBean subjectBean = new SubjectBean();
            subjectBean.setSubjectId(subjectId);
            subjectBean.setName(tEliteSubject.getName());
            subjectBean.setBrief(tEliteSubject.getBrief());
            subjectBean.setCover(tEliteSubject.getCover());
            subjectBean.setDetail(detail);
            subjectBean.setRecentSubject(recentSubject);

            PageWrapperBean pageWrapper = new PageWrapperBean();
            UserInfo userInfo = null;
            Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
            if (null != bpId && bpId > 0)
                userInfo = userInfoService.getUserInfoByBpid(bpId);
            pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, subjectBean, false));
            pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
            model.put("pageWrapper", pageWrapper);
        }

        return AgentUtil.getSource(request) == AgentSource.PC ? "pc/special-topic/special"
                : "mobile/special/special-detail";
    }

    @RequestMapping({ "app/fs/{subjectId}" })
    public String appGetSubjectById(@PathVariable("subjectId") String subjectId, ModelMap model, HttpSession session, HttpServletRequest request) {
        /**
         * use blank template
         */
//        if (StringUtils.isBlank(subjectId)) return Constants.PAGE_404;
//        try {
//            long decodedSubjectId = IDUtil.decodeId(subjectId);
//            TEliteSubject subject = adapter.getHistoryById(decodedSubjectId);
//            model.put("id", subjectId);
//        } catch (Exception e) {
//            log.error("", e);
//        }
//        return "app/subject/subject";
        /**
         * use beetl render
         */
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSubject tEliteSubject = new TEliteSubject();
        Long decodedId = IDUtil.decodeId(subjectId);
        if (null == decodedId || decodedId <= 0) return Constants.APP_PAGE_404;
        tEliteSubject = featureService.getEliteSubjectById(decodedId);
        if (null == tEliteSubject || tEliteSubject.getId() <= 0) return Constants.APP_PAGE_404;
        model.put("id", subjectId);
        model.put("name", HtmlUtils.htmlUnescape(tEliteSubject.getName()));
        model.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
        model.put("cover_small", ImageUtil.getSmallImage(tEliteSubject.getCover()));
        model.put("cover_medium",
                ImageUtil.getSmallImage(tEliteSubject.getCover(), null, Constants.MEDIUM_IMAGE_RATIO));
        model.put("brief", HtmlUtils.htmlUnescape(tEliteSubject.getBrief()));
        model.put("num", ContentUtil.getSubjectNum(tEliteSubject.getDetail()));
        model.put("hasFavorited", InteractionUtil.hasFavorited(bpId, decodedId, BpInteractionTargetType.ELITE_SUBJECT));
        // TODO 解析subject属性detail的json串，可以写成一个方法保存在util中
        JSONArray detail = new JSONArray();
        detail = resolveSubjectDetail(tEliteSubject.getDetail());
        model.put("detail", detail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        model.put("update_time", sdf.format(new Date(tEliteSubject.getUpdateTime())));
        JSONArray recentSubject = resolveRecentSubject();
        model.put("recentSubject", recentSubject);
        return "app/subject/subject";
    }

    // 微信小程序的api接口
    @RequestMapping(value = { "wx/fs/{subjectId}.json", "fs/{subjectId}.json" })
    @ResponseBody
    public String wxGetSubjectById(@PathVariable("subjectId") String subjectId, HttpSession session,
            HttpServletRequest request, HttpServletResponse response) {
        return getSubjectByIdMethod(subjectId, session);
    }

    @RequestMapping(value = { "app/fs/{subjectId}.json", "fs/{subjectId}.json" })
    @ResponseBody
    public String appGetSubjectById(@PathVariable("subjectId") String subjectId, HttpSession session,
            HttpServletRequest request, HttpServletResponse response) {
        return getSubjectByIdMethod(subjectId, session);
    }

    private String getSubjectByIdMethod(String subjectId, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONObject dataJSON = new JSONObject();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSubject tEliteSubject = new TEliteSubject();
        Long decodedId = IDUtil.decodeId(subjectId);
        if (null == decodedId || decodedId <= 0)
            return ResponseJSON.getErrorParamsJSON().toString();
        tEliteSubject = featureService.getEliteSubjectById(decodedId);
        if (null != tEliteSubject && tEliteSubject.getId() > 0) {
            dataJSON.put("id", subjectId);
            dataJSON.put("name", HtmlUtils.htmlUnescape(tEliteSubject.getName()));
            dataJSON.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
            dataJSON.put("cover_small", ImageUtil.getSmallImage(tEliteSubject.getCover()));
            dataJSON.put("cover_medium",
                    ImageUtil.getSmallImage(tEliteSubject.getCover(), null, Constants.MEDIUM_IMAGE_RATIO));
            dataJSON.put("brief", HtmlUtils.htmlUnescape(tEliteSubject.getBrief()));
            dataJSON.put("num", ContentUtil.getSubjectNum(tEliteSubject.getDetail()));
            dataJSON.put("hasFavorited", InteractionUtil.hasFavorited(bpId, decodedId, BpInteractionTargetType.ELITE_SUBJECT));
            // TODO 解析subject属性detail的json串，可以写成一个方法保存在util中
            JSONArray detail = new JSONArray();
            detail = resolveSubjectDetail(tEliteSubject.getDetail());
            dataJSON.put("detail", detail);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            dataJSON.put("update_time", sdf.format(new Date(tEliteSubject.getUpdateTime())));
            JSONArray recentSubject = resolveRecentSubject();
            dataJSON.put("recentSubject", recentSubject);
        }
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }

    @RequestMapping(value = { "ask/feature/topic/index.json",
            "ft/index.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getTopicIndex() {
        return getTopicIndexMethod();
    }

    @RequestMapping(value = { "wx/ft/index.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetTopicIndex() {
        return getTopicIndexMethod();
    }

    @RequestMapping(value = { "app/ft/index.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetTopicIndex() {
        return getTopicIndexMethod();
    }

    public String getTopicIndexMethod() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        TEliteTopic tEliteTopic = new TEliteTopic();

        List<TEliteTopic> tEliteTopics = featureService.getEliteTopic(0, 1);
        if (null != tEliteTopics && tEliteTopics.size() > 0) {
            tEliteTopic = tEliteTopics.get(0);
        } else {
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        JSONObject data = new JSONObject();
        data.put("id", IDUtil.encodeId(tEliteTopic.getId()));
        data.put("title", HtmlUtils.htmlUnescape(tEliteTopic.getTitle()));
        data.put("brief", HtmlUtils.htmlUnescape(tEliteTopic.getBrief()));
        data.put("cover", ImageUtil.removeImgProtocol(tEliteTopic.getCover()));
        data.put("wapCover", ImageUtil.getWapFocusCover(tEliteTopic.getCover()));
        data.put("question_id", IDUtil.encodeId(tEliteTopic.getQuestionId()));
        try {
            TEliteQuestion question = adapter.getQuestionById(tEliteTopic.getQuestionId());
            data.put("question_name", HtmlUtils.htmlUnescape(question.getTitle()));
            data.put("question_detail", question.getDetail());
        } catch (Exception e) {
            log.error("", e);
        }
        data.put("start_time", tEliteTopic.getStartTime());
        data.put("end_time", tEliteTopic.getEndTime());
        data.put("update_time", tEliteTopic.getUpdateTime());
        resJSON.put("data", data);

        return resJSON.toString();
    }

    @RequestMapping("ask/feature/topic/list.html")
    public String getTopicList(@RequestParam(value = "start", required = true) Integer start,
            @RequestParam(value = "count", required = true) Integer count, ModelMap model) {
        if (null == start || start < 0 || null == count || count <= 0)
            return Constants.PAGE_404;
        List<TEliteTopic> tEliteTopics = featureService.getEliteTopic(start, count);
        if (null != tEliteTopics && tEliteTopics.size() > 0) {
            model.put("topicList", tEliteTopics);
        } else {
            return Constants.PAGE_404;
        }
        // TODO 返回话题列表页
        return Constants.PAGE_404;
    }
    
    @RequestMapping(value = "app/ft.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getTopicListData(PageBean page) {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	int start = (page.getCurrPageNo() - 1) * page.getPageSize();
    	int count = page.getPageSize();
    	page.setTotal(featureService.getTopicCount());
    	List<EliteFeatureItemBean> list = new ArrayList<EliteFeatureItemBean>();
    	List<TEliteTopic> topics = featureService.getEliteTopic(start, count);
    	topics.forEach(topic -> list.add(ConvertUtil.convertToFeatureItemBean(topic)));
    	JSONObject dataJSON = new JSONObject();
    	dataJSON.putAll(JSONObject.fromObject(page));
    	dataJSON.put("list", list);
    	resJSON.put("data", dataJSON);
    	return resJSON.toString();
    }

    @RequestMapping({ "ask/feature/topic/{topicId}.html", "ft/{topicId}" })
    public String getTopicById(@PathVariable("topicId") String topicId, ModelMap model) {
        Long decodedId = IDUtil.decodeId(topicId);
        if (null == decodedId || decodedId <= 0)
            return Constants.PAGE_404;
        TEliteTopic tEliteTopic = new TEliteTopic();
        tEliteTopic = featureService.getEliteTopicById(decodedId);

        if (null != tEliteTopic && tEliteTopic.getId() > 0) {
            model.put("id", topicId);
            model.put("title", tEliteTopic.getTitle());
            model.put("brief", tEliteTopic.getBrief());
            model.put("cover", ImageUtil.removeImgProtocol(tEliteTopic.getCover()));
            model.put("question_id", IDUtil.encodeId(tEliteTopic.getQuestionId()));
            try {
                TEliteQuestion question = adapter.getQuestionById(tEliteTopic.getQuestionId());
                model.put("question_name", question.getTitle());
                model.put("question_detail", question.getDetail());
            } catch (Exception e) {
                log.error("", e);
            }
            model.put("start_time", tEliteTopic.getStartTime());
            model.put("end_time", tEliteTopic.getEndTime());
            model.put("update_time", tEliteTopic.getUpdateTime());
        } else
            return Constants.PAGE_404;

        // TODO 返回问题话题页
        return Constants.PAGE_404;
    }

    @RequestMapping(value = { "ask/feature/tagNav.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getTagNav() {
        return getTagNavMethod();
    }

    @RequestMapping(value = { "wx/ask/feature/tagNav/json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetTagNav() {
        return getTagNavMethod();
    }

    public String getTagNavMethod() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONArray tagNav = featureService.getTagNav();
        if (null == tagNav || tagNav.isEmpty())
            return ResponseJSON.getErrorInternalJSON().toString();
        resJSON.put("data", tagNav);
        return resJSON.toString();
    }

    @RequestMapping(value = { "ask/feature/slogan.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSlogan() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        String slogan = featureService.getSlogan();
        resJSON.put("slogan", slogan);
        return resJSON.toString();
    }

    @RequestMapping(value = { "ask/feature/tagSquare.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getTagSquare(@RequestParam(value = "tagId", required = true) String tagId) {
        return getTagSquareMethod(tagId);
    }

    @RequestMapping(value = { "wx/ask/feature/tagSquare.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetTagSquare(@RequestParam(value = "tagId", required = true) String tagId) {
        return getTagSquareMethod(tagId);
    }

    public String getTagSquareMethod(String tagId) {
        Long decodedTagId = IDUtil.decodeId(tagId);
        if (null == decodedTagId || decodedTagId < 0)
            return ResponseJSON.getErrorParamsJSON().toString();
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONArray data = new JSONArray();
        List<Integer> ids = featureService.getTagSquare(decodedTagId.intValue());
        // 标签广场的解析方法
        try {
            for (Integer id : ids) {
                Tag tag = tagAdapter.getTagById(id);
                JSONObject tagJSON = new JSONObject();
                tagJSON.put("tag_id", IDUtil.encodeId(id));
                tagJSON.put("tag_name", tag.getName());
                data.add(tagJSON);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        resJSON.put("data", data);
        return resJSON.toString();

    }

    // 获取精选回答, not useful now
    @RequestMapping(value = { "ask/feature/featured.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getFeatured(@ModelAttribute("bean") FeedListDisplayBean bean, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        if (bean.getCurrPageNo() < 1)
            return ResponseJSON.getErrorParamsJSON().toString();
        Integer start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
        Integer count = bean.getPageSize();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (null == bpId || bpId.longValue() <= 0)
            bpId = null;
        bean = featureService.getTopFeeds(bean, bpId, start, count);
        resJSON.put("data", bean);
        return resJSON.toString();

    }

    // 获取相关回答
    @RequestMapping(value = { "ask/feature/relevant/questions.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRelevantQuestions(@RequestParam(value = "questionId", required = true) String questionId,
            HttpSession session) {
        return getRelevantQuestionsMethod(questionId, session);
    }

    @RequestMapping(value = { "wx/ask/feature/relevant/questions.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRelevantQuestions(@RequestParam(value = "questionId", required = true) String questionId,
            HttpSession session) {
        return getRelevantQuestionsMethod(questionId, session);
    }

    public String getRelevantQuestionsMethod(String questionId, HttpSession session) {
        Long decodedId = -1l;
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        decodedId = IDUtil.decodeId(questionId);
        if (decodedId.longValue() == -1)
            return ResponseJSON.getErrorParamsJSON().toString();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        List<TEliteQuestion> relevantQuestions = new ArrayList<>();
        List<TEliteQuestion> tEliteQuestions = featureService.getRelevantQuestion(bpId, decodedId);
        if (null != tEliteQuestions && tEliteQuestions.size() > 0)
            relevantQuestions = tEliteQuestions;
        JSONArray data = new JSONArray();
        for (TEliteQuestion question : relevantQuestions) {
            JSONObject questionJSON = new JSONObject();
            String encodedId = IDUtil.encodeId(question.getId());
            questionJSON.put("id", encodedId);
            questionJSON.put("title", question.getTitle());
            questionJSON.put("questionNum", featureService.getAnswerNum(question.getId()));
            data.add(questionJSON);
        }
        resJSON.put("data", data);
        return resJSON.toString();
    }

    // 获取问题相关标签
    @RequestMapping(value = { "ask/feature/relevant/tags.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRelevantTags(@RequestParam(value = "questionId", required = true) String questionId) {
        return getRelevanteTagsMethod(questionId);
    }

    @RequestMapping(value = { "wx/ask/feature/relevant/tags.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRelevantTags(@RequestParam(value = "questionId", required = true) String questionId) {
        return getRelevanteTagsMethod(questionId);
    }

    public String getRelevanteTagsMethod(String questionId) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long decodedId = IDUtil.decodeId(questionId);
        if (decodedId.longValue() < 0)
            return ResponseJSON.getErrorParamsJSON().toString();
        List<Tag> tags = featureService.getQuestionRecomTags(decodedId);
        JSONArray data = new JSONArray();
        for (Tag tag : tags) {
            JSONObject tagJSON = new JSONObject();
            String encodedId = IDUtil.encodeId((long) tag.getId());
            tagJSON.put("id", encodedId);
            tagJSON.put("name", tag.getName());
            data.add(tagJSON);
        }
        resJSON.put("data", data);
        return resJSON.toString();

    }

    // 获取用户推荐
    @RequestMapping(value = { "ask/feature/relevant/users.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecomUsers(@RequestParam(value = "tag", required = false) Integer tag, HttpSession session) {
        return getRecomUserMethod(tag, session);
    }

    @RequestMapping(value = { "wx/ask/feature/relevant/users.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecomUsers(@RequestParam(value = "tag", required = false) Integer tag, HttpSession session) {
        return getRecomUserMethod(tag, session);
    }

    @RequestMapping(value = { "app/ask/feature/relevant/users.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecomUsers(@RequestParam(value = "tag", required = false) Integer tag, HttpSession session) {
        return getRecomUserMethod(tag, session);
    }

    public String getRecomUserMethod(Integer tag, HttpSession session) {
        if (null == tag || tag < 0 || tag > 3)
            tag = 0;
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (null == viewerId || viewerId.longValue() < 0) {
            resJSON.put("data", "");
            return resJSON.toString();
        }
        List<UserDetailDisplayBean> recomUsers = featureService.getRecomUsers(viewerId, tag);
        JSONArray data = new JSONArray();
        for (UserDetailDisplayBean recomUser : recomUsers) {
            JSONObject recomUserJSON = new JSONObject();
            recomUserJSON.put("bpId", recomUser.getBpId());
            recomUserJSON.put("avatar", recomUser.getAvatar());
            recomUserJSON.put("nick", recomUser.getNick());
            recomUserJSON.put("followed", recomUser.isHasFollowed());
            recomUserJSON.put("authenticated", recomUser.isAuthenticated());
            recomUserJSON.put("fansNum", recomUser.getFansNum());
            recomUserJSON.put("questionNum", recomUser.getQuestionNum());
            recomUserJSON.put("answerNum", recomUser.getAnswerNum());
            recomUserJSON.put("homeUrl", recomUser.getHomeUrl());
            data.add(recomUserJSON);
        }
        resJSON.put("data", data);
        return resJSON.toString();
    }

    // 获取标签推荐
    @RequestMapping(value = { "ask/feature/recom/tags.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getRecomTags(HttpSession session) {
        return getRecomTagsMethod(session);
    }

    @RequestMapping(value = { "wx/ask/feature/recom/tags.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetRecomTags(HttpSession session) {
        return getRecomTagsMethod(session);
    }

    @RequestMapping(value = { "app/ask/feature/recom/tags.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecomTags(HttpSession session) {
        return getRecomTagsMethod(session);
    }

    public String getRecomTagsMethod(HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONArray data = new JSONArray();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        List<Tag> tags = featureService.getRecomTags(bpId);
        for (Tag tag : tags) {
            JSONObject tagJSON = new JSONObject();
            tagJSON.put("id", IDUtil.encodeId((long) tag.getId()));
            tagJSON.put("name", tag.getName());
            int[] nums = InteractionUtil.getInteractionNumsForTag(tag.getId());
            tagJSON.put("follow", nums[0]);
            data.add(tagJSON);
        }
        resJSON.put("data", data);
        return resJSON.toString();
    }

    // 获取推荐专家列表
    @RequestMapping(value = { "f/experts.json" })
    @ResponseBody
    public String getRecomExperts(PageBean page, HttpServletRequest request) {
        return getRecomExpertsMethod(page, request);
    }

    @RequestMapping(value = { "wx/f/experts.json" })
    @ResponseBody
    public String wxGetRecomExperts(PageBean page, HttpServletRequest request) {
        return getRecomExpertsMethod(page, request);
    }

    @RequestMapping(value = { "app/f/experts.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecomExperts(PageBean page, HttpServletRequest request) {
        return getRecomExpertsMethod(page, request);
    }

    public String getRecomExpertsMethod(PageBean page, HttpServletRequest request) {
        JSONObject resJSON = EliteResponseJSON.getDefaultResJSON();
        JSONObject dataJSON = new JSONObject();
        List<Long> ids = new ArrayList<Long>();
        if (AgentUtil.getSource(request) == AgentSource.MOBILE) {
            ids = identityService.getAllRecomExperts();
        } else if (AgentUtil.getSource(request) == AgentSource.WECHAT) {
            int start = 0;
            int count = Constants.WECHAT_RECOM_EXPERTS_NUM;
            ids = identityService.getRecomExperts(start, count);
        } else {
            page.setPageSize(Constants.DEFAULT_RECOM_EXPERTS_PAGE_SIZE);
            page.setTotal(identityService.getRecomNum());
            int start = (page.getCurrPageNo() - 1) * page.getPageSize();
            int count = page.getPageSize();
            ids = identityService.getRecomExperts(start, count);
            dataJSON.putAll(JSONObject.fromObject(page));
        }
        List<UserDetailDisplayBean> userDetails = new ArrayList<UserDetailDisplayBean>();
        if (null != ids) {
            ids.forEach(id -> userDetails.add(bpUserService.getUserDetailByBpId(id, null, true)));
        }
        dataJSON.put("experts", userDetails);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }

    // 用于app的feature数据，比如专题，专栏，热门讨论等列表,因为app是展示在首页，所以需要在service端加缓存
    @RequestMapping(value = { "app/f/wrapper.json" }, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetFeatureWrapper() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        EliteFeatureAppWrapperBean featureWrapper = featureService.getAppWrapper();
        resJSON.put("data", featureWrapper);
        return resJSON.toString();
    }

    // 获取焦点图
    @RequestMapping(value = "f/focus.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getFocusContent() {
        return getFocusContentMethod();
    }

    @RequestMapping(value = "wx/f/focus.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetFocusContent() {
        return getFocusContentMethod();
    }

    public String getFocusContentMethod() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        List<EliteFocusBean> focusList = featureService.getFocus();
        if (null == focusList || focusList.size() <= 0)
            return ResponseJSON.getErrorInternalJSON().toString();
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("num", focusList.size());
        dataJSON.put("focusList", focusList);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    public JSONArray resolveSubjectDetail(String detail) {
        JSONObject detailJSON = JSONObject.fromObject(detail);
        JSONArray resArray = new JSONArray();
        Iterator it = detailJSON.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (StringUtils.isBlank(key))
                continue;
            JSONObject section = new JSONObject();
            Matcher m = Pattern.compile(Constants.SUBJECT_SPLIT_CHAR + "(.*)").matcher(key);
            String name = key, sectionBrief = "";
            if (m.find()) {
                name = key.substring(0, m.start());
                sectionBrief = m.group(1);
            }
            section.put("section_name", HtmlUtils.htmlUnescape(name));
            section.put("section_brief", HtmlUtils.htmlUnescape(sectionBrief));
            JSONArray sectionDetail = new JSONArray();
            List<Long> ids = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> imgs = new ArrayList<>();
            // String[] answerIds = detailJSON.getString(key).split(" ");
            String detailString = detailJSON.getString(key);
            if (detailString.contains("##")) {
                m = Pattern.compile("(\\d+)\\s+##").matcher(detailString);
                while (m.find()) {
                    ids.add(Long.valueOf(m.group(1)));
                }
                m = Pattern.compile(Constants.SUBJECT_SPLIT_CHAR + "(.*?)" + Constants.SUBJECT_SPLIT_CHAR)
                        .matcher(detailString);
                while (m.find()) {
                    titles.add(m.group(1));
                }
                m = Pattern.compile("http://\\S*").matcher(detailString);
                while (m.find()) {
                    imgs.add(m.group());
                }
            } else {
                Arrays.asList(detailString.split(" ")).forEach(id -> ids.add(Long.valueOf(id)));
            }
            try {
                for (int i = 0; i < ids.size(); i++) {
                    Long answerId = ids.get(i);
                    JSONObject part = new JSONObject();
                    TEliteAnswer answer = adapter.getAnswerById(answerId);
                    String answerContent = answer.getContent();
                    ContentBean contentBean = ContentUtil.parseContent(answerContent);
                    String brief = contentBean.getPlainText();
                    Long questionId = answer.getQuestionId();
                    TEliteQuestion question = adapter.getQuestionById(questionId);
                    String questionTitle = question.getTitle();
                    if (i < titles.size() && StringUtils.isNotBlank(titles.get(i)))
                        questionTitle = titles.get(i);
                    Long userId = answer.getBpId();
                    UserDetailDisplayBean userInfo = bpUserService.getUserDetailByBpId(userId, null);
                    String answerImg = Constants.DEFAULT_SUBJECT_COVER;
                    if (i < imgs.size())
                        answerImg = imgs.get(i);
                    part.put("question_id", IDUtil.encodeId(questionId));
                    part.put("question_title", HtmlUtils.htmlUnescape(questionTitle));
                    part.put("answer_id", IDUtil.encodeId(answerId));
                    part.put("answer_content", HtmlUtils.htmlUnescape(brief));
                    part.put("answer_img", ImageUtil.removeImgProtocol(answerImg));
                    part.put("answer_img_small", ImageUtil.getSmallImage(answerImg));
                    part.put("answer_img_medium",
                            ImageUtil.getSmallImage(answerImg, null, Constants.MEDIUM_IMAGE_RATIO));
                    part.put("user", userInfo);
                    sectionDetail.add(part);
                }
            } catch (Exception e) {
                log.error(" ", e);
            }
            section.put("section_detail", sectionDetail);
            resArray.add(section);
        }
        return resArray;
    }

    public JSONArray resolveRecentSubject() {
        JSONArray resJSON = new JSONArray();
        try {
            List<TEliteSubject> tEliteSubjects = featureService.getEliteSubject(0, Constants.SUBJECT_LENGTH);
            for (TEliteSubject tEliteSubject : tEliteSubjects) {
                JSONObject tEliteJSON = new JSONObject();
                Date time = new Date(tEliteSubject.getUpdateTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String date = sdf.format(time);
                tEliteJSON.put("id", IDUtil.encodeId(tEliteSubject.getId()));
                tEliteJSON.put("title", tEliteSubject.getName());
                tEliteJSON.put("date", date);
                tEliteJSON.put("cover", ImageUtil.removeImgProtocol(tEliteSubject.getCover()));
                tEliteJSON.put("cover_small", ImageUtil.getWapFocusCover(tEliteSubject.getCover()));
                tEliteJSON.put("cover_medium",
                        ImageUtil.getSmallImage(tEliteSubject.getCover(), null, Constants.MEDIUM_IMAGE_RATIO));
                tEliteJSON.put("num", ContentUtil.getSubjectNum(tEliteSubject.getDetail()));
                resJSON.add(tEliteJSON);
            }

        } catch (Exception e) {
            log.error("", e);
        }
        return resJSON;
    }

}
