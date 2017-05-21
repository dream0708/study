package com.sohu.bp;

import java.util.List;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.TEliteQuestion;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:bpEliteWeb/applicationContext.xml", "classpath:bpEliteWeb-servlet.xml"})
public class AdapterTest {
	   private static final Logger log = LoggerFactory.getLogger(AdapterTest.class);

	   private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	   
	   @Test
	   public void test(){
		   try {
			List<TEliteQuestion> questions = eliteAdapter.getQuestionsBySpecial(2l, BpType.Elite_Column.getValue());
			System.out.println("result : " + questions.size());
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
}
