package com.sohu.bp.elite.action.bean.tag;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/9/8
 */
public class TagRelatedItemDisplayBean extends PageBean implements Serializable{
    private static final long serialVersionUID = 1785255850603403397L;
    private TagItemBean tag;
    private List<SimpleFeedItemBean> feedItemList;
    private PageWrapperBean pageWrapper;

    public TagItemBean getTag() {
        return tag;
    }

    public void setTag(TagItemBean tag) {
        this.tag = tag;
    }

    public List<SimpleFeedItemBean> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
        this.feedItemList = feedItemList;
    }

    public PageWrapperBean getPageWrapper() {
        return pageWrapper;
    }

    public void setPageWrapper(PageWrapperBean pageWrapper) {
        this.pageWrapper = pageWrapper;
    }
}
