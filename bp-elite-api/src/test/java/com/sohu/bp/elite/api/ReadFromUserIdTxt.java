package com.sohu.bp.elite.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.service.BpService.Processor.loginByMobileCaptcha;

public class ReadFromUserIdTxt {
	private static final Logger log = LoggerFactory.getLogger(ReadFromUserIdTxt.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	public static void main(String[] args) {
		String filePath = "d:/opt/data/folder/userId.txt";
		List<Long> idList = readUserIdTxt(filePath);
		System.out.println(idList);
		try {
			insertDatabase(idList);
		} catch (ParseException e) {
			log.error("", e);
		}
	
	}
	public static List<Long> readUserIdTxt(String filePath){
		File file = new File(filePath);
		BufferedReader reader = null;
		List<Long> idList = new ArrayList<>();
		try{
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		reader = new BufferedReader(isr);
		String line = reader.readLine();
		String[] ids = line.split(",");
		for(String id : ids){
			idList.add(Long.valueOf(id));
		}
		} catch(Exception e){
			log.error("",e);
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return idList;
	}
	
	public static void insertDatabase(List<Long> idList) throws ParseException{
		Long userId = null;
		for(Long id : idList){
			TEliteUser userElite = new TEliteUser();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		userElite.setFirstLoginTime(sdf.parse("2016-10-01").getTime());
    		userElite.setLastLoginTime(sdf.parse("2016-10-10").getTime());
    		userElite.setBpId(id);
    		userElite.setStatus(1);
    		userElite.setFirstLogin(0);
    		try{
    			userId = eliteAdapter.insertUser(userElite);
    		} catch(Exception e){
    			continue;
    		}
    		if(null != userId && userId >0){
    			log.info("insert user bpId = {} suceed!", new Object[]{userId});
    		}
		}
	}
}
