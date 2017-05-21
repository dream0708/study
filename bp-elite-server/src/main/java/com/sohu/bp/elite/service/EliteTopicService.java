package com.sohu.bp.elite.service;


import java.util.List;

import com.sohu.bp.elite.persistence.EliteTopic;

public interface EliteTopicService {
	
	public Boolean setEliteTopicHistory(EliteTopic eliteTopic);
	
	public EliteTopic getEliteTopicById(Long id);
	public Long getEliteTopicCount();
	public Long getEliteTopicCountByStatus(Integer status);
	public List<EliteTopic> getAllEliteTopic(Integer start, Integer count);
	public List<EliteTopic> getAllEliteTopicByStatus(Integer status, Integer start, Integer count);
	
}
