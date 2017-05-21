package com.sohu.bp.elite.consumer.util;

import com.sohu.bp.elite.consumer.Configuration;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author nicholastang
 * 2016-10-18 20:10:01
 * TODO 问答工具类
 */
public class ToolUtil
{
	private static final Logger logger = LoggerFactory.getLogger(ToolUtil.class);
	private static Configuration configuration = null;
	static {
		configuration = (Configuration) SpringUtil.getBean("configuration");
	}
	public static boolean isQuestionValid(int questionStatus) {
		if (questionStatus == EliteQuestionStatus.AUDITING.getValue()
				|| questionStatus == EliteQuestionStatus.PASSED.getValue()
				|| questionStatus == EliteQuestionStatus.PUBLISHED.getValue()) {
			return true;
		}
		return false;
	}

	public static boolean isAnswerValid(int answerStatus) {
		if (answerStatus == EliteAnswerStatus.AUDITING.getValue()
				|| answerStatus == EliteAnswerStatus.PASSED.getValue()
				|| answerStatus == EliteAnswerStatus.PUBLISHED.getValue()) {
			return true;
		}
		return false;
	}

	public static String getQuestionUrl(long questionId) {
		return "https://" + configuration.get("eliteDomain", "bar.focus.cn") + "/q/" + IDUtil.encodeId(questionId);
	}

	public static String getAnswerUrl(long answerId) {
		return "https://" + configuration.get("eliteDomain", "bar.focus.cn") + "/a/" + IDUtil.encodeId(answerId);
	}

}