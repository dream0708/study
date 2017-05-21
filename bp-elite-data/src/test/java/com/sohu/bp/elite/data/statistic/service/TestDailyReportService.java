package com.sohu.bp.elite.data.statistic.service;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.data.statistic.bean.*;
import com.sohu.bp.elite.data.statistic.service.impl.OverallDailyReportServiceImpl;
import com.sohu.bp.elite.data.statistic.util.IDUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.hive.HiveClient;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TUserListResult;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.ObjectUserRelationListResult;
import com.sohu.bp.thallo.model.RelationStatus;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import javax.security.sasl.SaslException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nicholastang on 2016/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:/bpEliteData-servlet.xml"})
public class TestDailyReportService {
    private static Logger logger = LoggerFactory.getLogger(TestDailyReportService.class);
    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    public static final String HOST = "http://bpelite";
    private static final String VISIT_HOST = "http://bar.focus.cn";
    private static final List<String> UserUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/%s/home.html");

            add(HOST + "/p%s");
        }
    };
    private static final List<String> QuestionUrlList = new ArrayList<String>(){
        {
            add(HOST + "/ask/question/%s/answers.html");
            add(HOST + "/q/%s");
        }
    };
    @Resource
    private DailyReportService dailyReportService;
    @Resource
    private OverallDailyReportServiceImpl overallDailyReportService;
    @Resource
    private VelocityEngine velocityEngine;
    @Resource
    private HiveClient hiveClient;
    //@Test
    public void testReport(){
        dailyReportService.dailyOverallReport();
    }

    //@Test
    public void testGetHotQuery() {
        List<WordBean> wordBeanList = dailyReportService.getHotQueryWord("2016-11-14", "", false);
        for(WordBean wordBean : wordBeanList){
            logger.info(wordBean.getWord());
            logger.info(wordBean.getNum().toString());
        }
    }

    //@Test
    public void testSendMail() {
        try {

            Map<String, Object> model = new HashMap<>();
            model.put("a", 0);
            model.put("b", 0);
            model.put("number", new NumberTool());
            String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/test.vm", "utf-8", model);
            logger.info(content);
//            String[] mailSendToArray = new String[]{"nicholastang@sohu-inc.com"};
//            if(null == mailSendToArray || mailSendToArray.length <= 0)
//                return retVal;
//
//
//            retVal = emailService.send(from, mailSendToArray, title, content);
        }catch(Exception e){
            logger.error("", e);
        }
    }

    //@Test
    public void testSearch() {
        try{
            TSearchUserCondition searchUserCondition = new TSearchUserCondition();
            searchUserCondition.setStatus(EliteUserStatus.VALID.getValue())
                    .setFrom(0)
                    .setCount(1);
            TUserListResult userListResult = eliteAdapter.searchUser(searchUserCondition);
            logger.info("********************************8");
            logger.info("TOTAL " + userListResult.getTotal());
        }catch(Exception e){
            logger.error("", e);
        }
    }

    //@Test
    public void testGetHotUser(){
        int popUserCount = 0;
        List<UserBean> POP_USER_LIST = new ArrayList<>();
        List<String> hotUserList = new ArrayList<>();
        for(String userBaseUrl : UserUrlList){
            hotUserList.add(String.format(userBaseUrl, "%"));
        }
        String startTimeStr = "2016-11-20";
        List<HotDataBean> hotDataList = dailyReportService.getHotData(hotUserList, startTimeStr, "", false);
        int userLoopCount = 0;
        String reg = "";
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
                    logger.info(userId+"");

                }catch(Exception e){
                    logger.error("", e);
                    continue;
                }


                if(++popUserCount >= 30)
                    break;
            }


        }
    }

    //@Test
    public void TestGetHotQuestion(){
        String startTimeStr = "2016-11-22";
        int popQuestionCount = 0;
        List<QuestionBean> POP_QUESTION_LIST = new ArrayList<>();
        List<String> hotQuestionList = new ArrayList<>();
        for(String questionBaseUrl : QuestionUrlList){
            hotQuestionList.add(String.format(questionBaseUrl, "%"));
        }
        List<HotDataBean> hotDataList = dailyReportService.getHotData(hotQuestionList, startTimeStr, "", false);
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
                Long questionId = IDUtil.smartDecodeId(encodedQuestionId);

                try {
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

    }

    @Test
    public void testGetRefer(){
        List<ReferBean> referBeanList = overallDailyReportService.getReferList("2016-12-8");
        System.out.print(45);
    }

    //@Test
    public void testGetSourceList(){
        List<SourceBean> sourceBeanList = dailyReportService.getSourceBeanList("2016-12-7", HOST);
        for(SourceBean sourceBean : sourceBeanList){
            System.out.println(sourceBean.getDesc());
            System.out.println(sourceBean.getPv());
            System.out.println(sourceBean.getUv());
            System.out.println("----------------------------------");
        }
    }

    @Test
    public void testPvUv() {
//        List<String> query = null;
//        try {
//            query = hiveClient.query("select count(*) from nginx_log where url like \"/ds/yidian/article%\" and logdate=\"2016-12-26\"");
//        } catch (SaslException e) {
//            e.printStackTrace();
//        } catch (TException e) {
//            e.printStackTrace();
//        }
//
//        for (String s : query) {
//            System.out.println(s);
//        }

        Map<String, List<String>> map = overallDailyReportService.rssPvUv(DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd"));
        Map<String, Object> model = new HashMap<>();
        model.put("RSS", map);
        String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/overall_daily_report.vm", "utf-8", model);
        logger.info(content);
    }

    @Test
    public void testVM() {
        try {
            Map<String, Object> model = new HashMap<>();
            Map<String, List<String>> map = new HashMap<>();
            map.put("Zaker", Arrays.asList("561", "22"));
            model.put("RSS", map);
            String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/overall_daily_report.vm", "utf-8", model);
            logger.info(content);
//            String[] mailSendToArray = new String[]{"nicholastang@sohu-inc.com"};
//            if(null == mailSendToArray || mailSendToArray.length <= 0)
//                return retVal;
//
//
//            retVal = emailService.send(from, mailSendToArray, title, content);
        }catch(Exception e){
            logger.error("", e);
        }
    }

    @Test
    public void testListToString() {
        List<String> list = Arrays.asList("sss", "ttt");
        System.out.println(list);
    }
}
