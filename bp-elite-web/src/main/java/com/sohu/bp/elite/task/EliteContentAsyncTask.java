package com.sohu.bp.elite.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.util.SpringUtil;

/**
 * 
 * @author nicholastang
 * 2016-10-14 20:00:00
 * TODO
 */
public class EliteContentAsyncTask extends EliteAsyncTask
{
	private static final Logger logger = LoggerFactory.getLogger(EliteContentAsyncTask.class);
	private static WasherService washerService;
	
	private Long materialId;
	private BpType materialType;
    private String ip;
	private List<EliteAsyncTaskOper> operList;

	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	public BpType getMaterialType() {
		return materialType;
	}

	public void setMaterialType(BpType materialType) {
		this.materialType = materialType;
	}

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<EliteAsyncTaskOper> getOperList() {
		return operList;
	}

	public void setOperList(List<EliteAsyncTaskOper> operList) {
		this.operList = operList;
	}

	public EliteContentAsyncTask(List<EliteAsyncTaskOper> operList, Long materialId, BpType materialType, String ip) {
		this.operList = operList;
		this.materialId = materialId;
		this.materialType = materialType;
        this.ip = ip;
	}
	
	@Override
	public void run() {
		if (null != operList && operList.size() > 0) {
			for (EliteAsyncTaskOper oper : operList) {
				switch (oper) {
					case RESAVECONTENT:
						logger.info("resave content for answer or question.materialId="+materialId+";materialType="+materialType.getDesc());
						if (null == washerService) {
							washerService = (WasherService) SpringUtil.getBean("washerService");
						}
						washerService.resaveContent(materialId, materialType, ip);
						break;
				}
					
			}
		}
	}
	
	@Override
	public Boolean call() {
        boolean result = true;
        if (null != operList && operList.size() > 0) {
            for (EliteAsyncTaskOper oper : operList) {
                switch (oper) {
                case RESAVECONTENT:
                    logger.info("resave content for answer or question.materialId=" + materialId + ";materialType="
                            + materialType.getDesc());
                    if (null == washerService) {
                        washerService = (WasherService) SpringUtil.getBean("washerService");
                    }
                    if (!washerService.resaveContent(materialId, materialType, ip)) {
                        result = false;
                    };
                    break;
                }

            }
        }
        return result;
	}
}