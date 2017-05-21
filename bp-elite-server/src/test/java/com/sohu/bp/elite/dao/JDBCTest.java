package com.sohu.bp.elite.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;

import kafka.utils.SystemTime;
import net.sf.json.JSONObject;

/**
 * 通过JDBC来增删改查数据库中的字段。
 * 
 * @author zhijungou 2016年11月14日
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration({"classpath:spring/applicationContext-*.xml",
// "classpath:springmvc-servlet.xml"})
public class JDBCTest {
    private static Logger log = LoggerFactory.getLogger(JDBCTest.class);
    private static String url = "jdbc:mysql://10.16.1.238:3306/bp_decorate_0?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull";
    private static String url_product = "jdbc:mysql://10.10.13.155:3306/bp_dec_0?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull" ;
    private static String[] mysqls = { "mysql://10.16.1.238:3306/bp_decorate_0", "mysql://10.16.1.238:3306/bp_decorate_1", "mysql://10.16.1.238:3306/bp_decorate_2", "mysql://10.16.1.238:3306/bp_decorate_3" };
    private static String[] mysqls_product = {"mysql://10.10.13.155:3306/bp_dec_0", "mysql://10.11.165.67:3306/bp_dec_1", "mysql://10.16.53.98:3306/bp_dec_2", "mysql://192.168.5.31:3306/bp_dec_3"};
    private static String[] usernames = { "root", "root", "root", "root" };
    private static String[] passwords = { "mysql", "mysql", "mysql", "mysql" };
    private static String[] usernames_product = {"bp_dec_0_rw", "bp_dec_1_rw", "bp_dec_2_rw", "bp_dec_3_rw"};
    private static String[] passwords_product = {"B83NFc6N8s2o5ED", "hm+8nsvd3bAxp4E", "n1ixJ0mv5eyqC0M", "oIyD5z3FGIh4GBe"};
    private static final int SIZE = 16;
    private static String sql_answer = "select * from elite_answer_0 where source = '10' and source_url = ''";
    private static String update_sql_answer = "update elite_answer_0 set source = '2' where source ='10' and source_url = ''";
    private static String sql_question = "select * from elite_question_0 where source = '10' and source_url = ''";
    private static String SQL_ALL_QUESTION = "select * from elite_question_0";
    private static String SQL_ALL_USER = "select * from elite_user_0";
    private static String SQL_ALL_ANSWER = "select * from elite_answer_0";
    private static String update_sql_question = "update elite_question_0 set source = '2' where source ='10' and source_url = ''";
    private static String update_sql_users = "alter table elite_user_0 add area_code bigint(20) not null comment '地区码'";
    private static String update_sql_users2 = "alter table elite_user_0 modify area_code bigint(20) default null comment '地区码'";
    private static String SQL_ALL_EXPERTTEAM = "select * from elite_expert_team";
    
    //question + options | answer + specialId + specialType | answer by question + specialId + speicalType
    private static String QUESTION_ADD_OPTIONS = "alter table elite_question_0 add options text default null comment '用于记录投票贴等选择内容'";
    private static String QUESTION_DROP_OPTIONS = "alter table elite_question_0 drop options";
    private static String ANSWER_ADD_SPECIAL_ID = "alter table elite_answer_0 add special_id bigint(20) default null comment '回答所选的特色内容id'";
    private static String ANSWER_ADD_SPECIAL_TYPE = "alter table elite_answer_0 add special_type int(11) default null comment '回答所属特色内容种类'";
    private static String ANSWER_BY_QUESTION_ADD_SPECIAL_ID = "alter table elite_answer_byquestionid_0 add special_id bigint(20) default null comment '回答所选特色内容id'";
    private static String ANSWER_BY_QUESTION_ADD_SPECIAL_TYPE = "alter table elite_answer_byquestionid_0 add special_type int(11) default null comment '回答所属特色内容种类'";
    private static String ANSWER_BY_QUESTION_ADD_UNIFIED_KEY = "alter table elite_answer_byquestionid_0 add key `index_by_special`(`special_type`, `special_id`)";

    //elite_expert_team
    private static String EXPERT_TEAM_ADD_GROUP = "alter table elite_expert_team add team bigint(20) default null comment '用于记录专家分组'";
    //kafka
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    
//     @Resource
    private NotifyService notifyService;

    public static void main(String[] args) {
        JDBCTest jdbc = new JDBCTest();
//        try {
//            jdbc.getIdsAndAction(SQL_ALL_ANSWER, bpId -> {
////          notifyService.notify2Statistic(bpId, BpType.Answer.getValue(),
////          EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
//              System.out.println("id : " + bpId);
//     });
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        jdbc.sendKafkaMessage(12802L, BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue(), "elite");
    }
    //用于获取ids并操作的示例
//    @Test
    public void tablesAndActionTest() {
        try {
            getIdsAndAction(SQL_ALL_ANSWER, bpId -> {
//                notifyService.notify2Statistic(bpId, BpType.Answer.getValue(),
//                EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
                System.out.println("id : " + bpId);
            });
        } catch (SQLException e) {
            log.error("", e);
        }
    }
    //扫单表获取ids并操作的示例
//    @Test
    public void tableAndActionTest() {
        String myUrl = url.replace("mysql://10.16.1.238:3306/bp_decorate_0", mysqls[2]);
        try {
            getIdsAndAction(SQL_ALL_EXPERTTEAM, myUrl, usernames[2], passwords[2], bpId -> {
                notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(),
                EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
                System.out.println("id : " + bpId);
            });
        } catch (SQLException e) {
            log.error("", e);
        }
    }
    
    //扫表更新操作的示例
//    @Test
    public void tablesAndUpdateTest() {
        try {
            getIdsAndUpdate(QUESTION_ADD_OPTIONS);
        } catch (Exception e) {
            log.error("", e);
        }
    }
    //单表更新
    @Test
    public void singTableUpdateTest() {
        try {
            String localUrl = url.replace("mysql://10.16.1.238:3306/bp_decorate_0", mysqls[2]);
            singleTableAlert(EXPERT_TEAM_ADD_GROUP, localUrl, usernames[0], passwords[0]);
        } catch (Exception e) {
            log.error("", e);
        }
    }
    //用于扫表更新(增删改)
    public void getIdsAndUpdate(String sqlQuery) throws SQLException {
        for (int index = 0; index < 4; index++) {
            String mysql_url = url.replace("mysql://10.16.1.238:3306/bp_decorate_0", mysqls[index]);
            String username = usernames[index];
            String password = passwords[index];
            try (Connection conn = DriverManager.getConnection(mysql_url, username, password)) {
                int start = index * SIZE;
                int end = (index + 1) * SIZE;
                for (int i = start; i < end; i++) {
                    String new_sql = sqlQuery.replaceFirst("\\d", String.valueOf(i));
                    try (PreparedStatement ps = conn.prepareStatement(new_sql)) {
                        ps.executeUpdate();
                    }
                }
            }

        }

    }
    //用于扫表获取id并进行操作
    public void getIdsAndAction(String sqlQuery, Consumer<Long> consumer) throws SQLException {
        for (int index = 0; index < 4; index++) {
//            String mysql_url = url_product.replace("mysql://10.16.1.238:3306/bp_decorate_0", mysqls[index]);
            String mysql_url = url_product.replace("mysql://10.10.13.155:3306/bp_dec_0", mysqls_product[index]);
            String username = usernames_product[index];
            String password = passwords_product[index];
            try (Connection conn = DriverManager.getConnection(mysql_url, username, password)) {
                int start = index * SIZE;
                int end = (index + 1) * SIZE;
                for (int i = start; i < end; i++) {
                    String new_sql = sqlQuery.replaceFirst("\\d", String.valueOf(i));
                    try (PreparedStatement ps = conn.prepareStatement(new_sql)) {
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            Long id = rs.getLong(1);
                            consumer.accept(id);
                        }
                    }
                }
            }

        }
    }
    //用于单表获取id
    public void getIdsAndAction(String sqlQuery, String url, String username, String password, Consumer<Long> consumer) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    consumer.accept(id);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
    //用于单表进行修改
    public void singleTableAlert(String sqlQuery, String url, String username, String password) {
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement ps = con.prepareStatement(sqlQuery)) {
                int num = ps.executeUpdate();
                System.out.println("result : " + num);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
    public void sendKafkaMessage(Long objectId, Integer objectType, Integer notifyType, String topic) {
        JSONObject msg = new JSONObject();
        msg.put("objectId", objectId);
        msg.put("objectType", objectType);
        msg.put("notifyType", notifyType);
        System.out.println("notify to statistic. message = " + msg.toString());
        try
        {
            kafkaProducer.send(topic, msg.toString());
        }catch(Exception e)
        {
            log.error("", e);
        }
    }

}
