package com.sohu.bp.elite.api.api;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.sohu.bp.elite.api.enums.ProduceActionType;
import com.sohu.bp.elite.api.util.RequestUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.utils.crypt.AESUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSON;
import net.sf.json.JSONObject;


public class EliteCrawlTxt {
	private static final Logger log = LoggerFactory.getLogger(EliteCrawlTxt.class); 
	private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private static final BpExtendServiceAdapter extendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	
	private static final TimelineService timelineService = AchelousTimeline.getService();
	
	public static void main(String[] args) {
//		String[] accountId = {"D:/project/resource/eliteAccount/问答账号测试.txt","D:/project/resource/eliteAccount/问答账号.txt"};
//		String[] createFile = {"D:/project/resource/eliteCrawl/0923am新增问答.txt"};
//		String[] finalFile = {"D:/project/resource/eliteCrawl/0923am生成问答.txt"};

		String[] accountId = {"D:/opt/logs/elite-upload/问答账号.txt"};

		String[] createFile = {"D:/opt/logs/elite-upload/1102am新增问答.txt"};
		String[] finalFile = {"D:/opt/logs/elite-uploaded/final1102am新增问答.txt"};
		
		EliteCrawlTxt txtReader = new EliteCrawlTxt();
//		if(!txtReader.check(createFile[0])){
//			System.out.println("format of file is incorrect");
//			System.exit(0);
//		}
		Map accountMap = txtReader.getAccountId(accountId[0]);
		log.info("accountMap is = " + accountMap.toString());
		Boolean result = txtReader.setQuestionAndAnswerInformation(createFile[0], finalFile[0], accountMap);
		System.exit(0);
	}
	
	public Map<Integer, Long> getAccountId(String fileName){
		Map<Integer, Long> accountMap = new HashMap<>();
		BufferedReader reader = null;
		try{
			File file = new File(fileName);
			if(!file.exists()) return accountMap;
			System.out.println("一次读入一行");
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			reader = new BufferedReader(isr);
			int line = 2;
			String readLine = reader.readLine();
			readLine = reader.readLine();
			while(null != readLine){
				log.info("line index = {} and line content = {}", new Object[]{ line, readLine});
				readLine = readLine.trim();
				String[] ids = readLine.split("\t");
				Integer accountId = Integer.valueOf(ids[0]);
				Long bpId = Long.valueOf(ids[2]);
				accountMap.put(accountId, bpId);
				readLine = reader.readLine();
			}
			reader.close();
		} catch (Exception e){
			log.error("", e);
		}
		return accountMap;
	}
	
	public Boolean setQuestionAndAnswerInformation(String fileInput, String fileOutput, Map accountId){
		Boolean retVal = false;
		try{
			File filein = new File(fileInput);
			File fileout = new File(fileOutput);
			FileInputStream fisin = new FileInputStream(filein);
			FileOutputStream fisout = new FileOutputStream(fileout);
			InputStreamReader isrin = new InputStreamReader(fisin, "UTF-8");
			OutputStreamWriter oswout = new OutputStreamWriter(fisout, "UTF-8");
			
			BufferedReader fileReader = new BufferedReader(isrin);
			BufferedWriter fileWriter = new BufferedWriter(oswout);
			
			
			//开始进行解析文件
			//[0] - bpId	[1] - question	[2] - answer	[3] - comment	[4] - date	[5] - tag	[6] - questionId
			Integer lineIndex = 2;
			String readLine = fileReader.readLine();
			Long questionId = null, answerId = null, bpId = null, dateLong = null;
			Long questionComplexId = null, answerComplexId = null, bpComplexId = null, tagComplexId = null;
			ArrayList<Integer> tagIdList = null;
			
			for(lineIndex = 2; null != (readLine = fileReader.readLine());lineIndex ++){
				String[] stringBlocks = readLine.split("\t");
				if (0 == stringBlocks.length) continue;
				//check questionId of each line
				if(isNotNull(stringBlocks, 6)){
					questionId = AESUtil.decryptIdV2(stringBlocks[6]);
					questionComplexId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
					TEliteQuestion question = eliteAdapter.getQuestionById(questionId);
					String tags = question.getTagIds();
					String[] tagNames = tags.split(";");
					tagIdList = new ArrayList<Integer>();
					for(String tag : tagNames) {
						tagIdList.add(Integer.valueOf(tag));
					}
					continue;
				}
				bpId = (Long) accountId.get(Integer.valueOf(stringBlocks[0]));
				bpComplexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
				if (isNotNull(stringBlocks, 4)){
					dateLong = parseDate(stringBlocks[4]);
				} else dateLong = new Date().getTime();
				
				//fill question
				if (isNotNull(stringBlocks, 1)){
					tagIdList = new ArrayList<Integer>();
					String tagIds = "";
					String title = stringBlocks[1];
					TEliteQuestion question = new TEliteQuestion();
					if(isNotNull(stringBlocks, 5)){
						String tagString = stringBlocks[5];
						String[] tagNames = tagString.split(";");
						if(null != tagNames && tagNames.length > 0){
							for (String tag : tagNames){
								SearchTagCondition tagCondition = new SearchTagCondition();
								tagCondition.setKeywords(tag).setType(TagType.ELITE_TAG).setStatus(TagStatus.WORK).setFrom(0).setCount(1);
								TagListResult tagListResult = decorationAdapter.searchTag(tagCondition);
								if(null != tagListResult && tagListResult.getTotal() > 0){
									tagIdList.add(tagListResult.getTags().get(0).getId());
									tagIds += tagListResult.getTags().get(0).getId() + ";";
								}
							}
						}				
					}
					question.setBpId(bpId).setTitle(title).setTagIds(tagIds).setCreateTime(dateLong).setUpdateTime(dateLong).setStatus(EliteQuestionStatus.PASSED.getValue());
					questionId = eliteAdapter.insertQuestion(question);
					log.info("row index = {}, insert question succeed! question id = {}, question title = {}, question tags = {}",
							new Object[]{ lineIndex, questionId, question.getTitle(), question.getTagIds()});
					fileWriter.newLine();
					fileWriter.write("Question : id = " + questionId + "title = " + title);
					questionComplexId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
					timelineService.produce(bpComplexId, questionComplexId, ProduceActionType.ASK.getValue(), new Date(dateLong));
					for (Integer tag : tagIdList){
						tagComplexId = TimeLineUtil.getComplexId(tag.longValue(), BpType.Tag.getValue());
						timelineService.produce(tagComplexId, questionComplexId, ProduceActionType.ADD.getValue(), new Date(dateLong));
					}
				}
				else if(isNotNull(stringBlocks, 2)){
				//fill answer	
					TEliteAnswer answer = new TEliteAnswer();
					String content = stringBlocks[2];
					answer.setBpId(bpId).setContent(content).setQuestionId(questionId).setCreateTime(dateLong).setUpdateTime(dateLong).setStatus(EliteAnswerStatus.PASSED.getValue());
					answerId = eliteAdapter.insertAnswer(answer);
					log.info("row index = {}, insert answer succeed! answer id = {}, answer content = {}", new Object[]{lineIndex, answerId, content});
					fileWriter.write("Answer : id = " + answerId + "content = " + content);
					answerComplexId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
					timelineService.produce(bpComplexId, answerComplexId, ProduceActionType.ANSWER.getValue(), new Date(dateLong));
					timelineService.produce(questionComplexId, answerComplexId, ProduceActionType.ADD.getValue(), new Date(dateLong));
					for (Integer tag : tagIdList){
						tagComplexId = TimeLineUtil.getComplexId(tag, BpType.Tag.getValue());
						timelineService.produce(tagComplexId, answerComplexId, ProduceActionType.ADD.getValue(), new Date(dateLong));
					}
				}
				else if(isNotNull(stringBlocks, 3)){
					//fill comment
				JSONObject extraJSON = new JSONObject();
				String commentDetail = stringBlocks[3];
				extraJSON.put("data", commentDetail);
				BpInteractionDetail comment = new BpInteractionDetail();
				comment.setBpid(bpId).setTargetId(answerId).setTargetType(BpInteractionTargetType.ELITE_ANSWER)
					   .setType(BpInteractionType.COMMENT).setCreateTime(dateLong).setUpdateTime(dateLong)
					   .setCreateHost(10l).setCreatePort(8080).setExtra(extraJSON.toString());
				CodeMsgData codeMsgData = extendAdapter.addBpInteraction(comment);
				if(ResponseConstants.OK == codeMsgData.getCode()){
					JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
					Long commentId = dataJSON.getLong("interactionId");
					log.info("row index = {}, insert comment succeed, comment id = {}, comment detail = {}",
							new Object[]{lineIndex, commentId, commentDetail});
					fileWriter.write("Comment : id =" + commentId + "content = " + commentDetail);
				}				
				}
				Thread.sleep(100);
			}
			fileReader.close();
			fileWriter.close();
		} catch (Exception e){
			log.error("" ,e);
		}
		return true;
	}
	
	public Boolean isNotNull(String[] stringBlocks, Integer index){
		return (index < stringBlocks.length && null != stringBlocks[index] && "" != stringBlocks[index] && StringUtils.isNotBlank(stringBlocks[index]));
	}
	public Long parseDate(String oriDate){
		String year = "2016";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm");
		Long date = null;
		try{
		date = sdf.parse(year + oriDate).getTime();
		} catch (Exception e) {
			log.error("", e);
		}
		return date;
	}

	/**
	 * aimed to check the correct of file format
	 * [0] - bpId	[1] - question	[2] - answer	[3] - comment	[4] - date	[5] - tag	[6] - questionId
	 * 
	 */
	public Boolean check(String fileInput){
		try{
			FileInputStream fs = new FileInputStream(new File(fileInput));
			InputStreamReader ir = new InputStreamReader(fs);
			BufferedReader reader = new BufferedReader(ir);
			
			Integer lineIndex = 2;
			String readLine = reader.readLine();
			String bpId = null, question = null, answer = null, comment = null, tag = null, questionId = null;
			for(lineIndex = 2; null != (readLine = reader.readLine()); lineIndex ++ ){
				String[] stringBlocks = readLine.split("\t");
				if(0 == stringBlocks.length) {
					question = null;
					continue;
				}
				// date format is incorrect
				if(isNotNull(stringBlocks, 4)){
					Long date = parseDate(stringBlocks[4]);
					if(null == date){ return false;}
				}
				if(isNotNull(stringBlocks, 6)){
					questionId = question = stringBlocks[6];
					Long idLong = AESUtil.decryptIdV2(question);
					if(null == idLong || idLong <= 0) return false;
					continue;
				}
				//bpId is blank
				if(!isNotNull(stringBlocks, 0)) return false;
				if(isNotNull(stringBlocks, 1)) {
					question= stringBlocks[1];
					continue;
				}
				if(isNotNull(stringBlocks, 3)){
					if(null == question) return false;
					else continue;
				}
			}
			reader.close();
		} catch (Exception e){
			log.error("", e);
			return false;
		}
		
		return true;
	}
	

}
