package com.sohu.bp.elite.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * 
 * @author nicholastang
 * 2016-07-27 16:50:24
 * 自然化处理类
 */
public class HumanityUtil
{
	private static final Logger log = Logger.getLogger(HumanityUtil.class);
	
	private static final SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdfMinute = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat sdfDayMinute = new SimpleDateFormat("MM-dd HH:mm");
	private static final SimpleDateFormat sdfMonth = new SimpleDateFormat("MM-dd");
	private static final SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	/**
	 * 时间人类语言处理
	 * @param timeSec
	 * @return
	 */
	public static String humanityTime(long timeSec)
	{
		try
		{
			long currentTimeSec = System.currentTimeMillis();
			
			String givenDayStr = sdfDay.format(new Date(timeSec));
			String currentDayStr = sdfDay.format(new Date(currentTimeSec));
			long marginSecs = currentTimeSec - timeSec;

			if(marginSecs <= 1440*60*1000){
				if(givenDayStr.equalsIgnoreCase(currentDayStr)){
					if(marginSecs <= 5*60*1000)
					{//五分钟以内显示逻辑
						return "刚刚";
					}
					else if(marginSecs <= 60*60*1000)
					{//一个小时以内显示逻辑
						long marginMinutes = marginSecs/60000;
						return marginMinutes+"分钟前";
					}
					else if(marginSecs <= 360*60*1000)
					{//六个小时以内显示逻辑
						long marginHours = marginSecs/3600000;
						return marginHours+"小时前";
					}
					else
					{//24小时以内显示逻辑
						String givenMinutesStr = sdfMinute.format(new Date(timeSec));
						return givenMinutesStr;
					}
				}
				else{
					return "昨天";
				}
			}
			else if(marginSecs <= (long)525600*60*1000)
			{//一年内
				String giveMonthStr = sdfMonth.format(new Date(timeSec));
				return giveMonthStr;
			}
			else
			{//一年前
				String giveYearStr = sdfYear.format(new Date(timeSec));
				return giveYearStr;
			}
			
		}catch(Exception e)
		{
			log.error("", e);
		}
		return "未知";
	}
	/**
	 * 数字人类语言处理
	 * @param number
	 * @return
	 */
	public static String humanityNumber(long number)
	{
		if(number < 0)
		{
			return "未知";
		}
		else if(number <= 1000)
		{
			return ""+number;
		}
		else if(number <= 10000)
		{
			int thousandDig = (int)number/1000;
			int hundredDig = (int)(number%1000)/100;
			return thousandDig + (hundredDig > 0 ? ("."+hundredDig) : "") + "k";
		}
		else
		{
			long thousandDig = number/1000;
			return thousandDig+"k";
		}
	}
}