package arcanelux.library.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import arcanelux.library.baseclass.BaseAsyncTask;
import arcanelux.library.common.MySSLSocketFactory;

/**
 * 쉽게 통신할 수 있는 클래스
 */
public class ArcHttpClient {
	private final String TAG = this.getClass().getSimpleName();
	private boolean D = true;

	// Static Value
	public static final String METHOD_POST = "post";
	public static final String METHOD_GET = "get";
	public static final String CONTENTTYPE_FORM = "multipart/form-data";
	public static final String CONTENTTYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

	// Setting Value
	private HttpClient mHttpClient;
	private String curMethod = METHOD_POST;
	private String curContentType = CONTENTTYPE_FORM;
	private int curConnectionTimeout = 5000;
	private String curUrl;

	// MultipartEntityBuilder
	private MultipartEntityBuilder mBuilder;
	private String curCharset = "UTF-8";

	// Header, Value, File Map
	private HashMap<String, String> mapHeader;
	private HashMap<String, String> mapValue;
	private HashMap<String, String> mapFile;

	// Result
	private boolean resultSuccess = true;

	// Custom ProgressDialog
	private ProgressDialog mCustomProgressDialog;
	private boolean useCustomProgressDialog;


	/**
	 * 생성자, HttpMethod를 결정
	 */
	public ArcHttpClient(String url, String method){
		init(url, method, CONTENTTYPE_FORM);
	}
	public ArcHttpClient(String url, String method, String contentType){
		init(url, method, contentType);
	}
	private void init(String url, String method, String contentType){
		mHttpClient = getHttpClient();
		method = method.toLowerCase();
		if(method.equals("post")){
			curMethod = METHOD_POST;
		} else if(method.equals("get")){
			curMethod = METHOD_GET;
		}

		if(contentType.equals(CONTENTTYPE_FORM)){
			curContentType = CONTENTTYPE_FORM;
		} else if(contentType.equals(CONTENTTYPE_X_WWW_FORM_URLENCODED)){
			curContentType = CONTENTTYPE_X_WWW_FORM_URLENCODED;
		}
		curUrl = url;
		mapHeader = new HashMap<String, String>();
		mapValue = new HashMap<String, String>();

		// Content Type
		if(contentType.equals(CONTENTTYPE_FORM)){
			// Builder
			mBuilder = MultipartEntityBuilder.create();
			Charset chars = Charset.forName(curCharset);
			mBuilder.setCharset(chars);			
		} else if(contentType.equals(CONTENTTYPE_X_WWW_FORM_URLENCODED)){

		}
	}

	// Custom ProgressDialog 설정
	public void setCustomProgressDialog(ProgressDialog customProgressDialog){
		mCustomProgressDialog = customProgressDialog;
		useCustomProgressDialog = true;
	}

	// HTTP 통신 설정
	public void setDebugMode(boolean value){
		D = value;
	}
	public void setConnectionTimeout(int milliseconds){
		curConnectionTimeout = milliseconds;
	}
	public void setCharset(String charset){
		curCharset = charset;
		Charset chars = Charset.forName(curCharset);
		if(mBuilder != null) mBuilder.setCharset(chars);
	}
	public void addValue(String key, String value){
		mapValue.put(key, value);
	}
	public void addFile(String key, String filePath){
		FileBody bin = new FileBody(new File(filePath));
		mBuilder.addPart(key, bin);
	}
	public void addValueSet(HashMap<String, String> valuePair){
		Set<String> keySet = valuePair.keySet();
		Iterator<String> iterator = keySet.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String value = valuePair.get(key);
			mapValue.put(key, value);
		}
	}
	public void putFileSet(HashMap<String, String> filePair){
		Set<String> fileKeySet = filePair.keySet();
		Iterator<String> fileIterator = fileKeySet.iterator();
		while(fileIterator.hasNext()){
			String key = fileIterator.next();
			String value = filePair.get(key);
			addFile(key, value);
		}
	}
	public void addHeader(String name, String value){
		mapHeader.put(name, value);
	}
	public boolean isResultSuccess(){
		return resultSuccess;
	}

	// execute부분 설정 (BaseAsyncTask의 Old, New Version추가)
	public void execute(Context context, String title, boolean showDialog, ArcHttpClientExecuteCompletedListener listener){
		executeBase(context, "잠시만 기다려주세요...", title, showDialog, listener);
	}
	public void execute(Context context, String title, String message, boolean showDialog, ArcHttpClientExecuteCompletedListener listener){
		executeBase(context, title, message, showDialog, listener);
	}
	private void executeBase(Context context, String title, String message, boolean showDialog, ArcHttpClientExecuteCompletedListener listener){
		ExecuteTask task = new ExecuteTask(context, title, message, showDialog, listener);
		if(useCustomProgressDialog) task.setCustomProgressDialog(mCustomProgressDialog);
		task.execute();
	}

	/**
	 * background에서 직접 적용할 수 있는 함수
	 * @return request의 response문자열
	 */
	public String sendRequest(){
		return executeHttpClient();
	}

	private String executeHttpClient(){
		InputStream is = null;
		String result = "";
		resultSuccess = true;

		// HttpParam에 응답시간 5초 넘을 시 timeout
		HttpParams params = mHttpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, curConnectionTimeout);
		HttpConnectionParams.setSoTimeout(params, curConnectionTimeout);

		// Post, Get방식에 따라 다르게 작동
		if(curMethod.equals(METHOD_POST)){
			HttpPost httpPost = new HttpPost(curUrl);
			// Header 설정
			Set<String> headerKeySet = mapHeader.keySet();
			Iterator<String> headerIterator = headerKeySet.iterator();
			while(headerIterator.hasNext()){
				String key = headerIterator.next();
				String value = mapHeader.get(key);
				httpPost.setHeader(key, value);
				Log.d(TAG, "HttpPost Header : (" + key + ", " + value + ")");
			}

			// HttpEntity 생성, HttpPost setEntity
			// Multipart Form data 일 때
			if(curContentType.equals(CONTENTTYPE_FORM)){
				// ValuePair key, value 세팅
				if(mapValue != null){
					Set<String> keySet = mapValue.keySet();
					Iterator<String> iterator = keySet.iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						String value = mapValue.get(key);
						mBuilder.addTextBody(key, value, ContentType.create("text/plain", "utf-8"));
					}
				}


				// FilePair key,value String 세팅
				if(mapFile != null){
					Set<String> fileKeySet = mapFile.keySet();
					Iterator<String> fileIterator = fileKeySet.iterator();
					while(fileIterator.hasNext()){
						String key = fileIterator.next();
						String value = mapFile.get(key);

						FileBody bin = new FileBody(new File(value));
						mBuilder.addPart(key, bin);
					}
				}

				HttpEntity reqEntity = mBuilder.build();
				httpPost.setEntity(reqEntity);
			}
			// x-www-form-urlencoded 일 때
			else if(curContentType.equals(CONTENTTYPE_X_WWW_FORM_URLENCODED)){
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();

				Set<String> keySet = mapValue.keySet();
				Iterator<String> iterator = keySet.iterator();
				while(iterator.hasNext()){
					String key = iterator.next();
					String value = mapValue.get(key);
					postParams.add(new BasicNameValuePair(key, value));
				}
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(postParams));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return e.toString();
				}
			}


			// 요청 및 결과값 리턴
			try {
				HttpResponse response = mHttpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultSuccess = false;
				return e.toString();
			} catch (IOException e) {
				e.printStackTrace();
				resultSuccess = false;
				return e.toString();
			}
		}
		// GET 방식
		else if(curMethod.equals(METHOD_GET)){
			// Parameter -> NameValuePair
			List<NameValuePair> getParams = new ArrayList<NameValuePair>();
			Set<String> keySet = mapValue.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				String value = mapValue.get(key);
				getParams.add(new BasicNameValuePair(key, value));
			}

			String paramString = URLEncodedUtils.format(getParams, "utf-8");
			curUrl += "?";
			curUrl += paramString;
			//			Log.d(TAG, "Get URL : " + curUrl);
			HttpGet httpGet = new HttpGet(curUrl);

			// 요청 및 결과값 리턴
			try {
				HttpResponse response = mHttpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultSuccess = false;
				return e.toString();
			} catch (IOException e) {
				e.printStackTrace();
				resultSuccess = false;
				return e.toString();
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
			return e.toString();
		}
		return result;
	}

	/**
	 * executeHttpClient함수 실행 시, 자동으로 ExecuteTask를 실행하며, 실행 시 받은 Context와 listener를 사용해서 AsyncTask작업 후 listener의 동작을 수행한다
	 */
	class ExecuteTask extends BaseAsyncTask {
		private ArcHttpClientExecuteCompletedListener listener;
		private String resultString;

		public ExecuteTask(Context context, String title, boolean showDialog, ArcHttpClientExecuteCompletedListener listener) {
			super(context, title, showDialog);
			this.listener = listener;
		}
		public ExecuteTask(Context context, String progressTitle, String progressMessage, boolean showDialog, ArcHttpClientExecuteCompletedListener listener) {
			super(context, progressTitle, progressMessage, showDialog);
			this.listener = listener;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			resultString = executeHttpClient();
			return super.doInBackground(params);
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			listener.onExecuteCompleted(resultString);
		}
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
