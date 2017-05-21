package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.action.bean.CommonTipBean;
import com.sohu.bp.elite.action.bean.expert.ExpertApplyBean;
import com.sohu.bp.elite.action.bean.tag.TagSimpleBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.EliteTipType;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.FeatureService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import com.sohu.suc.platform.utils.StringUtils;

import net.sf.json.JSONObject;

/**
 * 专家申请
 * @author zhijungou
 * 2017年2月23日
 */
@Controller
public class ExpertAction {
    private static final Logger log = LoggerFactory.getLogger(ExpertAction.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final BpServiceAdapter bpAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    @Resource
    private BpUserService bpUserService; 
    @Resource
    private UserInfoService userService;
    @Resource
    private FeatureService featureService;
    
    
    @RequestMapping("expert/apply")
    public String showIndex(HttpServletRequest request, HttpSession session, ModelMap model) {
        Long bpId = (Long)session.getAttribute(SessionConstants.USER_ID);
        if (null == bpId || bpId <= 0) {
            log.info("[EXPERT APPLY ERROR]not login");
            return Constants.PAGE_404;
        }
        if (AgentUtil.getSource(request) != AgentSource.MOBILE) {
            log.info("[EXPERT APPLY ERROR]not mobile.agent="+request.getHeader("User-Agent"));
            return Constants.PAGE_404;
        }
        UserInfo userDetail = userService.getUserInfoByBpid(bpId);
        model.put("bpId", IDUtil.encodeId(bpId));
        model.put("avatar", userDetail.getAvatar());
        model.put("nick", userDetail.getNick());
        model.put("mobile", userDetail.getMobile());
        try {
            TEliteUser user = eliteAdapter.getUserByBpId(bpId);
            if (Objects.equals(user.getIdentity(), EliteUserIdentity.EXPERT_AUDITING.getValue())) {
                CommonTipBean tipBean = new CommonTipBean();
                tipBean.setTipType(EliteTipType.TIP_WAIT.getValue()).setTipTitle("等待").setTipContent("您已经注册了专家资格，请等待后台审核。");
                model.put("bean", tipBean);
                return "common/tip";
            }
            if (Objects.equals(user.getIdentity(), EliteUserIdentity.EXPERT.getValue())) {
                CommonTipBean tipBean = new CommonTipBean();
                tipBean.setTipType(EliteTipType.TIP_SUCCESS.getValue()).setTipTitle("成功").setTipContent("您已注册为专家，无需重新注册");
                model.put("bean", tipBean);
                return "common/tip";
            }
            model.put("description", user.getDescription());
            Tag selectedLocationTag = null;
            Tag countryTag = decorationAdapter.getSubTagListByParentId(0, TagType.ELITE_LOCATION_TAG, TagStatus.WORK).get(0);
            if (null == countryTag) {
                log.error("get country tag failed.");
                return "common/error";
            }
            Map<Integer, Tag> selectedTagIdMap = new HashMap<Integer, Tag>();
            String selectedTagIds = user.getTagIds();
            if (StringUtils.isNotBlank(selectedTagIds)) {
                String[] selectedTagIdArray = selectedTagIds.split(Constants.TAG_IDS_SEPARATOR);
                if (null != selectedTagIdArray) {
                    for(String selectedIdStr : selectedTagIdArray) {
                        Integer selectedId = Integer.parseInt(selectedIdStr);
                        Tag bpTag = decorationAdapter.getTagById(selectedId);
                        if (bpTag.getStatus() != TagStatus.WORK) {
                            continue;
                        }
//                        if (bpTag.getParents().contains(Constants.DEFAULT_SPLIT_CHAR + countryTag.getId() + Constants.DEFAULT_SPLIT_CHAR)) {
//                            selectedLocationTag = bpTag;
//                            continue;
//                        }
                        
                          if (Objects.equals(bpTag.getType(), TagType.ELITE_LOCATION_TAG)) {
                              selectedLocationTag = bpTag;
                              continue;
                          }
                        selectedTagIdMap.put(selectedId, bpTag);
                    }
                }
            }
            
            List<Tag> selectedTags = new ArrayList<>();
            for(Map.Entry<Integer, Tag> entry : selectedTagIdMap.entrySet()) {
                selectedTags.add(entry.getValue());
            }
            
          //原始专家申请的标签，采用的是一级标签
//            List<Tag> tags =  decorationAdapter.getSubTagListByParentId(0, TagType.ELITE_TAG, TagStatus.WORK);
//            JSONArray jsonArray = featureService.getTagNav();
//            Map<Integer, String> tagMap = new HashMap<Integer, String>();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                JSONObject data = jsonArray.getJSONObject(i);
//                tagMap.put((int)IDUtil.decodeId(data.getString("id")), data.getString("name"));
//            }
//            Iterator<Tag> iterator = tags.iterator();
//            while (iterator.hasNext()) {
//                Tag tag = iterator.next();
//                if (selectedTagIdMap.containsKey(tag.getId())) {
//                    iterator.remove();
//                }
//            }
//            model.put("selectedTaglist", convertTag2TagItemBean(selectedTags, tagMap));
//            model.put("Taglist", convertTag2TagItemBean(tags, tagMap));
            
            //新的专家申请标签，通过在admin中设置来获得
            List<Integer> tagIds = eliteAdapter.getExpertTagIds();
            List<Tag> tags = new ArrayList<Tag>();
            Iterator<Integer> iterator = tagIds.iterator();
            while (iterator.hasNext()) {
                if (selectedTagIdMap.containsKey(iterator.next())) iterator.remove();
            }
            tagIds.stream().forEach(id -> {
               try {
                   tags.add(decorationAdapter.getTagById(id));
               } catch (Exception e) {
                   log.error("", e);
               }
           });
            model.put("selectedTaglist", convertTag2TagItemBean(selectedTags));
            model.put("Taglist", convertTag2TagItemBean(tags));

            List<Tag> provinces = decorationAdapter.getSubTagListByParentId(countryTag.getId(), TagType.ELITE_LOCATION_TAG, TagStatus.WORK);
            model.put("provinces", convertTag2TagItemBean(provinces));
            if (null != selectedLocationTag) {
                String parentIds = selectedLocationTag.getParents();
                if (StringUtils.isNotBlank(parentIds)) {
                    String[] parentIdArray = parentIds.split(Constants.TAG_IDS_SEPARATOR);
                    if (null != parentIdArray) {
                        if (parentIdArray.length >= 3) {
                            model.put("locationId", selectedLocationTag.getId());
                            model.put("provinceId", Integer.parseInt(parentIdArray[2]));
                            model.put("locations", convertTag2TagItemBean(decorationAdapter.getSubTagListByParentId(Integer.parseInt(parentIdArray[2]), TagType.ELITE_LOCATION_TAG, TagStatus.WORK)));
                        } else if (parentIdArray.length >= 2) {
                            model.put("provinceId", selectedLocationTag.getId());
                            model.put("locations", convertTag2TagItemBean(decorationAdapter.getSubTagListByParentId(selectedLocationTag.getId(), TagType.ELITE_LOCATION_TAG, TagStatus.WORK)));
                        } 
                    }
                }
            }
 

        } catch (Exception e) {
            log.error("", e);
            log.info("[EXPERT APPLY ERROR]encounter exception");
            return Constants.PAGE_404;
        }

        return "mobile/expert/expert-apply";
    }
    
    @RequestMapping(value = "expert/apply/area", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getAreas(@RequestParam(value = "id", required = true) Integer id) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONObject dataJSON = new JSONObject();
        try {
            List<TagSimpleBean> tags = convertTag2TagItemBean(decorationAdapter.getSubTagListByParentId(id, TagType.ELITE_LOCATION_TAG, TagStatus.WORK));
            dataJSON.put("areas", tags);
        } catch (Exception e) {
            log.info("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    

    @RequestMapping(value = "expert/apply/submit", produces="application/json;charset=utf-8")
    @ResponseBody
    public String submitExpertApply(ExpertApplyBean bean, HttpServletRequest request, HttpSession session) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        if (AgentUtil.getSource(request) != AgentSource.MOBILE) return ResponseJSON.getErrorParamsJSON().toString();
        Long bpId = (Long)session.getAttribute(SessionConstants.USER_ID);
        if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        try {
            TEliteUser user = eliteAdapter.getUserByBpId(bpId);
            UserInfo userDetail = userService.getUserInfoByBpid(bpId);
            if (null != bean.getNick() && !Objects.equals(bean.getNick(), userDetail.getNick())) {
                CodeMsgData codeMsgData = bpAdapter.updateNick(bpId, bean.getNick());
                if (ResponseConstants.OK != codeMsgData.getCode()) return ResponseJSON.getErrorInternalJSON().toString();
            }
            if (null != bean.getAvatar() && !Objects.equals(bean.getAvatar(), userDetail.getAvatar())) {
                CodeMsgData codeMsgData = bpAdapter.updateAvatar(bpId, bean.getAvatar());
                if (ResponseConstants.OK != codeMsgData.getCode()) return ResponseJSON.getErrorInternalJSON().toString();
            }
            StringBuilder tagIds = new StringBuilder();
            if (null != bean.getTag() && bean.getTag().size() > 0) {
                List<Long> tagOrigin = bean.getTag();
                List<Long> tagFinal = getRandomListElement(tagOrigin);         
                tagFinal.forEach(id -> tagIds.append(id).append(Constants.TAG_IDS_SEPARATOR));
            }
            if (null != bean.getLocationId() && bean.getLocationId() > 0) {
                tagIds.append(bean.getLocationId()).append(Constants.TAG_IDS_SEPARATOR);
            } else if (null != bean.getProvinceId() && bean.getProvinceId() > 0) {
                tagIds.append(bean.getProvinceId()).append(Constants.TAG_IDS_SEPARATOR);
            }
            user.setDescription(bean.getDescription()).setTagIds(tagIds.toString()).setIdentity(EliteUserIdentity.EXPERT_AUDITING.getValue());
            eliteAdapter.updateUser(user); 
            
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        log.info("bpId = {} apply for expert identity", bpId);
        return resJSON.toString();
    }
    
    @RequestMapping(value = "app/expert/tags.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetExpertTags() {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        List<Tag> tags = new ArrayList<Tag>();
        try {
            List<Integer> tagIds = eliteAdapter.getExpertTagIds();
            tagIds.stream().forEach(id -> {
               try {
                   tags.add(decorationAdapter.getTagById(id));
               } catch (Exception e) {
                   log.error("", e);
               }
           });
        } catch (Exception e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("tags", convertTag2TagItemBean(tags));
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    private List<TagSimpleBean> convertTag2TagItemBean(List<Tag> tags) {
        List<TagSimpleBean> tagItems = new ArrayList<TagSimpleBean>();
        if (null == tags || tags.size() <= 0) return tagItems;
        tags.forEach(tag -> {
            TagSimpleBean tagItem = new TagSimpleBean();
            tagItem.setId(tag.getId());
            tagItem.setName(tag.getName());
            tagItems.add(tagItem);
        });
        return tagItems;
    }
    
    private List<TagSimpleBean> convertTag2TagItemBean(List<Tag> tags, Map<Integer, String> map) {
        List<TagSimpleBean> tagItems = new ArrayList<TagSimpleBean>();
        if (null == tags || tags.size() <= 0) return tagItems;
        tags.forEach(tag -> {
            TagSimpleBean tagItem = new TagSimpleBean();
            int id = tag.getId();
            String name = map.get(id);
            if (StringUtils.isNotBlank(name)) {
                tagItem.setId(id);
                tagItem.setName(map.get(id));
                tagItems.add(tagItem);
            }
        });
        return tagItems;
    }
    
    private static List<Long> getRandomListElement(List<Long> tags){
        if (tags.size() <= Constants.TAG_CONSTRAINT) return tags;
        List<Long> result = new ArrayList<Long>();
        Random rand = new Random();
        for (int i = 0; i < Constants.TAG_CONSTRAINT; i ++) {
            int index = rand.nextInt(tags.size());
            result.add(tags.get(index));
            tags.remove(index);
        }
        return result;
    }
}
