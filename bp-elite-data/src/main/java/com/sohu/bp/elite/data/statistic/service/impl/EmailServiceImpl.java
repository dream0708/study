package com.sohu.bp.elite.data.statistic.service.impl;

import com.sohu.bp.elite.data.statistic.service.EmailService;
import com.sohu.nuc.redisClient.util.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * create time: 2016年7月4日 上午9:55:28
 * @auther dexingyang
 */
public class EmailServiceImpl implements EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	private JavaMailSender javaMailSender;

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public boolean send(String from, String[] to, String subject, String text) {
		MimeMessage msg = javaMailSender.createMimeMessage();
		
		try {
	        MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8"); 
	        helper.setFrom(from);
	        helper.setTo(to);  
	        helper.setSubject(subject);   
	        helper.setText(text, true); 
			javaMailSender.send(msg);
			
			log.info("send mail from="+from+", subject=["+subject+"] sucess");
			
			return true;
		}catch(Exception me) {
			log.error("mail send error", me);
		}
		return false;
	}

	public static void main(String[] args){
		String pos = "1.2?channelid=364?timer=tc";
		pos = pos.replaceAll("^(\\d[\\d\\.]*).*$", "$1");
		System.out.println(pos);
	}
}
