package com.sohu.bp.elite.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteExpertTeamInfoDao;
import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;
import com.sohu.bp.elite.service.EliteExpertTeamInfoService;

public class EliteExpertTeamInfoServiceImpl implements EliteExpertTeamInfoService{
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamInfoServiceImpl.class);
    
    private EliteExpertTeamInfoDao eliteExpertTeamInfoDao;
    
    public void setEliteExpertTeamInfoDao(EliteExpertTeamInfoDao eliteExpertTeamInfoDao) {
        this.eliteExpertTeamInfoDao = eliteExpertTeamInfoDao;
    }
    
    @Override
    public Long insert(EliteExpertTeamInfo expertTeamInfo) {
       if (null == expertTeamInfo) return null;
       return eliteExpertTeamInfoDao.insert(expertTeamInfo);
    }

    @Override
    public EliteExpertTeamInfo getById(Long id) {
       if (null == id || id <= 0) return null;
       return eliteExpertTeamInfoDao.getById(id);
    }
    
    @Override
    public Boolean update(EliteExpertTeamInfo expertTeamInfo) {
        if (null == expertTeamInfo || null == expertTeamInfo.getId() || expertTeamInfo.getId() <= 0) return false;
        return eliteExpertTeamInfoDao.update(expertTeamInfo);
    }

    @Override
    public Map<Long, String> getTeamMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        List<EliteExpertTeamInfo> infos = eliteExpertTeamInfoDao.getList();
        if (null != infos && infos.size() > 0) {
            infos.forEach(info -> map.put(info.getId(), info.getTeamName()));
        }
        return map;
    }

    @Override
    public List<EliteExpertTeamInfo> getList() {
        return eliteExpertTeamInfoDao.getList();
    }


}
