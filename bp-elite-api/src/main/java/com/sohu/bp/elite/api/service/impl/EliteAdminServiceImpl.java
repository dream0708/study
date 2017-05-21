package com.sohu.bp.elite.api.service.impl;

import java.util.List;

import com.sohu.bp.elite.api.api.bean.AdminZombieBean;
import com.sohu.bp.elite.api.dao.EliteAdminDao;
import com.sohu.bp.elite.api.service.EliteAdminService;

public class EliteAdminServiceImpl implements EliteAdminService {
    private EliteAdminDao adminDao;
    
    public void setAdminDao(EliteAdminDao adminDao) {
        this.adminDao = adminDao;
    }

	@Override
	public boolean zombieInsert(AdminZombieBean bean) {
		if (null == bean || bean.getItemId() <= 0) return false;
		return adminDao.zombieInsert(bean);
	}

	@Override
	public boolean zombieRemove(AdminZombieBean bean) {
		if (null == bean || bean.getItemId() <= 0) return false;
		return adminDao.zombieRemove(bean);
	}

	@Override
	public List<AdminZombieBean> zombieGetList(int start, int count) {
		return adminDao.zombieGetList(start, count);
	}

    @Override
    public void zombieFlush() {
        adminDao.zombieFlush();
    }
    
}
