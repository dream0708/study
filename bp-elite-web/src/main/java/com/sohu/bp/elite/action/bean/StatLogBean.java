package com.sohu.bp.elite.action.bean;

import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-07-28 14:28:50
 * 统计的日志格式 详见http://code.sohuno.com/bp/bp-decoration-docs/blob/master/backend/api/stat_log.md
 */
public class StatLogBean
{
	private Long timestamp;
	private String ip;
	private String location_hash;
	private Integer source_agent;
	private String domain;
	private String url;
	private Integer device_type;
	private Integer os;
	private String os_version;
	private String device_brand;
	private String device_model;
	private Long area_code;
	private Integer request_status;
	private String browser;
	private String referer_url;
	private Integer action_type;
	private Integer action_obj_id;
	private Integer action_obj_type;
	private String cookie;
	private Double request_time;
	private Float version;
	private String user_suv;
	private String user_yyid;
	private String user_bpid;
	private List<RecomItemBean> recom_id_list;
	private Double dwell_time;
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getLocation_hash() {
		return location_hash;
	}
	public void setLocation_hash(String location_hash) {
		this.location_hash = location_hash;
	}
	public Integer getSource_agent() {
		return source_agent;
	}
	public void setSource_agent(Integer source_agent) {
		this.source_agent = source_agent;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getDevice_type() {
		return device_type;
	}
	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}
	public Integer getOs() {
		return os;
	}
	public void setOs(Integer os) {
		this.os = os;
	}
	public String getOs_version() {
		return os_version;
	}
	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}
	public String getDevice_brand() {
		return device_brand;
	}
	public void setDevice_brand(String device_brand) {
		this.device_brand = device_brand;
	}
	public String getDevice_model() {
		return device_model;
	}
	public void setDevice_model(String device_model) {
		this.device_model = device_model;
	}
	public Long getArea_code() {
		return area_code;
	}
	public void setArea_code(Long area_code) {
		this.area_code = area_code;
	}
	public Integer getRequest_status() {
		return request_status;
	}
	public void setRequest_status(Integer request_status) {
		this.request_status = request_status;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public String getReferer_url() {
		return referer_url;
	}
	public void setReferer_url(String referer_url) {
		this.referer_url = referer_url;
	}
	public Integer getAction_type() {
		return action_type;
	}
	public void setAction_type(Integer action_type) {
		this.action_type = action_type;
	}
	public Integer getAction_obj_id() {
		return action_obj_id;
	}
	public void setAction_obj_id(Integer action_obj_id) {
		this.action_obj_id = action_obj_id;
	}
	public Integer getAction_obj_type() {
		return action_obj_type;
	}
	public void setAction_obj_type(Integer action_obj_type) {
		this.action_obj_type = action_obj_type;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public Double getRequest_time() {
		return request_time;
	}
	public void setRequest_time(Double request_time) {
		this.request_time = request_time;
	}
	public Float getVersion() {
		return version;
	}
	public void setVersion(Float version) {
		this.version = version;
	}
	public String getUser_suv() {
		return user_suv;
	}
	public void setUser_suv(String user_suv) {
		this.user_suv = user_suv;
	}
	public String getUser_yyid() {
		return user_yyid;
	}
	public void setUser_yyid(String user_yyid) {
		this.user_yyid = user_yyid;
	}
	public String getUser_bpid() {
		return user_bpid;
	}
	public void setUser_bpid(String user_bpid) {
		this.user_bpid = user_bpid;
	}
	public List<RecomItemBean> getRecom_id_list() {
		return recom_id_list;
	}
	public void setRecom_id_list(List<RecomItemBean> recom_id_list) {
		this.recom_id_list = recom_id_list;
	}
	public Double getDwell_time() {
		return dwell_time;
	}
	public void setDwell_time(Double dwell_time) {
		this.dwell_time = dwell_time;
	}
	
	
}