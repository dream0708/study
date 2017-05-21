package com.sohu.bp.elite.api;

import com.sohu.bp.elite.api.auth.AuthenticationCenter;
import com.sohu.bp.elite.api.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class RebuildEliteIndexIndex {
    private static String rebuildUserUri = "/elite/innerapi/rebuild-index/question/answers";
    private static String appkey = "bp-decoration-search";
    private static String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";

    public static void RebildIndex() {
        long time = System.currentTimeMillis();
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Auth", appkey + "|" + AuthenticationCenter.generateSignature(rebuildUserUri, method, time, sec));
        Map<String, String> params = new HashMap<>();
        params.put("questionId", String.valueOf(14105));
        String res = HttpUtil.post("http://test.life.sohuno.com" + rebuildUserUri, params, headers);
        System.out.println("rebuild user result=" + res);
    }
    public static void main(String[] args) {
        RebildIndex();
    }
}
//	
//	public static void main(String[] args) {
////		RebildIndex();
//		EliteThriftServiceAdapter adapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//		try {
////			for(long id =2343; id <= 2352; id++){
//			Long id = AESUtil.decryptIdV2("6a77239052823941fce144f5d3bb4fcd");
//			boolean res = adapter.rebuildAnswersForQuestion(id);
////			}
////			TEliteAnswer answer = adapter.getAnswerById(AESUtil.decryptIdV2("0c3899ffbe42c82bf6527012c0ba67a8"));
////			String encoded = AESUtil.encryptIdV2(answer.getQuestionId());
////			System.out.println(encoded);
//		} catch (TException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	}
//
