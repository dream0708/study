package com.sohu.bp.elite.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IPDataHandler {

    private static final Logger logger = Logger.getLogger(IPDataHandler.class);
    String relativelyPath= "/opt/data";
    private String IP_DATA_PATH = relativelyPath + "/17monipdb.dat";
    private DataInputStream inputStream = null;
    private long fileLength = -1;
    private int dataLength = -1;
    private Map<String, String> cacheMap = null;
    private byte[] allData = null;

    public IPDataHandler() {
        File file;
        try {
            file = new File(IP_DATA_PATH);
        } catch (Exception s) {
            file = new File(relativelyPath + "/17monipdb.dat");
        }
        try {
            inputStream = new DataInputStream(new FileInputStream(file));
            fileLength = file.length();
            cacheMap = new HashMap<String, String>();
            if (fileLength > Integer.MAX_VALUE) {
                throw new Exception("the filelength over 2GB");
            }
            dataLength = (int) fileLength;
            allData = new byte[dataLength];
            inputStream.read(allData, 0, dataLength);
            dataLength = (int) getbytesTolong(allData, 0, 4, ByteOrder.BIG_ENDIAN);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private long getbytesTolong(byte[] bytes, int offerSet, int size, ByteOrder byteOrder) {
        if ((offerSet + size) > bytes.length || size <= 0) {
            return -1;
        }
        byte[] b = new byte[size];
        for (int i = 0; i < b.length; i++) {
            b[i] = bytes[offerSet + i];
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(b);
        byteBuffer.order(byteOrder);
        long temp = -1;
        if (byteBuffer.hasRemaining()) {
            temp = byteBuffer.getInt();
        }
        return temp;
    }


    private long ip2long(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);
        byte[] bytes = address.getAddress();
        return getbytesTolong(bytes, 0, 4, ByteOrder.BIG_ENDIAN);
    }


    private int getIntByBytes(byte[] b, int offSet) {
        if (b == null || (b.length < (offSet + 3))) {
            return -1;
        }
        byte[] bytes = Arrays.copyOfRange(allData, offSet, offSet + 3);
        byte[] bs = new byte[4];
        bs[3] = 0;
        for (int i = 0; i < 3; i++) {
            bs[i] = bytes[i];
        }
        return (int) getbytesTolong(bs, 0, 4, ByteOrder.LITTLE_ENDIAN);
    }


    String findGeography(String address) {
        if (address == null) {
            return "illegal address";
        }
        if (dataLength < 4 || allData == null) {
            return "illegal ip data";
        }
        String ip = "127.0.0.1";
        try {
            ip = Inet4Address.getByName(address).getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        String[] ipArray = ip.split("\\.");
        int ipHeadValue = Integer.parseInt(ipArray[0]);
        if (ipArray.length != 4 || ipHeadValue < 0 || ipHeadValue > 255) {
            return "illegal ip";
        }
        if (cacheMap.containsKey(ip)) {
            return cacheMap.get(ip);
        }
        long numIp = 1;
        try {
            numIp = ip2long(address);
        } catch (UnknownHostException e) {

        }
        int tempOffSet = ipHeadValue * 4 + 4;
        long start = getbytesTolong(allData, tempOffSet, 4, ByteOrder.LITTLE_ENDIAN);
        int max_len = dataLength - 1028;
        long resultOffSet = 0;
        int resultSize = 0;
        for (start = start * 8 + 1024; start < max_len; start += 8) {
            if (getbytesTolong(allData, (int) start + 4, 4, ByteOrder.BIG_ENDIAN) >= numIp) {
                resultOffSet = getIntByBytes(allData, (int) (start + 4 + 4));
                resultSize = (char) allData[(int) start + 7 + 4];
                break;
            }
        }
        if (resultOffSet <= 0) {
            return "resultOffSet too small";
        }
        byte[] add = Arrays.copyOfRange(allData, (int) (dataLength + resultOffSet - 1024), (int) (dataLength + resultOffSet - 1024 + resultSize));
        try {
            if (add == null) {
                cacheMap.put(ip, new String("no data found!!"));
            } else {
                cacheMap.put(ip, new String(add, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return cacheMap.get(ip);
    }

    public String[] getLocationByIp(String address) {
        try {
            String fullAddress = findGeography(address);
            fullAddress = fullAddress.replace("\t", " ");
            return fullAddress.split(" ");
        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {
        IPDataHandler obj = new IPDataHandler();
        System.out.println(obj.getLocationByIp("59.108.49.35"));
    }
}