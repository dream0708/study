package com.sohu.bp.elite.data.statistic.action;

import com.sohu.bp.elite.data.statistic.service.DailyReportService;
import com.sohu.bp.elite.data.statistic.service.OverallDailyReportService;
import org.apache.calcite.runtime.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by nicholastang on 2016/11/14.
 */
@Controller
@RequestMapping("/elite")
public class TrggerAction {
    @Resource
    DailyReportService reportService;

    @Resource
    OverallDailyReportService overallDailyReportService;

    @RequestMapping("/report")
    @ResponseBody
    public String trigger(String dateStr, String to)
    {
        if(StringUtils.isBlank(dateStr))
            reportService.dailyOverallReport();
        else if(StringUtils.isBlank(to))
            reportService.dailyOverallReport(dateStr);
        else
            reportService.dailyOverallReport(dateStr, to);

        return "ok";
    }

    @RequestMapping("/overall-report")
    @ResponseBody
    public String triggerOverall(String dateStr, String  to)
    {
        if(StringUtils.isBlank(dateStr))
            overallDailyReportService.dailyOverallReport();
        else if(StringUtils.isBlank(to))
            overallDailyReportService.dailyOverallReport(dateStr);
        else
            overallDailyReportService.dailyOverallReport(dateStr, to);

        return "ok";
    }
}
