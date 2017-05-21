package com.sohu.bp.elite.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "elite_question_bysepcial")
public class EliteQuestionByspecial extends AbstractEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long questionId;
	private Long specialId;
	private Integer specialType;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}


	public EliteQuestionByspecial setId(Long id) {
		this.id = id;
		return this;
	}

	@Column(name = "question_id")
	public Long getQuestionId() {
		return questionId;
	}


	public EliteQuestionByspecial setQuestionId(Long questionId) {
		this.questionId = questionId;
		return this;
	}

	@Column(name = "special_id")
	public Long getSpecialId() {
		return specialId;
	}


	public EliteQuestionByspecial setSpecialId(Long specialId) {
		this.specialId = specialId;
		return this;
	}

	@Column(name = "special_type")
	public Integer getSpecialType() {
		return specialType;
	}
	
	public EliteQuestionByspecial setSpecialType(Integer specialType) {
		this.specialType = specialType;
		return this;
	}
	
	@Override
	@Transient
	public Serializable getInternalId() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
