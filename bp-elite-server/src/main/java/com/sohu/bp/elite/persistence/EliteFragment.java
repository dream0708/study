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
 * 2016/8/17
 */

@Entity
@Table(name = "elite_fragment")
public class EliteFragment extends AbstractEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5282896640270622796L;
	Long id;
	Long userId;
	Integer position;
	Integer type;
	String name;
	String detail;
	Date createTime;
	Long createHost;
	Integer CreatePort; 
	Date updateTime;
	Long updateHost;
	Integer updatePort;
	Integer status;
	
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

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	
	@Column(name = "type", nullable =false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "name", nullable =false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "detail", nullable =false)
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	@Column(name = "create_time", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "create_host", nullable = true)
	public Long getCreateHost() {
		return createHost;
	}

	public void setCreateHost(Long createHost) {
		this.createHost = createHost;
	}
	
	@Column(name = "create_port", nullable = true)
	public Integer getCreatePort() {
		return CreatePort;
	}

	public void setCreatePort(Integer createPort) {
		CreatePort = createPort;
	}
	@Column(name = "update_time", nullable = true)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "update_host", nullable = true)
	public Long getUpdateHost() {
		return updateHost;
	}

	public void setUpdateHost(Long updateHost) {
		this.updateHost = updateHost;
	}
	@Column(name = "update_port", nullable = true)
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
	
	@Override
	@Transient
	public Serializable getInternalId() {
		return id;
	}
}
