package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "elite_answer")
public class EliteAnswer extends AbstractEntity {

	private static final long serialVersionUID = 36722764671553019L;
	private Long id;
	private Long bpId;
	private Long questionId;
	private String content;
	private Integer source;
	private String sourceUrl;
	private Date createTime;
	private Date updateTime;
	private Date publishTime;
	private Long createHost;
	private Integer createPort;
	private Long updateHost;
	private Integer updatePort;
	private Long areaCode;
	private Integer status;
	private Long specialId;
	private Integer specialType;


	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "bp_id", nullable = false)
	public Long getBpId() {
		return this.bpId;
	}

	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}

	@Column(name = "question_id", nullable = false)
	public Long getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	@Column(name = "content", nullable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "source")
	public Integer getSource() {
		return this.source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "publish_time")
	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	@Column(name = "create_host")
	public Long getCreateHost() {
		return this.createHost;
	}

	public void setCreateHost(Long createHost) {
		this.createHost = createHost;
	}

	@Column(name = "create_port")
	public Integer getCreatePort() {
		return this.createPort;
	}

	public void setCreatePort(Integer createPort) {
		this.createPort = createPort;
	}

	@Column(name = "update_host")
	public Long getUpdateHost() {
		return this.updateHost;
	}

	public void setUpdateHost(Long updateHost) {
		this.updateHost = updateHost;
	}

	@Column(name = "update_port")
	public Integer getUpdatePort() {
		return this.updatePort;
	}

	public void setUpdatePort(Integer updatePort) {
		this.updatePort = updatePort;
	}

	@Column(name = "area_code")
	public Long getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Long areaCode) {
		this.areaCode = areaCode;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "source_url")
	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}

    public Long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Long specialId) {
        this.specialId = specialId;
    }

    public Integer getSpecialType() {
        return specialType;
    }

    public void setSpecialType(Integer specialType) {
        this.specialType = specialType;
    }
	
}