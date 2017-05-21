package com.sohu.bp.elite.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 用于把图片上传到bp-upload中
 * 
 * @auther dexingyang
 */
public class BpImageClient {
    private static final Log logger = LogFactory.getLog(BpImageClient.class);
    private static final String SEC_CODE = "on%rsb*9ze^jom!)o@%v9e#fgmb!e394";
    
    public static String uploadUrl = "http://s1.life.sohuno.com/manage/upload/img/inneradd";
    
    private static final int sTimeout = 60000;
    private static final int conTimeout = 10000;

    public static String uploadImage(String filePath,String fileName){
        return uploadFile(uploadUrl,filePath,fileName);
    }
    
    public static String uploadFile(String url, String filePath,String fileName){
        String result = "";
        
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try{
            HttpPost httpPost = new HttpPost(url); 
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(sTimeout).setConnectTimeout(conTimeout).build();
            httpPost.setConfig(requestConfig);
            
            FileBody file = new FileBody(new File(filePath));
            StringBody nameDesc = new StringBody(fileName);
            StringBody sec_code = new StringBody(SEC_CODE);
            
            //对请求的表单域进行填充
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("upload", file);
            reqEntity.addPart("filename", nameDesc);
            reqEntity.addPart("sec_code", sec_code);
            
            httpPost.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if(HttpStatus.SC_OK == status){  
                HttpEntity entity = response.getEntity();  
                //获取返回内容
                if (entity != null) {  
                    result = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                }  
            } else {
                System.out.println(status);
                logger.error("Upload file failed. HttpStaus="+status);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Upload file "+fileName+" to url:"+uploadUrl+" error.",e);
        }finally{
            try{
                httpClient.close();
            }catch(Exception e){
                logger.error("close httpclient error.",e);
            }
        }
        
        return result;
    }
    
    public static void main(String args[]){
        String savePath = "D:\\sina.jpg";
        
        String result = uploadFile(uploadUrl,savePath,"2.jpg");
        //String result = uploadFile(savePath,"2.jpg");
        System.out.println(result);
    }
}
