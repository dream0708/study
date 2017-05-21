package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.List;

import com.sohu.bp.elite.constants.Constants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * @author limingcai
 * 2016年3月30日 上午10:21:06
 *
 */
public class IdListConvertUtils {
	public static String convertListToString(List<Long> data, Logger log) {
		if(data == null || data.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < data.size(); i++) {
			Long curID = data.get(i);
			if(i != data.size() - 1 && curID != null) {
				builder.append(curID);
				builder.append(Constants.DB_DEFAULT_SPLIT_CHAR);
			}
			else if(curID != null) {
				builder.append(curID);
			}
		}
		return builder.toString();
	}
	
	public static List<Long> convertStringToList(String ids, Logger log) {
		List<Long> ansList = new ArrayList<Long>();
		if(StringUtils.isEmpty(ids)) {
			return ansList;
		}
		String[] splits = ids.split(Constants.DB_DEFAULT_SPLIT_CHAR);
		for(String curS : splits) {
			try{
				long curT = Long.parseLong(curS);
				ansList.add(curT);
			}
			catch(Exception e) {
				log.error("convert tagid to long error.");
				return null;
			}
		}
		return ansList;
	}
}
