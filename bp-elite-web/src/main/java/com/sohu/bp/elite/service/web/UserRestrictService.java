package com.sohu.bp.elite.service.web;

public interface UserRestrictService {

	public boolean addQuestionNum(Long bpId);
	public boolean addAnswerNum(Long bpId);
	public boolean isQuestionRestrict(Long bpId);
	public boolean isAnswerRestrict(Long bpId);
	
}
