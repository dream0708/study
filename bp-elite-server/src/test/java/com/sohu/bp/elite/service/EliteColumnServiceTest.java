package com.sohu.bp.elite.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.sohu.bp.elite.enums.EliteTopicStatus;
import com.sohu.bp.elite.persistence.EliteColumn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*.xml", "classpath:bpEliteServer:*.xml"})
public class EliteColumnServiceTest {
	private static final Logger log = LoggerFactory.getLogger(EliteColumnServiceTest.class);
	@Resource
	EliteColumnService eliteColumnService;
	
	@Test
	public void insert(){
		EliteColumn eliteColumn = new EliteColumn();
		eliteColumn.setName("Hello world~~2").setCover("img://adb.jpg").setUserInfo("gg").setBrief("good").setType(1)
		.setCreateTime(new Date()).setUpdateTime(new Date()).setStatus(EliteTopicStatus.STATUS_WORK.getValue()).setContent("this is first eliteColumn");
		long id = eliteColumnService.insert(eliteColumn);
		Assert.isTrue(id > 0);
	}
	
	@Test 
	public void update(){
		long id = 1;
		EliteColumn eliteColumn = eliteColumnService.getEliteColumnById(id);
		eliteColumn.setStatus(EliteTopicStatus.STATUS_INVALID.getValue()).setName("test update");
		eliteColumnService.update(eliteColumn);
		eliteColumn = eliteColumnService.getEliteColumnById(id);
		Assert.isTrue(eliteColumn.getStatus().equals(EliteTopicStatus.STATUS_INVALID.getValue()));
	}
	
	@Test
	public void getEliteColums(){
		long count = eliteColumnService.getEliteColumnCount();
		List<EliteColumn> eliteColumns = eliteColumnService.getAllEliteColumn(0, -1);
		log.info("get all elite columns , count is " + count);
		eliteColumns.forEach(eliteColumn -> log.info(eliteColumn.toString()));
		Assert.notEmpty(eliteColumns);
	}
	
	@Test
	public void getEliteColumsByStatus(){
		long count = eliteColumnService.getEliteColumnCountByStatus(EliteTopicStatus.STATUS_WORK.getValue());
		List<EliteColumn> eliteColumns = eliteColumnService.getAllEliteColumnByStatus(0, -1, EliteTopicStatus.STATUS_WORK.getValue());
		log.info("get all elite columns , count is " + count);
		eliteColumns.forEach(eliteColumn -> log.info(eliteColumn.toString()));
		Assert.notEmpty(eliteColumns);
	}
}
