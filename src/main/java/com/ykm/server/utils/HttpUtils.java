package com.ykm.server.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.TreeMap;

public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	public static String getIP() throws Exception {
		//http://httpbin.org/ip
		HttpResponse tokenResponse = doGet("http://httpbin.org","/ip",null,null,null);
		String tokenResponseStr = IOUtil.inputStreamToString(tokenResponse.getEntity().getContent());//access_token=899BB4E54B155BA0F924DFF7A601A30C&expires_in=7776000&refresh_token=28F8BD643EB6CFD4CDEB72B405C1022C
		JsonObject tokenRespJson = new JsonParser().parse(tokenResponseStr).getAsJsonObject();
		return  tokenRespJson.get("origin").getAsString();
	}

	/**
	 * get
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doGet(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, Object> querys)
            throws Exception {
    	HttpClient httpClient = wrapClient(host);

    	HttpGet request = new HttpGet(buildUrl(host, path, querys));

    	if( headers!=null && headers.size()>0 ){
	        for (Map.Entry<String, String> e : headers.entrySet()) {
	        	request.addHeader(e.getKey(), e.getValue());
	        }
    	}

        return httpClient.execute(request);
    }

	/**
	 * post form
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPostForm(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, Object> querys)
            throws Exception {

    	HttpClient httpClient = wrapClient(host);

    	HttpPost request = new HttpPost(buildUrl(host, path, querys));
    	if( headers!=null && headers.size()>0 ){
	        for (Map.Entry<String, String> e : headers.entrySet()) {
	        	request.addHeader(e.getKey(), e.getValue());
	        }
    	}

        /*if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }*/
		HttpResponse httpResponse =  httpClient.execute(request);
		return httpResponse;
	}

	/**
	 * Post String
	 *
	 * @param host
	 * @param path
	 * @param
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPostJson(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, Object> querys,
                                      String body, String charset)
            throws Exception {
    	HttpClient httpClient = wrapClient(host);

    	HttpPost request = new HttpPost(buildUrl(host, path, querys));
		request.addHeader("Content-Type", String.format("application/json;charset=%s", charset));

    	if(headers!=null && headers.size()>0){
	        for (Map.Entry<String, String> e : headers.entrySet()) {
	        	request.addHeader(e.getKey(), e.getValue());
	        }
    	}

        if (StringUtils.isNotBlank(body)) {
        	request.setEntity(new StringEntity(body,charset));
        }

		HttpResponse httpResponse =  httpClient.execute(request);
		return httpResponse;
    }

	/**
	 * Put String
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, Object> querys,
                                     String body)
            throws Exception {
    	HttpClient httpClient = wrapClient(host);

    	HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
        	request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }

	/**
	 * Put stream
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, Object> querys,
                                     byte[] body)
            throws Exception {
    	HttpClient httpClient = wrapClient(host);

    	HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
        	request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }

	/**
	 * Delete
	 *
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doDelete(String host, String path, String method,
                                        Map<String, String> headers,
                                        Map<String, Object> querys)
            throws Exception {
    	HttpClient httpClient = wrapClient(host);

    	HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        return httpClient.execute(request);
    }

	public static String buildUrl(String host, String path, Map<String, Object> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(host);
    	if (!StringUtils.isBlank(path)) {
    		sbUrl.append(path);
        }
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, Object> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append("&");
        		}
        		if (StringUtils.isBlank(query.getKey()) && query.getValue()!=null) {
        			sbQuery.append(query.getValue());
                }
        		if (!StringUtils.isBlank(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (query.getValue()!=null) {
        				sbQuery.append("=");
        				sbQuery.append(URLEncoder.encode(query.getValue().toString(), "utf-8"));
        			}
                }
        	}
        	if (0 < sbQuery.length()) {
        		if (sbUrl.indexOf("?") > 0) {
					sbUrl.append("&").append(sbQuery);
				} else {
					sbUrl.append("?").append(sbQuery);
				}
        	}
        }

    	return sbUrl.toString();
    }


	 /**
     * 创建 SSL连接
     * @return
     * @throws GeneralSecurityException
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                        @Override
                        public boolean verify(String arg0, SSLSession arg1) {
                            return true;
                        }

                        @Override
                        public void verify(String host, SSLSocket ssl)
                                throws IOException {
                        }

                        @Override
                        public void verify(String host, X509Certificate cert)
                                throws SSLException {
                        }

                        @Override
                        public void verify(String host, String[] cns,
                                String[] subjectAlts) throws SSLException {
                        }

                    });

            return HttpClients.custom().useSystemProperties().setSSLSocketFactory(sslsf).build();

        } catch (GeneralSecurityException e) {
            throw e;
        }
    }


	private static HttpClient wrapClient(String host) throws GeneralSecurityException {
		HttpClient httpClient = HttpClients.createSystem();
		if (host.startsWith("https://")) {
			return createSSLInsecureClient();
		}
		return httpClient;
	}
	/**
	 * 解析url参数
	 * @param queryString
	 * @return
	 */
	public static Map<String,String>  paramsToMap(String queryString){
		TreeMap<String,String>  rtn = new TreeMap<String,String>();
		if(StringUtils.isEmpty(queryString)) return rtn;
		String[] queryStrTmp =  queryString.split("\\?");
		if(queryStrTmp != null && queryStrTmp.length ==2 ){
			queryString = queryStrTmp[1];
		}
		String[] paramTmp = queryString.split("&");
		if( paramTmp == null && paramTmp.length ==0 ){
			return rtn;
		}
		for(String tmp : paramTmp){
			String[] keyVals = tmp.split("=");
			if( keyVals==null || keyVals.length!=2 ) continue;
			rtn.put(keyVals[0],keyVals[1]);
		}
		return rtn;
	}

	/**
	 * post
	 * application/json
	 * @param url
	 * @param jsonBody
	 * @return
	 */
	public static String doApplicationJsonPost(String url, String jsonBody){
		return doApplicationJsonPost(url, jsonBody, true);
	}

	/**
	 * post
	 * application/json
	 * @param url
	 * @param jsonBody
	 * @param useURLEncoder
	 * @return
	 */
	public static String doApplicationJsonPost(String url, String jsonBody, boolean useURLEncoder){
		try{
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonBody).getAsJsonObject();
			HttpClient httpClient =  HttpClients.createDefault();
			if(url.startsWith("https")){
				httpClient = createSSLInsecureClient();
			}

			HttpPost request = new HttpPost(url);

			request.addHeader("Content-Type", "application/json;charset=utf-8");

			logger.info("Post json: {}", json.toString());

			String jsonStr = useURLEncoder ? URLEncoder.encode(json.toString(), "utf-8") : json.toString();
			request.setEntity(new StringEntity(jsonStr,"utf-8"));

			HttpResponse response =  httpClient.execute(request);

			String responseStr = IOUtil.inputStreamToString(response.getEntity().getContent());


			return responseStr;
		}catch(Exception e){
			logger.error(e.getMessage(), "url==>"+url+"jsonBody===>"+jsonBody);

		}
		return "";
	}

	public static String doApplicationJsonAndAccessTokenPost(String url, String jsonBody, boolean useURLEncoder,String accessToken){
		try{
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonBody).getAsJsonObject();
			HttpClient httpClient =  HttpClients.createDefault();
			if(url.startsWith("https")){
				httpClient = createSSLInsecureClient();
			}

			HttpPost request = new HttpPost(url);

			request.addHeader("Content-Type", "application/json;charset=utf-8");
			request.addHeader("Access-Token", accessToken);
			logger.info("Post json: {}", json.toString());

			String jsonStr = useURLEncoder ? URLEncoder.encode(json.toString(), "utf-8") : json.toString();
			request.setEntity(new StringEntity(jsonStr,"utf-8"));

			HttpResponse response =  httpClient.execute(request);

			String responseStr = IOUtil.inputStreamToString(response.getEntity().getContent());


			return responseStr;
		}catch(Exception e){
			logger.error(e.getMessage(), "url==>"+url+"jsonBody===>"+jsonBody);

		}
		return "";
	}

	/**
	 * 获取url对应的域名
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		String result = "";
		int j = 0,endIndex = 0;
		for (int i = 0; i < url.length(); i++) {
			if (url.charAt(i) == '/') {
				j++;
				if (j == 3){
					endIndex = i;
					break;
				}
			}
		}
		result = url.substring(0,endIndex);
		return result;
	}

}
