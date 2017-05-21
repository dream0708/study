//package com.sohu.bp.elite.api.api;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.hadoop.hbase.protobuf.generated.CellProtos.CellType;
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.openxml4j.opc.OPCPackage;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.fasterxml.jackson.dataformat.yaml.snakeyaml.scanner.Constant;
//import com.sohu.bp.decoration.service.BpGlobalTypeThriftService.create_args;
//import com.sohu.achelous.timeline.AchelousTimeline;
//import com.sohu.achelous.timeline.service.TimelineService;
//import com.sohu.achelous.timeline.service.impl.TimelineServiceImpl;
//import com.sohu.achelous.timeline.util.TimeLineUtil;
//import com.sohu.bp.decoration.service.BpGlobalTypeThriftService.Processor.create;
//import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
//import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
//import com.sohu.bp.elite.api.enums.BpType;
//import com.sohu.bp.elite.api.enums.ProduceActionType;
//import com.sohu.bp.elite.enums.EliteAnswerStatus;
//import com.sohu.bp.elite.enums.EliteQuestionStatus;
//import com.sohu.bp.elite.model.TEliteAnswer;
//import com.sohu.bp.elite.model.TEliteQuestion;
//import com.sohu.bp.model.BpInteractionDetail;
//import com.sohu.bp.model.BpInteractionTargetType;
//import com.sohu.bp.model.BpInteractionType;
//import com.sohu.bp.service.BpService.Processor.updateUserBindDataExpire;
//import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
//import com.sohu.bp.service.adapter.BpServiceAdapter;
//import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
//import com.sohu.nuc.model.CodeMsgData;
//import com.sohu.nuc.model.ResponseConstants;
//
//import net.sf.json.JSONObject;
//
////TODO 以后要读取时间
//public class SubjectCrawlTest {
//	
//	private static final Logger log = LoggerFactory.getLogger(SubjectCrawlTest.class);
//	private static final BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
//	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//	private static final BpExtendServiceAdapter extendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
//	private static final TimelineService timelineService = AchelousTimeline.getService();
//
//	
//	public static void main(String[] args){
//		try{
//			String createAccount = "d:/project/resource/subjectCrawl/问答账号.xlsx";
//			String[] subjectInformation = new String[]{"d:/project/resource/subjectCrawl/话题1——装修花销觉得值.xlsx",
//						 "d:/project/resource/subjectCrawl/话题二：买不起房的哪些年，如何花最少的钱装出最高逼格的出租屋？.xlsx",
//						 "d:/project/resource/subjectCrawl/话题3——怎样一张床，才能给你想要的高潮？.xlsx"};
//			String[] topicInformation = new String[]{"d:/project/resource/subjectCrawl/专题一：验房篇.xlsx",
//					     "d:/project/resource/subjectCrawl/专题二：预算篇：每天买买买钱包被掏空？是时候恶补装修预算了！.xlsx",
//					     "d:/project/resource/subjectCrawl/专题三：防水专题.xlsx"};
//			SubjectCrawlTest subjectCrawlTest = new SubjectCrawlTest();
////用于生成默认账号
////			subjectCrawlTest.createUser(createAccount);
//			
//			Map<Integer, Long> accountId = subjectCrawlTest.getAccountIdMap(createAccount);
//			for(int i = 0; i < subjectInformation.length; i++){
//			Boolean retVal = subjectCrawlTest.setSubjectInformation(subjectInformation[i], accountId);
//			}
//			for(int j = 0; j < topicInformation.length; j++){
//			Boolean retVal = subjectCrawlTest.setSubjectInformation(topicInformation[j], accountId);
//			}
//			
//		} catch (Exception e){
//			log.error("", e);
//		}
//		System.exit(0);
//	}
//	
//	public void createUser(String file){
//		try{
//			FileInputStream fileInputStream = new FileInputStream(file);
//			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//			fileInputStream.close();
//			XSSFSheet createAccount = workbook.getSheetAt(0);
//			Integer rowNum = createAccount.getLastRowNum();
//			Map<Integer, Long> accountIdMap = new HashMap<>();
//			for(Integer index = 1; index <= rowNum; index++){
//				XSSFRow row = createAccount.getRow(index);
//				Integer account = (int) row.getCell(0).getNumericCellValue();
//				String accoutName = row.getCell(1).getStringCellValue();
//				//addBpUser(mobile, nick, avatar, ip.toString(), port)
//				CodeMsgData codeMsgData = serviceAdapter.addBpUser("", accoutName, "", "", 1);
//				if(ResponseConstants.OK == codeMsgData.getCode()){
//					String data = codeMsgData.getData();
//					if(StringUtils.isNotBlank(data)){
//						JSONObject dataJSON = JSONObject.fromObject(data);
//						if(dataJSON.containsKey("bpid") && dataJSON.getLong("bpid") > 0){
//							Long bpId = dataJSON.getLong("bpid");
//							accountIdMap.put(account, bpId);
//							XSSFCell cell = row.createCell(2);
//							cell.setCellValue(Double.valueOf(bpId.toString()));
//						}
//					}
//				}
//			}
//			FileOutputStream fileOutputStream = new FileOutputStream(file);
//			workbook.write(fileOutputStream);
//			fileOutputStream.flush();
//			fileOutputStream.close();
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}
//	
//	public Map<Integer, Long> getAccountIdMap(String file){
//		Map<Integer, Long> accountId = new HashMap<>();
//		try{
//			FileInputStream fileInputStream = new FileInputStream(file);
//			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//			fileInputStream.close();
//			XSSFSheet sheet = workbook.getSheetAt(0);
//			Integer rowNum = sheet.getLastRowNum();
//			for(int index = 1; index <= rowNum; index++){
//				XSSFRow row = sheet.getRow(index);
//				Integer account = (int) row.getCell(0).getNumericCellValue();
//				Long id = (long) row.getCell(2).getNumericCellValue();
//				accountId.put(account, id);
//			}
//		} catch (Exception e){
//			log.error("", e);
//		}
//		return accountId;
//	}
//	
//	public Boolean setSubjectInformation(String file, Map accountId){
//		Boolean retValue = true;
//		try{
//			FileInputStream fileInputStream = new FileInputStream(file);
//			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
//			fileInputStream.close();
//			FileOutputStream fileOutputStream = new FileOutputStream(file);
//			Integer sheetNum = workbook.getNumberOfSheets();
//			for(int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex ++){
//			XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
//			Integer rowNum = sheet.getLastRowNum();
//			//读取问题
//			XSSFRow row = sheet.getRow(1);
//			Integer questionAccount = (int) row.getCell(0).getNumericCellValue();
//			String questionName = row.getCell(1).getStringCellValue();
//			TEliteQuestion question = new TEliteQuestion();
//			Long bpId = (Long) accountId.get(questionAccount);
//			question.setBpId(bpId)
//					.setTitle(questionName)
//					.setCreateTime(new Date().getTime())
//					.setStatus(EliteQuestionStatus.PASSED.getValue());
//			Long questionId = eliteAdapter.insertQuestion(question);
//			
//			//用于将问题推到timeline
//				long bpComplexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
//				long questionComplexId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
//				timelineService.produce(bpComplexId, questionComplexId, ProduceActionType.ASK.getValue(), new Date());
//				
//			XSSFCell cell = row.createCell(4);
//			cell.setCellValue(Double.valueOf(questionId.toString()));
//			//读取回答
//			Long answerId = 0l;
//			for(Integer index = 2; index <= rowNum; index++){
//				boolean flag = true;
//				row = sheet.getRow(index);
//				log.info("index = " + index.toString());
//				if(null == row || null == row.getCell(0) || 0 == row.getCell(0).getNumericCellValue()||2 == index){
//					while(null == row ||null == row.getCell(0)|| 0 == row.getCell(0).getNumericCellValue()) {
//							index++;
//							row = sheet.getRow(index);
//							if(index >  rowNum) {
//								flag = false;
//								break;
//							}
//						}
//					if(false == flag) break;
//					//填充回答
//					Integer answerAccount = (int) row.getCell(0).getNumericCellValue();
//					String answerContent = row.getCell(2).getStringCellValue();
//					TEliteAnswer answer = new TEliteAnswer();
//					bpId = (long) accountId.get(answerAccount);
//					answer.setBpId(bpId)
//						  .setQuestionId(questionId)
//						  .setContent(answerContent)
//						  .setCreateTime(new Date().getTime())
//						  .setStatus(EliteAnswerStatus.PASSED.getValue());
//					answerId = eliteAdapter.insertAnswer(answer);
//					//将answer推送到timeline
//					bpComplexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
//					Long answerComplexId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
//					timelineService.produce(bpComplexId, answerComplexId, ProduceActionType.ANSWER.getValue(), new Date());
//					timelineService.produce(questionComplexId, answerComplexId, ProduceActionType.ADD.getValue(), new Date());
//					
//					cell = row.createCell(4);
//					cell.setCellValue(Double.valueOf(answerId.toString()));
//					continue;
//				}
//				//进行评论
//				Integer commentAccount = (int) row.getCell(0).getNumericCellValue();
//				String commentDetail = row.getCell(3).getStringCellValue();
//				
//				BpInteractionDetail interaction = new BpInteractionDetail();
//				JSONObject extraJSON = new JSONObject();
//				extraJSON.put("data", commentDetail);
//				
//				interaction.setBpid((long) accountId.get(commentAccount))
//						   .setType(BpInteractionType.COMMENT)
//						   .setTargetId(answerId)
//						   .setTargetType(BpInteractionTargetType.ELITE_ANSWER)
//						   .setExtra(extraJSON.toString())
//						   .setCreateTime(new Date().getTime())
//						   .setUpdateTime(new Date().getTime())
//						   .setCreateHost(10l)
//						   .setCreatePort(8080);
//				
//				CodeMsgData codeMsgData = extendAdapter.addBpInteraction(interaction);
//				if(ResponseConstants.OK == codeMsgData.getCode()){
//					log.info("insert commetn succeeded; answerId = {} and comment detail = {}", new Object[]{ answerId.toString(), commentDetail.toString()});
//					JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
//					cell = row.createCell(4);
//					cell.setCellValue(Double.valueOf(dataJSON.getString("interactionId")));
//				}
//				else retValue = false;
//				
//			}
//			
//			
//			}
//			workbook.write(fileOutputStream);
//			fileOutputStream.close();
//		} catch (Exception e) {
//			log.error("" ,e);
//			retValue = false;
//		}
//
//		return retValue;
//	}
//	
////	public Long randomPublishandUpdateTime(Long floor, Long ceiling){
////		Random rand = new Random(47);
////		
////	}
//}
//
