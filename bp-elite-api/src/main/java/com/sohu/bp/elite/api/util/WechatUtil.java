package com.sohu.bp.elite.api.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.model.TEliteQuestion;

import net.sf.json.JSONObject;

/**
 * 用于微信推送的工具类
 * @author zhijungou
 * 2017年3月27日
 */
public class WechatUtil {
	private static final Logger log = LoggerFactory.getLogger(WechatUtil.class);
	private static final BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	
	public static String getWechatQuestionUrl(Long questionId) {
		if (null == questionId || questionId <= 0) return "https://home.focus.cn/decoration/login.html?ru=https://bar.focus.cn";
		String url =  "https://home.focus.cn/decoration/login.html?ru=https://bar.focus.cn/q/"+ IDUtil.encodeId(questionId);
		return url;
	}
	
	public static String getWechatAnswerUrl(Long answerId) {
	    if (null == answerId || answerId <= 0) return "https://home.focus.cn/decoration/login.html?ru=https://bar.focus.cn";
	    String url = "https://home.focus.cn/decoration/login.html?ru=https://bar.focus.cn/a/"+ IDUtil.encodeId(answerId);
	    return url;
	}
	
	public static String getInviteMessageWechatData(Long bpId, String name, TEliteQuestion question, String tags) {
        JSONObject firstData = new JSONObject();
        if (StringUtils.isBlank(name)) {
        	firstData.put("value", "问吧邀请您回答：" + question.getTitle());
        } else {
        	firstData.put("value", name + "邀请您回答: " + question.getTitle());
        }
        JSONObject keyword1Data = new JSONObject();
        if (StringUtils.isBlank(tags)) {
            StringBuilder tagSB = new StringBuilder("");
            String tagIds = question.getTagIds();
            if (StringUtils.isNotBlank(tagIds)) {
                String[] tagIdArray = tagIds.split(Constants.DEFAULT_SPLIT_CHAR);
                for (String tagIdStr : tagIdArray) {
                    try {
                        Integer tagId = Integer.parseInt(tagIdStr);
                        Tag tag = decorationServiceAdapter.getTagById(tagId);
                        if (null != tag && tag.getStatus() == TagStatus.WORK) {
                            tagSB.append(tag.getName()).append(" ");
                        }
                    }catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
            tags = tagSB.toString();
        }
        log.info("[WECHAT PUSH]tags="+tags);
        keyword1Data.put("value", tags);
        JSONObject keyword2Data = new JSONObject();
        keyword2Data.put("value", DateUtil.format(question.getPublishTime(), DateUtil.sdf));
        JSONObject keyword3Data = new JSONObject();
        keyword3Data.put("value", "邀请回答");
        JSONObject keyword4Data = new JSONObject();
        keyword4Data.put("value", DateUtil.format(System.currentTimeMillis(), DateUtil.sdf));
        JSONObject remarkData = new JSONObject();
        remarkData.put("value", "期待您的精彩回答");
        JSONObject data = new JSONObject();
        data.put("first", firstData);
        data.put("keyword1", keyword1Data);
        data.put("keyword2", keyword2Data);
        data.put("keyword3", keyword3Data);
        data.put("keyword4", keyword4Data);
        data.put("remarkData", remarkData);
        return data.toString();
	}
	
	public static String getHasNewAnswerMessage(String name, String title, Long date) {
	    JSONObject firstData = new JSONObject();
	    firstData.put("value", "您的问题有了新的回答");
	    JSONObject keyword1Data = new JSONObject();
	    keyword1Data.put("value", title);
	    JSONObject keyword2Data = new JSONObject();
	    keyword2Data.put("value", name);
	    JSONObject keyword3Data = new JSONObject();
	    keyword3Data.put("value", DateUtil.format(date, DateUtil.sdf));
	    JSONObject remarkData = new JSONObject();
	    remarkData.put("value", "点击查看");
	    JSONObject dataJSON = new JSONObject();
	    dataJSON.put("first", firstData);
	    dataJSON.put("keyword1", keyword1Data);
	    dataJSON.put("keyword2", keyword2Data);
	    dataJSON.put("keyword3", keyword3Data);
	    dataJSON.put("remark", remarkData);
	    return dataJSON.toString();
	}
}
