//package com.sohu.bp;
//
//import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
//import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
//import com.sohu.bp.elite.enums.EliteAnswerStatus;
//import com.sohu.bp.elite.enums.EliteQuestionStatus;
//import com.sohu.bp.elite.model.TAnswerListResult;
//import com.sohu.bp.elite.model.TEliteAnswer;
//import com.sohu.bp.elite.model.TEliteQuestion;
//import com.sohu.bp.elite.model.TQuestionListResult;
//import com.sohu.bp.elite.model.TSearchAnswerCondition;
//import com.sohu.bp.elite.model.TSearchQuestionCondition;
//import com.sohu.bp.elite.service.web.FeedService;
//import com.sohu.bp.elite.util.EliteStatusUtil;
//import javax.annotation.Resource;
//import org.apache.thrift.TException;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * @author zhangzhihao
// *         2016/9/12
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(value = {"classpath:bpEliteWeb/applicationContext.xml", "classpath:bpEliteWeb-servlet.xml"})
//public class RebuildFeedTest {
//
//    private Logger log = LoggerFactory.getLogger(RebuildFeedTest.class);
//
//    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//
//    @Resource
//    private FeedService feedService;
//
//    public long getBpId(){
//        return 7298258;
//    }
//
//    @Test
//    public void rebuildUserQuestions(){
//        long bpId = getBpId();
//
//        TSearchQuestionCondition condition = new TSearchQuestionCondition();
//        condition.setBpId(bpId)
//                .setFrom(0)
//                .setCount(Integer.MAX_VALUE)
//                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));
//
//        try {
//            TQuestionListResult listResult = eliteAdapter.searchQuestion(condition);
//            log.info("starting rebuild feed");
//            log.info("total question size=" + listResult.getTotal());
//
//            for(TEliteQuestion question : listResult.getQuestions()){
//
//                log.info("starting rebuild question, id=" + question.getId());
//                feedService.publishQuestion(bpId, question.getId(), question.getTagIds());
//                log.info("rebuild question succeeded");
//
//                rebuildQuestionAnswers(question.getId());
//            }
//
//        } catch (TException e) {
//            log.error("", e);
//        }
//    }
//
//    @Test
//    public void rebuildUserAnswers(){
//        long bpId = getBpId();
//
//        TSearchAnswerCondition condition = new TSearchAnswerCondition();
//        condition.setBpId(bpId)
//                .setFrom(0)
//                .setCount(Integer.MAX_VALUE)
//                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
//
//        try {
//            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
//            log.info("starting rebuild feed");
//            log.info("total answer size=" + listResult.getTotal());
//
//            for(TEliteAnswer answer : listResult.getAnswers()){
//
//                log.info("starting rebuild answer, id=" + answer.getId());
//                feedService.publishAnswer(bpId, answer.getId(), answer.getQuestionId());
//                log.info("rebuild answer succeeded");
//
//            }
//        } catch (TException e) {
//            log.error("", e);
//        }
//    }
//
//    public void rebuildQuestionAnswers(long questionId){
//        TSearchAnswerCondition condition = new TSearchAnswerCondition();
//        condition.setQuestionId(questionId)
//                .setFrom(0)
//                .setCount(Integer.MAX_VALUE)
//                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
//
//        try {
//            long bpId;
//            log.info("starting rebuild answers for question, questionId=" + questionId);
//            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
//            log.info("total answer size=" + listResult.getTotal());
//
//            for(TEliteAnswer answer : listResult.getAnswers()){
//                bpId = answer.getBpId();
//
//                log.info("starting rebuild answer, id=" + answer.getId());
//                feedService.publishAnswer(bpId, answer.getId(), answer.getQuestionId());
//                log.info("rebuild answer succeeded");
//            }
//
//        } catch (TException e) {
//            log.error("", e);
//        }
//    }
//}
