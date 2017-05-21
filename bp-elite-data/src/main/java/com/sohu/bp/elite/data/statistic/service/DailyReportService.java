package com.sohu.bp.elite.data.statistic.service;

import com.sohu.bp.elite.data.statistic.bean.HotDataBean;
import com.sohu.bp.elite.data.statistic.bean.ReferBean;
import com.sohu.bp.elite.data.statistic.bean.SourceBean;
import com.sohu.bp.elite.data.statistic.bean.WordBean;

import java.util.List;

/**
 * Created by nicholastang on 2016/11/10.
 */
public interface DailyReportService {
    /**
     * 产生前一天的日报
     */
    public boolean dailyOverallReport();

    /**
     * 产生指定日期前一天的日报
     * @param dateStr
     */
    public boolean dailyOverallReport(String dateStr);

    public boolean dailyOverallReport(String dateStr, String mailSendTo);

    public List<HotDataBean> getHotData(List<String> targetUrls, String timeStr, String deviceType, boolean needDevice);

    public List<WordBean> getHotQueryWord(String timeStr, String device, boolean needDevice);

    public List<ReferBean> getReferList(String dateStr);

    public List<SourceBean> getSourceBeanList(String timeStr, String targetUrl);

    public ReferBean getReferByAgent(String dateStr, String targetUrl, String userAgent);

    public ReferBean getReferByRefer(String dateStr, String targetUrl, String referUrl);

    public void testScheduler();
}
