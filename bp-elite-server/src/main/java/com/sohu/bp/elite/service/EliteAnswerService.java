package com.sohu.bp.elite.service;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteAnswer;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public interface EliteAnswerService {

    /**
     * 保存新回答
     * @param answer
     * @return
     */
    Long insert(EliteAnswer answer, boolean securityFlag);

    /**
     * 更新回答
     * @param answer
     */
    boolean update(EliteAnswer answer);

    /**
     * 根据回答id 获取回答信息
     * @param answerId
     * @return
     */
    EliteAnswer getById(Long answerId);

    /**
     * 根据问题id 获取所有回答信息
     * @param questionId
     * @return
     */
    List<EliteAnswer> getAnswersByQuestionId(Long questionId);

    /**
     * 获取问题下某些状态值的回答
     * @param questionId
     * @param statusList
     * @return
     */
    List<EliteAnswer> getQuestionAnswersByStatus(Long questionId, List<Integer> statusList);

    int getQuestionAnswerNumByStatus(Long questionId, List<Integer> statusList);

    /**
     * 获取用户下符合某些状态的回答
     * @param bpId
     * @param statusList
     * @return
     */
    List<EliteAnswer> getUserAnswersByStatus(Long bpId, List<Integer> statusList);

    int getUserAnswerNumByStatus(Long bpId, List<Integer> statusList);

    /**
     * 根据bp id 获取所有回答信息
     * @param bpId
     * @return
     */
    List<EliteAnswer> getAnswersByBpId(Long bpId);

    /**
     *
     * 回答审核接口
     *
     */
    ListResult getAuditingAnswers(Integer start, Integer count);
    boolean batchAudit(List<Long> passAnswerIds, List<Long> rejectedAnswerIds);
    boolean pass(Long answerId);
    boolean reject(Long answerId);

    boolean userDelete(Long answerId);
    boolean userDelete(List<Long> answerIds);

    boolean sysDelete(Long answerId);
    boolean sysDelete(List<Long> answerIds);
}
