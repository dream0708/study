package com.sohu.bp.elite.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.persistence.EliteQuestionByspecial;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
public class EliteQuestionBySpeicalDaoTest {
	@Resource
	EliteQuestionBySpecialDao eliteQuestionBySpecialDao;

//	@Test
//	public void TestSave(){
//		EliteQuestionByspecial eliteQuestionByspecial = new EliteQuestionByspecial();
//		eliteQuestionByspecial.setQuestionId(123l).setSpecialId(0l).setSpecialType(BpType.Elite_Column.getValue());
//		Long id = eliteQuestionBySpecialDao.save(eliteQuestionByspecial);
//		Assert.isTrue(id > 0);
//		eliteQuestionByspecial.setQuestionId(125l).setSpecialId(0l).setSpecialType(BpType.Elite_Column.getValue());
//		id = eliteQuestionBySpecialDao.save(eliteQuestionByspecial);
//		Assert.isTrue(id > 0);
//	}
	@Test
	public void TestUpdate(){
		EliteQuestionByspecial eliteQuestionByspecial = eliteQuestionBySpecialDao.get(0l, 123l);
		eliteQuestionByspecial.setQuestionId(1l);
		boolean result = eliteQuestionBySpecialDao.update(eliteQuestionByspecial);
		Assert.isTrue(result);
	}
	@Test
	public void TestGetQuestions(){
		List<Long> questionsList = eliteQuestionBySpecialDao.getQuestionsBySpecial(0l, BpType.Elite_Column.getValue());
		System.out.println("get questions num = " + questionsList.size());
		Assert.isTrue(questionsList.size() > 0  );
	}
}
