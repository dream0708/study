package com.sohu.bp.elite.util;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by nicholastang on 2017/1/19.
 */
public class NetUtil {
	private static final String NETWORK_CARD = "eth0";
	
    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }
    public static boolean isPortAvailable(int port) {
        Socket s = new Socket();
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getLocalIp() {
    	String ip = "0.0.0.0";
    	try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();
		} catch (UnknownHostException e) {
			return ip;
		}
    	return ip;
    }
    
    public static String getServerIp() {
    	String ip = "0.0.0.0";
    	try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				if (NETWORK_CARD.equals(networkInterface.getName())) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (address instanceof Inet6Address) continue;
						ip = address.getHostName();
					}
					break;
				}
			}
		} catch (SocketException e) {
			return ip;
		}
    	return ip;
    }
    
    public static void main(String[] args) {
		System.out.println("ip address is " + getLocalIp());
	}
}
