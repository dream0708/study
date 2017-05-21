package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteQuestionByspecial;

public interface EliteQuestionBySpecialDao {
	/**
	 * 保存专栏等和问题的关联表
	 * @param eliteQuestionByspecial
	 * @return
	 */
	Long save(EliteQuestionByspecial eliteQuestionByspecial);
	/**
	 * 根据specialType和specialId来获得对象
	 * @param specialType
	 * @param speicalType
	 * @return
	 */
	EliteQuestionByspecial get(Long specialId, Long questionId);
	/**
	 * 更新关联表
	 * @param eliteQuestionByspecial
	 * @return
	 */
	boolean update(EliteQuestionByspecial eliteQuestionByspecial);
	/**
	 * 根据questionId 和 specialId 来获取对象
	 * @param questionId
	 * @param specialId
	 * @return
	 */
	List<Long> getQuestionsBySpecial(Long specialId, Integer specialType);
}
