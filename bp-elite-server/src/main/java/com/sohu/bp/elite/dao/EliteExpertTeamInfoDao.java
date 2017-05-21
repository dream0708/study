package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;

/**
 * 专家团分组数据Dao
 * @author zhijungou
 * 2017年4月12日
 */
public interface EliteExpertTeamInfoDao {
    /**
     * 插入
     * @param expertTeamInfo
     * @return
     */
    Long insert(EliteExpertTeamInfo expertTeamInfo);
    /**
     * 更新
     * @param expertTeamInfo
     * @return
     */
    boolean update(EliteExpertTeamInfo expertTeamInfo);
    /**
     * 获取用户
     * @param id
     * @return
     */
    EliteExpertTeamInfo getById(Long id);
    /**
     * 获取有效的分组信息
     * @return
     */
    List<EliteExpertTeamInfo> getList();
}
