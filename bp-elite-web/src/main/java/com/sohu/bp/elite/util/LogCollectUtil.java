package com.sohu.bp.elite.util;
import com.alibaba.fastjson.JSONObject;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.utils.RealIpUtil;
import com.sohu.bp.utils.http.UserAgentUtil;
import eu.bitwalker.useragentutils.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by nicholastang on 2016/11/9.
 */
public class LogCollectUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogCollectUtil.class);
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    private static final String BP_HOME_VIEW_KAFKA_TOPIC = "bp-home-statistics";


    public static void sendStatisticsMsg(HttpServletRequest request, final HttpServletResponse response) {
        kafkaProducer.send(BP_HOME_VIEW_KAFKA_TOPIC, getDataMap(request, response).toJSONString());
        //logger.info("[STATISTIC LOG]" + getDataMap(request, response).toJSONString());
    }

    private static JSONObject getDataMap(final HttpServletRequest request, final HttpServletResponse response) {
        JSONObject dataMap = new JSONObject();
        dataMap.put("time", new Date().toLocaleString());
        String userAgent = request.getHeader("User-Agent");
        dataMap.put("userAgent", userAgent);
        DeviceType deviceType = UserAgentUtil.getDeviceType(userAgent);
        dataMap.put("deviceType", deviceType);
//        JSONObject areaObject = LocationUtil.getSavedArea(request);
//        dataMap.put("city", areaObject.getString("cityName"));
//        dataMap.put("province", areaObject.getString("provinceName"));
        
        String url = request.getRequestURL().toString();
        if(url.startsWith("http://home.focus.cn") || url.startsWith("https://home.focus.cn") || url.startsWith("http://test.home.focus.cn"))
        	url = url.substring(url.indexOf("home.focus.cn") + 13);
        logger.info(url);
        dataMap.put("url", url);
        dataMap.put("parameters", JSONObject.toJSON(request.getParameterMap()));
        String suvCookie = CookieUtil.getCookieValue(request, "SUV");
        long bpid = LoginUtil.getLoginBpid(request, response);
        dataMap.put("suv", suvCookie);
        dataMap.put("bpid", bpid);
        String refer = request.getHeader("referer");
        dataMap.put("refer", refer);
        String ip = RealIpUtil.getRealIP(request);
        dataMap.put("ip", ip);
        
        IPDataHandler ipDataHandler = new IPDataHandler();
        if(null != ipDataHandler)
        {
        	String[] locationArray = ipDataHandler.getLocationByIp(ip);
        	if(null != locationArray && locationArray.length >= 3)
        	{
        		dataMap.put("province", locationArray[1]);
        		dataMap.put("city", locationArray[2]);
        	}
        	else if(locationArray.length >= 2)
        	{
        		dataMap.put("province", locationArray[1]);
        		dataMap.put("city", locationArray[1]);
        	}
        }
        return dataMap;
    }

}
