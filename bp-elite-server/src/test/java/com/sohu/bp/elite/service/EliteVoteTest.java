package com.sohu.bp.elite.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.util.ConvertUtil;

/**
 * 测试站队和投票功能
 * @author zhijungou
 * 2017年4月7日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:spring/*.xml", "classpath:bpEliteServer:*.xml"})
public class EliteVoteTest {
    private static final Logger log = LoggerFactory.getLogger(EliteVoteTest.class);
    
    @Resource
    private EliteQuestionService questionService;
    @Resource
    private EliteAnswerService answerService;
    @Resource
    private NotifyService notifyService;
    
//    @Test
//    public void testVoteAction() {
//        JSONArray array = new JSONArray();
//        JSONObject ele = new JSONObject();
//        ele.put("description", "这是投票选项一");
//        ele.put("img", "http://avatarimg.bjcnc.img.sohucs.com/bp_ac052522f5614399b7eb46e06efa4cb1");
//        array.add(ele);
//        JSONObject ele2 = new JSONObject();
//        ele2.put("description", "这是投票选项二");
//        ele2.put("img", "http://s1.life.itc.cn/img/201609/9ee902a22ad26580598647f639fd6da4");
//        array.add(ele2);
//        JSONObject ele3 = new JSONObject();
//        ele3.put("description", "这是投票选项三");
//        ele3.put("img", "http://avatarimg.bjcnc.img.sohucs.com/bp_8338bb5783d94083a858a3d62a462a90");
//        array.add(ele3);
//        System.out.println("result : " + array);
//        TEliteQuestion question = new TEliteQuestion();
//        question.setBpId(7298258)
//        .setTitle("投票测试")
//        .setDetail("测试内容")
//        .setTagIds("")
//        .setSource(TEliteSourceType.WRITE_PC.getValue())
//        .setCreateTime(System.currentTimeMillis())
//        .setUpdateTime(System.currentTimeMillis())
//        .setPublishTime(System.currentTimeMillis())
//        .setCreateHost(0)
//        .setCreatePort(0)
//        .setSpecialType(BpType.Elite_Vote.getValue())
//        .setOptions(array.toString());
//        Long questionId = questionService.insert(ConvertUtil.convert(question));
//        System.out.println("result : " + questionId);
//    }
    
    @Test
    public void notifyTest() {
        Long id = 16103L;
        notifyService.notify2Statistic(id, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
    }

}
