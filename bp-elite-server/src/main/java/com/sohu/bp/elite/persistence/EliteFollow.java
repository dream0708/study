package com.sohu.bp.elite.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 
 * @author zhijungou
 * 2016年9月8日
 */
@Entity
@Table(name = "elite_follow")
public class EliteFollow implements Persistable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2546271823074545298L;
	
	Long id;
	Long userId;
	Long bpId;
	Integer type;
	Integer status;
	Date createTime;
	Long createHost;
	Integer createPort;
	Date updateTime;
	Long updateHost;
	Integer updatePort;
	
	@Transient
	@Override
	public Serializable getInternalId() {
		return id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "bp_id", nullable = false)
	public Long getBpId() {
		return bpId;
	}

	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}
	
	@Column(name = "type", nullable = false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name = "create_time", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "create_host", nullable = false)
	public Long getCreateHost() {
		return createHost;
	}

	public void setCreateHost(Long createHost) {
		this.createHost = createHost;
	}
	
	@Column(name = "create_port", nullable = false)
	public Integer getCreatePort() {
		return createPort;
	}

	public void setCreatePort(Integer createPort) {
		this.createPort = createPort;
	}
	
	@Column(name = "update_time", nullable = false)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Column(name = "update_host", nullable = false)
	public Long getUpdateHost() {
		return updateHost;
	}

	public void setUpdateHost(Long updateHost) {
		this.updateHost = updateHost;
	}
	@Column(name = "update_port", nullable = false)
	public Integer getUpdatePort() {
		return updatePort;
	}

	public void setUpdatePort(Integer updatePort) {
		this.updatePort = updatePort;
	}
	
	

}
