package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.persistence.EliteAnswerByquestionid;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public interface EliteAnswerByQuestionIdDao {

    /**
     * 插入新数据到关联表
     * @param answerByquestionid
     * @return
     */
    Long save(EliteAnswerByquestionid answerByquestionid);

    /**
     * 根据问题id和回答id得到关联数据
     * @param questionId
     * @param answerId
     * @return
     */
    EliteAnswerByquestionid get(Long questionId, Long answerId);

    /**
     * 更新关联数据
     * @param answerByquestionid
     */
    boolean update(EliteAnswerByquestionid answerByquestionid);

    /**
     * 根据问题 id 获取所有回答id
     * 不包括删除状态的数据
     * @param questionId
     * @return
     */
    List<Long> getAnswerIds(Long questionId);

    /**
     * 获取问题某些状态的回答
     * @param questionId
     * @param statusList
     * @return
     */
    List<Long> getQuestionAnswersByStatus(Long questionId, List<Integer> statusList);

    int getQuestionAnswerNumByStatus(Long questionId, List<Integer> statusList);
}
