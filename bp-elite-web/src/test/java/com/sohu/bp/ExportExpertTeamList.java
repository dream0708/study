package com.sohu.bp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.model.TUserListResult;
import com.sohu.bp.elite.util.DateUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapter;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class )
@ContextConfiguration({"classpath:bpEliteWeb/*.xml","classpath:*.xml"})
public class ExportExpertTeamList {
    private static final Logger log = LoggerFactory.getLogger(ExportExpertTeamList.class);
    private static final BpExtendServiceAdapter bpExtendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final BpWechatServiceAdapter wechatServiceAdapter = BpWechatServiceAdapterFactory.getWechatServiceAdapter();
    
    //导出专家列表
//    @Test
    public void export() {
        File file = new File("d:/专家列表.txt");
        try {
            file.createNewFile();
           } catch (Exception e) {
                log.error("", e);
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
                String firstLine = "id\t名称\t标签\t描述\t电话\n";
                writer.write(firstLine);
                TUserListResult userListResult = eliteAdapter.getExperts(0, Integer.MAX_VALUE);
                log.info("user num : " + userListResult.getTotal());
                List<TEliteUser> users = userListResult.getUsers();
                for (TEliteUser user : users) {
                    String nick = "", tags = "", description = "", cellphone = "";
                    Long bpId = user.getBpId();
                    CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
                    if (ResponseConstants.OK == codeMsgData.getCode()) {
                        JSONObject data = JSONObject.fromObject(codeMsgData.getData());
                        nick = data.getString("nick");
                        cellphone = data.getString("mobile");
                    }
                    description = user.getDescription();
                    String tagIds = user.getTagIds();
                    String[] array = tagIds.split(";");
                    StringBuilder stringBuilder = new StringBuilder();
                    if (null != array && array.length > 0 ){
                        for (String id : array) {
                            if (StringUtils.isNotBlank(id)) {
                                Tag tag= decorationAdapter.getTagById(Integer.valueOf(id));
                               stringBuilder.append(tag.getName() + ";");
                            }
                        }
                    }
                    tags = stringBuilder.toString();
                    writer.write(bpId.toString() + "\t" + nick + "\t" + tags + "\t" + description + "\t" + cellphone + "\n");
                }
            } catch (Exception e) {
                log.error("", e);
            }
    }
    
   //导出专家数据
    @Test
    public void exportDetail() {
        String startTime = "2017-04-11 14:15:00";
        String endTime = "2017-04-14 14:15:00";
        Long begin = null, end = null;
        try {
            begin = DateUtil.parse(startTime, DateUtil.sdf).getTime();
            end = DateUtil.parse(endTime, DateUtil.sdf).getTime();
        } catch (Exception e) {
            log.error("", e);
        }
        File file = new File("/opt/logs/专家数据列表.txt");
        try {
            file.createNewFile();
           } catch (Exception e) {
                log.error("", e);
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
//                String firstLine = "id\t名称\t标签\t描述\t电话\n";
                String firstLine = "id\t名称\t推送数\t回答数\t总问题数\t总回答数\t区间回答";
                writer.write(firstLine);
                TUserListResult userListResult = eliteAdapter.getExperts(0, Integer.MAX_VALUE);
                log.info("user num : " + userListResult.getTotal());
                List<TEliteUser> users = userListResult.getUsers();
                for (TEliteUser user : users) {
                    String nick = "";
                    Integer pushNum = 0, answerNum = 0, totalQuestionNum = 0, totalAnswerNum = 0, totalPeriodNum = 0;
                    Long bpId = user.getBpId();
                    CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
                    if (ResponseConstants.OK == codeMsgData.getCode()) {
                        JSONObject data = JSONObject.fromObject(codeMsgData.getData());
                        nick = data.getString("nick");
                    }
                    TEliteExpertTeam expertTeam = eliteAdapter.getExpertTeamByBpId(bpId);
                    TSearchQuestionCondition questionCondition = new TSearchQuestionCondition();
                    questionCondition.setBpId(user.getBpId()).setFrom(0).setCount(1).setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(),EliteQuestionStatus.PASSED.getValue()));
                    TQuestionListResult questionListResult = eliteAdapter.searchQuestion(questionCondition);
                    totalQuestionNum = (int)questionListResult.getTotal();
                    TSearchAnswerCondition answerCondition = new TSearchAnswerCondition();
                    answerCondition.setBpId(user.getBpId()).setFrom(0).setCount(1).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));
                    TAnswerListResult answerListResult = eliteAdapter.searchAnswer(answerCondition);
                    totalAnswerNum = (int) answerListResult.getTotal();
                    pushNum = expertTeam.getPushNum();
                    answerNum = expertTeam.getAnsweredNum();   
                    //解析区间时间的回答数
                    TSearchAnswerCondition condition = new TSearchAnswerCondition();
                    condition.setBpId(bpId).setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()))
                    .setMaxCreateTime(end).setMinCreateTime(begin).setFrom(0).setCount(1);
                    try {
                        TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
                        totalPeriodNum = (int)listResult.getTotal();
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    writer.write(bpId.toString() + "\t" + nick + "\t" + pushNum + "\t" + answerNum + "\t" + totalQuestionNum + "\t" + totalAnswerNum + "\t" + totalPeriodNum + "\n");
                }
            } catch (Exception e) {
                log.error("", e);
            }
    }
}
