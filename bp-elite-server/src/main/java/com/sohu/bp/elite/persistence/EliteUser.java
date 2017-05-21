package com.sohu.bp.elite.persistence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "elite_user")
public class EliteUser extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer gender;
	private Date birthday;
	private String description;
	private Integer grade;
	private String tagIds;
	private Integer status;
	private Date firstLoginTime;
	private Date lastLoginTime;
	private Integer gold;
	private Integer firstLogin;
	private Integer identity;
	private Long areaCode;
	
	@Column(name = "first_login_time")
	public Date getFirstLoginTime() {
		return firstLoginTime;
	}

	public EliteUser setFirstLoginTime(Date firstLoginTime) {
		this.firstLoginTime = firstLoginTime;
		return this;
	}
	@Column(name = "last_login_time")
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public EliteUser setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
		return this;
	}
	@Column(name = "gold")
	public Integer getGold() {
		return gold;
	}

	public EliteUser setGold(Integer gold) {
		this.gold = gold;
		return this;
	}
	@Column(name = "first_login")
	public Integer getFirstLogin() {
		return firstLogin;
	}

	public EliteUser setFirstLogin(Integer firstLogin) {
		this.firstLogin = firstLogin;
		return this;
	}
	@Column(name = "identity")
	public Integer getIdentity() {
		return identity;
	}

	public EliteUser setIdentity(Integer identity) {
		this.identity = identity;
		return this;
	}
	@Id
	@Column(name = "id")
	public Long getId() {
		return this.id;
	}

	public EliteUser setId(Long id) {
		this.id = id;
		return this;
	}
	@Column(name = "gender")
	public Integer getGender() {
		return this.gender;
	}

	public EliteUser setGender(Integer gender) {
		this.gender = gender;
		return this;
	}
	@Column(name = "birthday")
	public Date getBirthday() {
		return this.birthday;
	}

	public EliteUser setBirthday(Date birthday) {
		this.birthday = birthday;
		return this;
	}
	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public EliteUser setDescription(String description) {
		this.description = description;
		return this;
	}
	@Column(name = "grade")
	public Integer getGrade() {
		return this.grade;
	}

	public EliteUser setGrade(Integer grade) {
		this.grade = grade;
		return this;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public EliteUser setStatus(Integer status) {
		this.status = status;
		return this;
	}
	@Column(name = "tag_ids")
	public String getTagIds() {
		return tagIds;
	}

	public EliteUser setTagIds(String tagIds) {
		this.tagIds = tagIds;
		return this;
	}
	
    public Long getAreaCode() {
        return areaCode;
    }

    public EliteUser setAreaCode(Long areaCode) {
        this.areaCode = areaCode;
        return this;
    }

	@Transient
	public Serializable getInternalId() {
		return id;
	}
}