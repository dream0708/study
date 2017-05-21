package com.sohu.bp.elite.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.dao.EliteExpertTeamDao;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.service.EliteExpertTeamService;
import com.sohu.bp.elite.service.NotifyService;

public class EliteExpertTeamServiceImpl implements EliteExpertTeamService{
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamServiceImpl.class);
    private static final List<String> SORT_FIELD_LIST = new ArrayList<String>(){{
       add("pushNum");add("answeredNum");add("score");add("lastPushTime");add("lastAnsweredTime"); 
    }};
    private EliteExpertTeamDao eliteExpertTeamDao;
    private NotifyService notifyService;
    
    public void setEliteExpertTeamDao(EliteExpertTeamDao eliteExpertTeamDao) {
        this.eliteExpertTeamDao = eliteExpertTeamDao;
    }
    
    public void setNotifyService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }
    
    @Override
    public Long save(EliteExpertTeam expert) {
        Long id = -1L;
        if (null == expert || null == expert.getId() || expert.getId() <= 0) return id;
        id = eliteExpertTeamDao.save(expert);
        if (null != id && id > 0) {
            notify(expert.getId());
        }
        return id;
    }

    @Override
    public boolean update(EliteExpertTeam expert) {
        if (null == expert || null == expert.getId() || expert.getId() <= 0) return false;
        boolean flag = eliteExpertTeamDao.update(expert);
        if (flag) {
            notify(expert.getId());
        }
        return flag;
    }

    @Override
    public EliteExpertTeam get(Long bpId) {
        if (null == bpId || bpId <= 0) return null;
        return eliteExpertTeamDao.get(bpId);
    }

    @Override
    public List<EliteExpertTeam> getBatchExperts(List<Long> ids) {
        List<EliteExpertTeam> experts = new ArrayList<EliteExpertTeam>();
        if (null == ids) return experts;
        for (Long id : ids) {
            EliteExpertTeam expert = get(id);
            experts.add(expert);
        }
        return experts;
    }

    @Override
    public boolean addNewMessage(Long bpId, Long questionId) {
        if (null == bpId || bpId <= 0 || null == questionId || questionId <= 0) return false;
        boolean flag = eliteExpertTeamDao.addNewMessage(bpId, questionId);
        if (flag) {
            notify(bpId);
        }
        return flag;
    }

    @Override
    public boolean addBatchNewMessage(List<Long> bpIds, Long questionId) {
        boolean flag = true;
        if (null == bpIds || null == questionId || questionId <= 0) return false;
        for (Long bpId : bpIds) {
            if (!addNewMessage(bpId, questionId)) flag = false;
        }
        return flag;
    }

    @Override
    public boolean addNewAnswered(Long bpId, Long questionId) {
       if (null == bpId || bpId <= 0 || null == questionId || questionId <= 0) return false;
       boolean flag = eliteExpertTeamDao.addNewAnswered(bpId, questionId);
       if (flag) {
           notify(bpId);
       }
       return flag;
    }
    @Deprecated
    @Override
    public List<EliteExpertTeam> getExpertTeamsBySortField(Integer start, Integer count, String sortField) {
        if (null == start || null == count || !SORT_FIELD_LIST.contains(sortField)) return new ArrayList<EliteExpertTeam>();
        return eliteExpertTeamDao.getExpertTeamByCondition(start, count, sortField);
    }
    
    @Override
    public EliteExpertTeam getDefaultEliteEpxertTeam(Long bpId) {
        EliteExpertTeam expert =  new EliteExpertTeam().setAnsweredId("").setAnsweredNum(0).setId(bpId).setLastAnsweredTime(new Date()).setLastPushTime(new Date())
                .setIdentity(EliteUserIdentity.EXPERT.getValue()).setPushNum(0).setUnansweredId("").setScore(Constants.ELITE_EXPERT_TEAM_INIT_SCORE);
        return expert;
    }
    
    private void notify(Long bpId) {
        if (null == bpId || bpId <= 0) return;
        boolean result = notifyService.notify2Statistic(bpId, BpType.Elite_Expert_Team.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        if (result) log.info("index expert team id = {}", bpId);
    }

    @Override
    public boolean addExpertTag(Integer tagId) {
       if (null == tagId || tagId < 0) return false;
       return eliteExpertTeamDao.addExpertTag(tagId);
    }

    @Override
    public boolean removeExpertTag(Integer tagId) {
        if (null == tagId || tagId < 0) return false;
        return eliteExpertTeamDao.removeExpertTag(tagId);
    }

    @Override
    public List<Integer> getExpertTagIds() {
        return eliteExpertTeamDao.getExpertTagIds();
    }

}
