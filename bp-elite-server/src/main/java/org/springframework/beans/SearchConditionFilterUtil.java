package org.springframework.beans;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


public class SearchConditionFilterUtil {

    public static Map<String, String> getParamsFromCondition(Object source){
        Map<String, String> params = new HashMap<>();
        if(source == null)
            return params;
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source.getClass());
        for(PropertyDescriptor propertyDescriptor : propertyDescriptors){
            if(propertyDescriptor != null){
                Method readMethod = propertyDescriptor.getReadMethod();
                if(readMethod != null){
                    if(!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()))
                        readMethod.setAccessible(true);
                    Object value = null;
                    try {
                        value = readMethod.invoke(source);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if(value != null){
                        if(value instanceof String){
                            if(StringUtils.isNotBlank((String) value)){
                                params.put(propertyDescriptor.getName(), String.valueOf(value));
                            }
                        }else {
                            if(value.getClass().isAssignableFrom(Integer.class)){
                                if((int)value != -1){
                                    params.put(propertyDescriptor.getName(), String.valueOf(value));
                                }
                            }else if(value.getClass().isAssignableFrom(Long.class)){
                                if((long)value != -1L){
                                    params.put(propertyDescriptor.getName(), String.valueOf(value));
                                }
                            }
                        }
                    }
                }
            }
        }
        return params;
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
        CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getPropertyDescriptors();
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName)
            throws BeansException {
        CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getPropertyDescriptor(propertyName);
    }
}
