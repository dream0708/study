package com.sohu.bp.elite.service;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.model.TSearchUserCondition;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*.xml", "classpath:bpEliteServer:*.xml"})
public class EliteSearchServiceTest {
	private static final Logger log = LoggerFactory.getLogger(EliteSearchServiceTest.class);
	
	@Resource
	private EliteSearchService eliteSearchService;
	
	@Test
	public void testSearchUser(){
		TSearchUserCondition condition = new TSearchUserCondition();
		condition.setMinFirstLoginTime(0l).setMaxFirstLoginTime(new Date().getTime()).setFrom(0).setCount(10);
		ListResult listResult = eliteSearchService.searchUser(condition);
		long num = listResult.getTotal();
		Assert.isTrue(num > 0);
	}
}
