package com.sohu.bp.elite.api.dao;

import java.util.List;

import com.sohu.bp.elite.api.api.bean.AdminZombieBean;

public interface EliteAdminDao {
	boolean zombieInsert(AdminZombieBean bean);
	boolean zombieRemove(AdminZombieBean bean);
	List<AdminZombieBean> zombieGetList(int start, int count);
	void zombieFlush();
}
