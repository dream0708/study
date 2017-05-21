package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteExpertTeam;

/**
 * 
 * @author zhijungou
 * 2017年2月23日
 */
public interface EliteExpertTeamDao {
    /**
     * 保存专家信息
     * @param expert
     * @return
     */
    Long save(EliteExpertTeam expert);
    /**
     * 更新专家信息
     * @param expert
     * @return
     */
    boolean update(EliteExpertTeam expert);
    /**
     * 根据id获取专家信息
     * @param bpId
     * @return
     */
    EliteExpertTeam get(Long bpId);
    /**
     * 更新专家信息，有新的推送
     * @param bpId
     * @param questionId
     * @return
     */
    boolean addNewMessage(Long bpId, Long questionId);
    /**
     * 更新专家信息，回答新的问题
     * @param bpId
     * @param questionId
     * @return
     */
    boolean addNewAnswered(Long bpId, Long questionId);
    /**
     * 根据sortField来获取经过排序的专家列表
     * @param from
     * @param count
     * @param sortField
     * @return
     */
    List<EliteExpertTeam> getExpertTeamByCondition(Integer start, Integer count, String sortField);
    /**
     * 增加专家申请标签
     * @param tagId
     * @return
     */
    boolean addExpertTag(Integer tagId);
    /**
     * 删除专家申请标签
     * @param tagId
     * @return
     */
    boolean removeExpertTag(Integer tagId);
    /**
     * 获取所有专家申请标签
     * @return
     */
    List<Integer> getExpertTagIds();
}
