package com.sohu.bp.elite.dao;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteQuestion;

/**
 * @author zhangzhihao
 *         2016/7/8
 */
public interface EliteQuestionDao {

    /**
     * 新增加一个问题
     * @param question
     * @return
     */
    Long save(EliteQuestion question);

    /**
     * 更新问题
     * @param question
     */
    boolean update(EliteQuestion question);

    /**
     * 根据id 获取问题
     * @param questionId
     * @return
     */
    EliteQuestion get(Long questionId);

    /**
     * 获取待审问题列表
     * @param start
     * @param count
     * @return
     */
    ListResult getAuditingQuestions(Integer start, Integer count);

}
