package com.sohu.bp.elite.service;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteQuestion;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public interface EliteQuestionService {

    /**
     * 保存新问题
     * @param question
     * @return
     */
    Long insert(EliteQuestion question, boolean securityFlag);

    /**
     * 更新问题
     * @param question
     */
    boolean update(EliteQuestion question);

    /**
     * 根据问题id 获取问题信息
     * @param questionId
     * @return
     */
    EliteQuestion getById(Long questionId);

    /**
     * 根据问题id批量获取问题信息
     * @param questionIds
     * @return
     */
    ListResult getByIds(List<Long> questionIds);

    /**
     * 批量审核
     * 通过 和 驳回
     * @param passQuestionIds
     * @param rejectedQuestionIds
     * @return
     */
    Boolean batchAudit(List<Long> passQuestionIds, List<Long> rejectedQuestionIds);

    /**
     * 审核通过
     * @param questionId
     * @return
     */
    Boolean pass(Long questionId);

    /**
     * 审核驳回
     * @param questionId
     * @return
     */
    Boolean reject(Long questionId);

    /**
     * 批量系统删除
     * @param questionIds
     * @return
     */
    Boolean sysDelete(List<Long> questionIds);

    /**
     * 批量用户删除
     * @param questionIds
     * @return
     */
    Boolean userDelete(List<Long> questionIds);

    /**
     * 系统删除
     * @param questionId
     * @return
     */
    Boolean sysDelete(Long questionId);

    /**
     * 用户删除
     * @param questionId
     * @return
     */
    Boolean userDelete(Long questionId);

    /**
     * 获取待审列表
     * @param start
     * @param count
     * @return
     */
    ListResult getAuditingQuestions(Integer start, Integer count);

    /**
     * 根据bp id 获取 所有问题信息
     * 不包括删除状态数据
     * @param bpId
     * @return
     */
    List<EliteQuestion> getQuestionsByBpId(Long bpId);
    /**
     * 根据specialId和speicalType来获取问题id
     * @param specialId
     * @param specialType
     * @return
     */
    List<EliteQuestion> getQuestionsBySpeical(Long specialId, Integer specialType);

    List<EliteQuestion> getUserQuestionsByStatus(Long bpId, List<Integer> statusList);

    int getUserQuestionNumByStatus(Long bpId, List<Integer> statusList);
}
