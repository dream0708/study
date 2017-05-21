package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "elite_question")
public class EliteQuestion extends AbstractEntity {

	private static final long serialVersionUID = 8802847939918552109L;
	private Long id;
	private Long bpId;
	private String title;
	private String detail;
	private String tagIds;
	private Date createTime;
	private Date updateTime;
	private Date publishTime;
	private Long createHost;
	private Integer createPort;
	private Long updateHost;
	private Integer updatePort;
	private Integer relationType;
	private Long relationId;
	private Integer source;
	private String sourceUrl;
	private Integer status;
	private Integer version;
	private Long areaCode;
	private Long specialId;
	private	Integer specialType;
	private String options;
	private Map<Integer, Integer> counts;

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

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "detail")
	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Column(name = "tag_ids")
	public String getTagIds() {
		return this.tagIds;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
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

	@Column(name = "source")
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	@Column(name = "relation_type")
	public Integer getRelationType() {
		return relationType;
	}

	public void setRelationType(Integer relationType) {
		this.relationType = relationType;
	}

	@Column(name = "relation_id")
	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "source_url")
	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	@Column(name = "area_code")
	public Long getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Long areaCode) {
		this.areaCode = areaCode;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}
	@Column(name = "special_type")
	public Integer getSpecialType() {
		return specialType;
	}

	public void setSpecialType(Integer specialType) {
		this.specialType = specialType;
	}
	@Column(name = "special_id")
	public Long getSpecialId() {
		return specialId;
	}

	public void setSpecialId(Long specialId) {
		this.specialId = specialId;
	}

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
    @Transient
    public Map<Integer, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<Integer, Integer> counts) {
        this.counts = counts;
    }
}