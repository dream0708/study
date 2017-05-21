package com.sohu.bp.elite.action.bean.feature;

import java.io.Serializable;
import java.util.List;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

//app所有的feature的包装类
public class EliteFeatureAppWrapperBean implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6315960759381943033L;
    private EliteHeadFragment headFragment;
    private List<EliteFeatureItemBean> subjects;
    private List<EliteFeatureItemBean> columns;
    private List<EliteFeatureItemBean> topics;
    private List<UserDetailDisplayBean> users;
    
    public EliteHeadFragment getHeadFragment() {
        return headFragment;
    }
    public EliteFeatureAppWrapperBean setHeadFragment(EliteHeadFragment headFragment) {
        this.headFragment = headFragment;
        return this;
    }
    public List<EliteFeatureItemBean> getSubjects() {
        return subjects;
    }
    public EliteFeatureAppWrapperBean setSubjects(List<EliteFeatureItemBean> subjects) {
        this.subjects = subjects;
        return this;
    }
    public List<EliteFeatureItemBean> getColumns() {
        return columns;
    }
    public EliteFeatureAppWrapperBean setColumns(List<EliteFeatureItemBean> columns) {
        this.columns = columns;
        return this;
    }
    public List<EliteFeatureItemBean> getTopics() {
        return topics;
    }
    public EliteFeatureAppWrapperBean setTopics(List<EliteFeatureItemBean> topics) {
        this.topics = topics;
        return this;
    }
	public List<UserDetailDisplayBean> getUsers() {
		return users;
	}
	public EliteFeatureAppWrapperBean setUsers(List<UserDetailDisplayBean> users) {
		this.users = users;
		return this;
	}
  
}
