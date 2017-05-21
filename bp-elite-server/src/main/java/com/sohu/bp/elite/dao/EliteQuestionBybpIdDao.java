package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.persistence.EliteQuestionBybpid;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/8
 */
public interface EliteQuestionBybpIdDao {

    /**
     * 保存关联表
     * @param questionBybpid
     * @return
     */
    Long save(EliteQuestionBybpid questionBybpid);

    /**
     * 根据bp id 和问题id得到关联数据
     * @param bpId
     * @param questionId
     * @return
     */
    EliteQuestionBybpid get(Long bpId, Long questionId);

    /**
     * 更新关联数据
     * @param questionBybpid
     */
    boolean update(EliteQuestionBybpid questionBybpid);

    /**
     * 根据bp id 得到所有问题id
     * 不包括删除状态的数据
     * @param bpId
     * @return
     */
    List<Long> getQuestionIds(Long bpId);

    /**
     * 根据bp id 和 状态 获取所有问题id
     * @param bpId
     * @param status
     * @return
     */
    List<Long> getQuestionsIdsByBpIdAndStatus(Long bpId, Integer status);

    /**
     * 获取
     * @param bpId
     * @param statusList
     * @return
     */
    List<Long> getUserQuestionsByStatus(Long bpId, List<Integer> statusList);

    int getUserQuestionNumByStatus(Long bpId, List<Integer> statusList);

}
