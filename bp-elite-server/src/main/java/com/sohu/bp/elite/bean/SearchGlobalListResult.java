package com.sohu.bp.elite.bean;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhihao
 *         2016/8/23
 */
public class SearchGlobalListResult {
    private Map<String, Integer> totalCounts;
    private List<String> rank;
    private List<EliteSearchResultBean> questions;
    private List<EliteSearchResultBean> answers;
    private List<EliteSearchResultBean> users;

    public Map<String, Integer> getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Map<String, Integer> totalCounts) {
        this.totalCounts = totalCounts;
    }

    public List<String> getRank() {
        return rank;
    }

    public void setRank(List<String> rank) {
        this.rank = rank;
    }

    public List<EliteSearchResultBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<EliteSearchResultBean> questions) {
        this.questions = questions;
    }

    public List<EliteSearchResultBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<EliteSearchResultBean> answers) {
        this.answers = answers;
    }

    public List<EliteSearchResultBean> getUsers() {
        return users;
    }

    public void setUsers(List<EliteSearchResultBean> users) {
        this.users = users;
    }
}
