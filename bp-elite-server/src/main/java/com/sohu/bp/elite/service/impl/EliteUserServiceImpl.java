package com.sohu.bp.elite.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.dao.EliteUserDao;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.enums.SendCloudTemplate;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.persistence.EliteUser;
import com.sohu.bp.elite.service.EliteExpertTeamService;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.service.EliteUserService;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;

/**
 * 
 * @author zhijungou
 * 2016年10月20日
 */
public class EliteUserServiceImpl implements EliteUserService {
	private static final Logger log = LoggerFactory.getLogger(EliteUserServiceImpl.class);

    private EliteUserDao userDao;

    private EliteSearchService searchService;
    
    private NotifyService notifyService;
    
    private EliteExpertTeamService expertTeamService;

    public void setUserDao(EliteUserDao userDao) {
        this.userDao = userDao;
    }

    public void setSearchService(EliteSearchService searchService) {
        this.searchService = searchService;
    }
    
    public void setNotifyService(NotifyService notifyService) {
		this.notifyService = notifyService;
	}
    
    public void setExpertTeamService(EliteExpertTeamService expertTeamService) {
        this.expertTeamService = expertTeamService;
    }

    @Override
    public Long save(EliteUser user) {
        if(user.getFirstLoginTime() == null)
            user.setFirstLoginTime(new Date());
        if(user.getLastLoginTime() == null)
            user.setLastLoginTime(new Date());
        if(user.getStatus() == null || user.getStatus() == 0)
        	user.setStatus(EliteUserStatus.VALID.getValue());
        if(user.getFirstLogin() == null)
        	user.setFirstLogin(AgentSource.UNKNOWN.getValue());
        Long id = userDao.save(user);
        if (Objects.equals(user.getIdentity(), EliteUserIdentity.EXPERT.getValue())) updateExpertTeam(user);
        if(id != null && id > 0){
//            searchService.indexData(id, EliteSearchType.User.getValue(), null, false);
        	user.setId(id);
        	notify(user);
        }
        return id;
    }

    @Override
    public boolean update(EliteUser user) {
        if(user.getLastLoginTime() == null)
            user.setLastLoginTime(new Date());
        EliteUser originUser = get(user.getId());
        if (null == originUser) return false;
        else if (!Objects.equals(originUser.getIdentity(), user.getIdentity())) updateExpertTeam(user);
        boolean result = userDao.update(user);
        if(result){
//            searchService.indexData(user.getId(), EliteSearchType.User.getValue(), null, false);
        	notify(user);
        }
        return result;
    }

    @Override
    public EliteUser get(Long bpId) {
        return userDao.get(bpId);
    }   
    
    public void notify(EliteUser user){
    	if(null != user && user.getId() > 0){
	    	Long objectId = user.getId();
	    	Integer type = EliteNotifyType.ELITE_NOTIFY_INSERT.getValue();
	    	if(null != user.getStatus() && EliteUserStatus.VALID.getValue() == user.getStatus())  type = EliteNotifyType.ELITE_NOTIFY_INSERT.getValue();
	    	else type = EliteNotifyType.ELITE_NOTIFY_DELETE.getValue();
	    	if(notifyService.notify2Statistic(objectId, BpType.Elite_User.getValue(), type)){
	    		log.info("index elite user={} succeeded", new Object[]{user.getId()});
	    	}
    	}
    }

    @Override
    public ListResult getAuditingExperts(Integer start, Integer count) {
        return userDao.getAuditingExperts(start, count);
    }

    @Override
    public ListResult getExperts(Integer start, Integer count) {
        ListResult listResult = userDao.getExperts(start, count);
        if (listResult.getTotal() <= 0) {
            getExpertsByES();
            listResult = userDao.getExperts(start, count);
        }
        return listResult;
    }

    @Override
    public Boolean passExpert(Long bpId) {
        if (null == bpId || bpId <= 0)
            return false;
        if (createUserIfNotExist(bpId, EliteUserIdentity.EXPERT)) {
            TEliteMessageData messageData = new TEliteMessageData();
            String inboxMessageContent = EliteMessageData.EXPERT_REGISTER_PASS.getContent();
            messageData.setInboxMessageDataValue(EliteMessageData.EXPERT_REGISTER_PASS.getValue()).setInboxMessageContent(inboxMessageContent)
            .setSendCloudTemplate(SendCloudTemplate.EXPERT_AUDIT_PASS.getValue()).setSendCloudVariables(new HashMap<String, String>());
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.CELL_AND_INBOX, 
                    bpId, messageData, new TEliteMessageStrategy().setFrequenceType(TEliteMessageFrequenceType.UNLIMIT)));
            return true;
        }
        return false;
    }

    @Override
    public Boolean rejectExpert(Long bpId, String reason) {
        if (null == bpId || bpId <= 0)
            return false;
        if (createUserIfNotExist(bpId, EliteUserIdentity.NORMAL)) {
            TEliteMessageData messageData = new TEliteMessageData();
            String inboxMessageContent = EliteMessageData.EXPERT_REGISTER_REJECT.getContent().replace(Constants.MESSAGE_DATA_REASON, reason);
            messageData.setInboxMessageDataValue(EliteMessageData.EXPERT_REGISTER_REJECT.getValue()).setInboxMessageContent(inboxMessageContent)
            .setSendCloudTemplate(SendCloudTemplate.EXPERT_AUDIT_REJECT.getValue()).setSendCloudVariables(new HashMap<String, String>());
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.CELL_AND_INBOX, 
                    bpId, messageData, new TEliteMessageStrategy().setFrequenceType(TEliteMessageFrequenceType.UNLIMIT)));
            return true;
        }
        return false;
    }
   
    @Override
    public void reloadExpertsCache() {
        userDao.removeExpertsCache();
        getExpertsByES();
    }
    
    @Deprecated
    @Override
    public Boolean addExpertAuditing(Long bpId) {
        return true;
    }
    
    private void getExpertsByES() {
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setIdentity(EliteUserIdentity.EXPERT.getValue()).setStatus(EliteUserStatus.VALID.getValue()).setFrom(0).setCount(Integer.MAX_VALUE);
        ListResult searchResult = searchService.searchUser(condition);
        if (null != searchResult.getEntities()) {
            searchResult.getEntities().forEach(id -> userDao.addExpertCache((Long)id));
        }
    }

    @Override
    public Boolean batchAudit(List<Long> passIds, List<Long> rejectIds, String reason) {
        if (null != passIds && passIds.size() > 0) passIds.forEach(this::passExpert);
        if (null != rejectIds && rejectIds.size() > 0) rejectIds.forEach(id -> rejectExpert(id, reason));
        return true;
    }

    @Override
    public Long getExpertsNum() {
        return userDao.getExpertsNum();
    }

    @Override
    public EliteUser getExpert(Long bpId) {
        if (null == bpId || bpId <= 0) return null;
        return  userDao.getExpert(bpId);
    }
    
    //用于审核通过和驳回
    private boolean createUserIfNotExist(Long bpId, EliteUserIdentity identity) {
        if (null == bpId || bpId <= 0) return false;
        EliteUser user = get(bpId);
        if (null == user) {
            log.info("user bpId = {} doesn't in database, create user!", bpId);
            user = new EliteUser().setStatus(EliteUserStatus.VALID.getValue()).setFirstLoginTime(new Date()).setIdentity(identity.getValue()).setFirstLogin(AgentSource.UNKNOWN.getValue());
            return save(user) > 0;
        }
        if (!Objects.equals(user.getIdentity(), identity.getValue())) {
            user.setIdentity(identity.getValue());
            return update(user); 
        }
        return false;
    }
    
    //用于更新elite expert team
    private void updateExpertTeam(EliteUser user) {
        Long bpId = user.getId();
        EliteExpertTeam expertTeam = expertTeamService.get(bpId);
        if (Objects.equals(user.getIdentity(), EliteUserIdentity.EXPERT.getValue())) {
            if (null == expertTeam) {
                expertTeam = expertTeamService.getDefaultEliteEpxertTeam(bpId);
                expertTeamService.save(expertTeam);
            } else if (!Objects.equals(expertTeam.getIdentity(), EliteUserIdentity.EXPERT.getValue())) {
                expertTeam.setIdentity(EliteUserIdentity.EXPERT.getValue());
                expertTeamService.update(expertTeam);
            }
        }
        if (Objects.equals(user.getIdentity(), EliteUserIdentity.NORMAL.getValue())) {
            if (null != expertTeam) {
                expertTeam.setIdentity(EliteUserIdentity.NORMAL.getValue());
                expertTeamService.update(expertTeam);
            }
        }
    }
    
}
