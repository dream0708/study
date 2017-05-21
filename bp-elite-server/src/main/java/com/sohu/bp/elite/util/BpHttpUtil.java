package com.sohu.bp.elite.util;

import com.sohu.bp.util.HttpUtil;
import com.sohu.bp.util.ResponseJSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BpHttpUtil
{
	private static Logger log = LoggerFactory.getLogger(BpHttpUtil.class);
    
	public static String post(String uri, String host, Map<String, String> params){
		return post(uri, host, params, 4000, 2000);
	}
	
    public static String post(String uri, String host, Map<String, String> params, 
    		int readTimeout,int connectTimeout) {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(uri) || null == params || params.size() <= 0)
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	String method = "POST";
    	long timestamp = System.currentTimeMillis();
    	String sig = BpApiAuthUtil.generateAuthParam(uri, method, timestamp);
    	//log.info("gen sig for uri="+uri+" time="+(System.currentTimeMillis()-timestamp)+"ms");
    	
    	Map<String,String> headers = new HashMap<String,String>();
    	headers.put("Timestamp", String.valueOf(timestamp));
    	headers.put("Auth", sig);
    	String ret = HttpUtil.post(host+uri, params, readTimeout, connectTimeout, headers);
    	if(null == ret) {
    		resJSON = ResponseJSON.getErrorInternalJSON();
    	} else {
    		JSONObject jObject = null;
			try {
				jObject = JSONObject.fromObject(ret);
				int code = jObject.optInt("code", -1);
				if(code == 0) {
					resJSON = jObject;
				} else {
					resJSON = ResponseJSON.getErrorInternalJSON();
				}
			} catch (Exception e) {
				log.error("",e);
				resJSON = ResponseJSON.getErrorInternalJSON();
			}
    	}
    	return resJSON.toString();
    }
    
    public static String get(String uri, String host, Map<String, String> params){
    	return get(uri, host, params, 2000, 2000);
    }
    
    public static String get(String uri, String host, Map<String, String> params,
    		int readTimeout,int connectTimeout)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(uri) || null == params || params.size() <= 0)
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	String method = "GET";
    	long timestamp = System.currentTimeMillis();
    	String sig = BpApiAuthUtil.generateAuthParam(uri, method, timestamp);
    	
    	Map<String,String> headers = new HashMap<String,String>();
    	headers.put("Timestamp", String.valueOf(timestamp));
    	headers.put("Auth", sig);

    	String ret = HttpUtil.get(host+uri, params, readTimeout, connectTimeout, headers);
    	if(null == ret) {
    		resJSON = ResponseJSON.getErrorInternalJSON();
    	} else {
    		JSONObject jObject = null;
			try {
				jObject = JSONObject.fromObject(ret);
				int code = jObject.optInt("code", -1);
				if(code == 0) {
					resJSON = jObject;
				} else {
					resJSON = ResponseJSON.getErrorInternalJSON();
				}
			} catch (Exception e) {
				log.error("",e);
				resJSON = ResponseJSON.getErrorInternalJSON();
			}
    	}
    	return resJSON.toString();
    }

    public static void main(String[] args) {
		String host = "http://10.16.6.52:8095";
		String uri = "/decoration/search/innerapi/search/question";
		Map<String, String> params = new HashMap<>();
		params.put("statusArray", "2;3;7");
		params.put("from", "0");
		params.put("count", "30");
		params.put("minAnswerNum", "1");
		params.put("keywords", "北京");
		params.put("autoComplete", "0");
		String response = BpHttpUtil.post(uri, host, params);
		System.out.println(response);
	}
}