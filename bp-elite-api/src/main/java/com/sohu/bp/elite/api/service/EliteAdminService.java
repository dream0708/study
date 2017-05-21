package com.sohu.bp.elite.api.service;

import java.util.List;

import com.sohu.bp.elite.api.api.bean.AdminZombieBean;

public interface EliteAdminService {
	boolean zombieInsert(AdminZombieBean bean);
	boolean zombieRemove(AdminZombieBean bean);
	List<AdminZombieBean> zombieGetList(int start, int count);
	void zombieFlush();
}
