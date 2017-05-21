package com.sohu.bp.elite.data.statistic.bean;

import org.apache.velocity.tools.generic.NumberTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class DailyReportBean {
    private NumberTool number = new NumberTool();
    private String TIME_STR = "";
    private List<SourceBean> ARTICLE_SOURCE_LIST = new ArrayList<>();
    private List<SourceBean> SOURCE_LIST = new ArrayList<>();
    private Integer PC_OVERALL_TOTAL_PV = 0;
    private Integer PC_OVERALL_TOTAL_UV = 0;
    private Float PC_OVERALL_TOTAL_PV_DOD = (float)0;
    private Float PC_OVERALL_TOTAL_UV_DOD = (float)0;
    private Integer PC_OVERALL_INDEX_PV = 0;
    private Integer PC_OVERALL_INDEX_UV = 0;
    private Float PC_OVERALL_INDEX_PV_DOD = (float)0;
    private Float PC_OVERALL_INDEX_UV_DOD = (float)0;

    private Integer MOBILE_OVERALL_TOTAL_PV = 0;
    private Integer MOBILE_OVERALL_TOTAL_UV = 0;
    private Float MOBILE_OVERALL_TOTAL_PV_DOD = (float)0;
    private Float MOBILE_OVERALL_TOTAL_UV_DOD = (float)0;
    private Integer MOBILE_OVERALL_INDEX_PV = 0;
    private Integer MOBILE_OVERALL_INDEX_UV = 0;
    private Float MOBILE_OVERALL_INDEX_PV_DOD = (float)0;
    private Float MOBILE_OVERALL_INDEX_UV_DOD = (float)0;

    private Integer PC_SUBJECT_PV = 0;
    private Integer PC_SUBJECT_UV = 0;
    private Integer PC_SUBJECT_STAY = 0;
    private Float PC_SUBJECT_PV_DOD = (float)0;
    private Float PC_SUBJECT_UV_DOD = (float)0;
    private Integer PC_SUBJECT_LIST_PV = 0;
    private Integer PC_SUBJECT_LIST_UV = 0;

    private Integer MOBILE_SUBJECT_PV = 0;
    private Integer MOBILE_SUBJECT_UV = 0;
    private Integer MOBILE_SUBJECT_STAY = 0;
    private Float MOBILE_SUBJECT_PV_DOD = (float)0;
    private Float MOBILE_SUBJECT_UV_DOD = (float)0;
    private Integer MOBILE_SUBJECT_LIST_PV = 0;
    private Integer MOBILE_SUBJECT_LIST_UV = 0;

    private List<SubjectItemBean> SUBJECT_ITEM_LIST = new ArrayList<SubjectItemBean>();

    private Integer PC_TOPIC_PV = 0;
    private Integer PC_TOPIC_UV = 0;
    private Integer PC_TOPIC_STAY = 0;
    private Float PC_TOPIC_PV_DOD = (float)0;
    private Float PC_TOPIC_UV_DOD = (float)0;

    private Integer MOBILE_TOPIC_PV = 0;
    private Integer MOBILE_TOPIC_UV = 0;
    private Integer MOBILE_TOPIC_STAY = 0;
    private Float MOBILE_TOPIC_PV_DOD = (float)0;
    private Float MOBILE_TOPIC_UV_DOD = (float)0;

    private Integer PC_COLUMN_PV = 0;
    private Integer PC_COLUMN_UV = 0;
    private Integer PC_COLUMN_STAY = 0;
    private Float PC_COLUMN_PV_DOD = (float)0;
    private Float PC_COLUMN_UV_DOD = (float)0;
    private Integer PC_COLUMN_LIST_PV = 0;
    private Integer PC_COLUMN_LIST_UV = 0;
    private Integer PC_COLUMN_QUESTIONS_PV = 0;
    private Integer PC_COLUMN_QUESTIONS_UV = 0;

    private Integer MOBILE_COLUMN_PV = 0;
    private Integer MOBILE_COLUMN_UV = 0;
    private Integer MOBILE_COLUMN_STAY = 0;
    private Float MOBILE_COLUMN_PV_DOD = (float)0;
    private Float MOBILE_COLUMN_UV_DOD = (float)0;
    private Integer MOBILE_COLUMN_LIST_PV = 0;
    private Integer MOBILE_COLUMN_LIST_UV = 0;
    private Integer MOBILE_COLUMN_QUESTIONS_PV = 0;
    private Integer MOBILE_COLUMN_QUESTIONS_UV = 0;

    private Integer USER_NEW = 0;
    private Integer USER_TOTAL = 0;
    private Integer USER_ACTIVE = 0;
    private Integer QUESTION_SPIDER_NEW = 0;
    private Integer QUESTION_WRITE_NEW = 0;
    private Integer QUESTION_NEW = 0;
    private Integer QUESTION_TOTAL = 0;
    private Integer ANSWER_SPIDER_NEW = 0;
    private Integer ANSWER_WRITE_NEW = 0;
    private Integer ANSWER_NEW = 0;
    private Integer ANSWER_TOTAL = 0;

    private List<UserBean> NEW_USER_LIST = new ArrayList<>();

    private List<ReferBean> REFER_LIST = new ArrayList<>();

    private ReferBean WECHAT = new ReferBean();

    private ReferBean SOHUNEWS = new ReferBean();

    private ReferBean NEWSARTICLE = new ReferBean();

    private ReferBean WIFI = new ReferBean();

    private List<ReferBean> ARTICLE_REFER_LIST = new ArrayList<>();

    private ReferBean ARTICLE_WECHAT = new ReferBean();

    private ReferBean ARTICLE_SOHUNEWS = new ReferBean();

    private List<QuestionBean> POP_QUESTION_LIST = new ArrayList<QuestionBean>();

    private List<PageBean> POP_ARTICLE_PAGE_LIST = new ArrayList<>();

    private List<TagBean> POP_TAG_LIST = new ArrayList<TagBean>();

    private List<UserBean> POP_USER_LIST = new ArrayList<UserBean>();

    private List<WordBean> POP_SEARCHWORD_LIST = new ArrayList<WordBean>();

    private List<GrowQuestionBean> GROW_QUESTION_LIST = new ArrayList<GrowQuestionBean>();

    private List<GrowTagBean> GROW_TAG_LIST = new ArrayList<GrowTagBean>();

    private List<ActiveUserBean> ACTIVE_USER_LIST = new ArrayList<ActiveUserBean>();

    private List<TotalActiveUserBean> TOTAL_ACTIVE_USER_LIST = new ArrayList<TotalActiveUserBean>();

    public Integer getPC_OVERALL_TOTAL_PV() {
        return PC_OVERALL_TOTAL_PV;
    }

    public void setPC_OVERALL_TOTAL_PV(Integer PC_OVERALL_TOTAL_PV) {
        this.PC_OVERALL_TOTAL_PV = PC_OVERALL_TOTAL_PV;
    }

    public Integer getPC_OVERALL_TOTAL_UV() {
        return PC_OVERALL_TOTAL_UV;
    }

    public void setPC_OVERALL_TOTAL_UV(Integer PC_OVERALL_TOTAL_UV) {
        this.PC_OVERALL_TOTAL_UV = PC_OVERALL_TOTAL_UV;
    }

    public Float getPC_OVERALL_TOTAL_PV_DOD() {
        return PC_OVERALL_TOTAL_PV_DOD;
    }

    public void setPC_OVERALL_TOTAL_PV_DOD(Float PC_OVERALL_TOTAL_PV_DOD) {
        this.PC_OVERALL_TOTAL_PV_DOD = PC_OVERALL_TOTAL_PV_DOD;
    }

    public Float getPC_OVERALL_TOTAL_UV_DOD() {
        return PC_OVERALL_TOTAL_UV_DOD;
    }

    public void setPC_OVERALL_TOTAL_UV_DOD(Float PC_OVERALL_TOTAL_UV_DOD) {
        this.PC_OVERALL_TOTAL_UV_DOD = PC_OVERALL_TOTAL_UV_DOD;
    }

    public Integer getPC_OVERALL_INDEX_PV() {
        return PC_OVERALL_INDEX_PV;
    }

    public void setPC_OVERALL_INDEX_PV(Integer PC_OVERALL_INDEX_PV) {
        this.PC_OVERALL_INDEX_PV = PC_OVERALL_INDEX_PV;
    }

    public Integer getPC_OVERALL_INDEX_UV() {
        return PC_OVERALL_INDEX_UV;
    }

    public void setPC_OVERALL_INDEX_UV(Integer PC_OVERALL_INDEX_UV) {
        this.PC_OVERALL_INDEX_UV = PC_OVERALL_INDEX_UV;
    }

    public Float getPC_OVERALL_INDEX_PV_DOD() {
        return PC_OVERALL_INDEX_PV_DOD;
    }

    public void setPC_OVERALL_INDEX_PV_DOD(Float PC_OVERALL_INDEX_PV_DOD) {
        this.PC_OVERALL_INDEX_PV_DOD = PC_OVERALL_INDEX_PV_DOD;
    }

    public Float getPC_OVERALL_INDEX_UV_DOD() {
        return PC_OVERALL_INDEX_UV_DOD;
    }

    public void setPC_OVERALL_INDEX_UV_DOD(Float PC_OVERALL_INDEX_UV_DOD) {
        this.PC_OVERALL_INDEX_UV_DOD = PC_OVERALL_INDEX_UV_DOD;
    }

    public Integer getMOBILE_OVERALL_TOTAL_PV() {
        return MOBILE_OVERALL_TOTAL_PV;
    }

    public void setMOBILE_OVERALL_TOTAL_PV(Integer MOBILE_OVERALL_TOTAL_PV) {
        this.MOBILE_OVERALL_TOTAL_PV = MOBILE_OVERALL_TOTAL_PV;
    }

    public Integer getMOBILE_OVERALL_TOTAL_UV() {
        return MOBILE_OVERALL_TOTAL_UV;
    }

    public void setMOBILE_OVERALL_TOTAL_UV(Integer MOBILE_OVERALL_TOTAL_UV) {
        this.MOBILE_OVERALL_TOTAL_UV = MOBILE_OVERALL_TOTAL_UV;
    }

    public Float getMOBILE_OVERALL_TOTAL_PV_DOD() {
        return MOBILE_OVERALL_TOTAL_PV_DOD;
    }

    public void setMOBILE_OVERALL_TOTAL_PV_DOD(Float MOBILE_OVERALL_TOTAL_PV_DOD) {
        this.MOBILE_OVERALL_TOTAL_PV_DOD = MOBILE_OVERALL_TOTAL_PV_DOD;
    }

    public Float getMOBILE_OVERALL_TOTAL_UV_DOD() {
        return MOBILE_OVERALL_TOTAL_UV_DOD;
    }

    public void setMOBILE_OVERALL_TOTAL_UV_DOD(Float MOBILE_OVERALL_TOTAL_UV_DOD) {
        this.MOBILE_OVERALL_TOTAL_UV_DOD = MOBILE_OVERALL_TOTAL_UV_DOD;
    }

    public Integer getMOBILE_OVERALL_INDEX_PV() {
        return MOBILE_OVERALL_INDEX_PV;
    }

    public void setMOBILE_OVERALL_INDEX_PV(Integer MOBILE_OVERALL_INDEX_PV) {
        this.MOBILE_OVERALL_INDEX_PV = MOBILE_OVERALL_INDEX_PV;
    }

    public Integer getMOBILE_OVERALL_INDEX_UV() {
        return MOBILE_OVERALL_INDEX_UV;
    }

    public void setMOBILE_OVERALL_INDEX_UV(Integer MOBILE_OVERALL_INDEX_UV) {
        this.MOBILE_OVERALL_INDEX_UV = MOBILE_OVERALL_INDEX_UV;
    }

    public Float getMOBILE_OVERALL_INDEX_PV_DOD() {
        return MOBILE_OVERALL_INDEX_PV_DOD;
    }

    public void setMOBILE_OVERALL_INDEX_PV_DOD(Float MOBILE_OVERALL_INDEX_PV_DOD) {
        this.MOBILE_OVERALL_INDEX_PV_DOD = MOBILE_OVERALL_INDEX_PV_DOD;
    }

    public Float getMOBILE_OVERALL_INDEX_UV_DOD() {
        return MOBILE_OVERALL_INDEX_UV_DOD;
    }

    public void setMOBILE_OVERALL_INDEX_UV_DOD(Float MOBILE_OVERALL_INDEX_UV_DOD) {
        this.MOBILE_OVERALL_INDEX_UV_DOD = MOBILE_OVERALL_INDEX_UV_DOD;
    }

    public Integer getPC_SUBJECT_PV() {
        return PC_SUBJECT_PV;
    }

    public void setPC_SUBJECT_PV(Integer PC_SUBJECT_PV) {
        this.PC_SUBJECT_PV = PC_SUBJECT_PV;
    }

    public Integer getPC_SUBJECT_UV() {
        return PC_SUBJECT_UV;
    }

    public void setPC_SUBJECT_UV(Integer PC_SUBJECT_UV) {
        this.PC_SUBJECT_UV = PC_SUBJECT_UV;
    }

    public Integer getPC_SUBJECT_STAY() {
        return PC_SUBJECT_STAY;
    }

    public void setPC_SUBJECT_STAY(Integer PC_SUBJECT_STAY) {
        this.PC_SUBJECT_STAY = PC_SUBJECT_STAY;
    }

    public Float getPC_SUBJECT_PV_DOD() {
        return PC_SUBJECT_PV_DOD;
    }

    public void setPC_SUBJECT_PV_DOD(Float PC_SUBJECT_PV_DOD) {
        this.PC_SUBJECT_PV_DOD = PC_SUBJECT_PV_DOD;
    }

    public Float getPC_SUBJECT_UV_DOD() {
        return PC_SUBJECT_UV_DOD;
    }

    public void setPC_SUBJECT_UV_DOD(Float PC_SUBJECT_UV_DOD) {
        this.PC_SUBJECT_UV_DOD = PC_SUBJECT_UV_DOD;
    }

    public Integer getPC_SUBJECT_LIST_PV() {
        return PC_SUBJECT_LIST_PV;
    }

    public void setPC_SUBJECT_LIST_PV(Integer PC_SUBJECT_LIST_PV) {
        this.PC_SUBJECT_LIST_PV = PC_SUBJECT_LIST_PV;
    }

    public Integer getPC_SUBJECT_LIST_UV() {
        return PC_SUBJECT_LIST_UV;
    }

    public void setPC_SUBJECT_LIST_UV(Integer PC_SUBJECT_LIST_UV) {
        this.PC_SUBJECT_LIST_UV = PC_SUBJECT_LIST_UV;
    }

    public Integer getMOBILE_SUBJECT_PV() {
        return MOBILE_SUBJECT_PV;
    }

    public void setMOBILE_SUBJECT_PV(Integer MOBILE_SUBJECT_PV) {
        this.MOBILE_SUBJECT_PV = MOBILE_SUBJECT_PV;
    }

    public Integer getMOBILE_SUBJECT_UV() {
        return MOBILE_SUBJECT_UV;
    }

    public void setMOBILE_SUBJECT_UV(Integer MOBILE_SUBJECT_UV) {
        this.MOBILE_SUBJECT_UV = MOBILE_SUBJECT_UV;
    }

    public Integer getMOBILE_SUBJECT_STAY() {
        return MOBILE_SUBJECT_STAY;
    }

    public void setMOBILE_SUBJECT_STAY(Integer MOBILE_SUBJECT_STAY) {
        this.MOBILE_SUBJECT_STAY = MOBILE_SUBJECT_STAY;
    }

    public Float getMOBILE_SUBJECT_PV_DOD() {
        return MOBILE_SUBJECT_PV_DOD;
    }

    public void setMOBILE_SUBJECT_PV_DOD(Float MOBILE_SUBJECT_PV_DOD) {
        this.MOBILE_SUBJECT_PV_DOD = MOBILE_SUBJECT_PV_DOD;
    }

    public Float getMOBILE_SUBJECT_UV_DOD() {
        return MOBILE_SUBJECT_UV_DOD;
    }

    public void setMOBILE_SUBJECT_UV_DOD(Float MOBILE_SUBJECT_UV_DOD) {
        this.MOBILE_SUBJECT_UV_DOD = MOBILE_SUBJECT_UV_DOD;
    }

    public Integer getMOBILE_SUBJECT_LIST_PV() {
        return MOBILE_SUBJECT_LIST_PV;
    }

    public void setMOBILE_SUBJECT_LIST_PV(Integer MOBILE_SUBJECT_LIST_PV) {
        this.MOBILE_SUBJECT_LIST_PV = MOBILE_SUBJECT_LIST_PV;
    }

    public Integer getMOBILE_SUBJECT_LIST_UV() {
        return MOBILE_SUBJECT_LIST_UV;
    }

    public void setMOBILE_SUBJECT_LIST_UV(Integer MOBILE_SUBJECT_LIST_UV) {
        this.MOBILE_SUBJECT_LIST_UV = MOBILE_SUBJECT_LIST_UV;
    }

    public List<SubjectItemBean> getSUBJECT_ITEM_LIST() {
        return SUBJECT_ITEM_LIST;
    }

    public void setSUBJECT_ITEM_LIST(List<SubjectItemBean> SUBJECT_ITEM_LIST) {
        this.SUBJECT_ITEM_LIST = SUBJECT_ITEM_LIST;
    }

    public Integer getPC_TOPIC_PV() {
        return PC_TOPIC_PV;
    }

    public void setPC_TOPIC_PV(Integer PC_TOPIC_PV) {
        this.PC_TOPIC_PV = PC_TOPIC_PV;
    }

    public Integer getPC_TOPIC_UV() {
        return PC_TOPIC_UV;
    }

    public void setPC_TOPIC_UV(Integer PC_TOPIC_UV) {
        this.PC_TOPIC_UV = PC_TOPIC_UV;
    }

    public Integer getPC_TOPIC_STAY() {
        return PC_TOPIC_STAY;
    }

    public void setPC_TOPIC_STAY(Integer PC_TOPIC_STAY) {
        this.PC_TOPIC_STAY = PC_TOPIC_STAY;
    }

    public Float getPC_TOPIC_PV_DOD() {
        return PC_TOPIC_PV_DOD;
    }

    public void setPC_TOPIC_PV_DOD(Float PC_TOPIC_PV_DOD) {
        this.PC_TOPIC_PV_DOD = PC_TOPIC_PV_DOD;
    }

    public Float getPC_TOPIC_UV_DOD() {
        return PC_TOPIC_UV_DOD;
    }

    public void setPC_TOPIC_UV_DOD(Float PC_TOPIC_UV_DOD) {
        this.PC_TOPIC_UV_DOD = PC_TOPIC_UV_DOD;
    }

    public Integer getMOBILE_TOPIC_PV() {
        return MOBILE_TOPIC_PV;
    }

    public void setMOBILE_TOPIC_PV(Integer MOBILE_TOPIC_PV) {
        this.MOBILE_TOPIC_PV = MOBILE_TOPIC_PV;
    }

    public Integer getMOBILE_TOPIC_UV() {
        return MOBILE_TOPIC_UV;
    }

    public void setMOBILE_TOPIC_UV(Integer MOBILE_TOPIC_UV) {
        this.MOBILE_TOPIC_UV = MOBILE_TOPIC_UV;
    }

    public Integer getMOBILE_TOPIC_STAY() {
        return MOBILE_TOPIC_STAY;
    }

    public void setMOBILE_TOPIC_STAY(Integer MOBILE_TOPIC_STAY) {
        this.MOBILE_TOPIC_STAY = MOBILE_TOPIC_STAY;
    }

    public Float getMOBILE_TOPIC_PV_DOD() {
        return MOBILE_TOPIC_PV_DOD;
    }

    public void setMOBILE_TOPIC_PV_DOD(Float MOBILE_TOPIC_PV_DOD) {
        this.MOBILE_TOPIC_PV_DOD = MOBILE_TOPIC_PV_DOD;
    }

    public Float getMOBILE_TOPIC_UV_DOD() {
        return MOBILE_TOPIC_UV_DOD;
    }

    public void setMOBILE_TOPIC_UV_DOD(Float MOBILE_TOPIC_UV_DOD) {
        this.MOBILE_TOPIC_UV_DOD = MOBILE_TOPIC_UV_DOD;
    }

    public Integer getUSER_NEW() {
        return USER_NEW;
    }

    public void setUSER_NEW(Integer USER_NEW) {
        this.USER_NEW = USER_NEW;
    }

    public Integer getUSER_TOTAL() {
        return USER_TOTAL;
    }

    public void setUSER_TOTAL(Integer USER_TOTAL) {
        this.USER_TOTAL = USER_TOTAL;
    }

    public Integer getUSER_ACTIVE() {
        return USER_ACTIVE;
    }

    public void setUSER_ACTIVE(Integer USER_ACTIVE) {
        this.USER_ACTIVE = USER_ACTIVE;
    }

    public Integer getQUESTION_SPIDER_NEW() {
        return QUESTION_SPIDER_NEW;
    }

    public void setQUESTION_SPIDER_NEW(Integer QUESTION_SPIDER_NEW) {
        this.QUESTION_SPIDER_NEW = QUESTION_SPIDER_NEW;
    }

    public Integer getQUESTION_WRITE_NEW() {
        return QUESTION_WRITE_NEW;
    }

    public void setQUESTION_WRITE_NEW(Integer QUESTION_WRITE_NEW) {
        this.QUESTION_WRITE_NEW = QUESTION_WRITE_NEW;
    }

    public Integer getQUESTION_NEW() {
        return QUESTION_NEW;
    }

    public void setQUESTION_NEW(Integer QUESTION_NEW) {
        this.QUESTION_NEW = QUESTION_NEW;
    }

    public Integer getQUESTION_TOTAL() {
        return QUESTION_TOTAL;
    }

    public void setQUESTION_TOTAL(Integer QUESTION_TOTAL) {
        this.QUESTION_TOTAL = QUESTION_TOTAL;
    }

    public Integer getANSWER_SPIDER_NEW() {
        return ANSWER_SPIDER_NEW;
    }

    public void setANSWER_SPIDER_NEW(Integer ANSWER_SPIDER_NEW) {
        this.ANSWER_SPIDER_NEW = ANSWER_SPIDER_NEW;
    }

    public Integer getANSWER_WRITE_NEW() {
        return ANSWER_WRITE_NEW;
    }

    public void setANSWER_WRITE_NEW(Integer ANSWER_WRITE_NEW) {
        this.ANSWER_WRITE_NEW = ANSWER_WRITE_NEW;
    }

    public Integer getANSWER_NEW() {
        return ANSWER_NEW;
    }

    public void setANSWER_NEW(Integer ANSWER_NEW) {
        this.ANSWER_NEW = ANSWER_NEW;
    }

    public Integer getANSWER_TOTAL() {
        return ANSWER_TOTAL;
    }

    public void setANSWER_TOTAL(Integer ANSWER_TOTAL) {
        this.ANSWER_TOTAL = ANSWER_TOTAL;
    }

    public List<QuestionBean> getPOP_QUESTION_LIST() {
        return POP_QUESTION_LIST;
    }

    public void setPOP_QUESTION_LIST(List<QuestionBean> POP_QUESTION_LIST) {
        this.POP_QUESTION_LIST = POP_QUESTION_LIST;
    }

    public List<TagBean> getPOP_TAG_LIST() {
        return POP_TAG_LIST;
    }

    public void setPOP_TAG_LIST(List<TagBean> POP_TAG_LIST) {
        this.POP_TAG_LIST = POP_TAG_LIST;
    }

    public List<UserBean> getPOP_USER_LIST() {
        return POP_USER_LIST;
    }

    public void setPOP_USER_LIST(List<UserBean> POP_USER_LIST) {
        this.POP_USER_LIST = POP_USER_LIST;
    }

    public List<WordBean> getPOP_SEARCHWORD_LIST() {
        return POP_SEARCHWORD_LIST;
    }

    public void setPOP_SEARCHWORD_LIST(List<WordBean> POP_SEARCHWORD_LIST) {
        this.POP_SEARCHWORD_LIST = POP_SEARCHWORD_LIST;
    }

    public List<GrowQuestionBean> getGROW_QUESTION_LIST() {
        return GROW_QUESTION_LIST;
    }

    public void setGROW_QUESTION_LIST(List<GrowQuestionBean> GROW_QUESTION_LIST) {
        this.GROW_QUESTION_LIST = GROW_QUESTION_LIST;
    }

    public List<GrowTagBean> getGROW_TAG_LIST() {
        return GROW_TAG_LIST;
    }

    public void setGROW_TAG_LIST(List<GrowTagBean> GROW_TAG_LIST) {
        this.GROW_TAG_LIST = GROW_TAG_LIST;
    }

    public List<ActiveUserBean> getACTIVE_USER_LIST() {
        return ACTIVE_USER_LIST;
    }

    public void setACTIVE_USER_LIST(List<ActiveUserBean> ACTIVE_USER_LIST) {
        this.ACTIVE_USER_LIST = ACTIVE_USER_LIST;
    }

    public List<TotalActiveUserBean> getTOTAL_ACTIVE_USER_LIST() {
        return TOTAL_ACTIVE_USER_LIST;
    }

    public void setTOTAL_ACTIVE_USER_LIST(List<TotalActiveUserBean> TOTAL_ACTIVE_USER_LIST) {
        this.TOTAL_ACTIVE_USER_LIST = TOTAL_ACTIVE_USER_LIST;
    }

    public String getTIME_STR() {
        return TIME_STR;
    }

    public void setTIME_STR(String TIME_STR) {
        this.TIME_STR = TIME_STR;
    }

    public NumberTool getNumber() {
        return number;
    }

    public void setNumber(NumberTool number) {
        this.number = number;
    }

    public List<ReferBean> getREFER_LIST() {
        return REFER_LIST;
    }

    public void setREFER_LIST(List<ReferBean> REFER_LIST) {
        this.REFER_LIST = REFER_LIST;
    }

    public List<ReferBean> getARTICLE_REFER_LIST() {
        return ARTICLE_REFER_LIST;
    }

    public void setARTICLE_REFER_LIST(List<ReferBean> ARTICLE_REFER_LIST) {
        this.ARTICLE_REFER_LIST = ARTICLE_REFER_LIST;
    }

    public ReferBean getWECHAT() {
        return WECHAT;
    }

    public void setWECHAT(ReferBean WECHAT) {
        this.WECHAT = WECHAT;
    }

    public ReferBean getARTICLE_WECHAT() {
        return ARTICLE_WECHAT;
    }

    public void setARTICLE_WECHAT(ReferBean ARTICLE_WECHAT) {
        this.ARTICLE_WECHAT = ARTICLE_WECHAT;
    }

    public List<SourceBean> getARTICLE_SOURCE_LIST() {
        return ARTICLE_SOURCE_LIST;
    }

    public void setARTICLE_SOURCE_LIST(List<SourceBean> ARTICLE_SOURCE_LIST) {
        this.ARTICLE_SOURCE_LIST = ARTICLE_SOURCE_LIST;
    }

    public List<SourceBean> getSOURCE_LIST() {
        return SOURCE_LIST;
    }

    public void setSOURCE_LIST(List<SourceBean> SOURCE_LIST) {
        this.SOURCE_LIST = SOURCE_LIST;
    }

    public ReferBean getSOHUNEWS() {
        return SOHUNEWS;
    }

    public void setSOHUNEWS(ReferBean SOHUNEWS) {
        this.SOHUNEWS = SOHUNEWS;
    }

    public ReferBean getARTICLE_SOHUNEWS() {
        return ARTICLE_SOHUNEWS;
    }

    public void setARTICLE_SOHUNEWS(ReferBean ARTICLE_SOHUNEWS) {
        this.ARTICLE_SOHUNEWS = ARTICLE_SOHUNEWS;
    }

    public List<PageBean> getPOP_ARTICLE_PAGE_LIST() {
        return POP_ARTICLE_PAGE_LIST;
    }

    public void setPOP_ARTICLE_PAGE_LIST(List<PageBean> POP_ARTICLE_PAGE_LIST) {
        this.POP_ARTICLE_PAGE_LIST = POP_ARTICLE_PAGE_LIST;
    }

    public ReferBean getNEWSARTICLE() {
        return NEWSARTICLE;
    }

    public void setNEWSARTICLE(ReferBean NEWSARTICLE) {
        this.NEWSARTICLE = NEWSARTICLE;
    }

    public Integer getPC_COLUMN_PV() {
        return PC_COLUMN_PV;
    }

    public void setPC_COLUMN_PV(Integer PC_COLUMN_PV) {
        this.PC_COLUMN_PV = PC_COLUMN_PV;
    }

    public Integer getPC_COLUMN_UV() {
        return PC_COLUMN_UV;
    }

    public void setPC_COLUMN_UV(Integer PC_COLUMN_UV) {
        this.PC_COLUMN_UV = PC_COLUMN_UV;
    }

    public Integer getPC_COLUMN_STAY() {
        return PC_COLUMN_STAY;
    }

    public void setPC_COLUMN_STAY(Integer PC_COLUMN_STAY) {
        this.PC_COLUMN_STAY = PC_COLUMN_STAY;
    }

    public Float getPC_COLUMN_PV_DOD() {
        return PC_COLUMN_PV_DOD;
    }

    public void setPC_COLUMN_PV_DOD(Float PC_COLUMN_PV_DOD) {
        this.PC_COLUMN_PV_DOD = PC_COLUMN_PV_DOD;
    }

    public Float getPC_COLUMN_UV_DOD() {
        return PC_COLUMN_UV_DOD;
    }

    public void setPC_COLUMN_UV_DOD(Float PC_COLUMN_UV_DOD) {
        this.PC_COLUMN_UV_DOD = PC_COLUMN_UV_DOD;
    }

    public Integer getMOBILE_COLUMN_PV() {
        return MOBILE_COLUMN_PV;
    }

    public void setMOBILE_COLUMN_PV(Integer MOBILE_COLUMN_PV) {
        this.MOBILE_COLUMN_PV = MOBILE_COLUMN_PV;
    }

    public Integer getMOBILE_COLUMN_UV() {
        return MOBILE_COLUMN_UV;
    }

    public void setMOBILE_COLUMN_UV(Integer MOBILE_COLUMN_UV) {
        this.MOBILE_COLUMN_UV = MOBILE_COLUMN_UV;
    }

    public Integer getMOBILE_COLUMN_STAY() {
        return MOBILE_COLUMN_STAY;
    }

    public void setMOBILE_COLUMN_STAY(Integer MOBILE_COLUMN_STAY) {
        this.MOBILE_COLUMN_STAY = MOBILE_COLUMN_STAY;
    }

    public Float getMOBILE_COLUMN_PV_DOD() {
        return MOBILE_COLUMN_PV_DOD;
    }

    public void setMOBILE_COLUMN_PV_DOD(Float MOBILE_COLUMN_PV_DOD) {
        this.MOBILE_COLUMN_PV_DOD = MOBILE_COLUMN_PV_DOD;
    }

    public Float getMOBILE_COLUMN_UV_DOD() {
        return MOBILE_COLUMN_UV_DOD;
    }

    public void setMOBILE_COLUMN_UV_DOD(Float MOBILE_COLUMN_UV_DOD) {
        this.MOBILE_COLUMN_UV_DOD = MOBILE_COLUMN_UV_DOD;
    }

    public Integer getPC_COLUMN_LIST_PV() {
        return PC_COLUMN_LIST_PV;
    }

    public void setPC_COLUMN_LIST_PV(Integer PC_COLUMN_LIST_PV) {
        this.PC_COLUMN_LIST_PV = PC_COLUMN_LIST_PV;
    }

    public Integer getPC_COLUMN_LIST_UV() {
        return PC_COLUMN_LIST_UV;
    }

    public void setPC_COLUMN_LIST_UV(Integer PC_COLUMN_LIST_UV) {
        this.PC_COLUMN_LIST_UV = PC_COLUMN_LIST_UV;
    }

    public Integer getMOBILE_COLUMN_LIST_PV() {
        return MOBILE_COLUMN_LIST_PV;
    }

    public void setMOBILE_COLUMN_LIST_PV(Integer MOBILE_COLUMN_LIST_PV) {
        this.MOBILE_COLUMN_LIST_PV = MOBILE_COLUMN_LIST_PV;
    }

    public Integer getMOBILE_COLUMN_LIST_UV() {
        return MOBILE_COLUMN_LIST_UV;
    }

    public void setMOBILE_COLUMN_LIST_UV(Integer MOBILE_COLUMN_LIST_UV) {
        this.MOBILE_COLUMN_LIST_UV = MOBILE_COLUMN_LIST_UV;
    }

    public Integer getPC_COLUMN_QUESTIONS_PV() {
        return PC_COLUMN_QUESTIONS_PV;
    }

    public void setPC_COLUMN_QUESTIONS_PV(Integer PC_COLUMN_QUESTIONS_PV) {
        this.PC_COLUMN_QUESTIONS_PV = PC_COLUMN_QUESTIONS_PV;
    }

    public Integer getPC_COLUMN_QUESTIONS_UV() {
        return PC_COLUMN_QUESTIONS_UV;
    }

    public void setPC_COLUMN_QUESTIONS_UV(Integer PC_COLUMN_QUESTIONS_UV) {
        this.PC_COLUMN_QUESTIONS_UV = PC_COLUMN_QUESTIONS_UV;
    }

    public Integer getMOBILE_COLUMN_QUESTIONS_PV() {
        return MOBILE_COLUMN_QUESTIONS_PV;
    }

    public void setMOBILE_COLUMN_QUESTIONS_PV(Integer MOBILE_COLUMN_QUESTIONS_PV) {
        this.MOBILE_COLUMN_QUESTIONS_PV = MOBILE_COLUMN_QUESTIONS_PV;
    }

    public Integer getMOBILE_COLUMN_QUESTIONS_UV() {
        return MOBILE_COLUMN_QUESTIONS_UV;
    }

    public void setMOBILE_COLUMN_QUESTIONS_UV(Integer MOBILE_COLUMN_QUESTIONS_UV) {
        this.MOBILE_COLUMN_QUESTIONS_UV = MOBILE_COLUMN_QUESTIONS_UV;
    }

    public List<UserBean> getNEW_USER_LIST() {
        return NEW_USER_LIST;
    }

    public void setNEW_USER_LIST(List<UserBean> NEW_USER_LIST) {
        this.NEW_USER_LIST = NEW_USER_LIST;
    }

    public ReferBean getWIFI() {
        return WIFI;
    }

    public void setWIFI(ReferBean WIFI) {
        this.WIFI = WIFI;
    }
}
