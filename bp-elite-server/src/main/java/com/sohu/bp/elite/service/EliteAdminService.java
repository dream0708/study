package com.sohu.bp.elite.service;

import com.sohu.bp.elite.persistence.EliteAdmin;

/**
 * Created by nicholastang on 2017/3/15.
 */
public interface EliteAdminService {
    public boolean save(EliteAdmin eliteAdmin);
    public EliteAdmin get(long bpId);
    public Integer getAdminStatus(long bpId);
    public boolean superAdmin(long bpId);
    public boolean update(EliteAdmin eliteAdmin);
}
