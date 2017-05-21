package com.sohu.bp.elite.adapter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangzhihao
 *         2016/7/16
 */
public class EliteServiceAdapterFactory {
    private EliteServiceAdapterFactory(){
    }

    private static class EliteServiceAdapterHolder{
        private static final ApplicationContext ctx = new ClassPathXmlApplicationContext("bpEliteAdapter/applicationContext.xml");
    }

    public static EliteThriftServiceAdapter getEliteThriftServiceAdapter(){
        return EliteServiceAdapterHolder.ctx.getBean("eliteThriftServiceAdapter", EliteThriftServiceAdapter.class);
    }

}
