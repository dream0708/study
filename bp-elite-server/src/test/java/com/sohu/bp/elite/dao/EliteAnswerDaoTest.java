/*
package com.sohu.bp.elite.dao;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.persistence.EliteAnswer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;

*/
/**
 * @author zhangzhihao
 *         2016/7/19
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
public class EliteAnswerDaoTest {

    @Resource
    private EliteAnswerDao answerDao;

    @Test
    public void testSave(){
        EliteAnswer answer = new EliteAnswer();
        answer.setBpId(1234L);
        answer.setCreateTime(new Date());
        answer.setStatus(2);
        answer.setQuestionId(702L);
        answer.setContent("another answer content for question 702");
        Long id = answerDao.save(answer);
        System.out.println("\n save answer result=" + id);
        Assert.isTrue(id > 0);
    }

    @Test
    public void testUpdate(){
        EliteAnswer answer = answerDao.getById(202L);
        Assert.notNull(answer);
        answer.setStatus(EliteAnswerStatus.PUBLISHED.getValue());
        boolean result = answerDao.update(answer);
        Assert.isTrue(result);
    }

    @Test
    public void testGetById(){
        EliteAnswer answer = answerDao.getById(202L);
        Assert.notNull(answer);
        System.out.println("\n" + String.format("answer(id=%s, bpId=%s, questionId=%s, status=%s, content=%s, createTime=%s)",
                answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime()));
    }

    @Test
    public void testGetAuditingAnswers(){
        ListResult listResult = answerDao.getAuditingAnswers(0, 10);
        if(listResult != null && listResult.getTotal() > 0){
            System.out.println("\n auditing answers size=" + listResult.getTotal());
            EliteAnswer answer;
            for(Object obj : listResult.getEntities()){
                answer = (EliteAnswer) obj;
                System.out.println(String.format("answer(id=%s, bpId=%s, questionId=%s, status=%s, content=%s, createTime=%s)",
                        answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getStatus(), answer.getContent(), answer.getCreateTime()));
            }
        }
    }
}
*/
