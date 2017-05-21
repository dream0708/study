package com.sohu.bp.elite.data.statistic.service;

import com.sohu.bp.elite.data.statistic.bean.HotDataBean;
import com.sohu.bp.elite.data.statistic.bean.ReferBean;
import com.sohu.bp.elite.data.statistic.bean.SourceBean;
import com.sohu.bp.elite.data.statistic.bean.WordBean;

import java.util.List;

/**
 * Created by nicholastang on 2016/12/9.
 */
public interface OverallDailyReportService {
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

    public List<ReferBean> getReferList(String dateStr);
}
