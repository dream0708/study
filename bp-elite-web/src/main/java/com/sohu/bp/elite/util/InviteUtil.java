package com.sohu.bp.elite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.constants.Constants;

public class InviteUtil {
	private static final Logger log = LoggerFactory.getLogger(InviteUtil.class);
	private static final String SYSTEM_INVITE_KEY = "System";
	
	public static String getInviteCacheKey(Long questionId, Long inviteId) {
		String key = questionId.toString() + Constants.DEFAULT_SPLIT_CHAR + inviteId.toString();
		return key;
	}
	
	public static String getSystemInviteCacheKey(Long questionId) {
	    String key = questionId + Constants.DEFAULT_SPLIT_CHAR + SYSTEM_INVITE_KEY;
	    return key;
	}
}
