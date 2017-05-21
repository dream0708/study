package com.sohu.bp.elite.api.util;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/18
 */
public class PropertyFilterUtil {

    private static List<String> ignoredProperties = Arrays.asList("createHost", "createPort", "updateHost", "updatePort", "counts");

    public static SimplePropertyPreFilter getFilter(Class<?> type){
        String[] properties = new String[type.getFields().length];
        int i = 0;
        for(Field field : type.getFields()){
            if(!ignoredProperties.contains(field.getName()))
                properties[i++] = field.getName();
        }
        return new SimplePropertyPreFilter(type, properties);
    }
}
