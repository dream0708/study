package com.sohu.bp.elite.service.web.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.bean.Picture;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.util.*;
import com.sohu.bp.elite.zookeeper.StaticVCService;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author nicholastang
 * 2016-10-12 15:56:09
 * TODO
 */
public class WasherServiceImpl implements WasherService
{
	private static final Logger logger = LoggerFactory.getLogger(StaticVCService.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	private String downloadDir;
	
	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}
	
	@Override
	public void resaveImage(Long id, BpType bpType) {
		if(null == id || id.longValue() <= 0 || bpType == null)
			return;
		String html = "";
		try
		{
			switch(bpType)
			{
			case Question:
				TEliteQuestion question = eliteAdapter.getQuestionById(id);
				html = question.getDetail();
				if(StringUtils.isBlank(html))
					return;
				StringBuilder htmlBuilder = new StringBuilder(html);
				if(this.resaveImage(htmlBuilder))
				{
					question.setDetail(htmlBuilder.toString());
					try {
						if(!eliteAdapter.updateQuestion(question))
							logger.info("update question failed.questionId="+question.getId());
						else
							logger.info("update question success.questionId="+question.getId());
					} catch (TException e) {
						logger.error("", e);
					}
				}
				break;
			case Answer:
				TEliteAnswer answer = eliteAdapter.getAnswerById(id);
				html = answer.getContent();
				if(StringUtils.isBlank(html))
					return;
				htmlBuilder = new StringBuilder(html);
				if(this.resaveImage(htmlBuilder))
				{
					answer.setContent(htmlBuilder.toString());
					try {
						if(!eliteAdapter.updateAnswer(answer))
							logger.info("update answer failed.answerId="+answer.getId());
						else
							logger.info("update answer success.answerId="+answer.getId());
					} catch (TException e) {
						logger.error("", e);
					}
				}
				break;
			default:
				break;
			}
		}catch(Exception e)
		{
			logger.error("", e);
		}
		
	}
	
	@Override
	public boolean resaveImage(StringBuilder htmlBuilder) {
		boolean needResave = false;
		
		if(null == htmlBuilder)
			return needResave;
		try
		{
			String today = sdf.format(new Date());
            String saveDir = downloadDir + today +"/";
            File dir = new File(saveDir);
            UUID uuid = UUID.randomUUID();
            String namePrefix =  "tmp_"+uuid.toString().replace("-", "");
            if(!dir.exists())
                dir.mkdirs();
            int count=0;
            
			String content = htmlBuilder.toString();
			Document doc = Jsoup.parse(content);
			Elements imgEles = doc.select("img");
			Iterator<Element> iter = imgEles.iterator();
			while(iter.hasNext())
			{
				count++;
				Element ele = (Element)iter.next();
				String imgUrl = ele.absUrl("src");
				if(StringUtils.isNotBlank(imgUrl))
				{
					String lowerCaseSrc = imgUrl.toLowerCase();
					if(lowerCaseSrc.contains("s1.life.itc.cn") || lowerCaseSrc.contains("sohucs.com")) {
	                    continue;
	                }
					needResave = true;
					
					String imgName = namePrefix+"_"+count;
	                String savePath = saveDir + imgName;   //图片下载本地的存储路径
	                
	                long time1 = System.currentTimeMillis();
	                Picture pic = FileUtil.downloadImage(imgUrl, savePath);
	                if(pic == null){
	                    logger.error("download image error, url="+imgUrl);
	                    //图片下载失败的情况,删除img节点
	                    ele.remove();
	                    continue;
	                }
	                if(pic.getExt().equals("gif")){
	                	logger.info("img="+imgUrl+" type is gif, remove it");
	                	ele.remove();
	                	continue;
	                }
	                if(pic.getWidth() <= 70 || pic.getHeight() <= 70){
	                    //图片尺寸过小的图片,进行删除,很多都是表情之类的图片
	                    logger.info("img="+imgUrl+" size not ok,skip");
	                    ele.remove();
	                    continue;
	                }
	                
	                savePath = pic.getFilePath();  //回传保存的路径
	                long time2 = System.currentTimeMillis();
	                logger.info("download image time:"+(time2-time1)+"ms,savePath="+savePath+",url="+imgUrl);
	                
	                String imageFile = savePath;

                    //图片上传到bp-upload中
                    String result = BpImageClient.uploadImage(imageFile, imgName);
                    if(result != null){
                    	JSONObject job = JSON.parseObject(result);
                    	if(job.getIntValue("code") == 0){
                    		String newUrl = job.getString("url");
                    		ele.attr("src", newUrl);
                    		logger.info("put image to bp-upload success, time:"+(System.currentTimeMillis()-time2)+"ms,imgUrl="+newUrl);
                    	}else{
                    		logger.error("put image to bp-upload failed, imgName="+imgName+", result="+result);
                    	}
                    }else{
                        logger.error("put image to bp-upload failed, imgName="+imgName);
                    }
				}
			}
			htmlBuilder.delete(0, htmlBuilder.length()).append(doc.html());
		}catch(Exception e)
		{
			logger.error("", e);
			needResave = false;
		}
		
		return needResave;
	}

	@Override
	public boolean resaveContent(Long id, BpType bpType, String ip) {
	    
		if (null == id || id.longValue() <= 0 || bpType == null) {
			return false;
		}
		String html = "";
        StringBuilder htmlBuilder = null;
        boolean needUpdate = false;
        boolean result = false;
		try {
			switch (bpType) {
				case Question:
					TEliteQuestion question = eliteAdapter.getQuestionById(id);
                    //添加地域标签
                    Integer tagId = TagUtil.getCityTag(ip, 2);
                    String oriTags = question.getTagIds();
                    if (StringUtils.isBlank(oriTags)) {
                        oriTags = new String("");
                    }
                    if (tagId.intValue() > 0 &&
							!(Constants.TAG_IDS_SEPARATOR + oriTags)
                            .contains(new StringBuilder(Constants.TAG_IDS_SEPARATOR).append(tagId).append(Constants.TAG_IDS_SEPARATOR))) {
                        question.setTagIds(tagId + Constants.TAG_IDS_SEPARATOR + oriTags);
                        needUpdate = true;
                    }
					html = question.getDetail();
					if (StringUtils.isNotBlank(html)) {
                        htmlBuilder = new StringBuilder(html);
                        if (this.resaveContent(htmlBuilder, id, bpType) || this.resaveImage(htmlBuilder)) {
                            question.setDetail(htmlBuilder.toString());
                            needUpdate = true;
                        }
					}
					if (needUpdate) {
                        try {
                            if (!eliteAdapter.updateQuestion(question)) {
                                logger.info("update question failed.questionId=" + question.getId());
                            } else {
                                logger.info("update question success.questionId=" + question.getId());
                                result = true;
                            }
                        } catch (TException e) {
                            logger.error("", e);
                        }
                    } else {
                        result = true;
                    }
					break;
				case Answer:
					TEliteAnswer answer = eliteAdapter.getAnswerById(id);
					html = answer.getContent();
					if (StringUtils.isBlank(html)) {
						return false;
					}
					htmlBuilder = new StringBuilder(html);
					if (this.resaveContent(htmlBuilder, id, bpType) || this.resaveImage(htmlBuilder)) {
						answer.setContent(htmlBuilder.toString());
						try {
							if (!eliteAdapter.updateAnswer(answer)) {
								logger.info("update answer failed.answerId=" + answer.getId());
							} else {
								logger.info("update answer success.answerId=" + answer.getId());
								result = true;
							}
						} catch (TException e) {
							logger.error("", e);
						}
					} else {
					    result = true;
					}
					break;
				default:
					break;
			}
		} catch(Exception e) {
			logger.error("", e);
			
		}
		return result;
	}

	@Override
	public boolean resaveContent(StringBuilder htmlBuilder, long id, BpType bpType) {
		boolean needResave = false;
		if (null == htmlBuilder) {
			return needResave;
		}
		try {
			String content = htmlBuilder.toString();
			Document doc = Jsoup.parse(content);
			Elements links = doc.select("a");
			Iterator<Element> iter = links.iterator();
			while (iter.hasNext()) {
				Element linkEle = (Element) iter.next();
				if (linkEle.hasAttr(ContentUtil.AT_USER_ATTR_NAME)) {
					if (!linkEle.hasAttr(ContentUtil.AT_MARK_ATTR_NAME) || !("1").equalsIgnoreCase(linkEle.attr(ContentUtil.AT_MARK_ATTR_NAME))) {
						String userEncodedId = linkEle.attr(ContentUtil.AT_USER_ATTR_NAME);
						if (StringUtils.isBlank(userEncodedId)) {
							continue;
						}
						Long userId = IDUtil.decodeId(userEncodedId);
						String tipInboxMessage = "";
						TEliteMessageData messageData = null;
						switch (bpType) {
							case Question:
								tipInboxMessage = EliteMessageData.NEW_AT_TIP.getContent()
										.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getQuestionUri(IDUtil.encodeId(id)))
										.replace(Constants.MESSAGE_DATA_CONTENT_TYPE, "问题");
								messageData = new TEliteMessageData()
										.setInboxMessageDataValue(EliteMessageData.NEW_AT_TIP.getValue())
										.setInboxMessageContent(tipInboxMessage);
								break;
							case Answer:
								tipInboxMessage = EliteMessageData.NEW_AT_TIP.getContent()
										.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getAnswerUri(IDUtil.encodeId(id)))
										.replace(Constants.MESSAGE_DATA_CONTENT_TYPE, "回答");
								messageData = new TEliteMessageData()
										.setInboxMessageDataValue(EliteMessageData.NEW_AT_TIP.getValue())
										.setInboxMessageContent(tipInboxMessage);
								break;
							case Comment:
								tipInboxMessage = EliteMessageData.NEW_REPLY_TIP.getContent()
										.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getAnswerUri(IDUtil.encodeId(id)));
								messageData = new TEliteMessageData()
										.setInboxMessageDataValue(EliteMessageData.NEW_REPLY_TIP.getValue())
										.setInboxMessageContent(tipInboxMessage);
						}
						if (null != messageData) {
							//对于问题和回答中的@，在这里发送消息
							List<Long> sourceIdList = new ArrayList<>();
							sourceIdList.add(userId);
							EliteAsyncTaskPool.addTask(
									new EliteMessageDeliverAsyncTask(
											TEliteMessagePushType.INBOX,
											messageData,
											null,
											EliteMessageTargetType.SINGLE,
											BpType.Elite_User,
											sourceIdList));
						}
						//标记@为已通知，下次就不会再通知了
						linkEle.attr(ContentUtil.AT_MARK_ATTR_NAME, "1");
						needResave = true;
					}
				}
			}
			htmlBuilder.delete(0, htmlBuilder.length()).append(doc.body().html());
		}catch(Exception e)
		{
			logger.error("", e);
			needResave = false;
		}

		return needResave;
	}

}

	