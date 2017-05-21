package com.sohu.bp.elite.data.statistic.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author nicholastang
 *
 */
public class CommonUtils
{
	private static final Logger log  = LoggerFactory.getLogger(CommonUtils.class);
	private static final String NETWORK_CARD = "eth0";
	private static final String NETWORK_CARD_BOND = "bond0";
	private static final String DateFormatStr = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DateFormatStr);
	
	public static String assembleTimeRange(Date timeStart, Date timeEnd)
	{
		String timeRangeStr = "";
		if(null == timeStart && null == timeEnd)
		{
		}
		else if(null == timeStart)
		{
			String timeEndStr = sdf.format(timeEnd);
			if(StringUtils.isNotEmpty(timeEndStr))
				timeRangeStr = "='" + timeEndStr + "'";
		}
		else if(null == timeEnd)
		{
			String timeStartStr = sdf.format(timeStart);
			if(StringUtils.isNotEmpty(timeStartStr))
				timeRangeStr = "='" + timeStartStr + "'";
		}
		else
		{
			String timeStartStr = sdf.format(timeStart);
			String timeEndStr = sdf.format(timeEnd);
			if(StringUtils.isNotEmpty(timeStartStr) && StringUtils.isNotEmpty(timeEndStr))
				timeRangeStr = "between '" + timeStartStr + "' and '" + timeEndStr + "'";
		}
		return timeRangeStr;
	}
	
	public static String formatReason(List<String> elementList){
		if(elementList == null || elementList.size() == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(String element:elementList)
		{
			sb.append("[").append(element).append("]-");
		}
		if(sb.length() <= 0)
		{
			return "";
		}
		return sb.substring(0, sb.length()-1);
	}
	
	public static void transMap2Bean(Map<String, Object> map, Object obj) {
		  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                if (map.containsKey(key)) {  
                    Object value = map.get(key);  
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);  
                }  
  
            }  
  
        } catch (Exception e) {  
        	log.error("transMap2Bean Error " + e);  
        }  
  
        return;  
  
    }
	
	public static String getLocalIp()
	{
		String ip = "";  
        try  
        {  
            Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>)NetworkInterface.getNetworkInterfaces();  
            while (e1.hasMoreElements())  
            {  
                NetworkInterface ni = e1.nextElement();  
  
                //单网卡或者绑定双网卡  
                if ((NETWORK_CARD.equals(ni.getName()))  
                    || (NETWORK_CARD_BOND.equals(ni.getName())))  
                {  
                    Enumeration<InetAddress> e2 = ni.getInetAddresses();  
                    while (e2.hasMoreElements())  
                    {  
                        InetAddress ia = e2.nextElement();  
                        if (ia instanceof Inet6Address)  
                        {  
                            continue;  
                        }  
                        ip = ia.getHostAddress();  
                    }  
                    break;  
                }  
                else  
                {  
                    continue;  
                }  
            }  
        }  
        catch (SocketException e)  
        {  
        	log.error("IpGetter.getLocalIP出现异常！异常信息：" + e.getMessage());  
        }  
        return ip;  
	}
}