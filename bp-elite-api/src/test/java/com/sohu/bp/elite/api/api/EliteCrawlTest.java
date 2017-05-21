package com.sohu.bp.elite.api.api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table.Cell;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.SearchTagCondition;
import com.sohu.bp.decoration.model.TagListResult;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.enums.BpType;
import com.sohu.bp.elite.api.enums.ProduceActionType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.utils.crypt.AESUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;
import reactor.jarjar.com.lmax.disruptor.dsl.ProducerType;
import scala.reflect.generic.Trees.New;

public class EliteCrawlTest {

	private static final Logger log = LoggerFactory.getLogger(EliteCrawlTest.class);
	private static final BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private static final BpExtendServiceAdapter extendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static final TimelineService timelineService = AchelousTimeline.getService();
	private static final BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	
	public static void main(String[] args) throws ParseException {
//		String file = "d:/project/resource/eliteCrawl/1014am新增问答.xlsx";
//		String createAccount = "d:/project/resource/eliteAccount/问答账号.xlsx";
//	
//		EliteCrawlTest eliteCrawl = new EliteCrawlTest();
//		Map<Integer, Long> accountId = eliteCrawl.getAccountIdMap(createAccount);
//		Boolean retVal = eliteCrawl.setQuestionAndAnswer(file, accountId);
//		System.exit(0);
//		
		//重新添加timeline
//		Long questionComplexId = TimeLineUtil.getComplexId(10809l, BpType.Question.getValue());
//		Long bpComplexId = TimeLineUtil.getComplexId(7996725l, BpType.Elite_User.getValue());
//		String date = "0928-18:28";
//		timelineService.produce(bpComplexId, questionComplexId, ProduceActionType.ADD.getValue(), new Date(eliteCrawl.convert2Time(date)));
//		System.exit(0);
//		TEliteAnswer answer = eliteAdapter.getAnswerById(AESUtil.decryptIdV2("be753bd5dd34917a2233bb69684862d1"));
//		answer.setContent("其实厨房也应该注重自然光源的采光，这样不仅可以有效杀灭厨房内的细菌，而且从环保节能方面来说也更好，白天做菜也不用开灯了。");
//		eliteAdapter.updateAnswer(answer);
//		Long[] ids = {8601358l};
//		for(Long id : ids){
//			TEliteUser userElite = new TEliteUser();
//    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    		userElite.setFirstLoginTime(sdf.parse("2016-10-01").getTime());
//    		userElite.setLastLoginTime(new Date().getTime());
//    		userElite.setBpId(id);
//    		userElite.setStatus(1);
//    		userElite.setFirstLogin(0);
//    		Long userId = eliteAdapter.insertUser(userElite);
//    		if(null != userId && userId >0){
//    			log.info("insert user bpId = {} suceed!", new Object[]{userId});
//    		}
//			System.out.println("insert user, bpId = " + id);
		try{
			TEliteSubject subject = eliteAdapter.getHistoryById(5l);
			System.out.println("subject name is " + subject.getName());
		} catch (Exception e){
			System.out.println("\n" + e);
		}
	}
	
	
	public Map<Integer, Long> getAccountIdMap(String file){
		Map<Integer, Long> accountId = new HashMap<>();
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			fileInputStream.close();
			XSSFSheet sheet = workbook.getSheetAt(0);
			Integer rowNum = sheet.getLastRowNum();
			for(int index = 1; index <= rowNum; index++){
				XSSFRow row = sheet.getRow(index);
				Integer account = (int) row.getCell(0).getNumericCellValue();
				Long id = (long) row.getCell(2).getNumericCellValue();
				accountId.put(account, id);
			}
		} catch (Exception e){
			log.error("", e);
		}
		return accountId;
	}
	
	public Boolean setQuestionAndAnswer(String file, Map accountId){
		Boolean retVal = false;
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			fileInputStream.close();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			Integer sheetNum = workbook.getNumberOfSheets();
			for(int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex ++){
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				Integer rowNum = sheet.getLastRowNum();
				//开始读取问题和答案
				Long questionId = null , answerId = null, bpComplexId = null, answerComplexId = null, questionComplexId = null, tagComplexId = null;
				List<Integer> tagIdList = null;
				for(int rowIndex = 1; rowIndex <= rowNum; rowIndex ++){
					XSSFRow row = sheet.getRow(rowIndex);
					if(isNullCell(row, 0)) continue;
					Integer answerAccount = (int) row.getCell(0).getNumericCellValue();
					String dateString = row.getCell(4).getStringCellValue();
					Long createTime = convert2Time(dateString);
					Long bpId = (long) accountId.get(answerAccount);
					bpComplexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
					//填充回答
					if(isNullCell(row, 1) && isNullCell(row, 3)){
						String answerContent = row.getCell(2).getStringCellValue();
						TEliteAnswer answer = new TEliteAnswer();
						answer.setBpId(bpId)
							  .setQuestionId(questionId)
							  .setContent(answerContent)
							  .setUpdateTime(createTime)
							  .setCreateTime(createTime)
							  .setStatus(EliteAnswerStatus.PASSED.getValue());
						answerId = eliteAdapter.insertAnswer(answer);
						if(null != answerId && answerId > 0){
							answerComplexId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
							timelineService.produce(bpComplexId, answerComplexId, ProduceActionType.ANSWER.getValue(), new Date(createTime));
							timelineService.produce(questionComplexId, answerComplexId, ProduceActionType.ADD.getValue(), new Date(createTime));
					        if(null != tagIdList && tagIdList.size() > 0){
					            for(Integer tagId : tagIdList){
					                tagComplexId = TimeLineUtil.getComplexId(tagId, BpType.Tag.getValue());
					                timelineService.produce(tagComplexId, answerComplexId, ProduceActionType.ADD.getValue(), new Date(createTime));
					            }
					        }
							XSSFCell cell = row.createCell(6);
							cell.setCellValue(answerId.doubleValue());
							log.info("row index = {}, insert answer succeed, answerId = {} and questionContent = {}", new Object[]{rowIndex, answerId, answer.getContent()});
						}
					} 
					else if(isNullCell(row, 1)) {
						//评论
						JSONObject extraJSON = new JSONObject();
						String commentDetail = row.getCell(3).getStringCellValue();
						extraJSON.put("data", commentDetail);
						BpInteractionDetail interactionDetail = new BpInteractionDetail();
						interactionDetail.setBpid(bpId)
										 .setType(BpInteractionType.COMMENT)
										 .setTargetId(answerId)
										 .setTargetType(BpInteractionTargetType.ELITE_ANSWER)
										 .setExtra(extraJSON.toString())
										 .setCreateTime(createTime)
										 .setUpdateTime(createTime)
										 .setCreateHost(10l)
										 .setCreatePort(8080);
						CodeMsgData codeMsgData = extendAdapter.addBpInteraction(interactionDetail);
						if(ResponseConstants.OK == codeMsgData.getCode()){
							JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
							Long commentId = dataJSON.getLong("interactionId");
							log.info("row index = {}, insert comment succeed, commentId = {} and commentDetail = {}", new Object[]{rowIndex, commentId, commentDetail});
							XSSFCell cell = row.createCell(6);
							cell.setCellValue(commentId.doubleValue());
						}
						
					} else{
						//填充问题
						tagIdList = new ArrayList<>();
						TEliteQuestion question = new TEliteQuestion();
						String questionTitle = row.getCell(1).getStringCellValue();
						String tagString = "";
						if(!isNullCell(row, 5)) tagString = row.getCell(5).getStringCellValue();
						String tagIds = "";
						if(StringUtils.isNotBlank(tagString)){
							String[] tagNames = tagString.split(";");
							if(null != tagNames && tagNames.length > 0)
							{
								for(String tagName : tagNames)
								{
									
									SearchTagCondition searchTagCondition = new SearchTagCondition();
									searchTagCondition.setKeywords(tagName);
									searchTagCondition.setType(TagType.ELITE_TAG);
									searchTagCondition.setStatus(TagStatus.WORK);
									searchTagCondition.setFrom(0);
									searchTagCondition.setCount(1);
									TagListResult tagListResult = decorationServiceAdapter.searchTag(searchTagCondition);
									if(null != tagListResult && tagListResult.getTags() != null && tagListResult.getTags().size() > 0)
									{
										tagIds = tagIds + tagListResult.getTags().get(0).getId() + ";";
										tagIdList.add(tagListResult.getTags().get(0).getId());
									}
									
								}
							}
						}
						question.setTagIds(tagIds);
						question.setBpId(bpId)
							    .setTitle(questionTitle)
							    .setUpdateTime(createTime)
							    .setCreateTime(createTime)
							    .setStatus(EliteQuestionStatus.PASSED.getValue());
						questionId = eliteAdapter.insertQuestion(question);
						if(null != questionId && questionId > 0){
							XSSFCell cell = row.createCell(6);
							cell.setCellValue(questionId.doubleValue());
							questionComplexId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
							timelineService.produce(bpComplexId, questionComplexId, ProduceActionType.ASK.getValue(), new Date(createTime));
							
							if(null != tagIdList && tagIdList.size() > 0){
								for(Integer tagId : tagIdList){
									tagComplexId = TimeLineUtil.getComplexId(tagId, BpType.Tag.getValue());
									timelineService.produce(tagComplexId, questionComplexId, ProduceActionType.ADD.getValue(), new Date(createTime));
								}
							}
						}
					}
					Thread.sleep(200);
				}
			}
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			retVal = true;
		} catch (Exception e) {
			log.error("", e);
		}
		return retVal;
	}
	
	public Boolean isNullCell(XSSFRow row, Integer index){
		return (null == row || null == row.getCell(index) || null == row.getCell(index).getRawValue() || "" == row.getCell(index).getRawValue());
	}
	
	public Long convert2Time(String dateString){
		String year = "2016";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hh:mm");
		Long date = null;
		try{
			date = sdf.parse(year+dateString).getTime();
		} catch (Exception e) {
			log.error("", e);
		}
		return date;
		
	}
	
}
