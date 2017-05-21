package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteQuestionBySpecialDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteQuestionByspecial;

public class EliteQuestionBySpecialDaoImpl extends JdbcDaoImpl implements EliteQuestionBySpecialDao{
	
	private static final Logger log = LoggerFactory.getLogger(EliteQuestionBybpIdDaoImpl.class);
	private TablePartitioner partitioner;
	
	public void setPartitioner(TablePartitioner partitioner) {
		this.partitioner = partitioner;
	}
	
	public String getTableName(Long specialId){
		return DbPartitionHelper.getTableName(partitioner, specialId);
	}
	
	@Override
	public Long save(EliteQuestionByspecial eliteQuestionByspecial) {
		Long id = -1l;
		if(null == eliteQuestionByspecial || eliteQuestionByspecial.getQuestionId() == null || eliteQuestionByspecial.getSpecialId() == null ||
				eliteQuestionByspecial.getSpecialType() == null){
			log.error("insert question into elite_question_byspecial failed!");
			return id;
		}
		if(eliteQuestionByspecial.getSpecialId() == 0 && eliteQuestionByspecial.getSpecialType() == 0) return id;
		id = super.insert(eliteQuestionByspecial, getTableName(eliteQuestionByspecial.getSpecialId()));
		log.info("insert question into elite_question_byspecial succeed! id = " + id);
		return id;
	}

	@Override
	public boolean update(EliteQuestionByspecial eliteQuestionByspecial) {
		if(null == eliteQuestionByspecial || eliteQuestionByspecial.getQuestionId() == null || eliteQuestionByspecial.getSpecialId() == null ||
				eliteQuestionByspecial.getSpecialType() == null){
			log.error("update question into elite_question_byspecial failed!");
			return false;
		}
		int i = super.update(eliteQuestionByspecial, getTableName(eliteQuestionByspecial.getSpecialId()));
		if(i > 0){
			log.info("update question into elite_question_byspecial succeed!");
			return true;
		}else{
			log.error("update question into elite_question_byspecial succeed!");
			return true;
		}

	}

	@Override
	public List<Long> getQuestionsBySpecial(Long specialId, Integer specialType) {
		if(null == specialId || null == specialType) return null;
		List<Long> questionIds = new ArrayList<>();
		Criteria criteria = Criteria.create(EliteQuestionByspecial.class).where("specialType", new Object[]{specialType}).and("specialId", new Object[]{specialId});
		List<EliteQuestionByspecial> questions = super.queryList(criteria, getTableName(specialId));
		if(questions != null){
			questions.forEach(question -> questionIds.add(question.getQuestionId()));
			log.info("get questions form elite_question_byspecial succeed! questions = " + questionIds.toString());
		}
		return questionIds;
	}

	@Override
	public EliteQuestionByspecial get(Long specialId, Long questionId) {
		if(null == specialId || null == questionId) return null;
		Criteria criteria = Criteria.create(EliteQuestionByspecial.class).where("specialId", new Object[]{specialId}).and("questionId", new Object[]{questionId});
		EliteQuestionByspecial eliteQuestionByspecial = super.querySingleResult(criteria, getTableName(specialId));
		return eliteQuestionByspecial;
	}

}
