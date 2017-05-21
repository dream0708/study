package com.sohu.bp.elite.api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.api.dao.EliteDataDao;
import com.sohu.bp.elite.api.service.EliteDataService;

import net.sf.json.JSONObject;

public class EliteDataServiceImpl implements EliteDataService{
    private static final Logger log = LoggerFactory.getLogger(EliteDataServiceImpl.class);
    private EliteDataDao dataDao;
    
    public void setDataDao(EliteDataDao dataDao) {
        this.dataDao = dataDao;
    }
    
    @Override
    public JSONObject getAppFocus() {
       return dataDao.getAppFocus();
    }

    @Override
    public void saveAppFoucs(JSONObject focusData) {
        dataDao.saveAppFoucs(focusData);
    }

}
