package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.enums.EliteAdminStatus;
import com.sohu.bp.elite.enums.EliteGenderType;
import com.sohu.bp.elite.persistence.EliteAdmin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
public class EliteAdminTest {

    @Resource
    private EliteAdminDao eliteAdminDao;

    @Test
    public void testSave(){
    	EliteAdmin eliteAdmin6 = new EliteAdmin();
    	eliteAdmin6.setId(1117L);

    	eliteAdmin6.setStatus(EliteAdminStatus.NONE.getValue());
    	eliteAdminDao.save(eliteAdmin6);
//    	EliteAdmin eliteAdmin5 = new EliteAdmin();
//    	eliteAdmin5.setBpId(91278);
//    	eliteAdmin5.setStatus(EliteAdminStatus.NONE.getValue());
//    	eliteAdminDao.save(eliteAdmin5);
//    	System.out.println("\n the result is " + saveEliteAdmin5);
    	
//    	EliteAdmin eliteAdmin4 = new EliteAdmin();
//    	eliteAdmin4.setBpId(7778);
//    	eliteAdmin4.setStatus(EliteAdminStatus.SUPER_ADMIN.getValue());
//    	boolean saveEliteAdmin4 = eliteAdminDao.save(eliteAdmin4);
//    	System.out.println("\n the result is " + saveEliteAdmin4);
    	
//        EliteAdmin eliteAdmin1 = new EliteAdmin();
//        eliteAdmin1.setBpId(1234);
//        eliteAdmin1.setStatus(EliteAdminStatus.SUPER_ADMIN.getValue());
//        EliteAdmin eliteAdmin2 = new EliteAdmin();
//        eliteAdmin2.setBpId(7834);
////        eliteAdmin2.setStatus(EliteAdminStatus.NONE.getValue());
//        EliteAdmin eliteAdmin3 = new EliteAdmin();
//        eliteAdmin3.setBpId(9034);
//        eliteAdmin3.setStatus(EliteAdminStatus.SUPER_ADMIN.getValue());
        //boolean saveEliteAdmin1 = eliteAdminDao.save(eliteAdmin1);
//        boolean saveEliteAdmin2 = eliteAdminDao.save(eliteAdmin2);
//        boolean saveEliteAdmin3 = eliteAdminDao.save(eliteAdmin3);
//        System.out.println("\n the result of testSave1 = " + saveEliteAdmin1);
       // System.out.println("\n the result of testSave2 = " + saveEliteAdmin2);
//        System.out.println("\n the result of testSave3 = " + saveEliteAdmin3);
        
    }

    @Test
    public void testUpdate(){
    	EliteAdmin eliteAdmin = eliteAdminDao.get(8594477);
    	if (eliteAdmin == null) {
    		System.out.println("failed~~~");
    	}
    	System.out.println("get user succeed! user id is :" + eliteAdmin.getId());
    	eliteAdmin.setStatus(EliteAdminStatus.NONE.getValue());
    	Boolean retVal = eliteAdminDao.update(eliteAdmin);
    	System.out.print("update user result is :" + retVal);
    }

    @Test
    public void testGet(){
    	EliteAdmin eliteAdmin = eliteAdminDao.get(7834);
    	System.out.println("testGet is succeed, the status is " + eliteAdmin.getStatus());
    }
    
    @Test
    public void testGetAdminStatus(){
    	Integer statusInteger = eliteAdminDao.getAdminStatus(7834);
    	System.out.println("testGetAdminStatus is succeed, the status is " + statusInteger);
    }
    
    @Test
    public void testSuperAdmin(){
    	boolean isSuper = eliteAdminDao.superAdmin(1234);
    	System.out.println("testSuperAdmin is succeed, the result is " + isSuper);
    }
    
}