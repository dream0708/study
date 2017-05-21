package com.sohu.bp.elite.api.service.impl;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.api.service.InteractionService;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-31 19:58:00
 * TODO
 */
public class InteractionServiceImpl implements InteractionService
{
	private BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static Logger logger = LoggerFactory.getLogger(InteractionServiceImpl.class);
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.api.service.InteractionService#like(java.lang.Long, java.lang.Long, com.sohu.bp.model.BpInteractionTargetType)
	 */
	@Override
	public boolean like(Long bpId, Long targetId, BpInteractionTargetType targetType) {
		boolean retVal = false;
		try
		{     
		    Random rand = new Random();
			BpInteractionDetail interaction = new BpInteractionDetail();
	        interaction.setBpid(bpId)
	                    .setType(BpInteractionType.LIKE)
	                    .setTargetId(targetId)
	                    .setTargetType(BpInteractionTargetType.ELITE_ANSWER)
	                    .setExtra(new JSONObject().toString())
	                    .setCreateTime(new Date().getTime())
	                    .setCreateHost(rand.nextInt(Integer.MAX_VALUE))
	                    .setCreatePort(rand.nextInt(1000));
	
	        CodeMsgData responseData = extendServiceAdapter.addBpInteraction(interaction);
	        if(ResponseConstants.OK == responseData.getCode()){
	        	logger.info("Like succeeded");
	            retVal = true;
	        }else {
	        	logger.info("Like error");
	        }
		}catch(Exception e)
		{
			logger.error("", e);
		}
        return retVal;
	}
	
}