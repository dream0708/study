package com.sohu.bp.elite.dao;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteAnswer;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public interface EliteAnswerDao {

    /**
     * 保存新回答
     * @param answer
     * @return
     */
    Long save(EliteAnswer answer);

    /**
     * 更新回答
     * @param answer
     */
    boolean update(EliteAnswer answer);

    /**
     * 根据回答id得到回答
     * @param id
     * @return
     */
    EliteAnswer getById(Long id);

    /**
     * 获取待审回答列表
     * @param start
     * @param count
     * @return
     */
    ListResult getAuditingAnswers(Integer start, Integer count);
}
