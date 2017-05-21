package com.sohu.bp.elite;

public class OverallData
{
	//静态资源版本号
	private static String staticVerCode;
	private static String appStaticVerCode;

	public static String getStaticVerCode() {
		return staticVerCode;
	}

	public static void setStaticVerCode(String staticVerCode) {
		OverallData.staticVerCode = staticVerCode;
	}

    public static String getAppStaticVerCode() {
        return appStaticVerCode;
    }

    public static void setAppStaticVerCode(String appStaticVerCode) {
        OverallData.appStaticVerCode = appStaticVerCode;
    }

}