package com.sohu.bp.elite.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtil {

	public static final String[] searchByTelNum(String keyWord){
		String regex = "tel:(\\d{11})";
		Matcher m = Pattern.compile(regex).matcher(keyWord);
		String tel = null;
		StringBuilder newKeyWord = new StringBuilder(keyWord);
		if(m.find()){
			tel = m.group(1);
			newKeyWord.delete(m.start(), m.end());
		}
		return new String[]{newKeyWord.toString(),tel};
	}
	
	public static void main(String[] args) {
		String keyWord = "哈哈哈哈 tel:13810138235 咦咦咦咦咦";
		String[] tel = searchByTelNum(keyWord);
		System.out.println("tel:" + tel[0] + " KeyWord:" + tel[1]);

	}
}
