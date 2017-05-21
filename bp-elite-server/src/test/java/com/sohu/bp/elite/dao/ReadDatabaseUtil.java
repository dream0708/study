package com.sohu.bp.elite.dao;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.utils.Base36Number;
import com.sohu.bp.utils.crypt.AESUtil;

import net.sf.json.JSONObject;

/**
 * 用于从数据库读取数据，进行后续操作，包括但不限于：
 * 读取问题等推到sitemap，统计用户真实数据，读取数据
 * @author zhijungou
 * 2016年11月17日
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
public class ReadDatabaseUtil {
	private final static Logger log = LoggerFactory.getLogger(ReadDatabaseUtil.class);
	private static String URL = "jdbc:${mysql.url}?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull";
	private static String URL_PLACEHOLDER = "${mysql.url}";
	private static String[] MYSQLS_TEST = {"mysql://10.16.1.238:3306/bp_decorate_0", "mysql://10.16.1.238:3306/bp_decorate_1", "mysql://10.16.1.238:3306/bp_decorate_2","mysql://10.16.1.238:3306/bp_decorate_3"};
	private static String[] MYSQLS_PRODUCTION = {"mysql://10.10.13.157/bp_dec_0", "mysql://10.11.165.74/bp_dec_1", "mysql://10.16.53.116/bp_dec_2","mysql://192.168.5.122/bp_dec_3"};
	private static String[] USERNAMES_TEST = {"root", "root", "root", "root"};
	private static String[]	PASSWORDS_TEST = {"mysql", "mysql", "mysql", "mysql"};
	private static String[] USERNAMES_PRODUCTION = {"bp_dec_0_ro", "bp_dec_1_ro", "bp_dec_2_ro", "bp_dec_3_ro"};
	private static String[]	PASSWORDS_PRODUCTION = {"_8rlxNL2o5Led4F", "5HqiEupameHooL0", "gbLHeydFjHtcw16", "3epm6eEpk84kzoG"};
	private static String SELECT_QUESTIONID_SQL = "SELECT id FROM elite_question_0 WHERE status='7'";
	private static String SELECT_USER_SQL = "SELECT id FROM elite_user_0";
	private static String DIR = "d:/opt/data/questionId/";
	private static int DB_SIZE = 16;
	private static String kafkaTopic = "SITEMAP_QUEUE";
	BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
	
	@Resource 
	NotifyService notifyService;
	
//	public static void main(String[] args) {
//		File path = new File(DIR);
//		if(!path.exists()){
//			path.mkdirs();
//		}
////		File file = new File(path, "questionId");
//		ReadQuestionIdFromMysql test = new ReadQuestionIdFromMysql();
////		test.getQuestionId(file);
//		File inputFile = new File(path, "questionId");
//		File outputFile = new File(path, "questionId2");
//		test.seoMethod(1000, inputFile, outputFile);
//		inputFile.delete();
//		outputFile.renameTo(inputFile);
//		outputFile.delete();
//		int count = new ReadQuestionIdFromMysql().countUser();
//	}
	
	@Test
	public void rebuildUser(){
		try{
			for(int index = 0; index <=3; index++){
				String mysql_url = URL.replace(URL_PLACEHOLDER, MYSQLS_TEST[index]);
				List<Long> bpIds = new ArrayList<>();
				String username = USERNAMES_TEST[index];
				String password = PASSWORDS_TEST[index];
				try(Connection conn = (Connection) DriverManager.getConnection(mysql_url, username, password)){
					int start = index * DB_SIZE;
					int end = (index + 1) * DB_SIZE;
					for(int i = start; i < end; i++){
						String sql = SELECT_USER_SQL.replaceFirst("\\d", String.valueOf(i));
						try(PreparedStatement ps = conn.prepareStatement(sql)){
							try(ResultSet rs = ps.executeQuery()){
								while(rs.next()){
									bpIds.add(rs.getLong(1));
								}
							}
						}
					}
				}
				System.out.println("----------result---------" );
				for(Long bpId : bpIds) {
					System.out.print(bpId + "\t");
					notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
				}
			}
		} catch (Exception e){
			log.error("", e);
		}
	}
	
	public void getQuestionId(File file){
		try(BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))){
			for(int index=0; index < 4; index++){
				String mysql_url = URL.replace("mysql://10.16.1.238:3306/bp_decorate_0", MYSQLS_TEST[index]);
				String username = USERNAMES_TEST[index];
				String password = PASSWORDS_TEST[index]; 
				try(Connection conn = (Connection) DriverManager.getConnection(mysql_url, username, password)){
					int start = index * DB_SIZE;
					int end = (index + 1) * DB_SIZE;
					for(int i = start; i < end; i++){
						String sql = SELECT_QUESTIONID_SQL.replaceFirst("\\d", String.valueOf(i));
						try(PreparedStatement ps = conn.prepareStatement(sql)){
							try(ResultSet rs = ps.executeQuery()){
								while(rs.next()){
									write.write(String.valueOf(rs.getLong(1)) + "\n");
								}
							}
						}
					}
				} catch (Exception e){
					log.error("", e);
				}
			}
		} catch(Exception e){
			log.error("", e);
		}
	}
	
	public void seoMethod(int num, File inputFile, File outputFile){
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))){
			String line = "";
			int index = 0;
			for(index = 0, line = reader.readLine(); index <= num && null!=line; index++, line = reader.readLine()){
				if(StringUtils.isBlank(line)) continue;
				Long id = Long.valueOf(line);
				push2Kafka(id);
			}
			while(null != line){
				writer.write(line + "\n");
				line = reader.readLine();
			}
		} catch (Exception e){
			log.error("", e);
		}
	}
	
	public void push2Kafka(Long id){
		Integer objectType = BpType.Question.getValue();
		String encryptId = encodeId(id);
		JSONObject msgJSON = new JSONObject();
		msgJSON.put("objectType", objectType);
		msgJSON.put("encryptId", encryptId);
		msgJSON.put("objectId", id);
		log.info("send information to kafka : " + msgJSON.toString());
		kafkaProducer.send(kafkaTopic, msgJSON.toString());
	}
	
    public static String encodeId(long id){
        return Base36Number.encrypt(id);
    }

    public static long decodeId(String source){
    	long id = Base36Number.decrypt(source);
    	if (id > 0) {
    	    return id;
    	}
    	return AESUtil.decryptIdV2(source);
    }
    
	public int countUser(){
		int count = 0;
		try{
			for(int index=0; index < 4; index++){
				String mysql_url = URL.replace("mysql://10.16.1.238:3306/bp_decorate_0", MYSQLS_TEST[index]);
				String username = USERNAMES_TEST[index];
				String password = PASSWORDS_TEST[index]; 
				try(Connection conn = (Connection) DriverManager.getConnection(mysql_url, username, password)){
					int start = index * DB_SIZE;
					int end = (index + 1) * DB_SIZE;
					for(int i = start; i < end; i++){
						String sql = SELECT_USER_SQL.replaceFirst("\\d", String.valueOf(i));
						try(PreparedStatement ps = conn.prepareStatement(sql)){
							try(ResultSet rs = ps.executeQuery()){
								while(rs.next()){
									count += 1;
								}
							}
						}
					}
				} catch (Exception e){
					log.error("", e);
				}
			}
		} catch(Exception e){
			log.error("", e);
		}
		return count;
	}
}
