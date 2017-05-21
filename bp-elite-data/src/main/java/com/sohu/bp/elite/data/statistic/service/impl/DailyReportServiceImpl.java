package com.sohu.bp.elite.data.statistic.service.impl;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.data.statistic.bean.*;
import com.sohu.bp.elite.data.statistic.enums.SourceType;
import com.sohu.bp.elite.data.statistic.service.DailyReportService;
import com.sohu.bp.elite.data.statistic.service.EmailService;
import com.sohu.bp.elite.data.statistic.service.UserInfoService;
import com.sohu.bp.elite.data.statistic.util.CommonUtils;
import com.sohu.bp.elite.data.statistic.util.IDUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.hive.HiveClient;
import com.sohu.bp.elite.model.*;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.ObjectUserRelationListResult;
import com.sohu.bp.thallo.model.RelationStatus;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sohu.bp.elite.data.statistic.enums.SourceType.*;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class DailyReportServiceImpl implements DailyReportService {
    private static final Logger logger = LoggerFactory.getLogger(DailyReportServiceImpl.class);
    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    private static BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final SimpleDateFormat sdfDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int FeatureValid = 1;
    private static final String HIVE_SPLIT_CHAR = "<>";
    private static final String SUBJECT_SPLIT_CHAR = "##";
    private static final String FIELD_SPLIT_CHAR = ";";
    private static final String DEVICE_UNKOWN = "UNKNOWN";
    private static final String DEVICE_COMPUTER = "COMPUTER";
    private static final String DEVICE_MOBILE = "MOBILE";
    private static final String LOG_TABLE_NAME = "javaweb_log";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private static final String from = "home-data@sohu-inc.com";
    private static final String title = "焦点家居问吧每日数据报告";
    private static final String QuestionValidStatusArray = "2;3;7;";
    private static final String AnswerValidStatusArray = "2;3;7;";
    private static final int POP_QUESTION_NUM = 30;
    private static final int POP_TAG_NUM = 30;
    private static final int POP_USER_NUM = 30;
    private static final int POP_SEARCHWORD_NUM = 10;
    private static final int NEW_ANSWER_QUESTION_NUM = 100;
    private static final int NEW_ANSWER_TAG_NUM = 30;
    private static final int ACTIVE_USER_NUM = 30;
    private static final int TOTAL_DATA_USER_NUM = 50;
    private static final int REFER_LIM_NUM = 25;

    private static final String HOST = "http://bpelite";
    private static final String VISIT_HOST = "http://bar.focus.cn";
    private static final String MAIN_URL = "/";
    private static final String UA_WECHAT = "MicroMessenger";
    private static final String UA_SOHUNEWS = "SohuNews";
    private static final String UA_NEWSARTICLE = "NewsArticle";
    private static final String UA_WIFI = "wkbrowser";
    private static final List<String> TotalUrlList = new ArrayList<String>(){
        {
            add(HOST);
        }
    };
    private static final List<String> IndexUrlList= new ArrayList<String>(){
        {
            add(HOST + "/ask/index.html");
            add(HOST + "/");
        }
    };
    private static final String IndexUrlNew = "http://bar.focus.cn";
    private static final List<String> SubjectUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/feature/subject/%s.html");
            add(HOST + "/fs/%s");
        }
    };
    private static final String SubjectUrlNew = "http://bar.focus.cn/fs/%s";
    private static final List<String> SubjectListUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/feature/subject/list.html");
            add(HOST + "/fs");
        }
    };
    private static final String SubjectListUrlNew = "http://bar.focus.cn/fs";
    private static final List<String> QuestionUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/question/%s/answers.html");
            add(HOST + "/q/%s");
        }
    };
    private static final String QuestionUrlNew = "http://bar.focus.cn/q/%s";

    private static final String ColumnUrl = HOST + "/fc/%s";
    private static final String ColumnListUrl = HOST + "/fc";
    private static final String ColumnQuestionUrl = HOST + "/fc/%s/q";
    private static final List<String> AnswerUrlPcList = new ArrayList<String>(){
        {
            add(HOST + "/ask/question/%s/answers.html#%s");
            add(HOST + "/q/%s#%s");
        }
    };
    private static final String AnswerUrlPcNew = "http://bar.focus.cn/q/%s#%s";
    private static final List<String> AnswerUrlMobileList = new ArrayList<String>(){
        {
            add(HOST + "/ask/answer/%s/comments.html");
            add(HOST + "/a/%s");
        }
    };
    private static final String AnswerUrlMobileNew = "http://bar.focus.cn/a/%s";
    private static final List<String> TagUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/tag/%s/index.html");
            add(HOST + "/ask/tag/%s/questions.html");
            add(HOST +"/ask/tag/%s/answers.html");

            add(HOST + "/t/%s");
            add(HOST + "/tq/%s");
            add(HOST + "/ta/%s");
        }
    };
    private static final String TagIndexUrlNew = "http://bar.focus.cn/t/%s";
    private static final List<String> UserUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/%s/home.html");
//            add(HOST + "/ask/user/%s/home.html");
//            add(HOST + "/ask/user/%s/question/home.html");
//            add(HOST + "/ask/user/%s/answer/home.html");
//            add(HOST + "/ask/foreman/%s/home.html");
//            add(HOST + "/ask/foreman/%s/question/home.html");
//            add(HOST + "/ask/foreman/%s/answer/home.html");
//            add(HOST + "/ask/self/%s/home.html");
//            add(HOST + "/ask/self/%s/question/home.html");
//            add(HOST + "/ask/self/%s/answer/home.html");
//            add(HOST + "/ask/designer/%s/home.html");
//            add(HOST + "/ask/designer/%s/question/home.html");
//            add(HOST + "/ask/designer/%s/answer/home.html");
//            add(HOST + "/ask/company/%s/home.html");
//            add(HOST + "/ask/company/%s/question/home.html");
//            add(HOST + "/ask/company/%s/answer/home.html");

            add(HOST + "/p%s");
//            add(HOST + "/pu/%s");
//            add(HOST + "/puq/%s");
//            add(HOST + "/pua/%s");
//            add(HOST + "/ps/%s");
//            add(HOST + "/psq/%s");
//            add(HOST + "/psa/%s");
//            add(HOST + "/pd/%s");
//            add(HOST + "/pdq/%s");
//            add(HOST + "/pda/%s");
//            add(HOST + "/pf/%s");
//            add(HOST + "/pfq/%s");
//            add(HOST + "/pfa/%s");
//            add(HOST + "/pc/%s");
//            add(HOST + "/pcq/%s");
//            add(HOST + "/pca/%s");
        }
    };
    private static final List<String> SearchUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/search/global.html");
            add(HOST + "/ask/search/answer.html");
            add(HOST + "/ask/search/question.html");
            add(HOST + "/ask/search/tag.html");
            add(HOST + "/ask/search/user.html");

            add(HOST + "/s");
            add(HOST + "/sa");
            add(HOST + "/sq");
            add(HOST + "/st");
            add(HOST + "/su");
        }
    };

    private UserInfoService userInfoService;
    private EmailService emailService;
    private HiveClient hiveClient;
    private boolean checkIp = true;
    private VelocityEngine velocityEngine;
    private String runningServer;
    private String mailSendTo = "nicholastang@sohu-inc.com";


    public UserInfoService getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean isCheckIp() {
        return checkIp;
    }

    public void setCheckIp(boolean checkIp) {
        this.checkIp = checkIp;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String getRunningServer() {
        return runningServer;
    }

    public void setRunningServer(String runningServer) {
        this.runningServer = runningServer;
    }

    public String getMailSendTo() {
        return mailSendTo;
    }

    public void setMailSendTo(String mailSendTo) {
        this.mailSendTo = mailSendTo;
    }

    public HiveClient getHiveClient() {
        return hiveClient;
    }

    public void setHiveClient(HiveClient hiveClient) {
        this.hiveClient = hiveClient;
    }

    public boolean dailyOverallReport() {
        return this.dailyOverallReport(sdf.format(new Date()));
    }

    public boolean dailyOverallReport(String dateStr) {
        return this.dailyOverallReport(dateStr, this.mailSendTo);
    }

    public boolean dailyOverallReport(String dateStr, String mailSendTo) {
        boolean retVal = false;
        if (StringUtils.isBlank(dateStr))
            return retVal;
        if (StringUtils.isBlank(mailSendTo) || StringUtils.isBlank(title))
            return retVal;
        String localIp = CommonUtils.getLocalIp();
//        if (checkIp && !localIp.equalsIgnoreCase(runningServer)) {
//            logger.info("ip:" + localIp + " hopeip:" + runningServer);
//            return retVal;
//        }

        logger.info("start generating report.");

        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        }catch(Exception e){
            logger.error("", e);
        }
        if (null == date) {
            logger.info("parse date failed. date string=" + dateStr);
            return retVal;
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 1);

        String startTimeStr = sdf.format(calendar.getTime());
        if (StringUtils.isBlank(startTimeStr))
            return retVal;

        calendar.setTime(date);
        day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 8);
        String weekAgoTimeStr = sdf.format(calendar.getTime());
        if (StringUtils.isBlank(weekAgoTimeStr))
            return retVal;

        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month-1);
        String monthAgoTimeStr = sdf.format(calendar.getTime());
        if(StringUtils.isBlank(monthAgoTimeStr))
            return retVal;

        DailyReportBean reportBean = new DailyReportBean();

        long workTime = System.currentTimeMillis();
        long pauseTime = System.currentTimeMillis();

        reportBean.setTIME_STR(startTimeStr);
        //获取PC总pv
        Integer[] PC_OVERALL_TOTAL_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, TotalUrlList, true);
        reportBean.setPC_OVERALL_TOTAL_PV(PC_OVERALL_TOTAL_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_TOTAL_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;


        //获取PC总UV
        reportBean.setPC_OVERALL_TOTAL_UV(PC_OVERALL_TOTAL_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_TOTAL_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC总PV与上周同比
        Integer[] pcTotalWeekAgoPV_UV = this.getPvUv(DEVICE_COMPUTER, weekAgoTimeStr, TotalUrlList, true);
        if (pcTotalWeekAgoPV_UV[0] == 0)
            reportBean.setPC_OVERALL_TOTAL_PV_DOD((float) 0);
        else
            reportBean.setPC_OVERALL_TOTAL_PV_DOD(floatDivision(reportBean.getPC_OVERALL_TOTAL_PV(), pcTotalWeekAgoPV_UV[0]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_TOTAL_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC总UV与上周同比
        if (pcTotalWeekAgoPV_UV[1] == 0)
            reportBean.setPC_OVERALL_TOTAL_UV_DOD((float) 0);
        else
            reportBean.setPC_OVERALL_TOTAL_UV_DOD(floatDivision(reportBean.getPC_OVERALL_TOTAL_UV(), pcTotalWeekAgoPV_UV[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_TOTAL_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC首页PV
        Integer[] PC_OVERALL_INDEX_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, IndexUrlList, false);
        reportBean.setPC_OVERALL_INDEX_PV(PC_OVERALL_INDEX_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_INDEX_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC首页UV
        reportBean.setPC_OVERALL_INDEX_UV(PC_OVERALL_INDEX_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_INDEX_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC首页PV与上周同比
        Integer[] pcIndexWeekAgoPV_UV = this.getPvUv(DEVICE_COMPUTER, weekAgoTimeStr, IndexUrlList, false);
        if (pcIndexWeekAgoPV_UV[0] == 0)
            reportBean.setPC_OVERALL_INDEX_PV_DOD((float) 0);
        else
            reportBean.setPC_OVERALL_INDEX_PV_DOD(floatDivision(reportBean.getPC_OVERALL_INDEX_PV(), pcIndexWeekAgoPV_UV[0]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_INDEX_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC首页UV与上周同比
        if (pcIndexWeekAgoPV_UV[1] == 0)
            reportBean.setPC_OVERALL_INDEX_UV_DOD((float) 0);
        else
            reportBean.setPC_OVERALL_INDEX_UV_DOD(floatDivision(reportBean.getPC_OVERALL_INDEX_UV(), pcIndexWeekAgoPV_UV[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_OVERALL_INDEX_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE总pv
        Integer[] MOBILE_OVERALL_TOTAL_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, TotalUrlList, true);
        reportBean.setMOBILE_OVERALL_TOTAL_PV(MOBILE_OVERALL_TOTAL_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_TOTAL_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE总UV
        reportBean.setMOBILE_OVERALL_TOTAL_UV(MOBILE_OVERALL_TOTAL_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_TOTAL_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE总PV与上周同比
        Integer[] mobileTotalWeekAgoPV_UV = this.getPvUv(DEVICE_MOBILE, weekAgoTimeStr, TotalUrlList, true);
        if (mobileTotalWeekAgoPV_UV[0] == 0)
            reportBean.setMOBILE_OVERALL_TOTAL_PV_DOD((float) 0);
        else
            reportBean.setMOBILE_OVERALL_TOTAL_PV_DOD(floatDivision(reportBean.getMOBILE_OVERALL_TOTAL_PV(), mobileTotalWeekAgoPV_UV[0]));
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_TOTAL_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE总UV与上周同比
        if (mobileTotalWeekAgoPV_UV[1] == 0)
            reportBean.setMOBILE_OVERALL_TOTAL_UV_DOD((float) 0);
        else
            reportBean.setMOBILE_OVERALL_TOTAL_UV_DOD(floatDivision(reportBean.getMOBILE_OVERALL_TOTAL_UV(), mobileTotalWeekAgoPV_UV[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_TOTAL_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE首页PV
        Integer[] MOBILE_OVERALL_INDEX_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, IndexUrlList, false);
        reportBean.setMOBILE_OVERALL_INDEX_PV(MOBILE_OVERALL_INDEX_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_INDEX_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE首页UV
        reportBean.setMOBILE_OVERALL_INDEX_UV(MOBILE_OVERALL_INDEX_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_INDEX_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE首页PV与上周同比
        Integer[] mobileIndexWeekAgoPV_UV = this.getPvUv(DEVICE_MOBILE, weekAgoTimeStr, IndexUrlList, false);
        if (mobileIndexWeekAgoPV_UV[0] == 0)
            reportBean.setMOBILE_OVERALL_INDEX_PV_DOD((float) 0);
        else
            reportBean.setMOBILE_OVERALL_INDEX_PV_DOD(floatDivision(reportBean.getMOBILE_OVERALL_INDEX_PV(), mobileIndexWeekAgoPV_UV[0]));

        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_INDEX_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取MOBILE首页UV与上周同比
        if (mobileIndexWeekAgoPV_UV[1] == 0)
            reportBean.setMOBILE_OVERALL_INDEX_UV_DOD((float) 0);
        else
            reportBean.setMOBILE_OVERALL_INDEX_UV_DOD(floatDivision(reportBean.getMOBILE_OVERALL_INDEX_UV(), mobileIndexWeekAgoPV_UV[1]));

        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_OVERALL_INDEX_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;



        //获取目前展示的专题
        TEliteSubject eliteSubject = null;
        TEliteSubject lastEliteSubject = null;
        try {
            TSubjectListResult listResult = eliteAdapter.getHistoryByStatus(FeatureValid, 0, 2);
            if (null != listResult && listResult.getTotal() > 0) {
                List<TEliteSubject> tEliteSubjects = listResult.getSubjects();
                if (null != tEliteSubjects && tEliteSubjects.size() > 0) {
                    eliteSubject = tEliteSubjects.get(0);
                    lastEliteSubject = tEliteSubjects.get(1);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        if (null == eliteSubject || null == lastEliteSubject) {
            logger.info("get elite subject failed.");
            return false;
        }

        List<String> currentSubjectUrlList = new ArrayList<String>();
        List<String> weekAgoSubjectUrlList = new ArrayList<String>();
        for(String subjectBaseUrl : SubjectUrlList){
            currentSubjectUrlList.add(String.format(subjectBaseUrl, IDUtil.encodedId(eliteSubject.getId())));
            currentSubjectUrlList.add(String.format(subjectBaseUrl, IDUtil.encodeIdAES(eliteSubject.getId())));

            weekAgoSubjectUrlList.add(String.format(subjectBaseUrl, IDUtil.encodedId(lastEliteSubject.getId())));
            weekAgoSubjectUrlList.add(String.format(subjectBaseUrl, IDUtil.encodeIdAES(lastEliteSubject.getId())));
        }

        //获取PC专题页PV
        Integer[] PC_SUBJECT_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, currentSubjectUrlList, true);
        reportBean.setPC_SUBJECT_PV(PC_SUBJECT_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题页UV
        reportBean.setPC_SUBJECT_UV(PC_SUBJECT_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题页停留时长
        reportBean.setPC_SUBJECT_STAY(0);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_STAY finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题页PV与上周同比
        Integer[] pcSubjectWeekAgoPV_UV = this.getPvUv(DEVICE_COMPUTER, weekAgoTimeStr, weekAgoSubjectUrlList, true);
        if (pcSubjectWeekAgoPV_UV[0] == 0)
            reportBean.setPC_SUBJECT_PV_DOD((float) 0);
        else
            reportBean.setPC_SUBJECT_PV_DOD(floatDivision(reportBean.getPC_SUBJECT_PV(), pcSubjectWeekAgoPV_UV[0]));

        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题页UV与上周同比
        if (pcSubjectWeekAgoPV_UV[1] == 0)
            reportBean.setPC_SUBJECT_UV_DOD((float) 0);
        else
            reportBean.setPC_SUBJECT_UV_DOD(floatDivision(reportBean.getPC_SUBJECT_UV(), pcSubjectWeekAgoPV_UV[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题列表页PV
        Integer[] PC_SUBJECT_LIST_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, SubjectListUrlList, false);
        reportBean.setPC_SUBJECT_LIST_PV(PC_SUBJECT_LIST_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_LIST_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取PC专题列表页UV
        reportBean.setPC_SUBJECT_LIST_UV(PC_SUBJECT_LIST_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_SUBJECT_LIST_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动专题页PV
        Integer[] MOBILE_SUBJECT_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, currentSubjectUrlList, true);
        reportBean.setMOBILE_SUBJECT_PV(MOBILE_SUBJECT_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题页UV
        reportBean.setMOBILE_SUBJECT_UV(MOBILE_SUBJECT_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题页停留时长
        reportBean.setMOBILE_SUBJECT_STAY(0);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_STAY finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题页PV与上周同比
        Integer[] mobileSubjectWeekAgoPV_UV = this.getPvUv(DEVICE_MOBILE, weekAgoTimeStr, weekAgoSubjectUrlList, true);
        if (mobileSubjectWeekAgoPV_UV[0] == 0)
            reportBean.setMOBILE_SUBJECT_PV_DOD((float) 0);
        else
            reportBean.setMOBILE_SUBJECT_PV_DOD(floatDivision(reportBean.getMOBILE_SUBJECT_PV(), mobileSubjectWeekAgoPV_UV[0]));

        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_PV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题页UV与上周同比
        if (mobileSubjectWeekAgoPV_UV[1] == 0)
            reportBean.setMOBILE_SUBJECT_UV_DOD((float) 0);
        else
            reportBean.setMOBILE_SUBJECT_UV_DOD(floatDivision(reportBean.getMOBILE_SUBJECT_UV(), mobileSubjectWeekAgoPV_UV[1]));

        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_UV_DOD finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题列表页PV
        Integer[] MOBILE_SUBJECT_LIST_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, SubjectListUrlList, false);
        reportBean.setMOBILE_SUBJECT_LIST_PV(MOBILE_SUBJECT_LIST_PV_UV[0]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_LIST_PV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取移动端专题列表页UV
        reportBean.setMOBILE_SUBJECT_LIST_UV(MOBILE_SUBJECT_LIST_PV_UV[1]);
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_SUBJECT_LIST_UV finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;


        //获取专题页各个回答的数据
        List<SubjectItemBean> SUBJECT_ITEM_LIST = new ArrayList<SubjectItemBean>();
        List<Long> subjectItemIdList = this.resolveSubjectDetail(eliteSubject.getDetail());
        for(Long itemId : subjectItemIdList) {
            try{
                TEliteAnswer subjectItem = eliteAdapter.getAnswerById(itemId);
                List<String> curAnswerUrPclList = new ArrayList<String>();
                for(String answerUrl : AnswerUrlPcList) {
                    curAnswerUrPclList.add(
                            String.format(
                                    answerUrl, IDUtil.encodedId(subjectItem.getQuestionId()), IDUtil.encodedId(subjectItem.getId())
                            )
                    );
                    curAnswerUrPclList.add(
                            String.format(
                                    answerUrl, IDUtil.encodeIdAES(subjectItem.getQuestionId()), IDUtil.encodeIdAES(subjectItem.getId())
                            )
                    );
                }

                List<String> curQuestionUrlList = new ArrayList<String>();
                for(String questionUrl : QuestionUrlList) {
                    curQuestionUrlList.add(String.format(questionUrl, IDUtil.encodedId(subjectItem.getQuestionId())));
                    curQuestionUrlList.add(String.format(questionUrl, IDUtil.encodeIdAES(subjectItem.getQuestionId())));
                }

                List<String> curAnswerUrlMobileList = new ArrayList<String>();
                for(String answerUrl : AnswerUrlMobileList){
                    curAnswerUrlMobileList.add(
                            String.format(
                                    answerUrl, IDUtil.encodedId(subjectItem.getQuestionId()), IDUtil.encodedId(subjectItem.getId())
                            )
                    );
                    curAnswerUrlMobileList.add(
                            String.format(
                                    answerUrl, IDUtil.encodeIdAES(subjectItem.getQuestionId()), IDUtil.encodeIdAES(subjectItem.getId())
                            )
                    );
                }

                if(subjectItem.getId() > 0){
                    SubjectItemBean subjectItemBean = new SubjectItemBean();
                    TEliteQuestion questionItem = eliteAdapter.getQuestionById(subjectItem.getQuestionId());
                    subjectItemBean.setTitle(questionItem.getTitle());
                    subjectItemBean.setLink(String.format(AnswerUrlPcNew, IDUtil.encodedId(subjectItem.getQuestionId()), IDUtil.encodedId(subjectItem.getId())));

                    Integer[] subjectItemPcPvUv = this.getPvUv(DEVICE_COMPUTER, startTimeStr, curQuestionUrlList, true);
                    subjectItemBean.setPc_pv(subjectItemPcPvUv[0]);
                    subjectItemBean.setPc_uv(subjectItemPcPvUv[1]);
                    Integer[] subjectItemMobilePvUv = this.getPvUv(DEVICE_MOBILE, startTimeStr, curQuestionUrlList, true);
                    subjectItemBean.setMobile_pv(subjectItemMobilePvUv[0]);
                    subjectItemBean.setMobile_uv(subjectItemMobilePvUv[1]);
                    SUBJECT_ITEM_LIST.add(subjectItemBean);
                }
            }catch(Exception e){
                logger.error("", e);
            }
        }
        reportBean.setSUBJECT_ITEM_LIST(SUBJECT_ITEM_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get SUBJECT_ITEM_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;


        //获取话题的数据
        TEliteTopic currentEliteTopic = null;
        TEliteTopic weekAgoEliteTopic = null;
        List<TEliteTopic> eliteTopics = null;
        try{
            eliteTopics = eliteAdapter.getAllEliteTopicByStatus(1, 0, 2);
        }catch(Exception e){
            logger.error("", e);
        }
        if(null != eliteTopics && eliteTopics.size() > 1){
            currentEliteTopic = eliteTopics.get(0);
            weekAgoEliteTopic = eliteTopics.get(1);
        }
        if(null == currentEliteTopic || null == weekAgoEliteTopic)
            return false;
        List<String> currentTopicUrlList = new ArrayList<String>();
        List<String> weekAgoTopicUrlList = new ArrayList<String>();
        for(String questionUrl : QuestionUrlList){
            currentTopicUrlList.add(String.format(questionUrl, IDUtil.encodedId(currentEliteTopic.getQuestionId())));
            currentTopicUrlList.add(String.format(questionUrl, IDUtil.encodeIdAES(currentEliteTopic.getQuestionId())));

            weekAgoTopicUrlList.add(String.format(questionUrl, IDUtil.encodedId(weekAgoEliteTopic.getQuestionId())));
            weekAgoTopicUrlList.add(String.format(questionUrl, IDUtil.encodeIdAES(weekAgoEliteTopic.getQuestionId())));
        }

        Integer[] PC_TOPIC_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, currentTopicUrlList, true);
        reportBean.setPC_TOPIC_PV(PC_TOPIC_PV_UV[0]);
        reportBean.setPC_TOPIC_UV(PC_TOPIC_PV_UV[1]);
        reportBean.setPC_TOPIC_STAY(0);
        Integer[] weekAgoPcTopicPvUv = this.getPvUv(DEVICE_COMPUTER, weekAgoTimeStr, weekAgoTopicUrlList, true);
        if(weekAgoPcTopicPvUv[0] == 0)
            reportBean.setPC_TOPIC_PV_DOD((float)0);
        else
            reportBean.setPC_TOPIC_PV_DOD(this.floatDivision(reportBean.getPC_TOPIC_PV(), weekAgoPcTopicPvUv[0]));
        if(weekAgoPcTopicPvUv[1] == 0)
            reportBean.setPC_TOPIC_UV_DOD((float)0);
        else
            reportBean.setPC_TOPIC_UV_DOD(this.floatDivision(reportBean.getPC_TOPIC_UV(), weekAgoPcTopicPvUv[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get PC_TOPIC_DATA finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        Integer[] MOBILE_TOPIC_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, currentTopicUrlList, false);
        reportBean.setMOBILE_TOPIC_PV(MOBILE_TOPIC_PV_UV[0]);
        reportBean.setMOBILE_TOPIC_UV(MOBILE_TOPIC_PV_UV[1]);
        reportBean.setMOBILE_TOPIC_STAY(0);
        Integer[] weekAgoMobileTopicPvUv = this.getPvUv(DEVICE_MOBILE, weekAgoTimeStr, weekAgoTopicUrlList, false);
        if(weekAgoMobileTopicPvUv[0] == 0)
            reportBean.setMOBILE_TOPIC_PV_DOD((float)0);
        else
            reportBean.setMOBILE_TOPIC_PV_DOD(this.floatDivision(reportBean.getMOBILE_TOPIC_PV(), weekAgoMobileTopicPvUv[0]));
        if(weekAgoMobileTopicPvUv[1] == 0)
            reportBean.setMOBILE_TOPIC_UV_DOD((float)0);
        else
            reportBean.setMOBILE_TOPIC_UV_DOD(this.floatDivision(reportBean.getMOBILE_TOPIC_UV(), weekAgoMobileTopicPvUv[1]));
        pauseTime = System.currentTimeMillis();
        logger.info("get MOBILE_TOPIC_DATA finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;


        //获取专栏数据
        TEliteColumn currentEliteColumn = null;
        TEliteColumn weekAgoEliteColumn = null;
        List<TEliteColumn> eliteColumns = null;
        try{
            TColumnListResult listResult = eliteAdapter.getAllEliteColumnByStatus(0, 2, 1);
            if(null != listResult && listResult.getTotal() > 0 && listResult.getColumns().size() > 0){
                eliteColumns = listResult.getColumns();

                currentEliteColumn = eliteColumns.get(0);

                if(eliteColumns.size() > 1){
                    weekAgoEliteColumn = eliteColumns.get(1);
                }
            }
        }catch(Exception e){
            logger.error("", e);
        }
        if(null != currentEliteColumn) {
            List<String> currentColumnUrlList = new ArrayList<String>();
            List<String> weekAgoColumnUrlList = new ArrayList<String>();
            currentColumnUrlList.add(String.format(ColumnUrl, IDUtil.encodedId(currentEliteColumn.getId())));
            Integer[] PC_COLUMN_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, currentColumnUrlList, true);
            reportBean.setPC_COLUMN_PV(PC_COLUMN_PV_UV[0]);
            reportBean.setPC_COLUMN_UV(PC_COLUMN_PV_UV[1]);
            reportBean.setPC_COLUMN_STAY(0);
            Integer[] MOBILE_COLUMN_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, currentColumnUrlList, true);
            reportBean.setMOBILE_COLUMN_PV(MOBILE_COLUMN_PV_UV[0]);
            reportBean.setMOBILE_COLUMN_UV(MOBILE_COLUMN_PV_UV[1]);
            reportBean.setMOBILE_COLUMN_STAY(0);

            List<String> columnListUrlList = new ArrayList<>();
            columnListUrlList.add(ColumnListUrl);
            Integer[] PC_COLUMN_LIST_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, columnListUrlList, false);
            reportBean.setPC_COLUMN_LIST_PV(PC_COLUMN_LIST_PV_UV[0]);
            reportBean.setPC_COLUMN_LIST_UV(PC_COLUMN_LIST_PV_UV[1]);

            Integer[] MOBILE_COLUMN_LIST_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, columnListUrlList, false);
            reportBean.setMOBILE_COLUMN_LIST_PV(MOBILE_COLUMN_LIST_PV_UV[0]);
            reportBean.setMOBILE_COLUMN_LIST_UV(MOBILE_COLUMN_LIST_PV_UV[1]);

            List<String> columnQuestionUrlList = new ArrayList<>();
            columnQuestionUrlList.add(String.format(ColumnQuestionUrl, IDUtil.encodedId(currentEliteColumn.getId())));
            Integer[] PC_COLUMN_QUESTIONS_PV_UV = this.getPvUv(DEVICE_COMPUTER, startTimeStr, columnQuestionUrlList, true);
            reportBean.setPC_COLUMN_QUESTIONS_PV(PC_COLUMN_QUESTIONS_PV_UV[0]);
            reportBean.setPC_COLUMN_QUESTIONS_UV(PC_COLUMN_QUESTIONS_PV_UV[1]);

            Integer[] MOBILE_COLUMN_QUESTIONS_PV_UV = this.getPvUv(DEVICE_MOBILE, startTimeStr, columnQuestionUrlList, true);
            reportBean.setMOBILE_COLUMN_QUESTIONS_PV(MOBILE_COLUMN_QUESTIONS_PV_UV[0]);
            reportBean.setMOBILE_COLUMN_QUESTIONS_UV(MOBILE_COLUMN_QUESTIONS_PV_UV[1]);

            pauseTime = System.currentTimeMillis();
            logger.info("get COLUMN_DATA finish. waste time=" + (pauseTime - workTime));
            workTime = pauseTime;
        }

//        if(null != weekAgoEliteColumn){
//            weekAgoColumnUrlList.add(String.format(ColumnUrl, IDUtil.encodedId(weekAgoEliteColumn.getId())));
//            Integer[] weekAgoPcColumnPvUv = this.getPvUv(DEVICE_COMPUTER, weekAgoTimeStr, weekAgoColumnUrlList, true);
//            if(weekAgoPcColumnPvUv[0] == 0)
//                reportBean.setPC_COLUMN_PV_DOD((float)0);
//            else
//                reportBean.setPC_COLUMN_PV_DOD(this.floatDivision(reportBean.getPC_COLUMN_PV(), weekAgoPcColumnPvUv[0]));
//            if(weekAgoPcColumnPvUv[1] == 0)
//                reportBean.setPC_COLUMN_UV_DOD((float)0);
//            else
//                reportBean.setPC_COLUMN_UV_DOD(this.floatDivision(reportBean.getPC_COLUMN_UV(), weekAgoPcColumnPvUv[1]));
//
//            Integer[] weekAgoMobileColumnPvUv = this.getPvUv(DEVICE_MOBILE, weekAgoTimeStr, weekAgoColumnUrlList, true);
//
//
//        }



        //获取refer TOP25
//        reportBean.setARTICLE_REFER_LIST(this.getArticleReferList(startTimeStr));
//        pauseTime = System.currentTimeMillis();
//        logger.info("get ARTICLE_REFER_LIST finish. waste time=" + (pauseTime - workTime));
//        workTime = pauseTime;
//
//        reportBean.setARTICLE_WECHAT(this.getReferByAgent(startTimeStr, MAIN_URL, UA_WECHAT));
//        pauseTime = System.currentTimeMillis();
//        logger.info("get ARTICLE_WECHAT_REFER finish. waste time=" + (pauseTime - workTime));
//        workTime = pauseTime;
//
//        reportBean.setARTICLE_SOHUNEWS(this.getReferByAgent(startTimeStr, MAIN_URL, UA_SOHUNEWS));
//        pauseTime = System.currentTimeMillis();
//        logger.info("get ARTICLE_SOHUNEWS_REFER finish. waste time=" + (pauseTime - workTime));
//        workTime = pauseTime;

        reportBean.setREFER_LIST(this.getReferList(startTimeStr));
        pauseTime = System.currentTimeMillis();
        logger.info("get REFER_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setWECHAT(this.getReferByAgent(startTimeStr, HOST, UA_WECHAT));
        pauseTime = System.currentTimeMillis();
        logger.info("get WECHAT_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setSOHUNEWS(this.getReferByAgent(startTimeStr, HOST, UA_SOHUNEWS));
        pauseTime = System.currentTimeMillis();
        logger.info("get SOHUNEWS_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setNEWSARTICLE(this.getReferByAgent(startTimeStr, HOST, UA_NEWSARTICLE));
        pauseTime = System.currentTimeMillis();
        logger.info("get NEWSARTICLE_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setWIFI(this.getReferByAgent(startTimeStr, HOST, UA_WIFI));
        pauseTime = System.currentTimeMillis();
        logger.info("get WIFI_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取具体带量的数据
        reportBean.setSOURCE_LIST(this.getSourceBeanList(startTimeStr, HOST));

        reportBean.setARTICLE_SOURCE_LIST(this.getSourceBeanList(startTimeStr, MAIN_URL));

        //获取用户数据
        //新增用户 0-未知 1-PC 10-移动 20-小程序
        List<UserBean> newUserBeanList = new ArrayList<>();
        TSearchUserCondition searchUserCondition = new TSearchUserCondition();
        try {
            searchUserCondition.setStatus(EliteUserStatus.VALID.getValue())
                    .setMinFirstLoginTime(sdf.parse(startTimeStr).getTime())
                    .setMaxFirstLoginTime(sdf.parse(sdf.format(new Date())).getTime())
                    .setFirstLogin("0;1;10;20")
                    .setFrom(0)
                    .setCount(Integer.MAX_VALUE);
        }catch(Exception e){
            logger.error("", e);
            return false;
        }

        List<Long> userIdList = new ArrayList<>();
        try {
            TUserIdListResult userIdListResult = eliteAdapter.searchUserId(searchUserCondition);
            reportBean.setUSER_NEW((int) userIdListResult.getTotal());
            userIdList = userIdListResult.getUserIds();

        }catch(Exception e) {
            logger.error("", e);
            reportBean.setUSER_NEW(0);
        }

        if (userIdList != null && userIdList.size() > 0) {
            for (Long userId : userIdList) {
                try {
                    TEliteUser eliteUser = eliteAdapter.getUserByBpId(userId);
                    UserBean newUserBean = new UserBean();
                    newUserBean.setLink("https://bar.focus.cn/pu/" + IDUtil.encodedId(eliteUser.getBpId()));
                    newUserBean.setFrom(eliteUser.getFirstLogin());
                    newUserBean.setRegisterTime(sdfDetail.format(new Date(eliteUser.getFirstLoginTime())));

                    UserInfo userInfo = userInfoService.getUserInfoByBpid(userId);
                    newUserBean.setTitle(userInfo.getNick());
                    newUserBeanList.add(newUserBean);
                }catch (Exception e){
                    logger.error("", e);
                }
            }
        }

        reportBean.setNEW_USER_LIST(newUserBeanList);
        pauseTime = System.currentTimeMillis();
        logger.info("get USER_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //用户总量
        searchUserCondition = new TSearchUserCondition();
        searchUserCondition.setStatus(EliteUserStatus.VALID.getValue())
                .setFrom(0)
                .setCount(1);
        try{
            TUserIdListResult userIdListResult = eliteAdapter.searchUserId(searchUserCondition);
            reportBean.setUSER_TOTAL((int) userIdListResult.getTotal());
        }catch(Exception e){
            logger.error("", e);
            reportBean.setUSER_TOTAL(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get USER_TOTAL finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        Set<Long> newQuestionIdSet = new HashSet<Long>();
        //新增问题总量
        TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
        try
        {
            searchQuestionCondition.setStatusArray(QuestionValidStatusArray)
                    .setMinCreateTime(sdf.parse(startTimeStr).getTime())
                    .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                    .setFrom(0)
                    .setCount(Integer.MAX_VALUE);
        }catch(Exception e){
            logger.error("", e);
        }
        try{
            TQuestionIdListResult questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
            reportBean.setQUESTION_NEW((int) questionIdListResult.getTotal());
            List<Long> questionIdList = questionIdListResult.getQuestionIds();
            for(Long questionId : questionIdList){
                newQuestionIdSet.add(questionId);
            }
        }catch(Exception e){
            logger.error("", e);
            reportBean.setQUESTION_NEW(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get QUESTION_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //抓取问题增量
        searchQuestionCondition = new TSearchQuestionCondition();
        try{
            searchQuestionCondition.setStatusArray(QuestionValidStatusArray)
                    .setSource(TEliteSourceType.CRAWL.getValue())
                    .setMinCreateTime(sdf.parse(startTimeStr).getTime())
                    .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                    .setFrom(0)
                    .setCount(Integer.MAX_VALUE);
        }catch (Exception e){
            logger.error("", e);
            return false;
        }
        try{
            TQuestionIdListResult questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
            reportBean.setQUESTION_SPIDER_NEW((int) questionIdListResult.getTotal());
            List<Long> questionIdList = questionIdListResult.getQuestionIds();
            for(Long questionId : questionIdList){
                if(newQuestionIdSet.contains(questionId))
                    newQuestionIdSet.remove(questionId);
            }
        }catch(Exception e){
            logger.error("", e);
            reportBean.setQUESTION_SPIDER_NEW(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get QUESTION_SPIDER_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //新增手工发布问题
        reportBean.setQUESTION_WRITE_NEW(reportBean.getQUESTION_NEW()-reportBean.getQUESTION_SPIDER_NEW());
        pauseTime = System.currentTimeMillis();
        logger.info("get QUESTION_WIRTE_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //问题总数
        searchQuestionCondition = new TSearchQuestionCondition();
        searchQuestionCondition.setStatusArray(QuestionValidStatusArray)
                .setFrom(0)
                .setCount(1);
        try{
            TQuestionIdListResult questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
            reportBean.setQUESTION_TOTAL((int) questionIdListResult.getTotal());
        }catch(Exception e){
            logger.error("", e);
            reportBean.setQUESTION_TOTAL(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get QUESTION_TOTAL finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        Set<Long> newAnswerIdSet = new HashSet<Long>();
        //新增回答总量
        TSearchAnswerCondition searchAnswerCondition = new TSearchAnswerCondition();
        try{
            searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                    .setMinCreateTime(sdf.parse(startTimeStr).getTime())
                    .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                    .setFrom(0)
                    .setCount(Integer.MAX_VALUE);
        }catch (Exception e){
            logger.error("", e);
            return false;
        }
        try{
            TAnswerIdListResult answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
            reportBean.setANSWER_NEW((int) answerIdListResult.getTotal());
            List<Long> answerIdList = answerIdListResult.getAnswerIds();
            for(Long answerId : answerIdList){
                newAnswerIdSet.add(answerId);
            }
        }catch(Exception e){
            logger.error("", e);
            reportBean.setANSWER_NEW(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get ANSWER_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //抓取回答增量
        searchAnswerCondition = new TSearchAnswerCondition();
        try{
            searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                    .setSource(TEliteSourceType.CRAWL.getValue())
                    .setMinCreateTime(sdf.parse(startTimeStr).getTime())
                    .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                    .setFrom(0)
                    .setCount(Integer.MAX_VALUE);
        }catch (Exception e){
            logger.error("", e);
            return false;
        }
        try{
            TAnswerIdListResult answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
            reportBean.setANSWER_SPIDER_NEW((int) answerIdListResult.getTotal());
            List<Long> answerIdList = answerIdListResult.getAnswerIds();
            for(Long answerId : answerIdList){
                if(newAnswerIdSet.contains(answerId))
                    newAnswerIdSet.remove(answerId);
            }
        }catch(Exception e){
            logger.error("", e);
            reportBean.setANSWER_SPIDER_NEW(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get ANSWER_SPIDER_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //新增手工发布回答
        reportBean.setANSWER_WRITE_NEW(reportBean.getANSWER_NEW()-reportBean.getANSWER_SPIDER_NEW());
        pauseTime = System.currentTimeMillis();
        logger.info("get ANSWER_WIRTE_NEW finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //回答总数
        searchAnswerCondition = new TSearchAnswerCondition();
        searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                .setFrom(0)
                .setCount(1);
        try{
            TAnswerIdListResult answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
            reportBean.setANSWER_TOTAL((int) answerIdListResult.getTotal());
        }catch(Exception e){
            logger.error("", e);
            reportBean.setANSWER_TOTAL(0);
        }
        pauseTime = System.currentTimeMillis();
        logger.info("get ANSWER_TOTAL finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //活跃用户
        Map<Long, Integer> activeUserMap = new HashMap<Long, Integer>();
        for(Long writeQuestionId : newQuestionIdSet){
            TEliteQuestion question = null;
            try{
                question = eliteAdapter.getQuestionById(writeQuestionId);
            }catch(Exception e)
            {
                logger.error("", e);
            }
            if(null == question)
                return false;
            if(activeUserMap.containsKey(question.getBpId()))
                activeUserMap.put(question.getBpId(), activeUserMap.get(question.getBpId()) + 1);
            else
                activeUserMap.put(question.getBpId(), 1);
        }
        for(Long writeAnswerId : newAnswerIdSet){
            TEliteAnswer answer = null;
            try{
                answer = eliteAdapter.getAnswerById(writeAnswerId);
            }catch(Exception e){
                logger.error("", e);
            }
            if(null == answer)
                return false;
            if(activeUserMap.containsKey(answer.getBpId()))
                activeUserMap.put(answer.getBpId(), activeUserMap.get(answer.getBpId()) + 1);
            else
                activeUserMap.put(answer.getBpId(), 1);
        }
        reportBean.setUSER_ACTIVE(activeUserMap.size());
        pauseTime = System.currentTimeMillis();
        logger.info("get USER_ACTIVE finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取主站访问最高的30个地址
//        int popArticleCount = 0;
//        List<PageBean>  POP_ARTICLE_PAGE_LIST = new ArrayList<>();
//        List<String> hotArticlePageList = new ArrayList<>();
//        hotArticlePageList.add(MAIN_URL + "%");
//        List<HotDataBean> articleHotDataList = this.getHotData(hotArticlePageList, startTimeStr, "", false);
//        if(null != articleHotDataList && articleHotDataList.size() > 0){
//            for(HotDataBean hotDataBean : articleHotDataList){
//                PageBean pageBean = new PageBean();
//                pageBean.setUrl(hotDataBean.getUrl());
//                pageBean.setPv(hotDataBean.getPv());
//                pageBean.setUv(hotDataBean.getUv());
//
//                POP_ARTICLE_PAGE_LIST.add(pageBean);
//                if(++popArticleCount >= POP_QUESTION_NUM)
//                    break;
//            }
//        }
//        reportBean.setPOP_ARTICLE_PAGE_LIST(POP_ARTICLE_PAGE_LIST);
//        pauseTime = System.currentTimeMillis();
//        logger.info("get POP_ARTICLE_30_PAGE finish. waste time=" + (pauseTime - workTime));
//        workTime = pauseTime;

        //获取访问最高30个问题的列表
        int popQuestionCount = 0;
        List<QuestionBean> POP_QUESTION_LIST = new ArrayList<>();
        List<String> hotQuestionList = new ArrayList<>();
        for(String questionBaseUrl : QuestionUrlList){
            hotQuestionList.add(String.format(questionBaseUrl, "%"));
        }
        List<HotDataBean> hotDataList = this.getHotData(hotQuestionList, startTimeStr, "", false);
        //解析id
        String reg = "";
        for(int i=0; i<QuestionUrlList.size(); i++){
            if(i > 0)
                reg += "|";
            reg +=  "(" + String.format(QuestionUrlList.get(i), "(((?!/).)*)") + ")";
        }
        for(HotDataBean hotData : hotDataList){
            QuestionBean questionBean = new QuestionBean();
            questionBean.setPv(hotData.getPv());
            questionBean.setUv(hotData.getUv());
            questionBean.setLink(hotData.getUrl());
            if(questionBean.getLink().startsWith(HOST + "/q/go"))
                continue;
            String encodedQuestionId = "";
            Matcher m = Pattern.compile(reg).matcher(questionBean.getLink());
            if (m.find()) {
                boolean initCheck = true;
                for (int i = 1; i < m.groupCount(); i++) {
                    String matchPart = m.group(i);
                    if (StringUtils.isNotBlank(matchPart)) {
                        if (initCheck) {
                            initCheck = false;
                            continue;
                        } else {
                            encodedQuestionId = matchPart;
                            break;
                        }
                    }
                }

                if(StringUtils.isBlank(encodedQuestionId))
                    continue;
                try {
                    Long questionId = IDUtil.smartDecodeId(encodedQuestionId);
                    TEliteQuestion eliteQuestion = eliteAdapter.getQuestionById(questionId);
                    questionBean.setTitle(eliteQuestion.getTitle());

                    ObjectUserRelationListResult listResult1 = thalloServiceAdapter.getReactiveListByUserType(questionId, BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
                    questionBean.setFansNum((int)listResult1.getTotal());
                }catch(Exception e){
                    logger.error("", e);
                    continue;
                }

            }

            questionBean.setLink(questionBean.getLink().replace(HOST, VISIT_HOST));
            POP_QUESTION_LIST.add(questionBean);
            if(++popQuestionCount >=30)
                break;
        }
        reportBean.setPOP_QUESTION_LIST(POP_QUESTION_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get POP_30_QUESTION finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取访问最高的30个标签
        int popTagCount = 0;
        List<TagBean> POP_TAG_LIST = new ArrayList<>();
        List<String> hotTagList = new ArrayList<>();
        for(String tagBaseUrl : TagUrlList){
            hotTagList.add(String.format(tagBaseUrl, "%"));
        }
        hotDataList = this.getHotData(hotTagList, startTimeStr, "", false);
        //解析id
        reg = "";
        for(int i=0; i<TagUrlList.size(); i++){
            if(i > 0)
                reg += "|";
            reg +=  "(" + String.format(TagUrlList.get(i), "(((?!/).)*)") + ")";
        }
        for(HotDataBean hotData : hotDataList){
            TagBean tagBean = new TagBean();
            tagBean.setPv(hotData.getPv());
            tagBean.setUv(hotData.getUv());
            tagBean.setLink(hotData.getUrl());

            Matcher m = Pattern.compile(reg).matcher(tagBean.getLink());
            if (m.find()) {
                boolean initCheck = true;
                String encodedTagId = "";
                for (int i = 1; i < m.groupCount(); i++) {
                    String matchPart = m.group(i);
                    if (StringUtils.isNotBlank(matchPart)) {
                        if (initCheck) {
                            initCheck = false;
                            continue;
                        } else {
                            encodedTagId = matchPart;
                            break;
                        }
                    }
                }

                if(StringUtils.isBlank(encodedTagId))
                    continue;

                try {
                    Long tagId = IDUtil.smartDecodeId(encodedTagId);
                    Tag tag = decorationServiceAdapter.getTagById(tagId.intValue());
                    tagBean.setTitle(tag.getName());

                    ObjectUserRelationListResult listResult1 = thalloServiceAdapter.getReactiveListByUserType(tagId, BpType.Tag.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
                    tagBean.setFansNum((int)listResult1.getTotal());
                }catch(Exception e){
                    logger.error("", e);
                    continue;
                }

            }

            tagBean.setLink(tagBean.getLink().replace(HOST, VISIT_HOST));
            POP_TAG_LIST.add(tagBean);
            if(++popTagCount >= POP_TAG_NUM)
                break;
        }
        reportBean.setPOP_TAG_LIST(POP_TAG_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get POP_30_TAG finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //获取访问最高的30个用户
        int popUserCount = 0;
        List<UserBean> POP_USER_LIST = new ArrayList<>();
        List<String> hotUserList = new ArrayList<>();
        for(String userBaseUrl : UserUrlList){
            hotUserList.add(String.format(userBaseUrl, "%"));
        }
        hotDataList = this.getHotData(hotUserList, startTimeStr, "", false);
        int userLoopCount = 0;
        reg = "";
        for(int i=0; i<UserUrlList.size(); i++){
            if(userLoopCount++ > 0){
                reg += "|";
            }
            String tempUrl = UserUrlList.get(i);
            if(tempUrl.startsWith(HOST + "/ask")){
                reg += "(" + String.format(tempUrl, "user/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "self/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "foreman/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "designer/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "company/(((?!/).)*)") + ")";

                reg += "|(" + String.format(tempUrl, "user/(((?!/).)*)/question") + ")";
                reg += "|(" + String.format(tempUrl, "self/(((?!/).)*)/question") + ")";
                reg += "|(" + String.format(tempUrl, "foreman/(((?!/).)*)/question") + ")";
                reg += "|(" + String.format(tempUrl, "designer/(((?!/).)*)/question") + ")";
                reg += "|(" + String.format(tempUrl, "company/(((?!/).)*)/question") + ")";

                reg += "|(" + String.format(tempUrl, "user/(((?!/).)*)/answer") + ")";
                reg += "|(" + String.format(tempUrl, "self/(((?!/).)*)/answer") + ")";
                reg += "|(" + String.format(tempUrl, "foreman/(((?!/).)*)/answer") + ")";
                reg += "|(" + String.format(tempUrl, "designer/(((?!/).)*)/answer") + ")";
                reg += "|(" + String.format(tempUrl, "company/(((?!/).)*)/answer") + ")";
            } else {
                reg += "(" + String.format(tempUrl, "u/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "ua/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "uq/(((?!/).)*)") + ")";

                reg += "|(" + String.format(tempUrl, "s/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "sa/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "sq/(((?!/).)*)") + ")";

                reg += "|(" + String.format(tempUrl, "d/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "dq/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "da/(((?!/).)*)") + ")";

                reg += "|(" + String.format(tempUrl, "f/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "fa/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "fq/(((?!/).)*)") + ")";

                reg += "|(" + String.format(tempUrl, "c/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "ca/(((?!/).)*)") + ")";
                reg += "|(" + String.format(tempUrl, "cq/(((?!/).)*)") + ")";
            }

        }
        logger.info("REG = " + reg);
        for(HotDataBean hotData : hotDataList){
            UserBean userBean = new UserBean();
            userBean.setPv(hotData.getPv());
            userBean.setUv(hotData.getUv());
            userBean.setLink(hotData.getUrl());

            //解析id
            logger.info("LINK = " + userBean.getLink());
            Matcher userMatcher = Pattern.compile(reg).matcher(userBean.getLink());
            String encodedId = "";
            if(userMatcher.find()) {

                boolean initCheck = true;
                for (int i = 1; i < userMatcher.groupCount(); i++) {
                    String matchPart = userMatcher.group(i);
                    if (StringUtils.isNotBlank(matchPart)) {
                        if (initCheck) {
                            initCheck = false;
                            continue;
                        } else {
                            encodedId = matchPart;
                            break;
                        }
                    }
                }
                if(StringUtils.isBlank(encodedId))
                    continue;
                logger.info("<<<<<<<<<<<<<<<<<<<<<<<");
                logger.info(userBean.getLink());
                logger.info(encodedId);
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>");
                try {
                    Long userId = IDUtil.smartDecodeId(encodedId);
                    if(userBean.getLink().contains("/company") || userBean.getLink().contains("/pc")){
                        DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(userId.intValue());
                        userBean.setTitle(decorationCompany.getShortName());

                        ObjectUserRelationListResult listResult1 = thalloServiceAdapter.getReactiveListByUserType(decorationCompany.getExpertId(), BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
                        userBean.setFansNum((int)listResult1.getTotal());
                    } else {
                        UserInfo userInfo = userInfoService.getUserInfoByBpid(userId);
                        userBean.setTitle(userInfo.getNick());

                        ObjectUserRelationListResult listResult1 = thalloServiceAdapter.getReactiveListByUserType(userId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
                        userBean.setFansNum((int)listResult1.getTotal());
                    }

                }catch(Exception e){
                    logger.error("", e);
                    continue;
                }

                userBean.setLink(userBean.getLink().replace(HOST, VISIT_HOST));
                POP_USER_LIST.add(userBean);
                if(++popUserCount >= POP_USER_NUM)
                    break;
            }


        }
        reportBean.setPOP_USER_LIST(POP_USER_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get POP_30_USER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;



        //获取访问热度最高的10个搜索词
        reportBean.setPOP_SEARCHWORD_LIST(this.getHotQueryWord(startTimeStr, "", false));
        pauseTime = System.currentTimeMillis();
        logger.info("get HOT_10_QUERY finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;



        //获取新增回答最多的30个问题
        int growQuestionCount = 0;
        List<GrowQuestionBean> GROW_QUESTION_LIST = new ArrayList<GrowQuestionBean>();
        Map<String, Integer> newMostQuestionMap = new HashMap<>();
        for(Long answerId : newAnswerIdSet){
            try {
                TEliteAnswer answer = eliteAdapter.getAnswerById(answerId);
                Long questionId = answer.getQuestionId();
                if(newMostQuestionMap.containsKey(questionId.toString()))
                    newMostQuestionMap.put(questionId.toString(), newMostQuestionMap.get(questionId.toString()) + 1);
                else
                    newMostQuestionMap.put(questionId.toString(), 1);
            }catch(Exception e){
                logger.error("", e);
            }
        }
        List<Map.Entry<String, Integer>> sortedList = this.sortHashMap(newMostQuestionMap);
        for(Map.Entry<String, Integer> questionMap : sortedList){
            Long questionId = Long.parseLong(questionMap.getKey());
            try {
                TEliteQuestion eliteQuestion = eliteAdapter.getQuestionById(questionId);
                Integer growAnswerD = questionMap.getValue();
                GrowQuestionBean growQuestionBean = new GrowQuestionBean();
                growQuestionBean.setLink(String.format(QuestionUrlNew, IDUtil.encodedId(eliteQuestion.getId())));
                growQuestionBean.setTitle(eliteQuestion.getTitle());
                growQuestionBean.setGrowAnsNumD(growAnswerD);

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setQuestionId(eliteQuestion.getId())
                        .setMinCreateTime(sdf.parse(weekAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                TAnswerIdListResult listResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                growQuestionBean.setGrowAnsNumW((int)listResult.getTotal());

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setQuestionId(eliteQuestion.getId())
                        .setMinCreateTime(sdf.parse(monthAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                listResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                growQuestionBean.setGrowAnsNumM((int)listResult.getTotal());

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setQuestionId(eliteQuestion.getId())
                        .setFrom(0)
                        .setCount(1);
                listResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                growQuestionBean.setTotalAnsNum((int)listResult.getTotal());

                growQuestionBean.setTotalComNum(0);

                GROW_QUESTION_LIST.add(growQuestionBean);
                if(++growQuestionCount >= NEW_ANSWER_QUESTION_NUM)
                    break;
            }catch(Exception e){
                logger.error("", e);
            }
        }
        reportBean.setGROW_QUESTION_LIST(GROW_QUESTION_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get GROW_QUESTION_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        //活跃用户top30
        int activeUserCount = 0;
        List<ActiveUserBean> activeUserList = new ArrayList<>();
        Map<String, Integer> userStatisticMap = new HashMap<String, Integer>();
        try {
            for (Long questionId : newQuestionIdSet) {
                TEliteQuestion eliteQuestion = eliteAdapter.getQuestionById(questionId);
                String bpIdStr = String.valueOf(eliteQuestion.getBpId());
                if (userStatisticMap.containsKey(bpIdStr))
                    userStatisticMap.put(bpIdStr, userStatisticMap.get(bpIdStr) + 1);
                else
                    userStatisticMap.put(bpIdStr, 1);
            }
            for (Long answerId : newAnswerIdSet) {
                TEliteAnswer eliteAnswer = eliteAdapter.getAnswerById(answerId);
                String bpIdStr = String.valueOf(eliteAnswer.getBpId());
                if (userStatisticMap.containsKey(bpIdStr))
                    userStatisticMap.put(bpIdStr, userStatisticMap.get(bpIdStr) + 1);
                else
                    userStatisticMap.put(bpIdStr, 1);
            }
        }catch(Exception e){
            logger.error("", e);
        }
        sortedList = this.sortHashMap(userStatisticMap);
        for(Map.Entry<String, Integer> entry : sortedList){
            Long bpId = Long.parseLong(entry.getKey());
            Integer growQAD = entry.getValue();
            UserInfo userInfo = userInfoService.getDecorateUserInfoByBpid(bpId);
            ActiveUserBean activeUserBean = new ActiveUserBean();
            activeUserBean.setLink(userInfoService.getUserHomeUrl(bpId));
            activeUserBean.setName(userInfo.getNick());
            activeUserBean.setGrowQAD(growQAD);

            try {
                searchQuestionCondition = new TSearchQuestionCondition();
                searchQuestionCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setMinCreateTime(sdf.parse(weekAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                TQuestionIdListResult questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
                activeUserBean.setGrowQAW((int)questionIdListResult.getTotal());

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setMinCreateTime(sdf.parse(weekAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                TAnswerIdListResult answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                activeUserBean.setGrowQAW(activeUserBean.getGrowQAW() + (int)answerIdListResult.getTotal());

                searchQuestionCondition = new TSearchQuestionCondition();
                searchQuestionCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setMinCreateTime(sdf.parse(monthAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
                activeUserBean.setGrowQAM((int)questionIdListResult.getTotal());

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setMinCreateTime(sdf.parse(monthAgoTimeStr).getTime())
                        .setMaxCreateTime(sdf.parse(sdf.format(new Date())).getTime())
                        .setFrom(0)
                        .setCount(1);
                answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                activeUserBean.setGrowQAM(activeUserBean.getGrowQAM() + (int)answerIdListResult.getTotal());

                searchQuestionCondition = new TSearchQuestionCondition();
                searchQuestionCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setFrom(0)
                        .setCount(1);
                questionIdListResult = eliteAdapter.searchQuestionId(searchQuestionCondition);
                activeUserBean.setTotalQA((int)questionIdListResult.getTotal());

                searchAnswerCondition = new TSearchAnswerCondition();
                searchAnswerCondition.setStatusArray(AnswerValidStatusArray)
                        .setBpId(bpId)
                        .setFrom(0)
                        .setCount(1);
                answerIdListResult = eliteAdapter.searchAnswerId(searchAnswerCondition);
                activeUserBean.setTotalQA(activeUserBean.getTotalQA() + (int)answerIdListResult.getTotal());

                activeUserBean.setGrowLikedD(0);
                activeUserBean.setGrowSharedD(0);
                activeUserList.add(activeUserBean);

                if(++activeUserCount >= ACTIVE_USER_NUM)
                    break;
            }catch(Exception e){
                logger.error("",e);
            }

        }
        reportBean.setACTIVE_USER_LIST(activeUserList);
        pauseTime = System.currentTimeMillis();
        logger.info("get ACTIVE_USER_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;


        try {
            Map<String, Object> model = new HashMap<>();
            model = transBean2Map(reportBean);
            String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/daily_report.vm", "utf-8", model);
            logger.info(content);
            String[] mailSendToArray = mailSendTo.split(FIELD_SPLIT_CHAR);
            if(null == mailSendToArray || mailSendToArray.length <= 0)
                return retVal;


            retVal = emailService.send(from, mailSendToArray, title, content);
        }catch(Exception e){
            logger.error("", e);
        }
        return retVal;
    }

    @Override
    public ReferBean getReferByAgent(String dateStr, String targetUrl, String userAgent){
        ReferBean referBean = new ReferBean();
        String hqlQuery = "select count(*) as pv, count(distinct(ip)) as uv from " + LOG_TABLE_NAME + " where time like '" + dateStr + "%' and url like '" + targetUrl + "%' and useragent like '%" + userAgent + "%'";
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 1) {
            String resultRaw = resultList.get(0);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String pvStr = pvStrArray[0];
            logger.info(pvStr);

            resultRaw = resultList.get(1);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String uvStr = uvStrArray[0];
            logger.info(uvStr);


            try{
                referBean.setPv(Integer.parseInt(pvStr));
                referBean.setUv(Integer.parseInt(uvStr));
            }catch(Exception e){
                logger.error("", e);
            }

        }
        return referBean;
    }

    @Override
    public ReferBean getReferByRefer(String dateStr, String targetUrl, String referUrl) {
        ReferBean referBean = new ReferBean();
        String hqlQuery = "select count(*) as pv, count(distinct(ip)) as uv from " + LOG_TABLE_NAME + " where time like '" + dateStr + "%' and url like '" + targetUrl + "%' and refer like '%" + referUrl + "%'";
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 1) {
            String resultRaw = resultList.get(0);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String pvStr = pvStrArray[0];
            logger.info(pvStr);

            resultRaw = resultList.get(1);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String uvStr = uvStrArray[0];
            logger.info(uvStr);


            try{
                referBean.setPv(Integer.parseInt(pvStr));
                referBean.setUv(Integer.parseInt(uvStr));
            }catch(Exception e){
                logger.error("", e);
            }

        }
        return referBean;
    }

    /*
    public List<ReferBean> getArticleReferList(String dateStr){
        int referCount = 0;
        List<ReferBean> referBeanList = new ArrayList<>();
        String hqlQuery = "select refer, count(*) as pv, count(distinct(ip)) as uv from " + LOG_TABLE_NAME + " where time like '" + dateStr + "%' and url like '"+MAIN_URL+"%' group by refer order by pv desc";
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }

        if (null != resultList && resultList.size() > 0) {
            String resultRaw = resultList.get(0);
            String[] referArray = resultRaw.split(HIVE_SPLIT_CHAR);
            resultRaw = resultList.get(1);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            resultRaw = resultList.get(2);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            try{
                if(referArray.length != pvStrArray.length || pvStrArray.length != uvStrArray.length){
                    logger.error("query result is not right");
                    return referBeanList;
                }

                for(int i=0; i<referArray.length; i++){
                    ReferBean referBean = new ReferBean();
                    referBean.setLink(referArray[i]);
                    referBean.setPv(Integer.parseInt(pvStrArray[i]));
                    referBean.setUv(Integer.parseInt(uvStrArray[i]));

                    referBeanList.add(referBean);
                    if(++referCount >= REFER_LIM_NUM)
                        break;
                }
            }catch(Exception e){
                logger.error("", e);
            }

        }
        return referBeanList;
    }
    */
    @Override
    public List<ReferBean> getReferList(String dateStr) {
        int referCount = 0;
        List<ReferBean> referList = new ArrayList<>();
        String hqlQuery = "select refer, count(*) as pv, count(distinct(ip)) as uv from " + LOG_TABLE_NAME + " where time like '" + dateStr + "%' and url like '" + HOST + "%' group by refer order by pv desc";
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }

        String articleQuery = "select count(*) as pv, count(distinct(ip)) as uv from " + LOG_TABLE_NAME + " where time like '" + dateStr + "%' and url like '" + HOST + "%' and refer like '%/article/%'";
        logger.info(articleQuery);
        Integer articlePv = 0;
        Integer articleUv = 0;
        List<String> articleList = new ArrayList<>();
        try {
            articleList = hiveClient.query(articleQuery);
            articlePv = Integer.parseInt(articleList.get(0).split(HIVE_SPLIT_CHAR)[0]);
            articleUv = Integer.parseInt(articleList.get(1).split(HIVE_SPLIT_CHAR)[0]);
        }catch (Exception e){
            logger.error("", e);
        }

        if (null != resultList && resultList.size() > 0) {
            String resultRaw = resultList.get(0);
            String[] referArray = resultRaw.split(HIVE_SPLIT_CHAR);
            resultRaw = resultList.get(1);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            resultRaw = resultList.get(2);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            try{
                if(referArray.length != pvStrArray.length || pvStrArray.length != uvStrArray.length){
                    logger.error("query result is not right");
                    return referList;
                }

                for(int i=0; i<referArray.length; i++){
                    ReferBean referBean = new ReferBean();
                    if(referArray[i].startsWith("http://home.focus.cn/article/"))
                        continue;
                    if(articlePv >= Integer.parseInt(pvStrArray[i])){
                        ReferBean extraReferBean = new ReferBean();
                        extraReferBean.setLink("文章最终页");
                        extraReferBean.setPv(articlePv);
                        extraReferBean.setUv(articleUv);
                        referList.add(extraReferBean);

                        articlePv = 0;
                        articleUv = 0;
                        if(++referCount >= REFER_LIM_NUM)
                            break;
                    }

                    referBean.setLink(referArray[i]);
                    referBean.setPv(Integer.parseInt(pvStrArray[i]));
                    referBean.setUv(Integer.parseInt(uvStrArray[i]));

                    referList.add(referBean);
                    if(++referCount >= REFER_LIM_NUM)
                        break;
                }
            }catch(Exception e){
                logger.error("", e);
            }

        }
        return referList;
    }

    @Override
    public List<SourceBean> getSourceBeanList(String timeStr, String targetUrl) {
        List<SourceBean> sourceBeanList = new ArrayList<>();
        String hqlQuery = "select ip, parameters from javaweb_log where time like '" + timeStr + "%' and url like '" + targetUrl + "%' and parameters like '%source%' and ip != ''";
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 1) {
            String ipRaw = resultList.get(0);
            String[] ipArray = ipRaw.split(HIVE_SPLIT_CHAR);

            String parametersRaw = resultList.get(1);
            String[] parametersArray = parametersRaw.split(HIVE_SPLIT_CHAR);

            if(ipArray.length != parametersArray.length){
                logger.error("source bean query result error");
                return sourceBeanList;
            }

            logger.info("Source length="+ipArray.length);

            Map<String, Integer> sourceStatisticMap = new HashMap<>();
            Map<String, Set<String>> sourceUvMap = new HashMap<>();

            Map<String, Integer> sourceStatisticMapSp = new HashMap<>();
            Map<String, Set<String>> sourceUvMapSp = new HashMap<>();
            for(int i=0; i<parametersArray.length; i++){
                try{
                    String parameters = parametersArray[i];
                    String ip = ipArray[i];
                    JSONObject paraJSON = JSONObject.fromObject(parameters);
                    if(paraJSON.containsKey("source") && paraJSON.containsKey("pos")) {
                        String source = paraJSON.getJSONArray("source").getString(0);
                        String pos = paraJSON.getJSONArray("pos").getString(0);
                        if(StringUtils.isBlank(pos))
                            continue;

                        if(!Pattern.compile("^(\\d[\\d\\.]*).*$").matcher(pos).find())
                            continue;
                        pos = pos.replaceAll("^(\\d[\\d\\.]*).*$", "$1");

                        for(Map.Entry<String, SourceType> entry : SourceType.sourceTypeMap.entrySet()) {
                            SourceType sourceItem = entry.getValue();
                            if (source.equalsIgnoreCase(sourceItem.getSource())) {
                                if (sourceStatisticMap.containsKey(sourceItem.getSourceDesc())) {
                                    sourceStatisticMap.put(sourceItem.getSourceDesc(), sourceStatisticMap.get(sourceItem.getSourceDesc()) + 1);
                                    sourceUvMap.get(sourceItem.getSourceDesc()).add(ip);
                                } else {
                                    sourceStatisticMap.put(sourceItem.getSourceDesc(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(sourceItem.getSourceDesc(), ipSet);
                                }

                                String specificPos = sourceItem.getDesc() + "_" + pos;
                                if (sourceStatisticMapSp.containsKey(specificPos)) {
                                    sourceStatisticMapSp.put(specificPos, sourceStatisticMapSp.get(specificPos) + 1);
                                    sourceUvMapSp.get(specificPos).add(ip);
                                } else {
                                    sourceStatisticMapSp.put(specificPos, 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMapSp.put(specificPos, ipSet);
                                }
                            }
                        }
                    }

                            /*
                            if(SM_SMALL_PIC.getPos().equalsIgnoreCase("0")){
                                if(sourceStatisticMap.containsKey(SM_SMALL_PIC.getCode())){
                                    sourceStatisticMap.put(SM_SMALL_PIC.getCode(), sourceStatisticMap.get(SM_SMALL_PIC.getCode()) + 1);

                                    sourceUvMap.get(SM_SMALL_PIC.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(SM_SMALL_PIC.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(SM_SMALL_PIC.getCode(), ipSet);
                                }
                            } else{
                                if(sourceStatisticMap.containsKey(SM_JIAJU_TEXT.getCode())){
                                    sourceStatisticMap.put(SM_JIAJU_TEXT.getCode(), sourceStatisticMap.get(SM_JIAJU_TEXT.getCode()) + 1);

                                    sourceUvMap.get(SM_JIAJU_TEXT.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(SM_JIAJU_TEXT.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(SM_JIAJU_TEXT.getCode(), ipSet);
                                }
                            }
                        } else if(source.equalsIgnoreCase(SM_SHEQU_TEXT.getSource())){
                            if(sourceStatisticMap.containsKey(SM_SHEQU_TEXT.getCode())){
                                sourceStatisticMap.put(SM_SHEQU_TEXT.getCode(), sourceStatisticMap.get(SM_SHEQU_TEXT.getCode()) + 1);

                                sourceUvMap.get(SM_SHEQU_TEXT.getCode()).add(ip);
                            }else{
                                sourceStatisticMap.put(SM_SHEQU_TEXT.getCode(), 1);

                                Set<String> ipSet = new HashSet<>();
                                ipSet.add(ip);
                                sourceUvMap.put(SM_SHEQU_TEXT.getCode(), ipSet);
                            }
                        } else if(source.equalsIgnoreCase(NM_SMALL_PIC.getSource())){
                            if(NM_SMALL_PIC.getPos().equalsIgnoreCase("0")){
                                if(sourceStatisticMap.containsKey(NM_SMALL_PIC.getCode())){
                                    sourceStatisticMap.put(NM_SMALL_PIC.getCode(), sourceStatisticMap.get(NM_SMALL_PIC.getCode()) + 1);

                                    sourceUvMap.get(NM_SMALL_PIC.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(NM_SMALL_PIC.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(NM_SMALL_PIC.getCode(), ipSet);
                                }

                            } else if(NM_HEADLINE.getPos().equalsIgnoreCase("1")){
                                if(sourceStatisticMap.containsKey(NM_HEADLINE.getCode())){
                                    sourceStatisticMap.put(NM_HEADLINE.getCode(), sourceStatisticMap.get(NM_HEADLINE.getCode()) + 1);

                                    sourceUvMap.get(NM_HEADLINE.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(NM_HEADLINE.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(NM_HEADLINE.getCode(), ipSet);
                                }
                            } else {
                                if(sourceStatisticMap.containsKey(NM_TEXT.getCode())){
                                    sourceStatisticMap.put(NM_TEXT.getCode(), sourceStatisticMap.get(NM_TEXT.getCode()) + 1);

                                    sourceUvMap.get(NM_TEXT.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(NM_TEXT.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(NM_TEXT.getCode(), ipSet);
                                }
                            }

                        } else if(source.equalsIgnoreCase(PW_SMALL_PIC.getSource())){
                            if(PW_SMALL_PIC.getPos().equalsIgnoreCase("0")){
                                if(sourceStatisticMap.containsKey(PW_SMALL_PIC.getCode())){
                                    sourceStatisticMap.put(PW_SMALL_PIC.getCode(), sourceStatisticMap.get(PW_SMALL_PIC.getCode()) + 1);

                                    sourceUvMap.get(PW_SMALL_PIC.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(PW_SMALL_PIC.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(PW_SMALL_PIC.getCode(), ipSet);
                                }

                            } else{
                                if(sourceStatisticMap.containsKey(PW_TEXT.getCode())){
                                    sourceStatisticMap.put(PW_TEXT.getCode(), sourceStatisticMap.get(PW_TEXT.getCode()) + 1);

                                    sourceUvMap.get(PW_TEXT.getCode()).add(ip);
                                }else{
                                    sourceStatisticMap.put(PW_TEXT.getCode(), 1);

                                    Set<String> ipSet = new HashSet<>();
                                    ipSet.add(ip);
                                    sourceUvMap.put(PW_TEXT.getCode(), ipSet);
                                }
                            }
                        } else if(source.equalsIgnoreCase(MSM_JIAJU.getSource())) {
                            if(sourceStatisticMap.containsKey(MSM_JIAJU.getCode())){
                                sourceStatisticMap.put(MSM_JIAJU.getCode(), sourceStatisticMap.get(MSM_JIAJU.getCode()) + 1);

                                sourceUvMap.get(MSM_JIAJU.getCode()).add(ip);
                            }else{
                                sourceStatisticMap.put(MSM_JIAJU.getCode(), 1);

                                Set<String> ipSet = new HashSet<>();
                                ipSet.add(ip);
                                sourceUvMap.put(MSM_JIAJU.getCode(), ipSet);
                            }
                        }

                    }
                    */
                }catch (Exception e){
                    logger.error("", e);
                }
            }
            for(Map.Entry<String, Integer> sourcePvEntry : sourceStatisticMap.entrySet()){
                SourceBean sourceBean = new SourceBean();
                String sourceDesc = sourcePvEntry.getKey();
                Integer pv = sourcePvEntry.getValue();
                sourceBean.setDesc(sourceDesc);
                sourceBean.setPv(pv);
                sourceBean.setUv(sourceUvMap.get(sourceDesc).size());

                sourceBeanList.add(sourceBean);
            }

            List<Map.Entry<String, Integer>> sortedMapList = sortHashMapByKey(sourceStatisticMapSp);
            for(Map.Entry<String, Integer> sourcePvEntrySp : sortedMapList){
                SourceBean sourceBean = new SourceBean();
                String sourceDesc = sourcePvEntrySp.getKey();
                Integer pv = sourcePvEntrySp.getValue();
                sourceBean.setDesc(sourceDesc);
                sourceBean.setPv(pv);
                sourceBean.setUv(sourceUvMapSp.get(sourceDesc).size());

                sourceBeanList.add(sourceBean);
            }
        }

        return sourceBeanList;
    }

    public Integer[] getPvUv(String device, String dateStr, List<String> targetUrls, boolean blur){
        Integer pv = 0;
        Integer uv = 0;
        StringBuilder targetUrlConditionSB = new StringBuilder("");
        if(null != targetUrls && targetUrls.size() > 0){
            targetUrlConditionSB.append("(");
            for(int i=0; i<targetUrls.size(); i++){
                if(i > 0)
                    targetUrlConditionSB.append(" or ");
                if(blur) {
                    targetUrlConditionSB.append("url like '" + targetUrls.get(i) + "%'");
                }
                else {
                    targetUrlConditionSB.append("url ='" + targetUrls.get(i) + "'");
                }
            }
            targetUrlConditionSB.append(")");
        }

        StringBuilder deviceConditionSB = new StringBuilder("");
        if(StringUtils.isNotBlank(device)){
            if(device.equalsIgnoreCase(DEVICE_COMPUTER)){
                deviceConditionSB.append("devicetype != '").append(DEVICE_MOBILE).append("' and ")
                .append("devicetype != '").append(DEVICE_UNKOWN).append("' ");
            } else if(device.equalsIgnoreCase(DEVICE_MOBILE)){
                deviceConditionSB.append("devicetype = '").append(DEVICE_MOBILE).append("' ");
            }
        }
        String deviceConditionStr = deviceConditionSB.toString();
        if(StringUtils.isBlank(deviceConditionStr))
            deviceConditionStr = "1=1 ";
        String hqlQuery = "select count(*), count(distinct(ip)) from " + LOG_TABLE_NAME + " where " + deviceConditionStr + " and time like '" + dateStr + "%' and " + targetUrlConditionSB.toString();
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 1) {
            String resultRaw = resultList.get(0);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String pvStr = pvStrArray[0];
            logger.info(pvStr);

            resultRaw = resultList.get(1);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String uvStr = uvStrArray[0];
            logger.info(uvStr);


            try{
                pv = Integer.parseInt(pvStr);
                uv = Integer.parseInt(uvStr);
            }catch(Exception e){
                logger.error("", e);
                pv = 0;
                uv = 0;
            }

        }

        return new Integer[]{pv, uv};
    }
    //获取pv
    public Integer getPv(String device, String dateStr, List<String> targetUrls, boolean blur){
        StringBuilder targetUrlConditionSB = new StringBuilder("");
        if(null != targetUrls && targetUrls.size() > 0){
            targetUrlConditionSB.append("(");
            for(int i=0; i<targetUrls.size(); i++){
                if(i > 0)
                    targetUrlConditionSB.append(" or ");
                if(blur) {
                    targetUrlConditionSB.append("url like '" + targetUrls.get(i) + "%'");
                }
                else {
                    targetUrlConditionSB.append("url ='" + targetUrls.get(i) + "'");
                }
            }
            targetUrlConditionSB.append(")");
        }
        StringBuilder deviceConditionSB = new StringBuilder("");
        if(StringUtils.isNotBlank(device)){
            if(device.equalsIgnoreCase(DEVICE_COMPUTER)){
                deviceConditionSB.append("devicetype != '").append(DEVICE_MOBILE).append("' and ")
                        .append("devicetype != '").append(DEVICE_UNKOWN).append("' ");
            } else if(device.equalsIgnoreCase(DEVICE_MOBILE)){
                deviceConditionSB.append("devicetype = '").append(DEVICE_MOBILE).append("' ");
            }
        }
        String deviceConditionStr = deviceConditionSB.toString();
        if(StringUtils.isBlank(deviceConditionStr))
            deviceConditionStr = "1=1 ";
        String hqlQuery = "select count(*) from " + LOG_TABLE_NAME + " where " + deviceConditionStr + " and time like '" + dateStr + "%' and " + targetUrlConditionSB.toString();
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = hiveClient.query(hqlQuery);
        }catch (Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 0) {
            String resultRaw = resultList.get(0);
            String[] pvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String pvStr = pvStrArray[0];
            logger.info(pvStr);

            Integer pv = 0;
            try{
                pv = Integer.parseInt(pvStr);
            }catch(Exception e){
                logger.error("", e);
                pv = 0;
            }

            return pv;
        }
        return 0;
    }

    //获取uv
    public Integer getUv(String device, String dateStr, List<String> targetUrls, boolean blur){
        StringBuilder targetUrlConditionSB = new StringBuilder("");
        if(null != targetUrls && targetUrls.size() > 0){
            targetUrlConditionSB.append("(");
            for(int i=0; i<targetUrls.size(); i++){
                if(i > 0)
                    targetUrlConditionSB.append(" or ");
                if(blur) {
                    targetUrlConditionSB.append("url like '" + targetUrls.get(i) + "%'");
                }
                else {
                    targetUrlConditionSB.append("url ='" + targetUrls.get(i) + "'");
                }
            }
            targetUrlConditionSB.append(")");
        }
        StringBuilder deviceConditionSB = new StringBuilder("");
        if(StringUtils.isNotBlank(device)){
            if(device.equalsIgnoreCase(DEVICE_COMPUTER)){
                deviceConditionSB.append("devicetype != '").append(DEVICE_MOBILE).append("' and ")
                        .append("devicetype != '").append(DEVICE_UNKOWN).append("' ");
            } else if(device.equalsIgnoreCase(DEVICE_MOBILE)){
                deviceConditionSB.append("devicetype = '").append(DEVICE_MOBILE).append("' ");
            }
        }
        String deviceConditionStr = deviceConditionSB.toString();
        if(StringUtils.isBlank(deviceConditionStr))
            deviceConditionStr = "1=1 ";
        String hqlQuery = "select count(distinct(ip)) from " + LOG_TABLE_NAME + " where " + deviceConditionStr + " and time like '" + dateStr + "%' and " + targetUrlConditionSB.toString();
        logger.info(hqlQuery);
        List<String> resultList = new ArrayList<String>();
        try{
            resultList = hiveClient.query(hqlQuery);
        }catch(Exception e){
            logger.error("", e);
        }
        if (null != resultList && resultList.size() > 0) {
            String resultRaw = resultList.get(0);
            String[] uvStrArray = resultRaw.split(HIVE_SPLIT_CHAR);
            String uvStr = uvStrArray[0];
            logger.info(uvStr);

            Integer uv = 0;
            try{
                uv = Integer.parseInt(uvStr);
            }catch(Exception e){
                logger.error("", e);
                uv = 0;
            }

            return uv;
        }
        return 0;
    }

    @Override
    public List<WordBean> getHotQueryWord(String timeStr, String deviceType, boolean needDevice){
        List<WordBean> wordBeanList = new ArrayList<>();
        String extraCondition = "";
        if(needDevice) {
            StringBuilder deviceConditionSB = new StringBuilder("");
            if(StringUtils.isNotBlank(deviceType)){
                if(deviceType.equalsIgnoreCase(DEVICE_COMPUTER)){
                    deviceConditionSB.append("and devicetype != '").append(DEVICE_MOBILE).append("' and ")
                            .append("devicetype != '").append(DEVICE_UNKOWN).append("' ");
                } else if (deviceType.equalsIgnoreCase(DEVICE_MOBILE)){
                    deviceConditionSB.append("and devicetype = '").append(DEVICE_MOBILE).append("' ");
                }
            }
            extraCondition = deviceConditionSB.toString();
        }
        StringBuilder targetUrlConditionSB = new StringBuilder("");
        if(null != SearchUrlList && SearchUrlList.size() > 0){
            targetUrlConditionSB.append("and (");
            for(int i=0; i<SearchUrlList.size(); i++){
                if(i > 0)
                    targetUrlConditionSB.append(" or ");
                targetUrlConditionSB.append("url = '" + SearchUrlList.get(i) + "'");
            }
            targetUrlConditionSB.append(")");
        }
        StringBuilder querySB = new StringBuilder("");
        querySB.append("select parameters from ")
                .append(LOG_TABLE_NAME).append(" ")
                .append("where time like '" + timeStr + "%' ")
                .append(extraCondition)
                .append(targetUrlConditionSB.toString()).append(" ");
        logger.info(querySB.toString());
        List<String> resultList = new ArrayList<>();
        try{
            resultList = hiveClient.query(querySB.toString());
        }catch (Exception e){
            logger.error("", e);
        }
        if(null != resultList && resultList.size() > 0){
            Map<String, Integer> wordCountMap = new HashMap<>();
            String parametersMix = resultList.get(0);
            String[] parametersArray = parametersMix.split(HIVE_SPLIT_CHAR);
            for(String parameters : parametersArray){
                try{
                    if(StringUtils.isBlank(parameters))
                        continue;
                    JSONObject parametersJSON = JSONObject.fromObject(parameters);
                    if (null != parametersJSON && parametersJSON.containsKey("keywords")) {
                        String searchWord = parametersJSON.getJSONArray("keywords").getString(0);
                        if (StringUtils.isNotBlank(searchWord)) {
                            searchWord = searchWord.trim();
                            if (wordCountMap.containsKey(searchWord)) {
                                wordCountMap.put(searchWord, wordCountMap.get(searchWord) + 1);
                            } else {
                                wordCountMap.put(searchWord, 1);
                            }
                        }
                    }
                }catch(Exception e){
                    logger.error("", e);
                }
            }

            List<Map.Entry<String, Integer>> sortedList = this.sortHashMap(wordCountMap);
            int wordBeanCount = 0;
            for(Map.Entry<String, Integer> entry : sortedList){
                WordBean wordBean = new WordBean();
                wordBean.setWord(entry.getKey());
                wordBean.setNum(entry.getValue());

                wordBeanList.add(wordBean);
                if(++wordBeanCount >= POP_SEARCHWORD_NUM)
                    break;
            }
        }
        return wordBeanList;

    }

    @Override
    public void testScheduler() {
        logger.info("***************123****************");
    }

    @Override
    public List<HotDataBean> getHotData(List<String> targetUrls, String timeStr, String deviceType, boolean needDevice){
        List<HotDataBean> hotDataList = new ArrayList<>();
        String extraCondition = "";
        if(needDevice) {
            StringBuilder deviceConditionSB = new StringBuilder("");
            if(StringUtils.isNotBlank(deviceType)){
                if(deviceType.equalsIgnoreCase(DEVICE_COMPUTER)){
                    deviceConditionSB.append("and devicetype != '").append(DEVICE_MOBILE).append("' and ")
                            .append("devicetype != '").append(DEVICE_UNKOWN).append("' ");
                } else if (deviceType.equalsIgnoreCase(DEVICE_MOBILE)){
                    deviceConditionSB.append("and devicetype = '").append(DEVICE_MOBILE).append("' ");
                }
            }
            extraCondition = deviceConditionSB.toString();
        }
        StringBuilder targetUrlConditionSB = new StringBuilder("");
        if(null != targetUrls && targetUrls.size() > 0){
            targetUrlConditionSB.append("and (");
            for(int i=0; i<targetUrls.size(); i++){
                if(i > 0)
                    targetUrlConditionSB.append(" or ");
                targetUrlConditionSB.append("url like '" + targetUrls.get(i) + "'");
            }
            targetUrlConditionSB.append(")");
        }
        StringBuilder querySB = new StringBuilder("");
        querySB.append("select url, count(*) as pv, count(distinct(ip)) as uv from ")
                .append(LOG_TABLE_NAME).append(" ")
                .append("where time like '" + timeStr + "%' ")
                .append(extraCondition)
                .append(targetUrlConditionSB.toString()).append(" ")
                .append("group by url order by pv desc");
        logger.info(querySB.toString());
        List<String> resultList = new ArrayList<>();
        try{
            resultList = hiveClient.query(querySB.toString());
        }catch (Exception e){
            logger.error("", e);
        }
        if(null != resultList && resultList.size() > 2){
            String urlRaw = resultList.get(0);
            String pvRaw = resultList.get(1);
            String uvRaw = resultList.get(2);

            String[] urlArray = urlRaw.split(HIVE_SPLIT_CHAR);
            String[] pvStrArray = pvRaw.split(HIVE_SPLIT_CHAR);
            String[] uvStrArray = uvRaw.split(HIVE_SPLIT_CHAR);
            if(urlArray.length != pvStrArray.length || pvStrArray.length != uvStrArray.length){
                logger.error("query hive result is wrong");
                return hotDataList;
            }
            for(int i=0; i<urlArray.length; i++){
                try{
                    HotDataBean hotData = new HotDataBean();
//                    if(!urlArray[i].startsWith("http://bar.focus.cn"))
//                        urlArray[i] = "http://bar.focus.cn" + urlArray[i];
                    //hotData.setUrl(urlArray[i].replace(HOST, VISIT_HOST));
                    hotData.setUrl(urlArray[i]);
                    hotData.setPv(Integer.parseInt(pvStrArray[i]));
                    hotData.setUv(Integer.parseInt(uvStrArray[i]));
                    hotDataList.add(hotData);
                }catch(Exception e){
                    logger.error("", e);
                }
            }
        }
        return hotDataList;
    }

    //整数相除，获得两位小数的浮点
    public static float floatDivision(int a, int b) {
        return (float) Math.round(((float) a / (float) b) * 100) / 100;
    }

    public static List<Long> resolveSubjectDetail(String detail) {
        JSONObject detailJSON = JSONObject.fromObject(detail);
        Iterator it = detailJSON.keys();
        List<Long> ids = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (StringUtils.isBlank(key)) continue;
            JSONObject section = new JSONObject();
            Matcher m = Pattern.compile(SUBJECT_SPLIT_CHAR + "(.*)").matcher(key);
            String name = key, sectionBrief = "";
            if (m.find()) {
                name = key.substring(0, m.start());
                sectionBrief = m.group(1);
            }
            section.put("section_name", name);
            section.put("section_brief", sectionBrief);

            String detailString = detailJSON.getString(key);
            if (detailString.contains("##")) {
                m = Pattern.compile("(\\d+)\\s+##").matcher(detailString);
                while (m.find()) {
                    ids.add(Long.valueOf(m.group(1)));
                }

            } else {
                String[] idStrArray = detailString.split(" ");
                for(String idStr : idStrArray){
                    ids.add(Long.parseLong(idStr));
                }
            }

        }
        return ids;
    }

    public static List<Map.Entry<String, Integer>> sortHashMap(Map<String, Integer> map){
        //将map.entrySet()转换成list
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    public static List<Map.Entry<String, Integer>> sortHashMapByKey(Map<String, Integer> map){
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }

    public static Map<String, Object> transBean2Map(Object obj) {

        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }
}
