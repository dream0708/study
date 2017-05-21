package com.sohu.bp.elite.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*;


/**
 * 
 * @author zhijungou
 * 2016年8月9日
 */

@Entity
@Table(name = "elite_subject")
public class EliteSubject extends AbstractEntity{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777330798135540077L;
	private Long id;
	private Long userId;
	private String name;
	private String brief;
	private String cover;
	private String detail;
	private Long areaCode;
	private Date createTime;
	private Date updateTime;
	private Long createHost;
	private Long updateHost;
	private Integer createPort;
	private Integer updatePort;
	private Integer status;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "cover", nullable = true)
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Column(name = "title", nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "brief", nullable = false)
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	@Column(name = "area_code", nullable = false)
	public Long getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(Long areaCode) {
		this.areaCode = areaCode;
	}
	@Column(name = "create_time", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "update_time", nullable = false)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "create_host", nullable = false)
	public Long getCreateHost() {
		return createHost;
	}
	public void setCreateHost(Long createHost) {
		this.createHost = createHost;
	}
	@Column(name = "update_host", nullable = false)
	public Long getUpdateHost() {
		return updateHost;
	}
	public void setUpdateHost(Long updateHost) {
		this.updateHost = updateHost;
	}
	@Column(name = "create_port", nullable = false)
	public Integer getCreatePort() {
		return createPort;
	}
	public void setCreatePort(Integer createPort) {
		this.createPort = createPort;
	}
	@Column(name = "update_port", nullable = false)
	public Integer getUpdatePort() {
		return updatePort;
	}
	public void setUpdatePort(Integer updatePort) {
		this.updatePort = updatePort;
	}
	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "detail", nullable = false)
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	@Transient
	public Serializable getInternalId()
	{
		return id;
	}
	

}
