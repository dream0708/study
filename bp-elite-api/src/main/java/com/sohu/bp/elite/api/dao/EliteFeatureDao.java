package com.sohu.bp.elite.api.dao;

import java.util.List;
import java.util.Map;

public interface EliteFeatureDao {
	public Boolean updateCache(String key);
	public String getAllUser();
	public List<Long> getInvitedList();
	public Boolean removeInvited(Long id);
	public Boolean addInvited(Long id);
	public List<Long> getEditList();
	public Boolean addEditHistory(Long id);
	/**
	 * 保存焦点图顺序
	 * @param objectId
	 * @param bpType
	 * @param order
	 * @return
	 */
	public Boolean saveFocusOrder(Long objectId, Integer bpType, Integer order);
	/**
	 * 获取焦点图顺序
	 * @param objectIds
	 * @param bpType
	 * @return
	 */
	public List<Integer> getOrdersByIds(List<Long> objectIds, Integer bpType);
}
