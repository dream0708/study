package com.sohu.bp.elite.api.dao;

import net.sf.json.JSONObject;

public interface EliteDataDao {
    /**
     * 获取焦点图数据
     * @return
     */
    JSONObject getAppFocus();
    /**
     * 保存焦点图数据
     * @param focusData
     */
    void saveAppFoucs(JSONObject focusData);
}
