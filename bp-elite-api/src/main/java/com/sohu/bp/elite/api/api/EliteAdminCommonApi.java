package com.sohu.bp.elite.api.api;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.elite.api.api.bean.AdminZombieBean;
import com.sohu.bp.elite.api.service.EliteAdminService;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 专门用于为admin开的通用api
 * @author zhijungou
 * 2017年5月12日
 */
@Controller
@RequestMapping("innerapi/admin")
public class EliteAdminCommonApi {
    private static final Logger log = LoggerFactory.getLogger(EliteAdminCommonApi.class);

    @Resource
    private EliteAdminService adminService;
    
    @RequestMapping(value = "zombie/insert", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String zombieTaskInsert(AdminZombieBean bean) {
    	boolean result = adminService.zombieInsert(bean);
    	if (result) {
    		return ResponseJSON.getSucJSON().toString();
    	}
    	return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    @RequestMapping(value = "zombie/remove", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String zombieTaskRemove(AdminZombieBean bean) {
    	boolean result = adminService.zombieRemove(bean);
    	if (result) {
    		return ResponseJSON.getSucJSON().toString();
    	}
    	return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    @RequestMapping(value = "zombie/tasks", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String zombieTasks(@RequestParam("start") int start, @RequestParam("count") int count) {
        List<AdminZombieBean> list = adminService.zombieGetList(start, count);
        JSONObject resJSON = ResponseJSON.getSucJSON();
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("list", list);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }
    
    @RequestMapping(value = "zombie/tasks-flush", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String zombieTasksFlush() {
        adminService.zombieFlush();
        return ResponseJSON.getSucJSON().toString();
    }
    
    
}
