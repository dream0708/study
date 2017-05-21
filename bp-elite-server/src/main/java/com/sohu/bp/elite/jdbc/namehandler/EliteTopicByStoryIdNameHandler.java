package com.sohu.bp.elite.jdbc.namehandler;

import com.sohu.bp.elite.jdbc.AutoField;

import java.util.Map;

/**
 * Created by linezhao on 2016/5/6 15:40.
 */
public class EliteTopicByStoryIdNameHandler extends DefaultNameHandler{
    @Override
    public String getTableName(Class<?> entityClass, Map<String, AutoField> fieldMap) {
        return "elite_topic_bystoryid";
    }
}
