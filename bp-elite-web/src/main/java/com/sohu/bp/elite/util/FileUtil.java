package com.sohu.bp.elite.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.sohu.bp.elite.bean.Picture;

/**
 * create time: 2015年12月22日 下午7:19:20
 * @auther dexingyang
 */
public class FileUtil {

    private static final Log logger = LogFactory.getLog(FileUtil.class);
    private static final Integer CONNECTION_TIMEOUT = 5000;
    private static final Integer READ_TIMEOUT = 40000;
    
    /**
     * 
     * @param url
     * @param savePath
     * @return [0]:width,[1]:height,[2]:ext
     */
    public static Picture downloadImage(String url,String savePath) {
        InputStream in = null;
        FileOutputStream fos = null;
        HttpURLConnection httpConnection = null;
        HttpClient httpclient = null;
        Picture pic = null;
        try {
            logger.info("start to download image.");
            long startTime = System.currentTimeMillis();
            if(url.startsWith("http://")){
            	URL rURL = new URL(url);
                httpConnection = (HttpURLConnection) rURL.openConnection();
                httpConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                httpConnection.setReadTimeout(READ_TIMEOUT);
                httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
                in = httpConnection.getInputStream();
            } else if(url.startsWith("https://")) {
            	//处理https链接的图片
            	httpclient = new SSLClient();
            	HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                	in = entity.getContent();
                }
            }
            File file = new File(savePath);
            fos = new FileOutputStream(file);
            
            int len = 0;
            final int MAXLEN = 1024*10;
            byte[] b = new byte[MAXLEN];
            while ((len = in.read(b)) > 0) {
                fos.write(b, 0, len);
            }
            logger.info("download image success:time="+(System.currentTimeMillis()-startTime)+"ms,url="+url);
            pic = getPicture(file);   //获取图片信息
        } catch (MalformedURLException me) {
            logger.error("MalformedURL error. " + me.toString() + " " + url);
            pic = null;
        } catch (IOException ie) {
            logger.error("downloadImage io error. " + ie.toString() + " " + url);
            pic = null;
        } catch (Exception e) {
            logger.error("downloadImage error. " + e.toString() + " " + url);
            pic = null;
        } finally {
            try {
                if(in != null)
                    in.close();
                if(fos != null)
                    fos.close();
                if(httpConnection != null)
                    httpConnection.disconnect();
                if(httpclient != null)
                	httpclient.getConnectionManager().shutdown();
            } catch (Exception fe) {
                logger.error("close error. " + fe.toString());
            }
        }
        return pic;
    }
    
    private static Picture getPicture(File file) {
        Picture pic = null;
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(file);
            Iterator iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                return null;
            }
            ImageReader reader = (ImageReader) iter.next();
            reader.setInput(iis);
            
            String filePath = file.getAbsolutePath();
            String ext = reader.getFormatName().toLowerCase();
            if("webp".equals(ext)){   //webp转换成jpg
                String renamePath = filePath + "_webp";
                File renameFile = new File(renamePath);
                file.renameTo(renameFile);
                File out = new File(filePath);
                
                BufferedImage im = ImageIO.read(renameFile);
                ImageIO.write(im, "jpg", out);
                ext = "jpg";
                logger.info("convert webp image to jpg,filePath="+filePath);
            }
            
            pic = new Picture();
            pic.setWidth(reader.getWidth(0));
            pic.setHeight(reader.getHeight(0));
            pic.setExt(ext);
            pic.setFilePath(filePath);
            reader.dispose();
        }catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }finally{
            if(iis != null){
                try{
                    iis.close();
                }catch(Exception e){}
            }
        }
        return pic;
    }
    
    public static byte[] readFromFile(String filePath){
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            in = new FileInputStream(filePath);
            baos = new ByteArrayOutputStream();
            int len = 0;
            final int MAXLEN = 1024*10;
            byte[] b = new byte[MAXLEN];
            while ((len = in.read(b)) > 0) {
                baos.write(b, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException ie) {
            logger.error("io error. " + ie.toString());
            return null;
        } catch (Exception e) {
            logger.error("read file error. " + e.toString());
            return null;
        } finally {
            try {
                if(in != null)
                    in.close();
                if(baos != null)
                    baos.close();
            } catch (Exception fe) {
                logger.error("close error. " + fe.toString());
            }
        }
    }
    
    /**
     * 从网络地址读取文件
     * @param url
     * @return
     */
    public static byte[] readFromUrl(String url){
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        
        try{
            // Create a custom response handler
            ResponseHandler<byte[]> responseHandler = new ResponseHandler<byte[]>() {
                public byte[] handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        if(entity != null)
                            return EntityUtils.toByteArray(entity);
                        else
                            return null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };

            return client.execute(get, responseHandler);
        }catch(Exception e){
            logger.error("Get file from "+url+" error",e);
        }finally{
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 关闭流,异常会被隐藏起来
     * 
     * @param in
     */
    public static void closeStream(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("close in error", e);
                }

            }
        }
    }
    
    public static void main(String args[]){
        //byte[] data = FileUtil.readFromUrl("http://bjctc.azure.sohu.com/peakviewcnc/mp/20140610/d8df5fbf3df448978ccc268a5fd875df.jpg?1");
        //System.out.println(data.length);
        String url = "https://mmbiz.qlogo.cn/mmbiz/Cicq3Mb1qwFY5NAmgoZrQBibvK3m8k0bjFQhQEP48CD15iaqPYgAKHCzDKR6DpE6Sg94seXWHzH0zqiaAIGDsRF7tA/0?wx_fmt=jpeg";
        String savePath = "D:\\1.jpg";
        Picture pic = downloadImage(url,savePath);
        System.out.println(pic);
    }
}
