package com.sohu.bp.elite.action;

import com.sohu.bp.util.ResponseJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nicholastang on 2016/11/18.
 */
@Controller
@RequestMapping("uc")
public class UserCaptureAction {
    private static final Logger logger = LoggerFactory.getLogger(UserCaptureAction.class);
    @ResponseBody
    @RequestMapping(value = {"ht"}, produces = "application/json;charset=utf-8")
    public String holdTime(int st, HttpServletRequest request, HttpServletResponse response){
        logger.info(request.getHeader("referer"));
        logger.info("" + st);
        return ResponseJSON.getSucJSON().toString();
    }
}
