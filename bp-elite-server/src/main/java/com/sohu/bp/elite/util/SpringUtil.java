package com.sohu.bp.elite.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/**
 * spring 工具类，通过它普通JAVA类能直接获取Spring的bean.
 *
 */
public class SpringUtil implements  ApplicationContextAware{
    private static ApplicationContext ctx;


    public static void main(String[] args) {

    }

    public void displayAppContext() {
        System.out.println(ctx);
    }

    public static Object getBean(String name){
        if(ctx != null)
            return ctx.getBean(name);
        else
            return null;
    }

    public static <T> T getBean(String name, Class<T> requiredType){
        if(ctx != null)
            return ctx.getBean(name, requiredType);
        else
            return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx=applicationContext;
    }

    public static ApplicationContext getCtx() {
        return ctx;
    }

}
