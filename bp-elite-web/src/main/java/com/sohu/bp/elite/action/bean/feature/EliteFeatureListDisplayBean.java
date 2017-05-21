package com.sohu.bp.elite.action.bean.feature;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.constants.Constants;

/**
 * 用于featureItemBean list的场景
 * @author zhijungou
 * 2017年5月16日
 */
public class EliteFeatureListDisplayBean extends PageBean{
    
    List<EliteFeatureItemBean> featureList;
    
    public EliteFeatureListDisplayBean() {
        this.setPageSize(Constants.DEFAULT_PAGE_SIZE);
    }
    
    public List<EliteFeatureItemBean> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<EliteFeatureItemBean> featureList) {
        this.featureList = featureList;
    }
}
