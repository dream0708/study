package com.sohu.bp.elite.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.util.ResponseJSON;

/**
 * 用于监控问答项目
 * @author zhijungou
 * 2016年12月7日
 */
@Controller
//@RequestMapping("m")
public class MonitorAction {
	private static final Logger log = LoggerFactory.getLogger(MonitorAction.class);
	
	@RequestMapping(value = "m", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String eliteWebMonitoring(){
		return ResponseJSON.getDefaultResJSON().toString();
	}
}
