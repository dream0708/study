package com.sohu.bp.elite.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;
import org.apache.log4j.Logger;

import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 */
public class SendCloudSmsUtil {
	
	private static Logger logger = Logger.getLogger(SendCloudSmsUtil.class);
	private static final String SMS_URL = "http://sendcloud.sohu.com/smsapi/send";

	private static final String SMS_KEY = "Spv2fGrqiwaWVmsSFASAJ2rDcjVsJ92H";
	private static final String SMS_USER = "SOHU_FOCUS_HOME";
	private static final String SMS_MSG_TYPE = "0";
	
	public static final String PARAM_QUESTION_TITLE = "questionTitle";
	public static final String PARAM_QUESTION_URI = "questionUri";
	public static final String PARAM_NICK_NAME = "nickname";
	
	private static final Map<String, String> sensitiveWordsReplaceMap = new HashMap<String, String>(){{
		put("装修", "家装");
	}};
	
	
	public static String sendMessage(Integer templateId, String destnumber){
		return sendMessage(templateId, destnumber, null);
	}
	
	public static String sendMessage(Integer templateId, String destnumber, Map<String, String> variable) {
		JSONObject retJSON = ResponseJSON.getDefaultResJSON();
		try {
			if (StringUtils.isBlank(destnumber) || null == templateId || templateId.intValue() <= 0) {
				retJSON = ResponseJSON.getErrorParamsJSON();
				return retJSON.toString();
			}
			// 填充参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("smsUser", SMS_USER);
			params.put("templateId", templateId.toString());
			params.put("msgType", SMS_MSG_TYPE);
			params.put("phone", destnumber);
			
			if(variable != null && variable.size() > 0){
				JSONObject job = new JSONObject();
				for(Map.Entry<String, String> iter:variable.entrySet()){
					String value = iter.getValue();
					if(StringUtils.isNotBlank(value))
						value = SendCloudSmsUtil.replaceSentitveWords(value);
					if ("questionTitle".equalsIgnoreCase(iter.getKey())) {
						value = (value.length() > 11) ? value.substring(0, 10) + "..." : value;
					}
					job.put(iter.getKey(), value);
				}
				params.put("vars", job.toString());
			}

			// 对参数进行排序
			Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					// 忽略大小写
					return arg0.compareToIgnoreCase(arg1);
				}
			});
			sortedMap.putAll(params);

			// 计算签名
			StringBuilder sb = new StringBuilder();
			sb.append(SMS_KEY).append("&");
			for (String s : sortedMap.keySet()) {
				sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
			}
			sb.append(SMS_KEY);
			String sig = DigestUtils.md5Hex(sb.toString());
			params.put("signature", sig);
			String sendSmsResponse = HttpUtil.post(SMS_URL, params);
			logger.info("SendCloudSmsUtil : sendMessage : " + destnumber + " : " + templateId + " : " + sendSmsResponse);

			JSONObject responseJSON = JSONObject.fromObject(sendSmsResponse);
			if (responseJSON.containsKey("result") && responseJSON.getBoolean("result")) {
				retJSON.put("ret", true);
			} else {
				retJSON.put("ret", false);
			}
		} catch (Exception e) {
			logger.error("", e);
			retJSON = ResponseJSON.getErrorInternalJSON();
		}
		return retJSON.toString();
	}
	
	public static String replaceSentitveWords(String content)
	{
		if(StringUtils.isBlank(content))
			return "";
		for(Map.Entry<String, String> iter : sensitiveWordsReplaceMap.entrySet()){
			String key = iter.getKey();
			if(content.contains(key))
			{
				content = content.replace(key, (String)sensitiveWordsReplaceMap.get(key));
			}
		}
		
		return content;
	}

	public static void main(String[] args) {
		// System.out.println(sendMessage(1615, "18511878337"));

		String mobile = "18511878337";
		Map<String, String> variable = new HashMap<String, String>();
		variable.put("nickname", "搜狐焦点家居网友");
		variable.put("questionTitle", "怎么样装修的有质感");
		variable.put("questionUri", "home.focus.cn/ask/index.html");
		SendCloudSmsUtil.sendMessage(2876, mobile, variable);
		//System.out.print(mobile.matches("^1[^0][0-9]{9}$"));
	}

}
