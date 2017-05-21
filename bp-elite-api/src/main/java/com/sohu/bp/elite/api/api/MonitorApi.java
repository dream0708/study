package com.sohu.bp.elite.api.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.util.ResponseJSON;

@Controller
@RequestMapping("m")
public class MonitorApi {
	
	@RequestMapping(value="", produces="application/json;charset=utf-8")
	@ResponseBody
	public String eliteApiMonitor(){
		return ResponseJSON.getDefaultResJSON().toString();
	}
}
