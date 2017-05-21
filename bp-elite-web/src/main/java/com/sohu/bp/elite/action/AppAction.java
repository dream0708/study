package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.ItemInfoBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureItemBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureListDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteRecomType;
import com.sohu.bp.elite.model.TColumnListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.service.web.ColumnService;
import com.sohu.bp.elite.service.web.FeatureService;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.CookieUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.elite.util.RequestUtil;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.enums.ActionType;
import com.sohu.bp.thallo.model.RelationAction;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 用于app的一些通用接口
 * @author zhijungou
 * 2017年5月8日
 */
@Controller
public class AppAction {
    private static final Logger log = LoggerFactory.getLogger(AppAction.class);
    private static final BpDecorationServiceAdapter bpDecorationAdapter =   BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final BpThalloServiceAdapter thalloAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    @Resource
    private FeatureService featureService;
    @Resource
    private ColumnService columnService;
    
    @RequestMapping(value = {"app/feature/recom/tags"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetRecomTags() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        List<JSONObject> tags = new ArrayList<JSONObject>();
        List<String> list = featureService.getRecomList(EliteRecomType.RECOM_TAG.getValue(), Integer.MAX_VALUE);
        for (String idString : list) {
            int id = Integer.valueOf(idString);
            try {
                Tag tag = bpDecorationAdapter.getTagById(id);
                JSONObject tagJSON = new JSONObject();
                tagJSON.put("tagId", tag.getId());
                tagJSON.put("tagName", tag.getName());
                tags.add(tagJSON);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("tagList", tags);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    /**
     * 通用follow
     * @param tagIds
     * @param session
     * @return
     */
    @RequestMapping(value = {"app/common/follow"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appBatchFollowTags(String ids, Integer bpType, HttpServletRequest request, HttpSession session) {
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        Long userId = null;
        BpType userType = BpType.Elite_User;
        if (null == bpId || bpId <= 0) {
            String fuv =  CookieUtil.getCookieValue(request, Constants.FUV);
            if (StringUtils.isBlank(fuv)) return ResponseJSON.getErrorParamsJSON().toString();
            userId = Long.valueOf(fuv);
            userType = BpType.FUV;
        } else {
            userId = bpId;
        }
        if (StringUtils.isBlank(ids)) return ResponseJSON.getErrorParamsJSON().toString();
        String[] idArray = ids.split(Constants.TAG_IDS_SEPARATOR);
        for (String idString : idArray) {
            try {
                Long id = Long.valueOf(idString);
                RelationAction relationAction = new RelationAction().setType(ActionType.TYPE_FOLLOW.getValue())
                        .setUserId(userId).setUserType(userType.getValue())
                        .setObjectId(id).setObjectType(bpType)
                        .setActIp(RequestUtil.getClientIPLong(request)).setActPort(RequestUtil.getClientPort(request)).setActTime(new Date().getTime());
                boolean result = thalloAdapter.doFollow(relationAction);
                log.info("app batch follow, userId = {} follow ids = {}, result = {}", new Object[]{userId, ids, result});
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return ResponseJSON.getDefaultResJSON().toString();
    }
    
    
    @RequestMapping(value = "app/ask/itemInfo", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String AppGetItemInfo(ItemInfoBean bean, HttpSession session) {
        if (null == bean.getItemType() || bean.getItemType() <= 0 || StringUtils.isBlank(bean.getItemId())) return ResponseJSON.getErrorParamsJSON().toString();
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        long id = 0;
        try {
            id = IDUtil.decodeId(bean.getItemId());
        } catch (Exception e) {
            log.error("", e);
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        try {
            switch (BpType.valueMap.get(bean.getItemType())) {
            case Question : 
                TEliteQuestion question = eliteAdapter.getQuestionById(id);
                bean.setUser(bpId == question.getBpId());
                bean.setHasFollowed(InteractionUtil.hasFollowed(bpId, id, bean.getItemType()));
                boolean[] answeredFlag = InteractionUtil.hasAnsweredAndChoosed(bpId, id);
                bean.setHasAnswered(answeredFlag[0]).setHasChoosed(answeredFlag[1]);
                if (question.getSpecialType() == BpType.Elite_VS.getValue()) {
                    bean.setHasFavorited(InteractionUtil.hasFavorited(bpId, id, BpInteractionTargetType.ELITE_VS));
                }
                break;
            case Answer:
                TEliteAnswer answer = eliteAdapter.getAnswerById(id);
                bean.setUser(bpId == answer.getBpId());
                bean.setHasLiked(InteractionUtil.hasLiked(bpId, id));
                bean.setHasTread(InteractionUtil.hasTread(bpId, id));
                bean.setHasFavorited(InteractionUtil.hasFavorited(bpId, id));
                break;
            default:
            }
        } catch (Exception e) {
            log.error("", e);
        }
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    /**
     * 获取精选内容：实际就为专题+专栏
     * @param detail
     * @return
     */
    @RequestMapping(value = "app/f/featured", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetFeaturedListContent(EliteFeatureListDisplayBean bean) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        long subjectNum = featureService.getEliteCount();
        TColumnListResult columnListResult = columnService.getColumns(0, 0);
        long columnNum = columnListResult.getTotal();
        long total = subjectNum + columnNum;
        bean.setTotal(total);
        int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
        int count = bean.getPageSize();
        long minNum = subjectNum > columnNum ? columnNum : subjectNum;
        List<EliteFeatureItemBean> features = new ArrayList<>();
        List<TEliteSubject> subjects = new ArrayList<TEliteSubject>();
        List<TEliteColumn> columns = new ArrayList<TEliteColumn>();
        if (minNum >= (start + count) / 2) {
            subjects = featureService.getEliteSubject(start / 2, count / 2);
            columns = columnService.getColumns(start / 2, count / 2).getColumns();
        } else {
            int maxStart = minNum - start / 2 > 0 ? start / 2 : start - (int) minNum;
            int maxCount = minNum - start / 2 > 0 ? count + start / 2 - (int) minNum : count;
            if (subjectNum > columnNum) {
                subjects = featureService.getEliteSubject(maxStart, maxCount);
                columns = columnService.getColumns(start / 2, count / 2).getColumns();
            } else {
                subjects = featureService.getEliteSubject(start / 2, count / 2);
                columns = columnService.getColumns(maxStart, maxCount).getColumns();
            }
        }

        features.addAll(ConvertUtil.convertColumnList(columns));
        features.addAll(ConvertUtil.convertSubjectList(subjects));
        features.sort(new Comparator<EliteFeatureItemBean>() {
            @Override
            public int compare(EliteFeatureItemBean o1, EliteFeatureItemBean o2) {
                return o1.getUpdateTime() > o2.getUpdateTime() ? -1 : 1;
            }
        });
        bean.setFeatureList(features);
        resJSON.put("data", bean);
        return resJSON.toString();
    }
}
