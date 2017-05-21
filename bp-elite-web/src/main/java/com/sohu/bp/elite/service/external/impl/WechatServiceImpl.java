package com.sohu.bp.elite.service.external.impl;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.external.WechatService;
import com.sohu.bp.elite.util.DateUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapter;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapterFactory;
import com.sohu.bp.wechat.model.BpWechatPlatform;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.formula.functions.Syd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicholastang on 2017/2/24.
 */
public class WechatServiceImpl implements WechatService {
    private static Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);
    private static BpWechatServiceAdapter wechatServiceAdapter = BpWechatServiceAdapterFactory.getWechatServiceAdapter();
    private static BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    private static final int WECHAT_PLATFORM_ID = 4;
    @Override
    public boolean sendTemplateMsg(long bpId, int templateId, String url, JSONObject data) {
        try {
            String openId = "";
            CodeMsgData codeMsgData = serviceAdapter.getOpenIdentificationsByBpid(bpId);
            if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
                JSONArray msgDataJSONArray = JSONArray.fromObject(codeMsgData.getData());
                if (null == msgDataJSONArray || msgDataJSONArray.size() == 0) {
                    logger.error("get unionId for bpId={} failed.", new String[]{String.valueOf(bpId)});
                    return false;
                }
                String msgData = "";
                for(int i=0; i<msgDataJSONArray.size(); i++) {
                    msgData = msgDataJSONArray.getString(i);
                    if (!msgData.startsWith("WECHAT_")) {
                        msgData = "";
                        continue;
                    }
                    break;
                }
                String unionId = msgData.substring(("WECHAT_").length());
                if (StringUtils.isBlank(unionId)) {
                    logger.error("unionId of bpId={} is empty", new String[]{String.valueOf(bpId)});
                    return false;
                }
                codeMsgData = extendServiceAdapter.getBpWechatOpenIdByUnionId(unionId);
                if (null != codeMsgData &&  codeMsgData.getCode() == ResponseConstants.OK) {
                    JSONObject wechatInfoJSON = JSONObject.fromObject(codeMsgData.getData());
                    if (null == wechatInfoJSON || !wechatInfoJSON.containsKey("openId")) {
                        logger.error("get open id for bpId={} unionId={} failed.codeMsgData={}", new String[]{String.valueOf(bpId), unionId, codeMsgData.getMessage()});
                        return false;
                    }
                    openId = wechatInfoJSON.getString("openId");
                } else {
                    logger.error("get openId for bpId={} unionId={} failed", new String[]{String.valueOf(bpId), unionId});
                    return false;
                }
            } else {
                logger.error("get unio Id for bpId={} failed.", new String[]{String.valueOf(bpId)});
                return false;
            }
            if (StringUtils.isBlank(openId)) {
                logger.error("openId is blank which bpId={}", new String[]{String.valueOf(bpId)});
                return false;
            }
            
            BpWechatPlatform bpWechatPlatform = wechatServiceAdapter.getWechatPlatform(WECHAT_PLATFORM_ID);
            if (wechatServiceAdapter.sendWechatTemplateMessage(bpWechatPlatform, openId, templateId, url, data.toString()) > 0) {
               return true;
            }
        }catch (Exception e) {
            logger.error("", e);
        }

        return false;
    }

    @Override
    public boolean sendAnswerInviteMsg(long bpId, TEliteQuestion question, String tags) {
        String url = "https://home.focus.cn/decoration/login.html?ru=https://bar.focus.cn/q/"+ IDUtil.encodeId(question.getId());
        JSONObject firstData = new JSONObject();
        firstData.put("value", "问吧邀请您回答：" + question.getTitle());
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
                        logger.error("", e);
                    }
                }
            }
            tags = tagSB.toString();
        }
        logger.info("[WECHAT PUSH]tags="+tags);
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
        return this.sendTemplateMsg(bpId, 5, url, data);
    }
    
    public static void main(String[] args) {
//        WechatServiceImpl wechat = new WechatServiceImpl();
//        EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//        try {
//            TEliteQuestion question = eliteAdapter.getQuestionById(IDUtil.decodeId("asyj1"));
//            boolean result = wechat.sendAnswerInviteMsg(IDUtil.decodeId("3b63ha"), question, "装修");
//            System.out.println("result : " + result);
//        } catch (Exception e) {
//            logger.error("", e);
//        }


        Long bpId = 9056908L;
        CodeMsgData codeMsgData = serviceAdapter.getOpenIdentificationsByBpid(bpId);
        System.out.println(codeMsgData);
        if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
            JSONArray msgDataJSONArray = JSONArray.fromObject(codeMsgData.getData());
            if (null == msgDataJSONArray || msgDataJSONArray.size() == 0) {
                logger.error("get unionId for bpId={} failed.", new String[]{String.valueOf(bpId)});
                return;
            }
            String msgData = "";
            for (int i = 0; i < msgDataJSONArray.size(); i++) {
                msgData = msgDataJSONArray.getString(i);
                if (!msgData.startsWith("WECHAT_")) {
                    msgData = "";
                    continue;
                }
                break;
            }
//            String[] msgDataArray = msgData.split("_");
//            if (null == msgDataArray || msgDataArray.length != 2) {
//                logger.error("message data={} of unionId for bpId={} is wrong", new String[]{msgData, String.valueOf(bpId)});
//                return;
//            }
//            String unionId = msgDataArray[1];
            String unionId = msgData.substring(("WECHAT_").length());
            System.out.println(unionId);
            if (StringUtils.isBlank(unionId)) {
                logger.error("unionId of bpId={} is empty", new String[]{String.valueOf(bpId)});
                return;
            }
            codeMsgData = extendServiceAdapter.getBpWechatOpenIdByUnionId(unionId);
            if (null != codeMsgData &&  codeMsgData.getCode() == ResponseConstants.OK) {
                JSONObject wechatInfoJSON = JSONObject.fromObject(codeMsgData.getData());
                if (null == wechatInfoJSON || !wechatInfoJSON.containsKey("openId")) {
                    logger.error("get open id for bpId={} unionId={} failed.codeMsgData={}", new String[]{String.valueOf(bpId), unionId, codeMsgData.getMessage()});
                    return;
                }
                String openId = wechatInfoJSON.getString("openId");
                System.out.println("OOOOOOOPENID="+openId);
            } else {
                logger.error("get openId for bpId={} unionId={} failed", new String[]{String.valueOf(bpId), unionId});
                return;
            }
        }

    }

}
