package com.sohu.bp.elite.util;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.*;
import com.sohu.bp.elite.action.bean.tag.TagDisplayBean;
import com.sohu.bp.elite.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhihao
 *         2016/9/5
 */
public class TagUtil {

    private static final Logger log = LoggerFactory.getLogger(TagUtil.class);
    private static final BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();

    public static String decodeTagIds(String tagIds){
        if(StringUtils.isBlank(tagIds)){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        long decodedTagId;
        for(String tagId : tagIds.split(Constants.TAG_IDS_SEPARATOR)){
            decodedTagId = -1;
            try{
                decodedTagId = IDUtil.decodeId(tagId);
            }catch (Exception e){
                log.error("", e);
            }
            if(decodedTagId != -1) {
                sb.append(String.valueOf(decodedTagId) + Constants.TAG_IDS_SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param ip
     * @param level level=1为省，直辖市
     * @return
     */
    public static Integer getCityTag(String ip, int level) {
        Integer tagId = -1;
        if (StringUtils.isBlank(ip) || ip.equalsIgnoreCase("127.0.0.1")) {
            return tagId;
        }
        IPDataHandler ipDataHandler = new IPDataHandler();
        String city = "";
        if (null != ipDataHandler) {
            String[] locationArray = ipDataHandler.getLocationByIp(ip);
            if(null != locationArray && locationArray.length >= 3)
            {
                city = locationArray[2];
            }
            else if(locationArray.length >= 2)
            {
                city = locationArray[1];
            }
        }
        log.info("[CLIENT_CITY]"+city);
        if (StringUtils.isBlank(city)) {
            return tagId;
        }

        SearchTagCondition condition = new SearchTagCondition();
        condition.setKeywords(city)
                .setFrom(0)
                .setCount(1)
                .setType(TagType.ELITE_LOCATION_TAG)
                .setStatus(TagStatus.WORK);

        TagListResult tagListResult = null;
        try {
            tagListResult = bpDecorationServiceAdapter.searchTag(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if (tagListResult == null || tagListResult.getTotal() == 0 || tagListResult.getTags().size() == 0){
            return tagId;
        }
        Tag tag = tagListResult.getTags().get(0);
        tagId = tag.getId();
        //如果设定了要获取的地域等级，当前的地域低于给定的等级，则将地域向其父地域移动，直到满足等级
        if (level > 0) {
            String parentIds = tag.getParents();
            if (StringUtils.isNotBlank(parentIds)) {
                String[] parentIdArray = parentIds.split(Constants.TAG_IDS_SEPARATOR);
                if (level+1 < parentIdArray.length) {
                    //标签的父标签id是0;全国;xx省（直辖市）;xxx市这种
                    tagId = Integer.parseInt(parentIdArray[level+1]);
                }
            }
        }

        return tagId;
    }
    
    public static String getTagNamesByIds(String tagString) {
        if (StringUtils.isBlank(tagString)) return "";
        String[] tagIds = tagString.split(Constants.TAG_IDS_SEPARATOR);
        StringBuilder tagBuilder = new StringBuilder();
        for (String tagId : tagIds) {
            try {
                Tag tag = bpDecorationServiceAdapter.getTagById(Integer.valueOf(tagId));
                tagBuilder.append(tag.getName()).append(Constants.TAG_IDS_SEPARATOR);
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
        return tagBuilder.toString();
    }

    public static void main(String[] args){
        String a = "0;6452;7031;7060;";
        System.out.println(a.split(Constants.TAG_IDS_SEPARATOR).length);
    }
}
