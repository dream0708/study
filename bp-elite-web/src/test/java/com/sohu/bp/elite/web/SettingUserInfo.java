package com.sohu.bp.elite.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;


import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

public class SettingUserInfo
{
	private static BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	
	public static void main(String[] args)
	{
		List<String> avatarList = new ArrayList<String>();
		
		File file = new File("D://url_total.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	avatarList.add(tempString.trim());
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        System.out.println("read avatar finish. total line="+avatarList.size());
        
        file = new File("D://account_online.txt");
        Random ran = new Random();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	String[] userInfoArray = tempString.split("\t");
            	if(userInfoArray.length < 3)
            		continue;
                String bpIdStr = userInfoArray[2];
                if(StringUtils.isBlank(bpIdStr))
                	continue;
                Long bpId = Long.parseLong(bpIdStr.trim());
                
                
                String randomAvatar = avatarList.get(ran.nextInt(avatarList.size()));
                CodeMsgData codeMsgData = bpServiceAdapter.updateAvatar(bpId, randomAvatar);
                if(null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK)
                {
                	System.out.println("update avatar success");
                }
                else
                {
                	System.out.println("update avatar failed.bpId="+bpId);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
		
	}
}