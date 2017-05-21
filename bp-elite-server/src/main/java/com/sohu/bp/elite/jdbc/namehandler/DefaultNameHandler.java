package com.sohu.bp.elite.jdbc.namehandler;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sohu.bp.elite.jdbc.AutoField;
import com.sohu.bp.elite.jdbc.NameHandler;
import com.sohu.bp.elite.util.NameUtils;

/**
 * 默认名称处理handler
 * 
 */
public class DefaultNameHandler implements NameHandler {

    /** 主键后缀 */
    private static final String PRI_SUFFIX = "_ID";

    public String getTableName(Class<?> entityClass, Map<String, AutoField> fieldMap) {
        //Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
        return NameUtils.getUnderlineName(entityClass.getSimpleName());
    }

    public String getPKName(Class<?> entityClass) {
        //String underlineName = NameUtils.getUnderlineName(entityClass.getSimpleName());
        //主键以表名加上“_id” 如user表主键即“user_id”
        //return underlineName + PRI_SUFFIX;
        return "id";
    }

    public String getColumnName(String fieldName) {
        String underlineName = NameUtils.getUnderlineName(fieldName);
        return underlineName;
    }

    public String getPKValue(Class<?> entityClass, String dialect) {
        if (StringUtils.equalsIgnoreCase(dialect, "oracle")) {
            //获取序列就可以了，默认seq_加上表名为序列名
            String tableName = this.getTableName(entityClass, null);
            return String.format("SEQ_%s.NEXTVAL", tableName);
        }
        return null;
    }
}
