package com.sohu.bp.elite.auth;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationCenter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationCenter.class);
    private static final String SEPERATOR = "|";

    @SuppressWarnings("serial")
    public static final Map<String, String> APPPair = new ConcurrentHashMap<String, String>() {
        {
            put("wx-programme", "rjk21wIlTnN4YvzoubN6mxAOm145vOaH");
        }
    };
    
    public static final long torDeviation = 3 * 60 * 1000;

    public static boolean isPassAuthentication(String uri, String method, long t, String auth) {
        String[] pair = auth.split("\\" + SEPERATOR);
        if (pair.length != 2) {
            log.error("[AUTH-CENTER]auth={" + auth + "} is invalid");
            return false;
        }

        String app_key = pair[0];
        String sig = pair[1];

        if (!APPPair.containsKey(app_key)) {
            log.error("[AUTH-CENTER]app_key={" + app_key + "} is invalid");
            return false;
        }
        String secret = APPPair.get(app_key);
        if (StringUtils.isEmpty(secret)) {
            return false;
        }

        long delta = Math.abs(System.currentTimeMillis() - t);
        if (delta > torDeviation) {
            log.error("[AUTH-CENTER]out of time, app_key=" + app_key);
            return false;
        }

        String checkSig = generateSignature(uri, method, t, secret);
        if (checkSig.equals(sig)) {
            return true;
        }
        return false;
    }

    public static String generateSignature(String uri, String method,
                                           long timestamp, String secret) {
//        String text = method + SEPERATOR + uri + SEPERATOR + timestamp;
//        try {
//            byte[] data = secret.getBytes();
//            // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
//            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
//            // 生成一个指定 Mac 算法 的 Mac 对象
//            Mac mac = Mac.getInstance("HmacSHA1");
//            // 用给定密钥初始化 Mac 对象
//            mac.init(secretKey);
//            String r = new String(Base64.encodeBase64(mac.doFinal(text
//                    .getBytes()))).trim();
//            return r;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//        return "";
        String algorithm = "HmacSHA1";
        String text = method + SEPERATOR + uri + SEPERATOR + timestamp;
        byte[] keyBytes = secret.getBytes();
        Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
        Mac mac=null;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(key);
            return byteArrayToHex(mac.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String base64GenerateSignature(String uri,String method,
                                           long timestamp, String secret) {
        String text = method+SEPERATOR+uri+SEPERATOR+timestamp;
        try {
            byte[] data = secret.getBytes();
            // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            // 生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            // 用给定密钥初始化 Mac 对象
            mac.init(secretKey);
            String r = new String(Base64.encodeBase64(mac.doFinal(text
                    .getBytes()))).trim();
            return r;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 16进制加密
     * @param a
     * @return
     */
    protected static String byteArrayToHex(byte [] a) {
        int hn, ln, cx;
        String hexDigitChars = "0123456789abcdef";
        StringBuffer buf = new StringBuffer(a.length * 2);
        for(cx = 0; cx < a.length; cx++) {
            hn = ((int)(a[cx]) & 0x00ff) /16 ;
            ln = ((int)(a[cx]) & 0x000f);
            buf.append(hexDigitChars.charAt(hn));
            buf.append(hexDigitChars.charAt(ln));
        }
        return buf.toString();

    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args){
        String sig = generateSignature("/wx/ask/square/initial", "POST", 1000, "p0o9i8u7");
        System.out.println(sig);

    }
}


