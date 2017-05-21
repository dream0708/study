package com.sohu.bp.elite.api.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.service.EliteUserService;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TEliteUser;

public class EliteUserServiceImpl implements EliteUserService {
    private static final Logger log = LoggerFactory.getLogger(EliteUserServiceImpl.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory
            .getEliteThriftServiceAdapter();

    @Override
    public boolean isUserInDatabase(Long bpId) {
        boolean res = true;
        try {
            eliteAdapter.getUserByBpId(bpId);
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    @Override
    public void createUser(Long bpId) {
        createUser(bpId, EliteUserIdentity.NORMAL);
    }

    @Override
    public boolean createUser(Long bpId, EliteUserIdentity identity) {
        TEliteUser user = new TEliteUser().setBpId(bpId).setFirstLoginTime(new Date().getTime())
                .setLastLoginTime(new Date().getTime()).setFirstLogin(AgentSource.CRAWL.getValue())
                .setStatus(EliteUserStatus.VALID.getValue()).setIdentity(identity.getValue());
        try {
            Long id = eliteAdapter.insertUser(user);
            log.info("user doesn't in database! create user bpId = {}", new Object[] { id });
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    @Override
    public void createUserIfNotExist(Long bpId) {
        boolean flag = isUserInDatabase(bpId);
        if (!flag)
            createUser(bpId);
    }

    @Override
    public boolean createUserIfNotExist(Long bpId, EliteUserIdentity identity) {
        TEliteUser user = null;
        try {
            user = eliteAdapter.getUserByBpId(bpId);
        } catch (Exception e) {
            return createUser(bpId, EliteUserIdentity.EXPERT);            
        }
        if (null != user && user.getIdentity() != identity.getValue()) {
            user.setIdentity(identity.getValue());
            try {
                eliteAdapter.updateUser(user);
            } catch (Exception e) {
                log.error("", e);
                return false;
            }
        }
        return true;
    }

}
