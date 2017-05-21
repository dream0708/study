package com.sohu.bp.elite.data.statistic.util;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.decoration.model.ExpertStatus;

import com.sohu.bp.elite.enums.BpUserType;

import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-08-26 11:37:19
 * TODO 关于bpuser的工具类
 */
public class BpUserUtil
{
	private static Logger log = LoggerFactory.getLogger(BpUserUtil.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static BpDecorationServiceAdapter bpDecorationServiceAdapter;
	private static BpServiceAdapter bpServiceAdapter;
	static{
		bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
		bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	}
	
	public static BpUserType getBpUserType(Long bpid)
	{
		BpUserType bpUserType = BpUserType.NONE;
		if(null == bpid || bpid.longValue() <= 0)
			return bpUserType;
		try {
            List<Expert> expertList = bpDecorationServiceAdapter.getExpertListById(bpid);
            boolean passed = false;
            for (Expert expert : expertList) {
                if (expert.getStatus().equals(ExpertStatus.PASS)) {
                	passed = true;
                	
                    if (expert.getRole().equals(ExpertRole.SELF_MEDIA)) {
                        if(expert.getManageCompanyId() > 0)
                        {
                        	bpUserType = BpUserType.COMPANY;
                        	break;
                        }
                        else
                        	bpUserType = BpUserType.SELF_MEDIA;
                    }
                    if (expert.getRole().equals(ExpertRole.FOREMAN)) {
                        bpUserType = BpUserType.FOREMAN;
                        break;
                    }
                    if (expert.getRole().equals(ExpertRole.DESIGNER)) {
                        bpUserType = BpUserType.DESIGNER;
                        break;
                    }
                }
            }
            
            if(bpUserType == BpUserType.NONE)
            	bpUserType = BpUserType.NORMAL_USER;
        } catch (Exception e) {
            log.error("", e);
        }
		
		return bpUserType;
	}
	

}