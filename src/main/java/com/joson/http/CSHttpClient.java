package com.joson.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 ** @Author JosonLiu
 ** @Date 2016年10月8日
 ** @Version 1.0
 **/
public class CSHttpClient {
	private static final Logger log = LoggerFactory.getLogger(CSHttpClient.class);
	private final RequestConfig requestConfig ;
	private final CloseableHttpClient httpClient;
	/**
	 * 通过 工厂类 factory{@link HttpClientFactory} 实现对 HttpClient 连接池的自定义 
	 * @param factory
	 */
	public CSHttpClient(HttpClientFactory factory){
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		this.requestConfig = RequestConfig.custom()
										  .setConnectTimeout(factory.getConnectionTimeOut())
										  .setConnectionRequestTimeout(factory.getConnectionRequestTimeOunt())
										  .setSocketTimeout(factory.getSocketTimeOunt())
										  .build();
		poolingHttpClientConnectionManager.setMaxTotal(factory.getMaxTotal());
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(factory.getMaxPerRoute());
		this.httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
	}
	/**
	 * 无参构造函数 使用默认的工厂类初始化连接池
	 */
	public CSHttpClient(){
		this(HttpClientFactory.getDefaultHttpClientFactory());
	}
	/**
	 * 获取池化后的原生 HttpClient 
	 * @return
	 * 		HttpClient
	 */
	public HttpClient getHttpClient(){
		return this.httpClient;
	}
	/**
	 * 执行一个 http 方法
	 * @param httpRequestBase
	 * @return
	 * @throws HttpClientException 
	 */
	public String executeMethod(HttpRequestBase httpRequestBase) throws HttpClientException{
		CloseableHttpResponse response = null ;
		StatusLine statusLine = null ;
		String result = "";
		setDefaultRequestConfig(httpRequestBase);//设定请求参数
		log.debug("execute request:{}",HttpClientUtil.decode(httpRequestBase.getURI().toString()));
		try {
			response = httpClient.execute(httpRequestBase);
			statusLine = response.getStatusLine();
			log.debug("response status:{}",statusLine);
			HttpEntity entity = response.getEntity();
			if( statusLine.getStatusCode() == HttpStatus.SC_OK){
				result = inputStreamToString(entity.getContent());
			}else{
				throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + statusLine);
			}
		} catch (Throwable e) {
			log.error("get data from url:{} fail, status: {} ",httpRequestBase.getURI(), statusLine,e);
			throw new HttpClientException("get data from url:"+ httpRequestBase.getURI() + " fail, status: " + statusLine,e);
		}finally{//释放资源
			if(response != null ){
				try {
					response.close();
				} catch (IOException e) {
					log.error("response close IOException:{}",httpRequestBase.getURI(), e);
				}
			}
			if(httpRequestBase != null ){
				httpRequestBase.releaseConnection();
			}
		}
		return result;
	}
    private void setDefaultRequestConfig(HttpRequestBase requestBase){
        RequestConfig config = requestBase.getConfig();
        if(config == null){
            requestBase.setConfig(requestConfig);	 
    		return;
        }
    	Builder builder = RequestConfig.custom();
    	if(config.getConnectionRequestTimeout() == -1){
    		builder.setConnectionRequestTimeout(requestConfig.getConnectionRequestTimeout());
    	}
    	if(config.getConnectTimeout() == -1){
    		builder.setConnectTimeout(requestConfig.getConnectTimeout());
    	}
    	if(config.getSocketTimeout() == -1){
    		builder.setSocketTimeout(requestConfig.getSocketTimeout());
    	}
		config = builder
				.setExpectContinueEnabled(config.isExpectContinueEnabled())
				.setStaleConnectionCheckEnabled(
						config.isStaleConnectionCheckEnabled())
				.setAuthenticationEnabled(config.isAuthenticationEnabled())
				.setRedirectsEnabled(config.isRedirectsEnabled())
				.setRelativeRedirectsAllowed(
						config.isRelativeRedirectsAllowed())
				.setCircularRedirectsAllowed(
						config.isCircularRedirectsAllowed())
				.setMaxRedirects(config.getMaxRedirects())
				.setCookieSpec(config.getCookieSpec())
				.setLocalAddress(config.getLocalAddress())
				.setProxy(config.getProxy())
				.setTargetPreferredAuthSchemes(
						config.getTargetPreferredAuthSchemes())
				.setProxyPreferredAuthSchemes(
						config.getProxyPreferredAuthSchemes()).build();
		requestBase.setConfig(config);
    }
    private String inputStreamToString(InputStream in) throws IOException{
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 256);
    	byte[] temp = new byte[1024 * 256];
    	int i=-1;
    	while((i=in.read(temp))!=-1){
    		byteArrayOutputStream.write(temp, 0, i);
    	}
    	return byteArrayOutputStream.toString();
    }
    /**
     * 执行一个HttpGet方法
     * @param url 请求地址
     * @return response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doGet(String url) throws HttpClientException  {
    	HttpGet get = new HttpGet(url);
        String result = this.executeMethod(get);
        return result;
    }
    /**
     * 执行一个HttPost请求
     * @param url 请求地址
     * @param parameters 自动参数按utf-8编码
     * @return  response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, Map<String, String> parameters) throws HttpClientException  { 
    	
    	HttpPost httpRequestBase = new HttpPost(url);
		if (parameters != null && !parameters.isEmpty()) {
			try {
				httpRequestBase.setEntity(new UrlEncodedFormEntity(
						HttpClientUtil.toNameValuePairs(parameters), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new HttpClientException(e);
			}
		}
        return this.executeMethod(httpRequestBase);
    }
    /**
     * 执行一个HttPost请求
     * @param url 请求地址
     * @param jsonStr  json字符串, 按utf-8编码
     * @return   response正确返回后的字符串
     * @throws HttpClientException
     */
    public String doPost(String url, String jsonStr) throws HttpClientException  {
    	HttpPost httpRequestBase = new HttpPost(url);
		if (jsonStr != null && !jsonStr.isEmpty()) {
			try {
				httpRequestBase.setHeader("Content-Type","application/json");
				httpRequestBase.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON));
			} catch (Throwable e) {
				throw new HttpClientException(e);
			}
		}
        return this.executeMethod(httpRequestBase);
    }
}

