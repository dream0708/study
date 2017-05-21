package com.sohu.bp.elite.hive;

import com.beust.jcommander.internal.Lists;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.auth.PlainSaslHelper;
import org.apache.hive.service.cli.thrift.*;
import org.apache.hive.service.rpc.thrift.*;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;

import javax.security.sasl.SaslException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by guohaozhao on 2016/10/28.
 */
public class HiveClientThriftImpl implements HiveClient {

    private static final Logger logger = Logger.getLogger(HiveClientThriftImpl.class);

    private static final String splitChar = "<>";
    public List<String> query(String hql) throws SaslException, TException {
//        String sql = " select count(url) from nginx_log where url like '%/decoration/index.html%' or  url like '/?%' or  url like '?%'";
        List<String> result = Lists.newArrayList();
        TTransport transport = HiveAuthFactory.getSocketTransport("192.168.45.85", 10000, 999999);
        transport = PlainSaslHelper.getPlainTransport("hive", "hive", transport);
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        TCLIService.Client client = new TCLIService.Client(protocol);
        transport.open();
        TOpenSessionReq openReq = new TOpenSessionReq();
        TOpenSessionResp openResp = client.OpenSession(openReq);
        TSessionHandle sessHandle = openResp.getSessionHandle();
        TExecuteStatementReq execReq = new TExecuteStatementReq(sessHandle, hql);
        TExecuteStatementResp execResp = client.ExecuteStatement(execReq);
        TOperationHandle stmtHandle = execResp.getOperationHandle();
        TFetchResultsReq fetchReq = new TFetchResultsReq(stmtHandle, TFetchOrientation.FETCH_FIRST, 1000000);
        TFetchResultsResp resultsResp = client.FetchResults(fetchReq);
        List<TColumn> res = resultsResp.getResults().getColumns();
        for (TColumn tCol : res) {
            StringBuilder stringBuilder = new StringBuilder(4096);
            if (tCol.getSetField() == TColumn._Fields.STRING_VAL) {
//                List<String> colValueList = tCol.getStringVal().getValues();
//                for(String colValue : colValueList){
//                    stringBuilder.append(colValue).append("!!");
//                }
                Iterator<String> it = tCol.getStringVal().getValuesIterator();
                while (it.hasNext()) {
                    stringBuilder.append(it.next() + splitChar);
                }
            } else if (tCol.getSetField() == TColumn._Fields.I32_VAL) {
                Iterator<Integer> it = tCol.getI32Val().getValuesIterator();
                while (it.hasNext()) {
                    stringBuilder.append(it.next() + splitChar);
                }
            } else if (tCol.getSetField() == TColumn._Fields.I64_VAL) {
                Iterator<Long> it = tCol.getI64Val().getValuesIterator();
                while (it.hasNext()) {
                    stringBuilder.append(it.next() + splitChar);
                }
            } else if (tCol.getSetField() == TColumn._Fields.BOOL_VAL) {
                Iterator<Boolean> it = tCol.getBoolVal().getValuesIterator();
                while (it.hasNext()) {
                    stringBuilder.append(it.next() + splitChar);
                }
            }
            result.add(stringBuilder.toString());
        }
        TCloseOperationReq closeReq = new TCloseOperationReq();
        closeReq.setOperationHandle(stmtHandle);
        client.CloseOperation(closeReq);
        TCloseSessionReq closeConnectionReq = new TCloseSessionReq(sessHandle);
        client.CloseSession(closeConnectionReq);
        transport.close();
        return result;
    }

    public List<String> queryByRow(String hql) throws SaslException, TException {
//        String sql = " select count(url) from nginx_log where url like '%/decoration/index.html%' or  url like '/?%' or  url like '?%'";
        List<String> result = Lists.newArrayList();
        TTransport transport = HiveAuthFactory.getSocketTransport("192.168.45.85", 10000, 99999);
        transport = PlainSaslHelper.getPlainTransport("hive", "hive", transport);
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        TCLIService.Client client = new TCLIService.Client(protocol);
        transport.open();
        TOpenSessionReq openReq = new TOpenSessionReq();
        TOpenSessionResp openResp = client.OpenSession(openReq);
        TSessionHandle sessHandle = openResp.getSessionHandle();
        TExecuteStatementReq execReq = new TExecuteStatementReq(sessHandle, hql);
        TExecuteStatementResp execResp = client.ExecuteStatement(execReq);
        TOperationHandle stmtHandle = execResp.getOperationHandle();
        TFetchResultsReq fetchReq = new TFetchResultsReq(stmtHandle, TFetchOrientation.FETCH_FIRST, 1000000);
        TFetchResultsResp resultsResp = client.FetchResults(fetchReq);
        List<TRow> res = resultsResp.getResults().getRows();
        for(TRow tRow : res) {
            StringBuilder stringBuilder = new StringBuilder(4096);
            List<TColumnValue> colValueList = tRow.getColVals();
            for(TColumnValue colVal: colValueList){
                if(colVal.getSetField() == TColumnValue._Fields.STRING_VAL) {
                    stringBuilder.append(colVal.getStringVal()).append("|");
                } else if (colVal.getSetField() == TColumnValue._Fields.I32_VAL) {
                    stringBuilder.append(colVal.getI32Val()).append("|");
                } else if (colVal.getSetField() == TColumnValue._Fields.I64_VAL) {
                    stringBuilder.append(colVal.getI64Val()).append("|");
                } else if (colVal.getSetField() == TColumnValue._Fields.BOOL_VAL) {
                    stringBuilder.append(colVal.getBoolVal()).append("|");
                }
            }

            result.add(stringBuilder.toString());
        }
        TCloseOperationReq closeReq = new TCloseOperationReq();
        closeReq.setOperationHandle(stmtHandle);
        client.CloseOperation(closeReq);
        TCloseSessionReq closeConnectionReq = new TCloseSessionReq(sessHandle);
        client.CloseSession(closeConnectionReq);
        transport.close();
        return result;
    }
    public static void main(String[] args)
    {
//        HiveClient hiveClient = new HiveClientThriftImpl();
//        StringBuilder querySB = new StringBuilder("");
//        querySB.append("select url, count(*) as pv, count(distinct(ip)) as uv from ")
//                .append("javaweb_log").append(" ")
//                .append("where time like '").append("2016-11-13").append("%' ")
//                .append("and url like '").append("/ask/question/%/answers.html").append("' ")
//                .append("group by url order by pv desc");
//        logger.info(querySB.toString());
//        try{
//            List<String> listResult = hiveClient.query(querySB.toString());
//            for(String result : listResult)
//                System.out.println(result);
//        }catch (Exception e){
//
//        }

    }
}


