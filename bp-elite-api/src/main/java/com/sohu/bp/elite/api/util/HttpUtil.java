package com.sohu.bp.elite.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
    private static final int sTimeout = 2000;
    private static final int conTimeout = 2000;

    private static CloseableHttpAsyncClient getAsyncClient(){
    	CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
    	return httpclient;
    }
    
	private static CloseableHttpClient getClient() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		return httpclient;
	}
	
	public static String post(String url, Map<String, String> params) {
	    return post(url,params,sTimeout,conTimeout, null);
	}
	
	public static String post(String url, Map<String, String> params,int readTimeout,int connectTimeout){
		return post(url,params,readTimeout,connectTimeout, null);
	}
	
	/**
	 * 会自动对参数进行UrlEncode
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, String> params, Map<String,String> headers) {
	    return post(url,params,sTimeout,conTimeout, headers);
	}
	
	public static String post(String url, Map<String, String> params,int readTimeout,int connectTimeout, Map<String, String> headers) {
        CloseableHttpClient httpclient = getClient();
        try {
            HttpPost httppost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
            httppost.setConfig(requestConfig);
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            if(params != null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    nvps.add(new BasicNameValuePair(key, params.get(key)));
                }
            }
            if(headers != null){
            	for(Map.Entry<String, String> entry:headers.entrySet()){
            		httppost.addHeader(entry.getKey(), entry.getValue());
            	}
            }

            try {
                log.debug("set utf-8 form entity to httppost");
                httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String charset = null;
                        if(entity!=null){
                            ContentType ct = ContentType.getOrDefault(entity);
                            charset = ct.getCharset() == null ? "ISO-8859-1":ct.getCharset().toString();
                        }
                        return entity != null ? EntityUtils.toString(entity,charset) : null;
                    } else {
                        HttpEntity entity = response.getEntity();
                        String charset = null;
                        if(entity!=null){
                            ContentType ct = ContentType.getOrDefault(entity);
                            charset = ct.getCharset() == null ? "ISO-8859-1":ct.getCharset().toString();
                        }
                        log.error("error msg:"+(entity != null ? EntityUtils.toString(entity,charset) : null));
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httppost, responseHandler);
            return responseBody;
        } catch (ClientProtocolException e) {
            log.error("call url:"+url+" error.",e);
        } catch (Exception e) {
            log.error("call url:"+url+" error:" + e.toString());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "{\"code\":-1,\"msg\":\"call url error\"}";
    }
	
    public static String get(String url, Map<String, String> params) {
        return get(url, params, sTimeout, conTimeout);
    }
    
    public static String get(String url, Map<String, String> params, Map<String,String> headers) {
	    return get(url,params,sTimeout,conTimeout, headers);
	}
    
    public static String get(String url, Map<String, String> params, int readTimeout,int connectTimeout, Map<String, String> headers)
    {
    	CloseableHttpClient httpclient = getClient();
		try {
			StringBuilder query = new StringBuilder();
			if(params != null && params.size() > 0){
				for (String param : params.keySet()) {
					if(query.length() > 0) query.append("&");
					query.append(param + "=");
					query.append(URLEncoder.encode(params.get(param), "UTF-8"));
				}
			}
			if(query.length() > 0){
				String join = "?";
				if(url.contains(join)) //原url含有querystring
					join = "&";
				url = url + join + query.toString();
			}
			HttpGet httpget = new HttpGet(url);
			if(headers != null){
            	for(Map.Entry<String, String> entry:headers.entrySet()){
            		httpget.addHeader(entry.getKey(), entry.getValue());
            	}
            }
			
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
            httpget.setConfig(requestConfig);
			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String charset = null;
						if(entity!=null){
							ContentType ct = ContentType.getOrDefault(entity);
							charset = ct.getCharset() == null ? "ISO-8859-1":ct.getCharset().toString();
						}
						return entity != null ? EntityUtils.toString(entity,charset) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody;
		} catch (ClientProtocolException e) {
			log.error("call url:"+url+" error,"+e.toString());
		} catch (Exception e) {
			log.error("call url:"+url+" error,"+e.toString());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "{\"code\":-1,\"msg\":\"call url error\"}";
    }
    
	/**
	 * 会自动对参数进行UrlEncode
	 * @param url
	 * @param params
	 * @param readTimeout 毫秒
	 * @param connectTimeout 毫秒
	 * @return
	 */
	public static String get(String url, Map<String, String> params, int readTimeout,int connectTimeout) {
		CloseableHttpClient httpclient = getClient();
		try {
			StringBuilder query = new StringBuilder();
			if(params != null && params.size() > 0){
				for (String param : params.keySet()) {
					if(query.length() > 0) query.append("&");
					query.append(param + "=");
					query.append(URLEncoder.encode(params.get(param), "UTF-8"));
				}
			}
			if(query.length() > 0){
				String join = "?";
				if(url.contains(join)) //原url含有querystring
					join = "&";
				url = url + join + query.toString();
			}
			HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
            httpget.setConfig(requestConfig);
			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String charset = null;
						if(entity!=null){
							ContentType ct = ContentType.getOrDefault(entity);
							charset = ct.getCharset() == null ? "ISO-8859-1":ct.getCharset().toString();
						}
						return entity != null ? EntityUtils.toString(entity,charset) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody;
		} catch (ClientProtocolException e) {
			log.error("call url:"+url+" error,"+e.toString());
		} catch (Exception e) {
			log.error("call url:"+url+" error,"+e.toString());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "{\"code\":-1,\"msg\":\"call url error\"}";
	}
	
	/**
	 * 会自动对参数进行UrlEncode
	 * @param url
	 * @param params
	 * @return
	 */
	public static void getWithoutResponse(String url, Map<String, String> params) {
		CloseableHttpClient httpclient = getClient();
		try {
			StringBuilder query = new StringBuilder();
			if(params != null && params.size() > 0){
				for (String param : params.keySet()) {
					if(query.length() > 0) query.append("&");
					query.append(param + "=");
					query.append(URLEncoder.encode(params.get(param), "UTF-8"));
				}
			}
			if(query.length() > 0){
				String join = "?";
				if(url.contains(join)) //原url含有querystring
					join = "&";
				url = url + join + query.toString();
			}
			HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(sTimeout).setConnectTimeout(conTimeout).build();
            httpget.setConfig(requestConfig);
            httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			log.error("call url:"+url+" error.",e);
		} catch (Exception e) {
			log.error("call url:"+url+" error.",e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 会自动对参数进行UrlEncode
	 * @param url
	 * @param params
	 * @return
	 */
	public static void asyncGet(String url, final Map<String, String> params, int readTimeout, int connectTimeout) {
		final CloseableHttpAsyncClient httpclient = getAsyncClient();
		httpclient.start();
		try {
			StringBuilder query = new StringBuilder();
			if(params != null && params.size() > 0){
				for (String param : params.keySet()) {
					if(query.length() > 0) query.append("&");
					query.append(param + "=");
					query.append(URLEncoder.encode(params.get(param), "UTF-8"));
				}
			}
			if(query.length() > 0){
				String join = "?";
				if(url.contains(join)) //原url含有querystring
					join = "&";
				url = url + join + query.toString();
			}
			HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
            httpget.setConfig(requestConfig);
			httpclient.execute(httpget, new FutureCallback<HttpResponse>() {

				@Override
				public void completed(HttpResponse result) {
					// TODO Auto-generated method stub
					log.info("GET create or update index success, id=" + (null == params.get("id") ? "" : params.get("id")));
					try {
		                httpclient.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
				}

				@Override
				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					log.warn("GET HTTP request fail! id=" + (null == params.get("id") ? "" : params.get("id")), ex);
					try {
		                httpclient.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }		
				}

				@Override
				public void cancelled() {
					// TODO Auto-generated method stub
					log.warn("GET HTTP request cancelled! id=" + (null == params.get("id") ? "" : params.get("id")));
					try {
		                httpclient.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }			
				}

            });
		} catch (Exception e) {
			log.error("GET call url:"+url+" error. id=" + (null == params.get("id") ? "" : params.get("id")), e);
		} finally {
//			try {
//				httpclient.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	/**
	 * 会自动对参数进行UrlEncode
	 * @param url
	 * @param params
	 * @return
	 */
	public static void asyncGet(String url, final Map<String, String> params) {
		asyncGet(url, params, sTimeout, conTimeout);
	}
	
	
	public static void asyncPost(final String url, final Map<String, String> params,int readTimeout,int connectTimeout) {
	      final CloseableHttpAsyncClient httpclient = getAsyncClient();
	        httpclient.start();
	        try {
	            HttpPost httppost = new HttpPost(url); 
	            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
	            httppost.setConfig(requestConfig);
	            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

	            Set<String> keySet = params.keySet();
	            for (String key : keySet) {
	                nvps.add(new BasicNameValuePair(key, params.get(key)));
	            }

	            try {
	                log.debug("set utf-8 form entity to httppost");
	                httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }

	            httpclient.execute(httppost, new FutureCallback<HttpResponse>() {

					@Override
					public void completed(HttpResponse result) {
						// TODO Auto-generated method stub
						log.info("POST HTTP request success, id=" + (null == params.get("id") ? "" : params.get("id")));
						try {
			                httpclient.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
					}

					@Override
					public void failed(Exception ex) {
						// TODO Auto-generated method stub
						log.warn("POST HTTP request fail! id=" + (null == params.get("id") ? "" : params.get("id"))+",url="+url, ex);
						try {
			                httpclient.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }		
					}

					@Override
					public void cancelled() {
						// TODO Auto-generated method stub
						log.warn("POST HTTP request cancelled! id=" + (null == params.get("id") ? "" : params.get("id")));
						try {
			                httpclient.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }			
					}

	            });
	        } catch (Exception e) {
	            log.error("POST call url:"+url+" error. id=" + (null == params.get("id") ? "" : params.get("id")),e);
	        } finally {
	        	//血的教训：别在这里关闭client
//	            try {
//	                httpclient.close();
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
	        }
	}
	
	public static void asyncPost(final String url, final Map<String, String> params) {
       asyncPost(url, params, sTimeout, conTimeout);
    }
	
	public static JSONObject parseJson(String res) {
		JSONObject jObject = JSONObject.fromObject(res);
		return jObject;
	}
	
	public static String getNoEncode(String url, Map<String, String> params) {
        return getNoEncode(url, params, sTimeout, conTimeout);
    }
	//无编码get
    public static String getNoEncode(String url, Map<String, String> params, int readTimeout,int connectTimeout) {
		CloseableHttpClient httpclient = getClient();
		try {
			StringBuilder query = new StringBuilder();
			if(params != null && params.size() > 0){
				for (String param : params.keySet()) {
					if(query.length() > 0) query.append("&");
					query.append(param + "=");
					query.append(params.get(param));
				}
			}
			if(query.length() > 0){
				String join = "?";
				if(url.contains(join)) //原url含有querystring
					join = "&";
				url = url + join + query.toString();
			}
			HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connectTimeout).build();
            httpget.setConfig(requestConfig);

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String charset = null;
						if(entity!=null){
							ContentType ct = ContentType.getOrDefault(entity);
							charset = ct.getCharset() == null ? "UTF-8":ct.getCharset().toString();
						}
						return entity != null ? EntityUtils.toString(entity,charset) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody;
		} catch (ClientProtocolException e) {
			log.error("call url:"+url+" error.",e);
		} catch (Exception e) {
			log.error("call url:"+url+" error.",e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "{\"code\":-1,\"msg\":\"call url error\"}";
	}
	
	public static void main(String args[]){
	
	}
}
