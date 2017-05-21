package com.sohu.bp.elite.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteSeoAsyncTask;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-11-08 11:04:24
 * TODO
 */
@Service
@Aspect
public class SeOptimize
{
	private static final Logger logger = LoggerFactory.getLogger(SeOptimize.class);
	
	@AfterReturning(
            pointcut="execution(* com.sohu.bp.elite.action.QuestionAction.publish*(..))",
            returning="returnValue")
    public void buildSiteMap(JoinPoint point, Object returnValue) {
		String retVal = returnValue.toString();
		if(StringUtils.isBlank(retVal))
			return;
		try
		{
			JSONObject retJSON = JSONObject.fromObject(retVal);
			if((null != retJSON) && (retJSON.getInt("code") == ResponseJSON.CODE_SUC))
			{
				JSONObject dataJSON = retJSON.getJSONObject("data");
				if(null != dataJSON && dataJSON.containsKey("id"))
				{
					String questionEncodedId = dataJSON.getString("id");
					if(StringUtils.isNotBlank(questionEncodedId))
					{
						Long questionId = IDUtil.decodeId(questionEncodedId);
						if(null != questionId && questionId.longValue() > 0)
						{
							EliteSeoAsyncTask task = new EliteSeoAsyncTask(BpType.Question.getValue(), questionEncodedId);
							EliteAsyncTaskPool.addTask(task);
							logger.info("build site map success,questionId="+questionId);
							return;
						}
					}
				}
			}
		}catch(Exception e)
		{
			logger.error("", e);
		}
		logger.info("build site map failed");
    }
}