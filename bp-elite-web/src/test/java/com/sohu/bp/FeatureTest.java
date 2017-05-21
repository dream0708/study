package com.sohu.bp;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.elite.service.web.InviteService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:bpEliteWeb/*.xml","classpath:*.xml"})
public class FeatureTest {	
	@Resource
	private InviteService inviteService;
	@Test
	public void inviteService(){
		Integer num = inviteService.getRecomNum();
		System.out.println("recom num is : " + num);
		List<Long> bpIdList = inviteService.getRecomInviteList(5, 5);
		System.out.println("recom id is :");
		for(Long bpId : bpIdList){
			System.out.print(bpId + "\t");
		}
	}
}
