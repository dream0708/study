package com.sohu.bp.elite.api.api;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.api.service.EliteDataService;
import com.sohu.bp.elite.api.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 用于admin的问答页面数据维护
 * @author zhijungou
 * 2017年3月31日
 */
@Controller
@RequestMapping("innerapi/data")
public class EliteDataApi {
    private static final Logger log = LoggerFactory.getLogger(EliteDataApi.class);
    
    @Resource
    private EliteDataService dataService;
    
    @RequestMapping(value = "getAppFocus", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getAppFocus() {
        JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
        JSONObject dataJSON = dataService.getAppFocus();
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    @RequestMapping(value = "saveAppFocus", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String saveAppFocus(String pic, String title, String url) {
        if (StringUtils.isBlank(pic) || StringUtils.isBlank(title) || StringUtils.isBlank(url)) return ResponseJSON.getErrorParametersErrorJSON().toString();
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("pic", pic);
        dataJSON.put("title", title);
        dataJSON.put("url", url);
        dataService.saveAppFoucs(dataJSON);
        return ResponseJSON.getDefaultResponseJSON().toString();
    }
}
