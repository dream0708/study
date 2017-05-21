package com.sohu.bp.elite.service.web;

import java.util.List;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.model.TColumnListResult;
import com.sohu.bp.elite.model.TEliteColumn;

public interface ColumnService {
	TEliteColumn getIndexColumn();
	TEliteColumn getColumnById(Long columnId);
	TColumnListResult getColumns(int start, int count);
	List<SimpleFeedItemBean> getQuestionsByColumnId(Long columnId, int start, int count);
	Long getQuestionNum(Long columnId);
}
