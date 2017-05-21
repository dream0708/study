package com.sohu.bp.elite.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sohu.bp.elite.model.TEliteExpertTeam;
/**
 * 专家团选择算法
 * 采用strategy pattern
 * 该类为抽象策略类
 * @author zhijungou
 * 2017年2月23日
 */
public interface EliteExpertTeamStrategy {
    /**
     * 
     * @param ids 专家id列表
     * @param tagMap 每个bpId对应的匹配标签
     * @param expertMap bpId-TEliteExpertTeam
     * @return
     */
    List<Long> getSelectedExpertTeams(Set<Long> ids, Map<Long, String> tagMap, Map<Long, TEliteExpertTeam> expertMap);
}
