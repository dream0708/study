/*
package com.sohu.bp.elite.dao;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.persistence.EliteQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;

*/
/**
 * @author zhangzhihao
 *         2016/7/11
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
public class EliteQuestionDaoTest {

    @Resource
    private EliteQuestionDao questionDao;

    @Test
    public void testSave(){
        EliteQuestion question = new EliteQuestion();
        question.setBpId(12619L);
        question.setCreateTime(new Date());
        question.setTitle("test qustion title dfgdsddfgdf");
        question.setDetail("test detail for question ffsfdgdf");
        question.setStatus(EliteQuestionStatus.AUDITING.getValue());
        Long id = questionDao.save(question);
        System.out.println("\n save result=" + id);
    }

    @Test
    public void testUpdate(){
        EliteQuestion question = new EliteQuestion();
        question.setId(702L);
        question.setBpId(12619L);
        question.setCreateTime(new Date());
        question.setTitle("this is title");
        question.setStatus(EliteQuestionStatus.AUDITING.getValue());
        questionDao.update(question);
    }

    @Test
    public void testGet(){
        EliteQuestion question = questionDao.get(702L);
        System.out.println("\nid="+question.getId());
        System.out.println("bpId="+question.getBpId());
        System.out.println("status="+question.getStatus());
    }

    @Test
    public void testGetQuestions(){
        ListResult listResult1 = questionDao.getQuestions(12619L, 0, 100);
        System.out.println("\nall questions are:");
        EliteQuestion question;
        if(listResult1 != null && listResult1.getTotal() > 0)
        for(Object obj : listResult1.getEntities()){
            question = (EliteQuestion)obj;
            System.out.println(String.format("bpId=%s, id=%s, status=%s",
                    question.getBpId(), question.getId(), question.getStatus()));
        }

        System.out.println("----------------------");

        ListResult listResult2 = questionDao.getQuestions(12619L, EliteQuestionStatus.PUBLISHED.getValue(), 0, 10);
        System.out.println("published questions are:");
        if(listResult2 != null && listResult2.getTotal() > 0)
        for(Object obj : listResult2.getEntities()){
            question = (EliteQuestion)obj;
            System.out.println(String.format("bpId=%s, id=%s, status=%s",
                    question.getBpId(), question.getId(), question.getStatus()));
        }

        System.out.println("----------------------");

        ListResult listResult3 = questionDao.getAuditingQuestions(0, 100);
        System.out.println("auditing questions are:");
        if(listResult3 != null && listResult3.getTotal() > 0)
        for(Object obj : listResult3.getEntities()){
            question = (EliteQuestion)obj;
            System.out.println(String.format("bpId=%s, id=%s, status=%s",
                    question.getBpId(), question.getId(), question.getStatus()));
        }

    }

}
*/
