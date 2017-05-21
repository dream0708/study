package com.sohu.bp.elite.sna.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * create time: 2013-6-5 下午2:09:00
 * @author: dexingyang
 */
public class AppUtil {

    public static String getOsName() {
        String os = "";
        os = System.getProperty("os.name");
        return os;
    }

    /**
     * Returns the MAC address of the computer.
     * 
     * @return the MAC address
     */
    public static String getMACAddress() {
        String address = "";
        String os = getOsName();
        if (os.startsWith("Windows")) {
            try {
                String command = "cmd.exe /c ipconfig /all";
                Process p = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("Physical Address") > 0 || line.indexOf("物理地址") > 0) {
                        int index = line.indexOf(":");
                        index += 2;
                        address = line.substring(index);
                        break;
                    }
                }
                br.close();
                return address.trim();
            } catch (IOException e) {
            }
        } else if (os.startsWith("Linux")) {
            String command = "/bin/sh -c ifconfig -a";
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("HWaddr") > 0) {
                        int index = line.indexOf("HWaddr") + "HWaddr".length();
                        address = line.substring(index);
                        break;
                    }
                }
                br.close();
            } catch (IOException e) {
            }
        }
        address = address.trim();
        return address;
    }
    
	/**
	 * 根据MAC地址计算当前服务器在sessionId中使用的前缀,防止多台服务器产生的sessionId有相同的情况
	 * @return
	 */
	public static String getPrefix() {
//		InetAddress addr = null;
//		try {
//			addr = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		byte[] ipAddr = addr.getAddress();
//		String prefix = new String(new Base64().encode(ipAddr));
//		prefix = prefix.replace("=", "").replace("\r", "").replace("\n", "");
	    String prefix = getMACAddress();
	    if(prefix != null && prefix.length() > 0){
	        prefix = prefix.replace(":", "").replace("-", "");
	        prefix = prefix.toLowerCase();
	    }else{
	        Random random = new Random(System.currentTimeMillis());
	        int num = random.nextInt(10000)+100;
	        prefix = String.valueOf(num);
	    }
		
		return prefix;
	}
	
	public static void main(String args[]){
	    System.out.println(AppUtil.getPrefix());
	}
}
