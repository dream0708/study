package com.sohu.bp.elite.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TEliteUser;


public class SaveZombieInDB {
    private static final Logger log = LoggerFactory.getLogger(SaveZombieInDB.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private String filePath = "d://zombies.txt";
    private String separator = "\t";
    
    @Test
    public void saveZombieInDB() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "utf-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] blocks = line.split(separator);
                if (blocks.length > 0) {
                    long bpId = Long.valueOf(blocks[0]);
                    try {
                        eliteAdapter.getUserByBpId(bpId);
                    } catch (Exception ex) {
                        System.out.println("bpId = " + bpId + " doesn't exist in db, start to create user");
                        TEliteUser user = new TEliteUser();
                        Long time = new Date().getTime();
                        user.setBpId(bpId).setFirstLoginTime(time).setLastLoginTime(time).setStatus(EliteUserStatus.VALID.getValue())
                        .setFirstLogin(AgentSource.CRAWL.getValue()).setIdentity(EliteUserIdentity.NORMAL.getValue());
                        eliteAdapter.insertUser(user);
                    }
                    System.out.println("bpId : " + bpId);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
