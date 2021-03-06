package com.ajaya.cashloan.core.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.ajaya.cashloan.core.common.context.Constant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by lsk on 2015/12/16.
 */
public class HttpUtil {
	
	private static final Logger logger = Logger.getLogger(HttpUtil.class);
	
	/**
	 * 默认的编码格式
	 */
	private static final String CHARSET = "UTF-8";
	
	/**
	 * 默认的超时时间 60 S
	 */
	private static final int TIMEOUT = 60000;

	private static Scanner scanner;

    public static String doGet(String url){
        return send(url,null,false,"utf8");
    }
    

    public static String postClient(String clientURL, Map<String, String> params) {
		String resp = postClient(clientURL, params, CHARSET, TIMEOUT);
		return resp;
	}
    
    /**
	 * 根据请求参数生成List<BasicNameValuePair>
	 * @param params
	 * @return
	 */
	private static List<BasicNameValuePair> wrapParam(Map<String, String> params) {
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		for (Entry<String, String> entry : params.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    data.add(new BasicNameValuePair(key, value));
		}
        return data;
	}
	
	/**
	 * 发送http请求
	 * @param clientURL
	 * @param params
	 * @return 
	 */
	public static String postClient(String clientURL, Map<String, String> params, String charset, int timeout){
		HttpPost post = new HttpPost(clientURL);
		CloseableHttpClient client = HttpClients.createDefault();
		String result = "";
		try {
			//参数封装
			List<BasicNameValuePair> paramsList = wrapParam(params);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramsList, charset);
			
			//设置请求和传输时长
			Builder builder = RequestConfig.custom();

			builder.setSocketTimeout(timeout);
			builder.setConnectTimeout(timeout);
			
			RequestConfig config = builder.build();
			
			post.setEntity(entity);
			post.setConfig(config);
			//发起请求
			CloseableHttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity, charset);
			}
		} catch (SocketTimeoutException e) {
			result = initResult(Constant.TIMEOUT_CODE_VALUE, "请求超时");
			logger.info("请求发送失败，SocketTimeoutException原因：", e);
		} catch (ClientProtocolException e) {
			result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，ClientProtocolException");
			logger.info("请求发送失败，ClientProtocolException原因：", e);
		} catch (UnsupportedEncodingException e) {
			result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，UnsupportedEncodingException");
			logger.info("请求发送失败，UnsupportedEncodingException原因：", e);
		} catch (IOException e) {
			result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常，IOException");
			logger.info("请求发送失败，IOException原因：", e);
		} finally {
			try {
				client.close();
				post.releaseConnection();
			} catch (IOException e) {
				result = initResult(Constant.CLIENT_EXCEPTION_CODE_VALUE, "请求异常");
				logger.info(e.toString(), e);
			}
		}
		return result;
	}
	
	/**
	 * 初始化返回信息，用于异常时
	 */
	public static String initResult(int code, String msg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constant.RESPONSE_CODE, code);
		data.put(Constant.RESPONSE_CODE_MSG, msg);
		return JSONObject.toJSONString(data);
	}
	
    
    public static String doPost(String url, Map<String, String> params){
        return send(url,params,true,"UTF-8");
    }
    
    public static String send(String url, Map<String, String> params,boolean post, String readEncode){
        InputStream input=null;
        try {
            URL realUrl=new URL(url);
            URLConnection connection=realUrl.openConnection();
            connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding","gzip,deflate,sdch");
            connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36 SE 2.X MetaSr 1.0");
            /*connection.setRequestProperty("Cookie","JSESSIONID=32754410A9908881317F32FE3FA84CB3; j_username=; j_password=");
            connection.setRequestProperty("Cache-Control","max-age=0");*/

            if(post){
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                ((HttpURLConnection)connection).setRequestMethod("POST");

				if (params != null && !params.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (Entry<String, String> entry : params.entrySet()) {
						String key = entry.getKey();
						String value = entry.getValue();
						sb.append(key+"="+URLEncoder.encode(value,"UTF-8")+"&");
					}
                    sb.deleteCharAt(sb.length()-1);
                    OutputStream out=connection.getOutputStream();
                    out.write(sb.toString().getBytes("UTF-8"));
                }
            }

            input = connection.getInputStream();
			scanner = new Scanner(input, readEncode);
			scanner.useDelimiter("$");
			return scanner.next();
        } catch (Exception e) {
        	 logger.error(e.getMessage(), e);
        } finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    
	/**
  	 * 发起http请求，获取响应结果
  	 * 
  	 * @param pageURL
  	 * @return
  	 */
  	public static String getHttpResponse(String targetURL) {
  		String result = "";
  		BufferedReader in = null;
  		InputStreamReader isr = null;
  		InputStream is = null;
  		HttpURLConnection huc = null;
  		try {
  			URL url = new URL(targetURL);
  			huc = (HttpURLConnection) url.openConnection();
  			is = huc.getInputStream();
  			isr = new InputStreamReader(is);
  			in = new BufferedReader(isr);
  			String line = null;
  			while (((line = in.readLine()) != null)) {
  				if (line.length() == 0)
  					continue;
  				result += line;
  			}
  		} catch (Exception e) {
  			logger.error(e.getMessage(), e);
  		} finally {
  			try {
  				if (is != null)
  					is.close();
  				if (isr != null)
  					isr.close();
  				if (in != null)
  					in.close();
  				if (huc != null)
  					huc.disconnect();
  			} catch (Exception e) {
  				logger.error(e.getMessage(), e);
  			}
  		}
  		return result;
  	}
  	
  	/**
     * 发送post请求
     * @param clientURL
     * @param jsonParam
     * @return
     */
    public static JSONObject postClient(String urlString ,String jsonObject) {
        JSONObject returnJson = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);            
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");//**注意点1**，需要此格式，后边这个字符集可以不设置
            connection.connect();
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonObject.toString().getBytes("UTF-8"));//**注意点2**，需要此格式
            out.flush();
            out.close();
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));//**注意点3**，需要此格式
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            returnJson = JSONObject.parseObject(sb.toString());
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
        	logger.error(e.getMessage());
        } catch (IOException e) {
        	logger.error(e.getMessage());
        }
        return returnJson;
    }
    
    /**
	 * get
	 * 
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doGet(String host, String path, String method, Map<String, String> headers,
			Map<String, String> querys) throws Exception {
		HttpClient httpClient = wrapClient(host);

		HttpGet request = new HttpGet(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		return httpClient.execute(request);
	}
	
	private static HttpClient wrapClient(String host) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (host.startsWith("https://")) {
			sslClient(httpClient);
		}

		return httpClient;
	}
	
	private static String buildUrl(String host, String path, Map<String, String> querys)
			throws UnsupportedEncodingException {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(host);
		if (!StringUtil.isEmpty(path)) {
			sbUrl.append(path);
		}
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StringUtil.isEmpty(query.getKey()) && !StringUtil.isEmpty(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StringUtil.isEmpty(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StringUtil.isEmpty(query.getValue())) {
						sbQuery.append("=");
						sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}
	
	private static void sslClient(HttpClient httpClient) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] xcs, String str) {

				}

				public void checkServerTrusted(X509Certificate[] xcs, String str) {

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry registry = ccm.getSchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
		} catch (KeyManagementException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

}
