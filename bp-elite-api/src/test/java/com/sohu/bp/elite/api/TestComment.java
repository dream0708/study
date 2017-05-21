//package com.sohu.bp.elite.api;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
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
//import com.sohu.bp.util.HostTool;
//import com.sohu.nuc.model.CodeMsgData;
//import com.sohu.nuc.model.ResponseConstants;
//
//import net.sf.json.JSONObject;
//
//public class TestComment {
//	
//	private static final BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
//	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//	private static final BpExtendServiceAdapter extendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
//	private static final TimelineService timelineService = AchelousTimeline.getService();
//
//	
//	public static void main(String[] args){
//		try{
//			Integer commentAccount = 11210;
//			String commentDetail = "沙发";
//			
//			BpInteractionDetail interaction = new BpInteractionDetail();
//			JSONObject extraJSON = new JSONObject();
//			extraJSON.put("data", commentDetail);
//			
//			interaction.setBpid((long) commentAccount)
//					   .setType(BpInteractionType.COMMENT)
//					   .setTargetId(522l)
//					   .setTargetType(BpInteractionTargetType.ELITE_ANSWER)
//					   .setExtra(extraJSON.toString())
//					   .setCreateTime(new Date().getTime())
//					   .setUpdateTime(new Date().getTime())
//					   .setCreateHost(HostTool.ipToLong("10.0.76.173"))
//					   .setCreatePort(8080);
//			
//			CodeMsgData codeMsgData = extendAdapter.addBpInteraction(interaction);
//			if(ResponseConstants.OK == codeMsgData.getCode()){
//				
//				JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
//			}			
//		} catch (Exception e){
//
//		}
//		System.exit(0);
//	}
//	
//	
//	
//}
