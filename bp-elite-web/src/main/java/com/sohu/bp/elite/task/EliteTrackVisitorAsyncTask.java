package com.sohu.bp.elite.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.util.LogCollectUtil;
import com.sohu.bp.elite.util.SpringUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-11-09 15:46:25
 * TODO
 */
public class EliteTrackVisitorAsyncTask extends EliteAsyncTask
{
	private static final Logger logger = LoggerFactory.getLogger(EliteTrackVisitorAsyncTask.class);
	
	private JoinPoint joinPoint;

	public JoinPoint getJoinPoint() {
		return joinPoint;
	}

	public void setJoinPoint(JoinPoint joinPoint) {
		this.joinPoint = joinPoint;
	}

	public EliteTrackVisitorAsyncTask(JoinPoint joinPoint)
	{
		this.joinPoint = joinPoint;
	}
	
	@Override
	public void run()
	{
		logger.info("track visitor");
		Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        for(Object arg : args){
        	if(arg instanceof HttpServletRequest)
        		request = (HttpServletRequest) arg;
        	else if(arg instanceof HttpServletResponse)
        		response = (HttpServletResponse) arg;
        }
        
        if(null != request && null != response)
        {
        	LogCollectUtil.sendStatisticsMsg(request, response);
        }
        else
        {
        	logger.info("track nothing");
        }
	}
}