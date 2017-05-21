package com.sohu.bp.elite.service.impl;

import com.sohu.bp.elite.dao.EliteAdminDao;
import com.sohu.bp.elite.enums.EliteAdminStatus;
import com.sohu.bp.elite.persistence.EliteAdmin;
import com.sohu.bp.elite.service.EliteAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicholastang on 2017/3/15.
 */
public class EliteAdminServiceImpl implements EliteAdminService {
    private static final Logger logger = LoggerFactory.getLogger(EliteAdminServiceImpl.class);

    private EliteAdminDao eliteAdminDao;

    public EliteAdminDao getEliteAdminDao() {
        return eliteAdminDao;
    }

    public void setEliteAdminDao(EliteAdminDao eliteAdminDao) {
        this.eliteAdminDao = eliteAdminDao;
    }

    @Override
    public boolean save(EliteAdmin eliteAdmin) {
        if (null == eliteAdmin || String.valueOf(eliteAdmin.getId()) == null || eliteAdmin.getId() <= 0
                || eliteAdmin.getStatus() == null) {
            return false;
        }
        if (this.get(eliteAdmin.getId()) != null) {
            return eliteAdminDao.update(eliteAdmin);
        }
        return eliteAdminDao.save(eliteAdmin);
    }

    @Override
    public EliteAdmin get(long bpId) {
        if (bpId <= 0) {
            return null;
        }
        return eliteAdminDao.get(bpId);
    }

    @Override
    public Integer getAdminStatus(long bpId) {
        if (bpId <=0 ) {
            return EliteAdminStatus.NONE.getValue();
        }
        return eliteAdminDao.getAdminStatus(bpId);
    }

    @Override
    public boolean superAdmin(long bpId) {
        if (bpId <= 0) {
            return false;
        }
        return eliteAdminDao.superAdmin(bpId);
    }

	@Override
	public boolean update(EliteAdmin eliteAdmin) {
		if (eliteAdmin == null) {
			return false;
		}
		return eliteAdminDao.update(eliteAdmin);
	}
}
