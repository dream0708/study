package com.sohu.bp.elite.api.util;

import com.sohu.bp.utils.Base36Number;
import com.sohu.bp.utils.crypt.AESUtil;

public class IDUtil {

    public static String encodeId(long id){
        return Base36Number.encrypt(id);
    }

    public static long decodeId(String source){
    	long id = Base36Number.decrypt(source);
    	if (id > 0) {
    	    return id;
    	}
    	return AESUtil.decryptIdV2(source);
    }
    
    public static void main(String[] args)
    {
    	System.out.println("****");
    	System.out.println(IDUtil.decodeId("5ct22y1"));
    	System.out.println("****");
    	//System.out.println(IDUtil.decodeId("a77ab63792"));
    	//System.out.println("****");
    	//System.out.println(IDUtil.decodeId("631cc0ee909cb33938b067176320e7e0"));
    	
    	Long id = new Long(11449);
    	//System.out.println(IDUtil.encodeId(id));
    }

}
