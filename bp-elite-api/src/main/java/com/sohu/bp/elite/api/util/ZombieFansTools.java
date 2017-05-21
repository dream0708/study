package com.sohu.bp.elite.api.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.nntp.Threadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-31 20:22:34
 * TODO 僵尸粉工具类
 */
public class ZombieFansTools
{
	private static Logger logger = LoggerFactory.getLogger(ZombieFansTools.class);
	public static final String ZombiePhonePrefix = "32000";
	public static final int MinZombiePhone = 0;
	public static final int MaxZombiePhone = 330047;
	private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	
	public static Long getRandomBpId()
	{
		long bpId = -1;
		Random rand = new Random();
		int randNum = rand.nextInt(MaxZombiePhone - MinZombiePhone) + MinZombiePhone;
		String seed = String.valueOf(randNum);
		while (seed.length() < 6) {
			seed = '0' + seed;
		}
		String randPhone = ZombiePhonePrefix + seed;
		
		return getBpId(randPhone);
	}

	public static Long getBpId(String mobile) {
		Long bpId = -1L;
		try
		{
			CodeMsgData data = bpServiceAdapter.getBpidByMobile(mobile);
			ResponseConstants code = data.code;
			if (!code.equals(ResponseConstants.OK)) {
				return null;
			}
			String resData = data.data;
			if (StringUtils.isBlank(resData)) {
				return null;
			}
			JSONObject jsonObject = null;
			try {
				jsonObject = JSONObject.fromObject(resData);
			} catch (Exception e) {
				return null;
			}
			if (jsonObject == null || jsonObject.isNullObject()) {
				return null;
			}
			bpId = jsonObject.optLong("bpid", -1);
		}catch(Exception e)
		{
			logger.error("", e);
		}

		return bpId;
	}

	/*
	public static void main(String[] args) throws  Exception{
		Map<Long, Integer> bpIdMap = new HashMap<>();
		Map<String, Integer> nickMap = new HashMap<>();
		FileWriter fw = null;
		try {
			fw = new FileWriter("/Users/nicholastang/workspace/temp-data-folder/zombies.txt");
			int i=0;
			int start = MinZombiePhone;
			while (i < 1000) {
				if (start >= MaxZombiePhone) {
					break;
				}
				start++;
				String startStr = String.valueOf(start);
				while (startStr.length() < 6) {
					startStr = '0' + startStr;
				}
				String phoneNum = ZombiePhonePrefix + startStr;
				Long bpId = getBpId(phoneNum);
				if (null == bpId || bpId.longValue() <= 0 || bpIdMap.containsKey(bpId)) {
					continue;
				}
				CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
				if (null == codeMsgData)
					System.out.println("get userinfo failed. bpid=" + bpId);
				else if (codeMsgData.getCode() != ResponseConstants.OK)
					System.out.println("code is not right, code message=" + codeMsgData.getMessage() + ", bpid=" + bpId);
				else {
					String infoJSONStr = codeMsgData.getData();
					System.out.println("<<<< " + infoJSONStr);
					if (StringUtils.isNotBlank(infoJSONStr))
						try {
							JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
							if (null != infoJSON) {
								if (nickMap.containsKey(infoJSON.getString("nick"))) {
									continue;
								}
								System.out.println(bpId + "\t" + infoJSON.getString("nick") + "\t" + infoJSON.getString("avatar"));
								fw.write(bpId + "\t" + infoJSON.getString("nick") + "\t" + infoJSON.getString("avatar") + "\n");
								i++;
								nickMap.put(infoJSON.getString("nick"), 1);
							}
							bpIdMap.put(bpId, 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
				Thread.sleep(300);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			fw.close();
		}

	}
	*/
	public static void main(String[] args) {
		File file = new File("/Users/nicholastang/desktop/new_zombie.csv");
		BufferedReader reader = null;
		FileWriter fw = null;
		Map<String, Boolean> bpIdMap = new HashMap<>();
		Map<String, Boolean> nickMap = new HashMap<>();
		try {
			fw = new FileWriter("/Users/nicholastang/desktop/new_new_zombie.txt");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] tempStringArray = tempString.split(",");
				System.out.println(tempString);
				if (null != tempStringArray && tempStringArray.length > 3) {
					String bpId = tempStringArray[0];
					String nick = tempStringArray[1];
					if (StringUtils.isBlank(bpId) || bpIdMap.containsKey(bpId)) {
						continue;
					}
					if (nickMap.containsKey(nick)) {
						continue;
					}
					fw.write(tempStringArray[0]+"\t"+tempStringArray[1]+"\t"+tempStringArray[2]+"\n");
					bpIdMap.put(bpId, true);
					nickMap.put(nick, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}