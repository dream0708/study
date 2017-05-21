package com.sohu.bp.elite.beetl;

import java.io.StringWriter;
import java.io.Writer;

import org.beetl.core.ConsoleErrorHandler;
import org.beetl.core.exception.BeetlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author: yuguangyuan
 * @date 2015年11月22日 下午5:49:47
 */
public class LogErrorHandler extends ConsoleErrorHandler{

	private static Logger log = LoggerFactory.getLogger(LogErrorHandler.class);

	@Override
	public void processExcption(BeetlException ex, Writer writer) {
		StringWriter logWriter = new StringWriter();
		super.processExcption(ex, logWriter);
		log.error(logWriter.toString());
	}
		
}
