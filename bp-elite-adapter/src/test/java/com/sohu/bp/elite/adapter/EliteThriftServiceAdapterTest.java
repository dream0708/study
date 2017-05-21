package com.sohu.bp.elite.adapter;

import com.sohu.bp.elite.enums.EliteAdminStatus;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.*;
import org.apache.thrift.TException;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/19
 */
public class EliteThriftServiceAdapterTest {

    private static final EliteThriftServiceAdapter adapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();

    @Test
    public void testInsertQuestion(){
        try {
            TEliteQuestion question = new TEliteQuestion();
            question.setBpId(12619L);
            question.setCreateTime(new Date().getTime());
            question.setStatus(EliteQuestionStatus.AUDITING.getValue());
            question.setTitle("测试问题1844");
            question.setDetail("问题详情1234567");
            long result  = adapter.insertQuestion(question);
            Assert.isTrue(result > 0);
            System.out.println("\n insert question result=" + result);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchQuestion(){
        TSearchQuestionCondition condition = new TSearchQuestionCondition();
        condition.setKeywords("测试");
        condition.setFrom(0);
        condition.setCount(100);

        try {
            TQuestionListResult listResult = adapter.searchQuestion(condition);
            System.out.println();
            System.out.println("search question total=" + listResult.getTotal());
            for(TEliteQuestion question : listResult.getQuestions()) {
                System.out.println(String.format("id=%d, title=%s, detail=%s",
                        question.getId(), question.getTitle(), question.getDetail()));
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetQuestion(){
        try {
            TEliteQuestion question = adapter.getQuestionById(702L);
            System.out.println();
            System.out.println(String.format("id=%s, bpId=%s, status=%s",
                    question.getBpId(), question.getId(), question.getStatus()));
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuestionAudit(){
        boolean result;
        try {
            /*result = adapter.passOneQuestion(702L);
            Assert.isTrue(result);

            result = adapter.rejectOneQuestion(702L);
            Assert.isTrue(result);

            List<Long> passQuestionIds = new ArrayList<>();
            passQuestionIds.add(702L);

            List<Long> rejectedQuestionIds = new ArrayList<>();
            rejectedQuestionIds.add(802L);

            result = adapter.batchAuditQuestion(passQuestionIds, rejectedQuestionIds);
            Assert.isTrue(result);*/

            /*result = adapter.deleteOneQuestion(702L);*/
            result = adapter.sysDeleteOneQuestion(702L);
            Assert.isTrue(result);

        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAuditingQuestions() {
        try {
            TQuestionListResult listResult = adapter.getAuditingQuestions(0, 10);
            System.out.println("\nauditing question size=" + listResult.getTotal());
            for(TEliteQuestion question : listResult.getQuestions()){
                System.out.println(String.format("id=%s, bpId=%s, status=%s",
                        question.getBpId(), question.getId(), question.getStatus()));
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetQuestionByIds(){
        try {
            List<Long> ids = new ArrayList<>();
            ids.add(702L);
            ids.add(802L);
            ids.add(902L);
            List<TEliteQuestion> questions = adapter.getQuestionsByIds(ids);
            System.out.println("\n  questions size=" + questions.size());
            for(TEliteQuestion question : questions){
                System.out.println(String.format("id=%s, bpId=%s, status=%s",
                        question.getBpId(), question.getId(), question.getStatus()));
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testInsertAnswer(){
        try {
            TEliteAnswer answer = new TEliteAnswer();
            answer.setBpId(1234L);
            answer.setCreateTime(new Date().getTime());
            answer.setStatus(EliteAnswerStatus.AUDITING.getValue());
            answer.setContent("answer content");
            answer.setQuestionId(702L);
            long result = adapter.insertAnswer(answer);
            Assert.isTrue(result > 0);
            System.out.println("\n insert answer result=" + result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAnswer(){
        try {
            TEliteAnswer answer = adapter.getAnswerById(302L);
            System.out.println();
            System.out.println(String.format("answer(id=%d, bpId=%d, questionId=%d, status=%d, content=%s, createTime=%d, updateTime=%d)",
                    answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime(), answer.getUpdateTime()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateAnswer(){
        try{
            TEliteAnswer answer = adapter.getAnswerById(302L);
            answer.setStatus(EliteAnswerStatus.REJECTED.getValue());
            boolean result = adapter.updateAnswer(answer);
            Assert.isTrue(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAnswersByQuestionId(){
        try{
            List<TEliteAnswer> answers = adapter.getAnswersByQuestionId(702);
            System.out.println();
            System.out.println("question answers size=" + answers.size());
            for(TEliteAnswer answer : answers){
                System.out.println(String.format("answer(id=%d, bpId=%d, questionId=%d, status=%d, content=%s, createTime=%s)",
                        answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAnswersByBpId(){
        try{
            List<TEliteAnswer> answers = adapter.getAnswersByBpId(1234);
            System.out.println();
            System.out.println("user answers size=" + answers.size());
            for(TEliteAnswer answer : answers){
                System.out.println(String.format("answer(id=%d, bpId=%d, questionId=%d, status=%d, content=%s, createTime=%s)",
                        answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAuditingAnswers(){
        try{
            TAnswerListResult  listResult = adapter.getAuditingAnswers(0, 10);
            System.out.println();
            System.out.println("user answers size=" + listResult.getTotal());
            for(TEliteAnswer answer : listResult.getAnswers()){
                System.out.println(String.format("answer(id=%d, bpId=%d, questionId=%d, status=%d, content=%s, createTime=%s)",
                        answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testAuditAnswer(){
        boolean result;
        try{
            List<Long> passIds = new ArrayList<>();
            passIds.add(202L);
            passIds.add(302L);

            List<Long> rejectedIds = new ArrayList<>();
            rejectedIds.add(402L);

            result = adapter.batchAuditAnswer(passIds, rejectedIds);
            Assert.isTrue(result);

            /*result = adapter.deleteOneAnswer(202);
            Assert.isTrue(result);*/

            /*result = adapter.sysDeleteOneAnswer(202);
            Assert.isTrue(result);*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void inserAdmin(){
        TEliteAdmin eliteAdmin = new TEliteAdmin();
        eliteAdmin.setBpId(19267L);
        eliteAdmin.setStatus(EliteAdminStatus.SUPER_ADMIN.getValue());
        try {
            adapter.saveEliteAdmin(eliteAdmin);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getQuestionAnswer(){
        try {
            List<TEliteAnswer> answers = adapter.getAnswersByQuestionId(14105);
            answers.forEach(answer -> System.out.println(answer.getId() + ":" + answer.getContent()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void testSearchQuestionId() {
        TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
        searchQuestionCondition.setStatusArray(new StringBuilder("").append(EliteQuestionStatus.AUDITING.getValue())
                .append(";").append(EliteQuestionStatus.PASSED.getValue())
                .append(";").append(EliteQuestionStatus.PUBLISHED.getValue()).toString());
        //searchQuestionCondition.setKeywords("上海");
        searchQuestionCondition.setFrom(0);
        searchQuestionCondition.setCount(30);
        searchQuestionCondition.setAutoComplete(1);
        searchQuestionCondition.setSortField("updateTime");
        searchQuestionCondition.setMinAnswerNum(1);
        searchQuestionCondition.setSortType(SortType.DESC);

        try {
            TQuestionIdListResult listResult = adapter.searchQuestionId(searchQuestionCondition);
            System.out.println(listResult);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        testSearchQuestionId();
//        Thread.sleep(3000);
//        System.out.println("********");
//        boolean status = adapter.superAdmin(75604L);
//        System.out.println("***"+status);

    }
}
