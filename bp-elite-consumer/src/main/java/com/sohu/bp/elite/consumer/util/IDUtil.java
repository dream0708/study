package com.sohu.bp.elite.consumer.util;

import com.sohu.bp.utils.Base36Number;
import com.sohu.bp.utils.crypt.AESUtil;

/**
 * @author zhangzhihao
 *         2016/8/15
 */
public class IDUtil {

    public static String encodeId(long id){
    	if(0 == id) return String.valueOf(id);
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
    	//System.out.println("result : " + AESUtil.decryptStringV2("59bc57b77bee4f3e1c59dac70493a1b03b3a5427643ae69fb0004f32f43049eed2a566fa303760334fe8db1ae4d9277c1629c9b4adde25217a687ea2a543f0c2"));
        System.out.println("result:"+AESUtil.encryptStringV2("11129|18511878337|1490153797000|ucE4uHGAglj5LuJ4"));
    }
    
    public static String encodeIdOrigin(long id){
    	return AESUtil.encryptIdV2(id);
    }
    
    public static long decodeIdOrigin(String source){
    	return AESUtil.decryptIdV2(source);
    }
}
