package com.sohu.bp.elite.service;

import java.util.List;
import java.util.Map;

import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;

public interface EliteExpertTeamInfoService {
    /**
     * 插入专家分组
     * @param expertTeamInfo
     * @return
     */
    Long insert(EliteExpertTeamInfo expertTeamInfo);
    /**
     * 根据id获取专家
     * @param id
     * @return
     */
    EliteExpertTeamInfo getById(Long id);
    /**
     * 更新专家分组信息
     * @param expertTeamInfo
     * @return
     */
    Boolean update(EliteExpertTeamInfo expertTeamInfo);
    /**
     * 
     * @return
     */
    List<EliteExpertTeamInfo> getList();
    /**
     * 获取专家分组信息
     * @return
     */
    Map<Long, String> getTeamMap();
}
