package com.sohu.bp.elite.web;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TQuestionIdListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;

public class TestUnansweredQuestions {
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final Logger log = LoggerFactory.getLogger(TestUnansweredQuestions.class);
    
    @Test
    public void testRebuild() {
        long bpId = 9065211;
        int count = 300;
        TSearchQuestionCondition condition = new TSearchQuestionCondition();
        condition.setCount(count).setSortField("updateTime").setSortType(SortType.DESC);
        int i = 0;
        while(i++<2) {
            try {
                long start = new Date().getTime();
                TQuestionIdListResult listResult = eliteAdapter.searchQuestionId(condition);
                List<Long> questionIds = listResult.getQuestionIds();
                long startId = questionIds.get(0);
                long endId = questionIds.get(questionIds.size() - 1);
                long latestTime = eliteAdapter.getQuestionById(startId).getUpdateTime();
                long oldestTime = eliteAdapter.getQuestionById(endId).getUpdateTime();
                TSearchAnswerCondition answerCondition = new TSearchAnswerCondition();
                answerCondition.setMaxPublishTime(latestTime).setMinPublishTime(oldestTime).setBpId(bpId).setFrom(0).setCount(Integer.MAX_VALUE);
                TAnswerListResult answerListResult = eliteAdapter.searchAnswer(answerCondition);
                List<TEliteAnswer> answers = answerListResult.getAnswers();
                for (TEliteAnswer answer : answers) {
                    questionIds.remove(eliteAdapter.getAnswerById(answer.getId()).getQuestionId());
                }
                long end = new Date().getTime();
                System.out.println("cost time : " + (end - start));
                System.out.println("result : " + questionIds);
                System.out.println("result size : " + questionIds.size());
            } catch(Exception e) {
                log.info("", e);
            }
        }
    }
    
//    @Test
    public void testThriftTime() {
        try {
            TSearchQuestionCondition condition = new TSearchQuestionCondition();
            condition.setCount(300).setSortField("updateTime").setSortType(SortType.DESC);
            TQuestionIdListResult listResult = eliteAdapter.searchQuestionId(condition);
            List<Long> ids = listResult.getQuestionIds();
            for (Long id : ids) {
                long start = new Date().getTime();
                TEliteQuestion question = eliteAdapter.getQuestionById(id);
                long end = new Date().getTime();
                System.out.println("cost time : " + (end - start));
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
    public static void main(String[] args) {
    }
}
