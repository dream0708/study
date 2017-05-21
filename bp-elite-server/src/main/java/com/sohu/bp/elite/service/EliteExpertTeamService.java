package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteExpertTeam;
/**
 * 专家团service类
 * @author zhijungou
 * 2017年2月23日
 */
public interface EliteExpertTeamService {
    /**
     * 保存专家
     * @param expert
     * @return
     */
    Long save(EliteExpertTeam expert);
    /**
     * 更新专家
     * @param expert
     * @return
     */
    boolean update(EliteExpertTeam expert);
    /**
     * 获取单个专家
     * @param bpId
     * @return
     */
    EliteExpertTeam get(Long bpId);
    /**
     * 批量获取专家
     * @param ids
     * @return
     */
    List<EliteExpertTeam> getBatchExperts(List<Long> ids);
    /**
     * 添加专家推送问题
     * @param bpId
     * @param questionId
     * @return
     */
    boolean addNewMessage(Long bpId, Long questionId);
    /**
     * 批量添加专家推送问题
     * @param bpIds
     * @param questionId
     * @return
     */
    boolean addBatchNewMessage(List<Long> bpIds, Long questionId);
    /**
     * 添加专家回答问题
     * @param bpId
     * @param questionId
     * @return
     */
    boolean addNewAnswered(Long bpId, Long questionId);
    /**
     * 根据sortType来获取专家团信息
     * @param from
     * @param count
     * @param sortType
     * @return
     */
    List<EliteExpertTeam> getExpertTeamsBySortField(Integer start, Integer count, String sortField);
    /**
     * 获得默认的专家信息
     * @param bpId
     * @return
     */
    EliteExpertTeam getDefaultEliteEpxertTeam(Long bpId);
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
     * 获取专家申请标签
     * @return
     */
    List<Integer> getExpertTagIds();
}
