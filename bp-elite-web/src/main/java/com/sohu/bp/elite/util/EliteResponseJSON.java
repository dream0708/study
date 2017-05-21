package com.sohu.bp.elite.util;

import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

public class EliteResponseJSON extends ResponseJSON{
	
	public static final int CODE_ERROR_NOT_LOGIN = 10;
	public static final int CODE_ERROR_QUESTION_NOT_EXIST = 20;
	public static final String MSG_ERROR_NOT_LOGIN = "user not login";
	public static final String MSG_ERROR_QUESTION_NOT_EXIST = "question not exist";
	
	public static JSONObject getNotLoginError(){
		JSONObject resJSON = new JSONObject();
		resJSON.put("code", CODE_ERROR_NOT_LOGIN);
		resJSON.put("msg", MSG_ERROR_NOT_LOGIN);
		return resJSON;
	}
	
	public static JSONObject getQuestionNotExistError(){
		JSONObject resJSON = new JSONObject();
		resJSON.put("code", CODE_ERROR_QUESTION_NOT_EXIST);
		resJSON.put("msg", MSG_ERROR_QUESTION_NOT_EXIST);
		return resJSON;
	}
}
