package com.sohu.bp.elite.listener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.service.web.ElitePushService;

import net.sf.json.JSONObject;

public class SquareListener extends KafkaListener {
	private Logger logger = LoggerFactory.getLogger(SquareListener.class);
	private ElitePushService elitePushService;
	
	public void setElitePushService(ElitePushService elitePushService){
		this.elitePushService = elitePushService;
	}
	
	
	public void init() {
		this.setTaskHandler(new Function<String, Boolean>() {
			@Override
			public Boolean apply(String msg) {
				try {
					if (StringUtils.isBlank(msg))
						return true;
					logger.info("read msg=" + msg);
					JSONObject msgJSON = JSONObject.fromObject(msg);
					Long objectId = msgJSON.getLong("data");
					if (null != objectId && objectId > 0) elitePushService.squareUnreadPush();
					return true;
				} catch (Exception e) {
					logger.info("", e);
					return true;
				}
			}
		});

		// 开始运行监听
		this.myRun();
	}
}
