package com.sohu.bp.elite;

import com.sohu.bp.elite.util.HttpUtil;

/**
 * Created by nicholastang on 2017/5/2.
 */
public class WebAccessTest {
    public static void highFrequencyAccessTest(){
        String url = "https://bar.focus.cn";
        int i=0;
        while (i++ < 100) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String response = HttpUtil.get(url, null);
                    if (response.contains("问吧")) {
                        System.out.println("OK");
                    } else {
                        System.out.println(response);
                    }
                }
            }).start();
        }

    }

    public static void main(String[] args) {
        highFrequencyAccessTest();
    }
}
