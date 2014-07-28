package arcanelux.library.common;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BasePref {
	private final static String TAG = "BasePref";
	protected final static String SAVED_VALUE = "savedValue";
	protected final static String JSONOBJECT = "jsonObject";
	protected final static String DATA_LOGIN = "data";
	protected final static String CACHE = "cache";

	// Display Width, Height 저장
	public static void setDisplaySize(Context context, int width, int height){
		setObject(context, SAVED_VALUE, "width", width);
		setObject(context, SAVED_VALUE, "height", height);
	}

	// Display Width 불러오기
	public static int getDisplayWidth(Context context){
		return getInteger(context, SAVED_VALUE, "width");
	}
	
	// Display Height 불러오기
	public static int getDisplayHeight(Context context){
		return getInteger(context, SAVED_VALUE, "height");
	}

	// Density 저장
	public static void setDensity(Context context){
		float density  = context.getResources().getDisplayMetrics().density;
		setObject(context, SAVED_VALUE, "density", density);
	}

	// Density 불러오기
	public static float getDensity(Context context){
		return getFloat(context, SAVED_VALUE, "density");
	}

	// Device ID 저장
	public static void setDeviceId(Context context){
		String deviceId="";
		TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		setObject(context, SAVED_VALUE, "deviceId", deviceId);
	}

	// Device ID 불러오기
	public static String getDeviceId(Context context){
		return getString(context, SAVED_VALUE, "deviceId");
	}

	// JSONObject를 String 형태로 저장
	public static void setJsonObject(Context context, String name, JSONObject jsonObject){
		String strJsonObject = jsonObject.toString();
		setObject(context, JSONOBJECT, name, strJsonObject);
	}

	// String으로 넘어온 JSONObject를 저장
	public static void setJsonObjectString(Context context, String name, String str){
		setObject(context, JSONOBJECT, name, str);
	}

	// String 형태로 저장되어있는 JsonObject를 JsonObject객체로 생성하여 리턴
	public static JSONObject getJsonObject(Context context, String name){
		String strJSONObject = getString(context, JSONOBJECT, name);
		try {
			return new JSONObject(strJSONObject);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	// String 형태로 저장되어있는 JsonObject를 String그대로 리턴
	public static String getJsonObjectString(Context context, String name){
		return getString(context, JSONOBJECT, name);
	}
	
	/**
	 * Pref에 저장된 값 삭제
	 * @param prefName
	 * @param context
	 */
	public static void clearPref(String prefName, Context context){
		SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	/** 로그인 관련 **/
	/** ID 저장 **/
	public static void setId(Context context, String id){
		setObject(context, DATA_LOGIN, "id", id);
	}
	/** ID 불러오기 **/
	public static String getId(Context context){
		return getString(context, DATA_LOGIN, "id");
	}
	/** Password 저장 **/
	public static void setPassword(Context context, String pw){
		setObject(context, DATA_LOGIN, "pw", pw);
	}
	/** Password 가져오기 **/
	public static String getPassword(Context context){
		return getString(context, DATA_LOGIN, "pw");
	}
	/** Email 저장 **/
	public static void setEmail(Context context, String email){
		setObject(context, DATA_LOGIN, "email", email);
	}
	/** Email 가져오기 **/
	public static String getEmail(Context context){
		return getString(context, DATA_LOGIN, "email");
	}
	/** 자동로그인 여부 저장 **/
	public static void setAutoLogin(Context context, boolean value){
		setObject(context, DATA_LOGIN, "autoLogin", value);
	}
	/** 자동로그인 여부 가져오기 **/
	public static boolean isAutoLogin(Context context){
		return getBoolean(context, DATA_LOGIN, "autoLogin");
	}
	
	/**
	 * Pref에 값 삽입, String, Integer, Boolean, Float, Long데이터 형 가능
	 */
	protected static boolean setObject(Context context, String prefName, String key, Object obj){
		SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(obj.getClass().equals(String.class)){
			editor.putString(key, (String) obj);
			editor.commit();
			return true;
		} else if(obj.getClass().equals(Integer.class)){
			editor.putInt(key, (Integer) obj);
			editor.commit();
			return true;
		} else if(obj.getClass().equals(Boolean.class)){
			editor.putBoolean(key, (Boolean) obj);
			editor.commit();
			return true;
		} else if(obj.getClass().equals(Long.class)){
			editor.putLong(key, (Long) obj);
			editor.commit();
			return true;
		} else if(obj.getClass().equals(Float.class)){
			editor.putFloat(key, (Float) obj);
			editor.commit();
			return true;
		} else{
			return false;
		}
	}
	
	private static Object getObject(Context context, String prefName, String key){
		SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		Map<String, ?> map = pref.getAll();
		Object obj = map.get(key);
		return obj;
	}
	
	protected static String getString(Context context, String prefName, String key){
		return (String) getObject(context, prefName, key);
	}
	protected static Integer getInteger(Context context, String prefName, String key){
		return (Integer) getObject(context, prefName, key);
	}
	protected static Boolean getBoolean(Context context, String prefName, String key){
		return (Boolean) getObject(context, prefName, key);
	}
	protected static Float getFloat(Context context, String prefName, String key){
		return (Float) getObject(context, prefName, key);
	}
	protected static Long getLong(Context context, String prefName, String key){
		return (Long) getObject(context, prefName, key);
	}
}
