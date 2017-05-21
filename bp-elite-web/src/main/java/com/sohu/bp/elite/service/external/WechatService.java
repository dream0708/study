package com.sohu.bp.elite.service.external;

import com.sohu.bp.elite.model.TEliteQuestion;
import net.sf.json.JSONObject;

import javax.annotation.Resource;

/**
 * Created by nicholastang on 2017/2/24.
 */
public interface WechatService {
    /**
     *
     * @param bpId
     * @param templateId
     * @param url
     * @param data 参照模板具体格式。如templateId=5对应的data格式为{
     *             first : "",
     *             keyword1: "",
     *             keyword2: "",
     *             keyword3: "",
     *             keyword4: "",
     *             remarkData: ""
     * }
     * @return
     */
    public boolean sendTemplateMsg(long bpId, int templateId, String url, JSONObject data);

    /**
     *
     * @param bpId 接收人的bpid
     * @param question 邀请回答的问题
     * @param tags 显示覆盖的领域 如果给null或"", 将采用问题中的标签
     * @return
     */
    public boolean sendAnswerInviteMsg(long bpId, TEliteQuestion question, String tags);
}
