package com.sohu.bp.elite.hive;

import org.apache.thrift.TException;

import javax.security.sasl.SaslException;
import java.util.List;

/**
 * Created by guohaozhao on 2016/10/31.
 */
public interface HiveClient {

    public List<String> query(String hql) throws SaslException, TException;

    public List<String> queryByRow(String hql) throws SaslException, TException;
}
