//package com.sohu.bp.elite.api.api;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.thrift.TException;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
//import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
//import com.sohu.bp.elite.api.auth.AuthenticationCenter;
//import com.sohu.bp.elite.model.TEliteUser;
//import com.sohu.bp.util.HttpUtil;
//
//public class TestApi
//{
//	private static String app_key = "app_test";
//	private static String app_secret = "t1i8RiTpMVLwQJK1iJuXPSuOlMYe4lvZ";
//	private static String host="http://10.10.24.207:8012";
//	
//	public static void TestCrawl()
//	{
//		long timestamp = System.currentTimeMillis();
//        String method = "GET";
//        String uri = "/elite/innerapi/crawl/test";
//        String sig = AuthenticationCenter.generateSignature(uri, method, timestamp, app_secret);
//
//        Map<String, String> params = new HashMap<String, String>();
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Timestamp", String.valueOf(timestamp));
//        headers.put("Auth", app_key + "|" + sig);
//        String response = HttpUtil.get(host + uri, params, headers);
//        System.out.println(response);
//	}
//	
//	public static void main(String[] args)
//	{
//		EliteThriftServiceAdapter adapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//		TEliteUser user = new TEliteUser();
//		user.setBpId(128l);
//		user.setFirstLoginTime(new Date().getTime());
//		user.setLastLoginTime(new Date().getTime());
//		user.setStatus(0);
//		try {
//			adapter.insertUser(user);
//		} catch (TException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}