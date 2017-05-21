package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.enums.EliteGenderType;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.persistence.EliteUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 
 * @author zhijungou
 * 2016年10月20日
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
public class EliteUserTest {

    @Resource
    private EliteUserDao userDao;

    @Test
    public void testSave(){
        EliteUser user = new EliteUser();
        user.setId(64l);
        user.setStatus(EliteUserStatus.VALID.getValue());
        user.setLastLoginTime(new Date());
        user.setFirstLoginTime(new Date());
        user.setDescription("She is a good girl.");
        Long id = userDao.save(user);
        System.out.println("\n save user result=" + id);
    }

    @Test
    public void testUpdate(){
    	EliteUser user = userDao.get(64l);
    	System.out.println("get user succeed! user id is :" + user.getId());
    	user.setLastLoginTime(new Date(new Date().getTime()+1000000));
    	Boolean retVal = userDao.update(user);
    	System.out.print("update user result is :" + retVal);
    }

    @Test
    public void testGet(){

    }
}

