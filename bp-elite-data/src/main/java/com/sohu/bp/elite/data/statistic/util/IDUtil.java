package com.sohu.bp.elite.data.statistic.util;

import com.sohu.bp.utils.Base36Number;
import com.sohu.bp.utils.crypt.AESUtil;

/**
 * @author zhangzhihao
 *         2016/8/15
 */
public class IDUtil {

    public static String encodeIdAES(long id){
        return AESUtil.encryptIdV2(id);
    }

    public static long decodeIdAES(String id){
        return AESUtil.decryptIdV2(id);
    }

    public static String encodedId(long id) {
        return Base36Number.encrypt(id);
    }

    public static long decodeId(String id) {
        return Base36Number.decrypt(id);
    }

    public static long smartDecodeId(String source) {
        long id = Base36Number.decrypt(source);
        if (id > 0) {
            return id;
        }
        return AESUtil.decryptIdV2(source);
    }
    
    public static void main(String[] args)
    {
    	System.out.println("****");
    	System.out.println(IDUtil.decodeId("4694j"));
    	System.out.println("****");
    	//System.out.println(IDUtil.decodeId("a77ab63792"));
    	System.out.println("****");
    	System.out.println(IDUtil.decodeId("631cc0ee909cb33938b067176320e7e0"));
    	
//    	Long id = new Long(11449);
//    	System.out.println(IDUtil.encodeId(id));
    }

}
