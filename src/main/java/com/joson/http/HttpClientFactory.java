package com.joson.http;
/***HttpClient 工厂类 ，通过该类实现对 HttpClient 连接池的常用参数自定义 ，可设置的参数有：总连接数、单个路由的最大连接数、连接超时时间、SOCKET超时时间、请求超时时间等 .
 ** @Author JosonLiu
 ** @Date 2016年10月8日
 ** @Version 1.0
 **/
public class HttpClientFactory {
	private int maxTotal = 30 ;//最大连接数
	private int maxPerRoute = 10 ; //每个给定路由的最大连接数
	private int connectionTimeOut = 3000 ;// 连接超时时间
	private int socketTimeOunt = 3000;// SOCKET 超时时间
	private int connectionRequestTimeOunt = 3000; //请求超时 时间
	/**
	 * 获取默认的HttpClient配置 <br/>
	 * 最大连接数：30 <br/>
	 * 最大路由数：10 <br/>
	 * 连接超时时间：3 秒 <br/>
	 * SOCKET超时时间：3 秒 <br/>
	 * 请求超时时间：3 秒
	 * @return HttpClientFactory对象
	 */
	public static HttpClientFactory getDefaultHttpClientFactory(){
		return new HttpClientFactory();
	}
	/**
	 * 默认为 30
	 * @return
	 */
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	/**
	 * 默认为 3 秒
	 * @return
	 */
	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}
	/**
	 * 默认为 10
	 * @return
	 */
	public int getMaxPerRoute() {
		return maxPerRoute;
	}
	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}
	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}
	/**
	 * 默认为 3 秒
	 * @return
	 */
	public int getSocketTimeOunt() {
		return socketTimeOunt;
	}
	@Override
	public String toString() {
		return "HttpClientFactory [maxTotal=" + maxTotal + ", maxPerRoute="
				+ maxPerRoute + ", connectionTimeOut=" + connectionTimeOut
				+ ", socketTimeOunt=" + socketTimeOunt
				+ ", connectionRequestTimeOunt=" + connectionRequestTimeOunt
				+ "]";
	}
	public void setSocketTimeOunt(int socketTimeOunt) {
		this.socketTimeOunt = socketTimeOunt;
	}
	/**
	 * 默认为 3 秒
	 * @return
	 */
	public int getConnectionRequestTimeOunt() {
		return connectionRequestTimeOunt;
	}
	public void setConnectionRequestTimeOunt(int connectionRequestTimeOunt) {
		this.connectionRequestTimeOunt = connectionRequestTimeOunt;
	}
	public static void main(String[] args) {
		System.out.println(HttpClientFactory.getDefaultHttpClientFactory());
	}
}

