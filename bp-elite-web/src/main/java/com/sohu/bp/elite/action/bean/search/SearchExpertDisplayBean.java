package com.sohu.bp.elite.action.bean.search;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 用于专家筛选
 * @author zhijungou
 * 2017年4月17日
 */
public class SearchExpertDisplayBean extends PageBean{
    private Integer tagId;
    private List<UserDetailDisplayBean> userList;
    public Integer getTagId() {
        return tagId;
    }
    public SearchExpertDisplayBean setTagId(Integer tagId) {
        this.tagId = tagId;
        return this;
    }
    public List<UserDetailDisplayBean> getUserList() {
        return userList;
    }
    public SearchExpertDisplayBean setUserList(List<UserDetailDisplayBean> userList) {
        this.userList = userList;
        return this;
    }
    
    
}
