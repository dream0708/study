package com.sohu.bp.elite.data.statistic.service.impl;

import com.sohu.bp.elite.data.statistic.bean.*;
import com.sohu.bp.elite.data.statistic.service.DailyReportService;
import com.sohu.bp.elite.data.statistic.service.EmailService;
import com.sohu.bp.elite.data.statistic.service.OverallDailyReportService;
import com.sohu.bp.elite.hive.HiveClient;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.security.sasl.SaslException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nicholastang on 2016/12/9.
 */
public class OverallDailyReportServiceImpl implements OverallDailyReportService{
    private Logger logger = LoggerFactory.getLogger(OverallDailyReportServiceImpl.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private static final String HIVE_SPLIT_CHAR = "<>";
    private static final String FIELD_SPLIT_CHAR = ";";
    private static final String LOG_TABLE_NAME = "javaweb_log";
    private static final String from = "home-data@sohu-inc.com";
    private static final String title = "焦点家居主站访问数据每日报告";
    private static final String MAIN_URL = "/";
    private static final String ARTICLE_URL = "%article";
    private static final String ASK_URL = "http://bpelite";
    private static final String UA_WECHAT = "MicroMessenger";
    private static final String UA_SOHUNEWS = "SohuNews";
    private static final String REFER_UC = "/ds/uczzd/article";

    private static final int REFER_LIM_NUM = 50;
    private static final int POP_PAGE_NUM = 50;
    private static final int POP_ARTICLE_PAGE_NUM = 50;

    private static final String MAIN_DOMAIN = "https://home.focus.cn";

    private DailyReportService dailyReportService;
    private EmailService emailService;
    private HiveClient hiveClient;
    private VelocityEngine velocityEngine;
    private String mailSendTo = "nicholastang@sohu-inc.com";

    public DailyReportService getDailyReportService() {
        return dailyReportService;
    }

    public void setDailyReportService(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public HiveClient getHiveClient() {
        return hiveClient;
    }

    public void setHiveClient(HiveClient hiveClient) {
        this.hiveClient = hiveClient;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String getMailSendTo() {
        return mailSendTo;
    }

    public void setMailSendTo(String mailSendTo) {
        this.mailSendTo = mailSendTo;
    }

    @Override
    public boolean dailyOverallReport() {
        return this.dailyOverallReport(sdf.format(new Date()));
    }

    @Override
    public boolean dailyOverallReport(String dateStr) {
        return this.dailyOverallReport(dateStr, this.mailSendTo);
    }

    @Override
    public boolean dailyOverallReport(String dateStr, String mailSendTo) {
        boolean retVal = false;
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

        OverallDailyReportBean reportBean = new OverallDailyReportBean();
        long workTime = System.currentTimeMillis();
        long pauseTime = System.currentTimeMillis();

        reportBean.setTIME_STR(startTimeStr);


        //获取refer TOP25
        reportBean.setREFER_LIST(this.getReferList(startTimeStr));
        pauseTime = System.currentTimeMillis();
        logger.info("get REFER_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setWECHAT(dailyReportService.getReferByAgent(startTimeStr, MAIN_URL, UA_WECHAT));
        pauseTime = System.currentTimeMillis();
        logger.info("get WECHAT_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setSOHUNEWS(dailyReportService.getReferByAgent(startTimeStr, MAIN_URL, UA_SOHUNEWS));
        pauseTime = System.currentTimeMillis();
        logger.info("get SOHUNEWS_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setUCAPP(dailyReportService.getReferByRefer(startTimeStr, MAIN_URL, REFER_UC));
        pauseTime = System.currentTimeMillis();
        logger.info("get UC_REFER finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setSOURCE_LIST(dailyReportService.getSourceBeanList(startTimeStr, MAIN_URL));
        pauseTime = System.currentTimeMillis();
        logger.info("get SOURCE_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        reportBean.setASK_SOURCE_LIST(dailyReportService.getSourceBeanList(startTimeStr, ASK_URL));
        pauseTime = System.currentTimeMillis();
        logger.info("get ASK_SOURCE_LIST finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        int popPageCount = 0;
        List<PageBean>  POP_PAGE_LIST = new ArrayList<>();
        List<String> hotPageList = new ArrayList<>();
        hotPageList.add(MAIN_URL + "%");
        List<HotDataBean> hotDataList = dailyReportService.getHotData(hotPageList, startTimeStr, "", false);
        if(null != hotDataList && hotDataList.size() > 0){
            for(HotDataBean hotDataBean : hotDataList){
                PageBean pageBean = new PageBean();
                pageBean.setUrl(MAIN_DOMAIN + hotDataBean.getUrl());
                pageBean.setPv(hotDataBean.getPv());
                pageBean.setUv(hotDataBean.getUv());

                POP_PAGE_LIST.add(pageBean);
                if(++popPageCount >= POP_PAGE_NUM)
                    break;
            }
        }
        reportBean.setPOP_PAGE_LIST(POP_PAGE_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get POP_PAGE finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        int popArticlePageCount = 0;
        List<PageBean>  POP_ARTICLE_PAGE_LIST = new ArrayList<>();
        List<String> hotArticlePageList = new ArrayList<>();
        hotArticlePageList.add(ARTICLE_URL + "%");
        List<HotDataBean> articleHotDataList = dailyReportService.getHotData(hotArticlePageList, startTimeStr, "", false);
        if(null != articleHotDataList && articleHotDataList.size() > 0){
            for(HotDataBean hotDataBean : articleHotDataList){
                PageBean pageBean = new PageBean();
                pageBean.setUrl(MAIN_DOMAIN + hotDataBean.getUrl());
                pageBean.setPv(hotDataBean.getPv());
                pageBean.setUv(hotDataBean.getUv());

                POP_ARTICLE_PAGE_LIST.add(pageBean);
                if(++popArticlePageCount >= POP_ARTICLE_PAGE_NUM)
                    break;
            }
        }
        reportBean.setPOP_ARTICLE_PAGE_LIST(POP_ARTICLE_PAGE_LIST);
        pauseTime = System.currentTimeMillis();
        logger.info("get POP_ARTICLE_PAGE finish. waste time=" + (pauseTime - workTime));
        workTime = pauseTime;

        Map<String, List<String>> rssPvUv = this.rssPvUv(DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd"));

        try {
            Map<String, Object> model = new HashMap<>();
            model = DailyReportServiceImpl.transBean2Map(reportBean);
            model.put("RSS", rssPvUv);
            String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/overall_daily_report.vm", "utf-8", model);
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
    public List<ReferBean> getReferList(String dateStr) {
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

    public Map<String, List<String>> rssPvUv(String dateStr) {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, String> targetSite = new HashMap<String, String>() {
            {
                put("一点资讯",   "url like '%/ds/yidian/article%'");
                put("UC",       "url like '%/ds/uczzd/article%'");
                put("Zaker",    "url like '%source=zaker%' or refer like '%zaker.com%'");
                put("花瓣",      "url like '%source=huaban%' or refer like '%huaban.com%'");
                put("QQ导航",       "url like '%source=qq%'");
                put("360",      "url like '%source=360%' or refer like '%image.so.com%'");
                put("今日头条",   "url like '%/ds/toutiao/article%'");
                put("手机QQ",   "url like '%/ds/qqbrowser/article%'");
                put("WIFI万能钥匙",   "url like '%/ds/wifi/article%'");
            }
        };
        String hqlFormat = "select count(*) as pv, count(distinct(concat(ipstring,useragent))) as uv  from nginx_log where logdate='%s' and (%s)";
        for (Map.Entry<String, String> entry : targetSite.entrySet()) {
            String hql = String.format(hqlFormat, dateStr, entry.getValue());
            logger.info(hql);
            List<String> pvUvResult = null;
            try {
                long startTime = System.currentTimeMillis();
                pvUvResult = this.hiveClient.query(hql);
                logger.info("query pv for {} use time: {}ms, result={}",
                        new Object[]{
                        entry.getKey(), System.currentTimeMillis() - startTime, pvUvResult});
            } catch (Exception e) {
                logger.error("query pv uv failed", e);
            }
            String pv = null;
            String uv = null;
            if (null != pvUvResult && pvUvResult.size() == 2) {
                String pvString = pvUvResult.get(0);
                String uvString = pvUvResult.get(1);
                String[] pvArray = pvString.split(HIVE_SPLIT_CHAR);
                if (pvArray.length > 0)
                    pv = pvArray[0];
                String[] uvArray = uvString.split(HIVE_SPLIT_CHAR);
                if (uvArray.length > 0)
                    uv = uvArray[0];
            }
            if (StringUtils.isNotBlank(pv) && StringUtils.isNotBlank(uv))
                result.put(entry.getKey(), Arrays.asList(pv, uv));
        }
        return result;
    }
}
