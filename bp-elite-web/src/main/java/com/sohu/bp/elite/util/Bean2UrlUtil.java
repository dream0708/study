package com.sohu.bp.elite.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.action.bean.question.EliteQuestionInviteBean;

/**
 * 用于在跳转的时候将bean转为url
 * @author zhijungou
 * 2016年12月21日
 */
public class Bean2UrlUtil {
	private static final Logger log = LoggerFactory.getLogger(Bean2UrlUtil.class);
	public static String bean2GetMethodUrl(Object object){
		Field[] fields = object.getClass().getDeclaredFields();
		StringBuilder url = new StringBuilder("?");
		try{
			for(Field field : fields){
				String name = field.getName();
				PropertyDescriptor pd = new PropertyDescriptor(name, object.getClass());
				Method method = pd.getReadMethod();
				Object value = method.invoke(object);
				if(null != value) url.append(name).append("=").append(value).append("&");
			}
		} catch (Exception e) {
			log.error("", e);
		}
		url.delete(url.length() - 1, url.length());
		return url.toString();
	}
}
