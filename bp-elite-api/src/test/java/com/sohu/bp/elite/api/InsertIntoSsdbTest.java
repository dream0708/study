package com.sohu.bp.elite.api;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.model.TEliteUser;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*", "classpath:applicationContext.xml"})
public class InsertIntoSsdbTest {
	private static final Logger log = LoggerFactory.getLogger(InsertIntoSsdbTest.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	@Resource
	CacheManager ssdbCacheManager;
	private SsdbCache ssdbCache;
	@PostConstruct
	public void init(){
		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(CacheConstants.CACHE_FIRST_FOLLOW);
	}
//	public static void main(String[] args) {
//		String filePath = "d:/opt/logs/elite-upload/问答账号测试.txt";
//		List<Long> idList = readFromTxt(filePath);
//		System.out.println(idList);
//	}
//	
//	public static List<Long> readFromTxt(String filePath){
//		List<Long> idList = new ArrayList<Long>();
//		File file = new File(filePath);
//		BufferedReader reader = null;
//		try{
//			FileInputStream fis = new FileInputStream(file);
//			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
//			reader = new BufferedReader(isr);
//			reader.readLine();
//			String line;
//			for(line = reader.readLine(); StringUtils.isNotBlank(line); line = reader.readLine()){
//				String[] ids = line.split("\t");
//				Long id = Long.valueOf(ids[2]);
//				idList.add(id);
//			}
//		} catch (Exception e){
//			log.error("", e);
//		}finally {
//			try {
//				reader.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return idList;
//	}
	
//	@Test
//	public void insert(){
//		String filePath = "d:/opt/logs/elite-upload/问答账号测试.txt";
//		List<Long> idList = readFromTxt(filePath);
//		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
//		List<String> oldIdList = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, num.intValue());
//		for(Long id : idList){
//			if(!oldIdList.contains(id)){
//				ssdbCache.zAdd(Constants.ELITE_FOLLOW_USER_LOGIN, 0l, id.toString());
//				log.info("add in ssdb id = {}", new Object[]{id});
//			}
//		}
//	}
	
	@Test
	public void readFromSsdbToCreateUser(){
		List<String> idList = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, -1);
		for(String id : idList){
			Long bpId = Long.valueOf(id);
			try{
				TEliteUser user = eliteAdapter.getUserByBpId(bpId);
			} catch (Exception e){
				log.info("user doesn't exist! bpid = {} ", new Object[]{bpId});
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DATE, -2);
				TEliteUser user = new TEliteUser().setBpId(bpId).setFirstLoginTime(cal.getTime().getTime())
						.setLastLoginTime(new Date().getTime()).setStatus(1).setFirstLogin(AgentSource.PC.getValue());
				try {
					Long userId = eliteAdapter.insertUser(user);
					log.info("insert user succeed! user bpId = {} ", new Object[]{userId});
				} catch (TException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}

