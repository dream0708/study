package com.sohu.bp.elite.api.util;

import net.sf.json.JSONObject;

/**
 * @author limingcai
 * 2016年3月17日 下午5:59:47
 *
 */
public class ResponseJSON {
	public static final String CODE_NAME = "code";
	public static final int CODE_SUC = 0;
	public static final int CODE_ERROR_PARAMETERS = 1;
	public static final int CODE_ERROR_INTERNAL = 2;
	
	public static final String MSG_NAME = "msg";
	public static final String MSG_SUC = "success";
	public static final String MSG_ERROR_PARAMETERS = "parameters error";
	public static final String MSG_ERROR_INTERNAL = "internal error";
	
	public static JSONObject getDefaultResponseJSON() {
		JSONObject json = new JSONObject();
		json.put(CODE_NAME, CODE_SUC);
		json.put(MSG_NAME, MSG_SUC);
		return json;
	}
	
	public static JSONObject getSucJSON() {
		JSONObject json = new JSONObject();
		json.put(CODE_NAME, CODE_SUC);
		json.put(MSG_NAME, MSG_SUC);
		return json;
	}
	
	public static JSONObject getErrorParametersErrorJSON() {
		JSONObject json = new JSONObject();
		json.put(CODE_NAME, CODE_ERROR_PARAMETERS);
		json.put(MSG_NAME, MSG_ERROR_PARAMETERS);
		return json;
	}

	public static JSONObject getErrorInteralErrorJSON() {
		JSONObject json = new JSONObject();
		json.put(CODE_NAME, CODE_ERROR_INTERNAL);
		json.put(MSG_NAME, MSG_ERROR_INTERNAL);
		return json;
	}
	
	public static JSONObject getJSON(int code, String msg) {
		JSONObject json = new JSONObject();
		json.put(CODE_NAME, code);
		json.put(MSG_NAME, msg);
		return json;
	}
	
	public static JSONObject addKeyValue(JSONObject jsonObject, String key, Object value) {
		jsonObject.put(key, value);
		return jsonObject;
	}
}
