package com.sohu.bp.elite.data.statistic.bean;

import org.apache.velocity.tools.generic.NumberTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholastang on 2016/12/9.
 */
public class OverallDailyReportBean {
    private NumberTool number = new NumberTool();
    private String TIME_STR = "";
    private List<SourceBean> SOURCE_LIST = new ArrayList<>();
    private List<ReferBean> REFER_LIST = new ArrayList<>();
    private ReferBean WECHAT = new ReferBean();
    private ReferBean SOHUNEWS = new ReferBean();
    private ReferBean UCAPP = new ReferBean();
    private List<PageBean> POP_PAGE_LIST = new ArrayList<>();
    private List<PageBean> POP_ARTICLE_PAGE_LIST = new ArrayList<>();

    private List<SourceBean> ASK_SOURCE_LIST = new ArrayList<>();

    public NumberTool getNumber() {
        return number;
    }

    public void setNumber(NumberTool number) {
        this.number = number;
    }

    public String getTIME_STR() {
        return TIME_STR;
    }

    public void setTIME_STR(String TIME_STR) {
        this.TIME_STR = TIME_STR;
    }

    public List<SourceBean> getSOURCE_LIST() {
        return SOURCE_LIST;
    }

    public void setSOURCE_LIST(List<SourceBean> SOURCE_LIST) {
        this.SOURCE_LIST = SOURCE_LIST;
    }

    public List<ReferBean> getREFER_LIST() {
        return REFER_LIST;
    }

    public void setREFER_LIST(List<ReferBean> REFER_LIST) {
        this.REFER_LIST = REFER_LIST;
    }

    public ReferBean getWECHAT() {
        return WECHAT;
    }

    public void setWECHAT(ReferBean WECHAT) {
        this.WECHAT = WECHAT;
    }

    public ReferBean getSOHUNEWS() {
        return SOHUNEWS;
    }

    public void setSOHUNEWS(ReferBean SOHUNEWS) {
        this.SOHUNEWS = SOHUNEWS;
    }

    public List<PageBean> getPOP_PAGE_LIST() {
        return POP_PAGE_LIST;
    }

    public void setPOP_PAGE_LIST(List<PageBean> POP_PAGE_LIST) {
        this.POP_PAGE_LIST = POP_PAGE_LIST;
    }

    public List<PageBean> getPOP_ARTICLE_PAGE_LIST() {
        return POP_ARTICLE_PAGE_LIST;
    }

    public void setPOP_ARTICLE_PAGE_LIST(List<PageBean> POP_ARTICLE_PAGE_LIST) {
        this.POP_ARTICLE_PAGE_LIST = POP_ARTICLE_PAGE_LIST;
    }

    public List<SourceBean> getASK_SOURCE_LIST() {
        return ASK_SOURCE_LIST;
    }

    public void setASK_SOURCE_LIST(List<SourceBean> ASK_SOURCE_LIST) {
        this.ASK_SOURCE_LIST = ASK_SOURCE_LIST;
    }

    public ReferBean getUCAPP() {
        return UCAPP;
    }

    public void setUCAPP(ReferBean UCAPP) {
        this.UCAPP = UCAPP;
    }
}
