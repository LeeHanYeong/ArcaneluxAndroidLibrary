package arcanelux.library.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;
import arcanelux.library.common.MySSLSocketFactory;

/**
 * 쉽게 통신할 수 있는 클래스
 */
public class ArcaneluxHttpClient {
	private final String TAG = this.getClass().getSimpleName();
	private boolean D = true;

	// Static Value
	public static final String METHOD_POST = "post";
	public static final String METHOD_GET = "get";

	// Setting Value
	private HttpClient mHttpClient;
	private String curMethod = METHOD_POST;
	private int curConnectionTimeout = 5000;
	private String curUrl;

	// MultipartEntityBuilder
	private MultipartEntityBuilder mBuilder;
	private String curCharset = "UTF-8";
	private HashMap<String, String> mapHeader;

	// Result
	private boolean resultSuccess = true;


	/**
	 * 생성자, HttpMethod를 결정
	 */
	public ArcaneluxHttpClient(String url, String method){
		mHttpClient = getHttpClient();
		method = method.toLowerCase();
		if(method.equals("post")){
			curMethod = METHOD_POST;
		} else if(method.equals("get")){
			curMethod = METHOD_GET;
		}
		curUrl = url;
		mapHeader = new HashMap<String, String>();
	}

	public void setDebugMode(boolean value){
		D = value;
	}
	public void setConnectionTimeout(int milliseconds){
		curConnectionTimeout = milliseconds;
	}
	public void setCharset(String charset){
		curCharset = charset;
	}
	public void putValue(String key, String value){
		mBuilder.addTextBody(key, value, ContentType.create("text/plain", "utf-8"));
	}
	public void putFile(String key, String filePath){
		FileBody bin = new FileBody(new File(filePath));
		mBuilder.addPart(key, bin);
	}
	public void putValueSet(HashMap<String, String> valuePair){
		Set<String> keySet = valuePair.keySet();
		Iterator<String> iterator = keySet.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String value = valuePair.get(key);
			putValue(key, value);
		}
	}
	public void putFileSet(HashMap<String, String> filePair){
		Set<String> fileKeySet = filePair.keySet();
		Iterator<String> fileIterator = fileKeySet.iterator();
		while(fileIterator.hasNext()){
			String key = fileIterator.next();
			String value = filePair.get(key);
			putFile(key, value);
		}
	}
	public void addHeader(String name, String value){
		mapHeader.put(name, value);
	}
	public boolean isResultSuccess(){
		return resultSuccess;
	}
	public String execute(){
		InputStream is = null;
		String result = "";
		resultSuccess = true;

		// HttpParam에 응답시간 5초 넘을 시 timeout
		HttpParams params = mHttpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, curConnectionTimeout);
		HttpConnectionParams.setSoTimeout(params, curConnectionTimeout);

		// Builder
		mBuilder = MultipartEntityBuilder.create();
		Charset chars = Charset.forName(curCharset);
		mBuilder.setCharset(chars);

		// Post, Get방식에 따라 다르게 작동
		if(curMethod.equals(METHOD_POST)){
			HttpPost httpPost = new HttpPost(curUrl);
			// Header 설정
			
			// HttpEntity 생성, HttpPost setEntity
			HttpEntity reqEntity = mBuilder.build();
			httpPost.setEntity(reqEntity);

			// 요청 및 결과값 리턴
			try {
				HttpResponse response = mHttpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultSuccess = false;
			} catch (IOException e) {
				e.printStackTrace();
				resultSuccess = false;
			}
		}
		else if(curMethod.equals(METHOD_GET)){
			HttpGet httpPost = new HttpGet(curUrl);

			// 요청 및 결과값 리턴
			try {
				HttpResponse response = mHttpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultSuccess = false;
			} catch (IOException e) {
				e.printStackTrace();
				resultSuccess = false;
			}
		}

		// 결과값 String으로 변환
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
			if(D) Log.d(TAG, result);
		}catch(Exception e){
			if(D) Log.e(TAG, "Error converting result "+e.toString());
			resultSuccess = false;
		}
		
		return result;
	}



	/**
	 * https요청 가능한 HttpClient생성 후 리턴
	 */
	private static DefaultHttpClient getHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			//	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			//	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);

		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}
