package com.sohu.bp.elite.consumer.listener;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.consumer.enums.EliteMessageTargetType;
import com.sohu.bp.elite.consumer.enums.NotifyType;
import com.sohu.bp.elite.consumer.service.UserInfoService;
import com.sohu.bp.elite.consumer.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.consumer.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.consumer.util.ToolUtil;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.model.*;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nicholastang on 2017/1/3.
 */
public class ZombieListener extends KafkaListener{
    private Logger logger = LoggerFactory.getLogger(ZombieListener.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();

    private UserInfoService userInfoService;

    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public void init(){

        this.setTaskHandler(new Function<String, Boolean>(){

            @Override
            public Boolean apply(String msg) {
                logger.info("[ZOMBIE]start to consume zombie message");
                if(StringUtils.isBlank(msg))
                    return true;
                logger.info("[ZOMBIE]read msg="+msg);
                try {
                    JSONObject msgJSON = JSONObject.fromObject(msg);
                    Integer notifyType = msgJSON.getInt("notifyType");
                    Long fansId = msgJSON.getLong("bpId");
                    UserInfo userInfo = userInfoService.getUserInfoByBpid(fansId);
                    try {
                        TEliteUser eliteUser = eliteAdapter.getUserByBpId(fansId);
                    } catch (TException e) {
                        TEliteUser user = new TEliteUser();
                        user.setBpId(fansId).setFirstLoginTime(new Date().getTime()).setLastLoginTime(new Date().getTime()).setStatus(1).setFirstLogin(AgentSource.CRAWL.getValue());
                        eliteAdapter.insertUser(user);
                    }

                    if (null != notifyType && NotifyType.valueMap.containsKey(notifyType)) {
                        NotifyType notifyTypeEnum = (NotifyType) NotifyType.valueMap.get(notifyType);
                        switch (notifyTypeEnum) {
                            case ADD_COMMENT:
                                break;
                            case ADD_FAN_FOR_PERSON:
                                if (msgJSON.getInt("generatorType") == BpInteractionTargetType.ELITE_USER.getValue()) {
                                    eliteAdapter.followPeople(msgJSON.getLong("generatorId"), fansId, 1L, 1, System.currentTimeMillis(), true);
                                }
                                break;
                            case ADD_FAN_FOR_QUESITON:
                                if (msgJSON.getInt("generatorType") == BpInteractionTargetType.ELITE_QUESTION.getValue()) {
                                    eliteAdapter.followQuestion(msgJSON.getLong("generatorId"), fansId, 1L, 1, System.currentTimeMillis(), true, true);
                                }
                                break;
                            case ADD_LIKE_FOR_ANSWER:
                                if (msgJSON.getInt("generatorType") == BpInteractionTargetType.ELITE_ANSWER.getValue()) {
                                    eliteAdapter.likeAnswer(msgJSON.getLong("generatorId"), fansId, 1L, 1, System.currentTimeMillis(), true, true);
                                }
                                break;
                            case ADD_LIKE_FOR_COMMENT:
                                //TODO 赞评论
                                break;
                            case ADD_FAVORITE_FOR_ANSWER:
                                if (msgJSON.getInt("generatorType") == BpInteractionTargetType.ELITE_ANSWER.getValue()) {
                                    eliteAdapter.favoriteAnswer(msgJSON.getLong("generatorId"), fansId, 1L, 1, System.currentTimeMillis(), true, true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("", e);
                    return false;
                }

                return true;

            }
        });

        //开始运行监听
        this.myRun();
    }
}
