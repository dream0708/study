package com.sohu.bp.elite.api.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.api.bean.IndexExpertTeamBean;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.enums.EliteMessageTargetType;
import com.sohu.bp.elite.api.enums.SendCloudTemplate;
import com.sohu.bp.elite.api.service.EliteExpertTeamService;
import com.sohu.bp.elite.api.service.UserInfoService;
import com.sohu.bp.elite.api.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.api.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.api.util.IDUtil;
import com.sohu.bp.elite.api.util.ResponseJSON;
import com.sohu.bp.elite.api.util.SendCloudSmsUtil;
import com.sohu.bp.elite.api.util.ToolUtil;
import com.sohu.bp.elite.api.util.WechatUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteSendWechatTemplate;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TEliteMessageTimePeriodType;
import com.sohu.bp.elite.model.TEliteQuestion;

import net.sf.json.JSONObject;

/**
 * 用于获取，维护专家团的接口
 * @author zhijungou
 * 2017年2月20日
 */
@RequestMapping("innerapi/team")
@Controller
public class EliteExpertTeamApi {
   //expertExpertTeam在建立索引时需要忽略的属性
   private static final String[] EXCLUDES = {"unansweredId", "answeredId"};
   private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamApi.class);
   private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
   private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
 
   @Resource
   private EliteExpertTeamService expertTeamService;
   
   @Resource
   private UserInfoService userInfoService;
   
   @Resource
   private Configuration configuration;
   
   private String mainHost;
   private String askDomain;
   
   @PostConstruct
   public void init() {
       mainHost = configuration.get("mainDomain");
       askDomain = configuration.get("askDomain");
   }
   
   @RequestMapping(value = "list", produces = "application/json;charset=utf-8")
   @ResponseBody
   public String getExpertTeamList() {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       List<String> list = expertTeamService.getExpertTeamList();
       JSONObject dataJSON = new JSONObject();
       dataJSON.put("list", list);
       resJSON.put("data", dataJSON);
       return resJSON.toString();
   }
   
   @RequestMapping(value = "add", produces = "application/json;charset=utf-8")
   @ResponseBody
   public String addExpert(@RequestParam(value = "bpId", required = true) Long bpId) {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParametersErrorJSON().toString();
       if (!expertTeamService.addExpert(bpId)) return ResponseJSON.getErrorInteralErrorJSON().toString();
       return resJSON.toString();
   }
   
   @RequestMapping(value = "remove", produces = "application/json;charset=utf-8")
   @ResponseBody
   public String removeExpert(@RequestParam(value = "bpId", required = true) Long bpId) {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParametersErrorJSON().toString();
       if (!expertTeamService.removeExpert(bpId)) return ResponseJSON.getErrorInteralErrorJSON().toString();
       return resJSON.toString();
   }
   
   @RequestMapping(value = "updateCache", produces = "application/json;charset=utf-8")
   @ResponseBody
   public String updateCache() {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       expertTeamService.updateCache();
       return resJSON.toString();
   }
   
   //用于admin向指定专家发送信息
   @RequestMapping(value = "invite", produces = "application/json;charset=utf-8")
   @ResponseBody
   public String inviteExpertTeam(@RequestParam(value = "bpId", required = true) Long bpId, 
           @RequestParam(value = "questionId", required = true) Long questionId, @RequestParam(value = "tagName", required = false) String tagName) {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       if (null == bpId || bpId <= 0 || null == questionId || questionId <= 0) return ResponseJSON.getErrorParametersErrorJSON().toString();
       log.info("invite elite team bpId = {} to answer question {}, tag = {}", new Object[]{bpId, questionId, tagName});
       try {
            TEliteQuestion question = eliteAdapter.getQuestionById(questionId);
            Long author = question.getBpId();
            UserInfo userInfo = userInfoService.getUserInfoByBpid(author);
            String name = userInfo.getNick();
            TEliteMessageData messageData = new TEliteMessageData();
            String title = question.getTitle();
            String inboxMessageContent = EliteMessageData.NEW_INVITE_BY_PERSON.getContent()
                    .replace(Constants.MESSAGE_DATA_JUMPURL, "https://" + askDomain + ToolUtil.getQuestionUri(IDUtil.encodeId(questionId)))
                    .replace(Constants.MESSAGE_DATA_NICKNAME, name)
                    .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, title);
            Map<String, String> sendCloudVariables = new HashMap<String, String>();
            sendCloudVariables.put(SendCloudSmsUtil.PARAM_NICK_NAME, name);
            sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_TITLE, title);
            sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_URI, ToolUtil.getLoginQuestionUrl(IDUtil.encodeId(questionId), bpId));
            messageData.setInboxMessageDataValue(EliteMessageData.NEW_INVITE_BY_PERSON.getValue()).setInboxMessageContent(inboxMessageContent)
            .setSendCloudTemplate(SendCloudTemplate.NEW_INVITE_BY_PERSON.getValue()).setSendCloudVariables(sendCloudVariables)
            .setWechatTemplateId(EliteSendWechatTemplate.NEW_INVITE_BY_PERSON.getValue()).setWechatUrl(WechatUtil.getWechatQuestionUrl(questionId)).setWechatData(WechatUtil.getInviteMessageWechatData(bpId, name, question, tagName));
            TEliteMessageStrategy strategy = new TEliteMessageStrategy().setTimePeriodType(TEliteMessageTimePeriodType.DAY_TIME).setFrequenceType(TEliteMessageFrequenceType.DAY)
                    .setFrequenceValue(Constants.EXPERT_TEAM_DEFAULT_NUM);
            new Thread(() -> {
                Future<Boolean> result = EliteAsyncTaskPool.submitTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.MEDIUM, messageData, strategy,
                        EliteMessageTargetType.SINGLE, BpType.Elite_User, new ArrayList<Long>(){{add(bpId);}}));
                try {
                    if (result.get()) {
                        eliteAdapter.addExpertNewPush(bpId, questionId);
                    }
                } catch (Exception ex) {
                    log.error("", ex);
                }
            }
            ).start();
            } catch (Exception e) {
              log.error("", e);
              return ResponseJSON.getErrorInteralErrorJSON().toString();
            }
       return resJSON.toString();
   }
   
   //用于更新和建立索引
   @ResponseBody
   @RequestMapping(value = "index-data", produces = "application/json;charset=utf-8")
   public String getById(@RequestParam(value = "id") Long bpId) {
       JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
       if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParametersErrorJSON().toString();
       try{
           TEliteExpertTeam expertTeam = eliteAdapter.getExpertTeamByBpId(bpId);
           
           if (null != expertTeam) {
//               JsonConfig config = new JsonConfig();
//               config.setExcludes(EXCLUDES);
//               JSONObject data = JSONObject.fromObject(expertTeam, config);
//               log.info("index elite-expert : " + data);
//               resJSON.put("data", data.toString());
               IndexExpertTeamBean bean = new IndexExpertTeamBean();
               bean.setBpId(expertTeam.getBpId());
               bean.setPushNum(expertTeam.getPushNum());
               bean.setAnsweredNum(expertTeam.getAnsweredNum());
               bean.setScore(expertTeam.getScore());
               bean.setIdentity(expertTeam.getIdentity());
               bean.setLastPushTime(expertTeam.getLastPushTime());
               bean.setLastAnsweredTime(expertTeam.getLastAnsweredTime());
               bean.setTeam(expertTeam.getTeam());
               JSONObject data = JSONObject.fromObject(bean);
               log.info("index elite-expert : " + data);
               resJSON.put("data", data);
                 
           } else {
               return ResponseJSON.getErrorInteralErrorJSON().toString();
           }
       } catch (Exception e) {
           log.info("", e);
       }
       return resJSON.toString();
   }
}
