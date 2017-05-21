package com.sohu.bp.elite.api.auth;


import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.util.HttpUtil;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationCenter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationCenter.class);
    private static final String SEPERATOR = "|";

    @SuppressWarnings("serial")
    public static final Map<String, String> APPPair = new ConcurrentHashMap<String, String>() {
        {
            put("app_test", "t1i8RiTpMVLwQJK1iJuXPSuOlMYe4lvZ");    //for junit test
            put("bp-decoration-search", "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i");
            put("bp-admin", "6EWFOBSTl6S2ofW7m8L0IIuZjcv1CweO");
            put("elite-crawl", "Cl2JKiGDGspS4UsfnI9EiJ9R5CdB1iKG");
            put("app_audit", "QUxjPdUtVVD5x0prpTcSt7dntzi1fDa8");
            put("ecology", "mpLGKB80Y1k2Btdwoat5l3ScLMvhFgn7");
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
        String text = method + SEPERATOR + uri + SEPERATOR + timestamp;
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

    public static void main(String[] args) throws TException, IOException {
//        long timesec = System.currentTimeMillis();
//        String secret = "MMk5P0dLbeXdevk1PPAd4tabEcEGxcCm";
//        String uri = "/bpapi/add-to-cart";
//        String method = "POST";
//        String sig = generateSignature(uri,method, timesec, secret);
//        System.out.println(sig);
        //System.out.println(getRandomString(32));
        //rebuildQuestionAndAnswers();
        //rebuildUser();
        testGetData();
    }

    private static void testGetData(){
        String uri = "/elite/innerapi/ecology/index";
        long time = System.currentTimeMillis();
        String method = "POST";
        String sec = "mpLGKB80Y1k2Btdwoat5l3ScLMvhFgn7";
        String appkey = "ecology";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Auth", appkey + "|" + generateSignature(uri, method, time, sec));
        Map<String, String> params = new HashMap<>();
        params.put("start", "0");
        params.put("count", "10");
        params.put("talent", "2");
        params.put("areaCode", "110000");
        params.put("force", "0");
        String res = HttpUtil.post("http://test.life.sohuno.com"+ uri, params, headers);
        System.out.println(res);
    }

    //重建用户索引
    private static void rebuildUser(){
        String rebuildUserUri = "/elite/innerapi/rebuild-index/user";
        long time = System.currentTimeMillis();
        String method = "POST";
        String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";
        String appkey = "bp-decoration-search";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Auth", appkey + "|" + generateSignature(rebuildUserUri, method, time, sec));

        Map<String, String> params = new HashMap<>();
        String[] bpIds = {"8402492", "140253"};
        for(String bpId : bpIds) {
            params.put("bpId", bpId);
            String res = HttpUtil.post("http://10.10.3.146:8012" + rebuildUserUri, params, headers);
            System.out.println("rebuild user result=" + res);
        }
    }

    //重建用户索引
    private static void rebuildUsers() throws IOException {
        String rebuildUserUri = "/elite/innerapi/rebuild-index/user";
        long time = System.currentTimeMillis();
        String method = "POST";
        String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";
        String appkey = "bp-decoration-search";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Auth", appkey + "|" + generateSignature(rebuildUserUri, method, time, sec));

        Map<String, String> params = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Lenovo\\Desktop\\users.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
            params.put("bpId", line.split("\t")[2]);
            String res = HttpUtil.post("http://10.10.3.146:8012" + rebuildUserUri, params, headers);
            System.out.println("rebuild user result=" + res);
        }
    }

    //重建所有问题和回答的索引（从搜索那边得到的问题数据）
    private static void rebuildQuestionAndAnswers() throws TException {
        String rebuildQuestionUri = "/elite/innerapi/rebuild-index/question";
        String rebuildQuestionAnswersUri = "/elite/innerapi/rebuild-index/question/answers";
        long time = System.currentTimeMillis();
        String method = "POST";
        String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";
        String appkey = "bp-decoration-search";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));

        Map<String, String> params = new HashMap<>();
        for(TEliteQuestion question : getAllQuestions()) {
            params.put("questionId", String.valueOf(question.getId()));
            headers.put("Auth", appkey + "|" + generateSignature(rebuildQuestionUri, method, time, sec));
            String res = HttpUtil.post("http://10.10.3.146:8012" + rebuildQuestionUri, params, headers);
            System.out.println("rebuild question result=" +res);

            params.put("questionId", String.valueOf(question.getId()));
            headers.put("Auth", appkey + "|" + generateSignature(rebuildQuestionAnswersUri, method, time, sec));
            String res2 = HttpUtil.post("http://10.10.3.146:8012" + rebuildQuestionAnswersUri, params, headers);
            System.out.println("rebuild question answers result=" +res2);

        }
    }
    private static List<TEliteQuestion> getAllQuestions() throws TException {
        EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
        TSearchQuestionCondition condition = new TSearchQuestionCondition();
        condition.setFrom(0)
                .setCount(Integer.MAX_VALUE)
                .setStatusArray(EliteQuestionStatus.PUBLISHED.getValue() + ";" + EliteQuestionStatus.PASSED.getValue());
        TQuestionListResult listResult = eliteAdapter.searchQuestion(condition);
        return listResult.getQuestions();
    }
}


