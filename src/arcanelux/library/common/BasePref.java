package arcanelux.library.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BasePref {
	private final static String TAG = "BasePref";
	// Display Width, Height 저장
	public static void setDisplaySize(Context context, int width, int height){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("width", width);
		editor.putInt("height", height);
		editor.commit();
	}

	// Display Width 불러오기
	public static int getDisplayWidth(Context context){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		int value = pref.getInt("width", 0);
		return value;
	}
	
	// Display Width 불러오기
		public static int getDisplayHeight(Context context){
			SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
			int value = pref.getInt("height", 0);
			return value;
		}

	// Density 저장
	public static void setDensity(Context context){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		float density  = context.getResources().getDisplayMetrics().density;
		editor.putFloat("density", density);
		editor.commit();
	}

	// Density 불러오기
	public static float getDensity(Context context){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		float density = pref.getFloat("density", 0.0f);
		return density;
	}
	
	// Device ID 저장
	public static void setDeviceId(Context context){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String deviceId = getCurrentDeviceId(context);
		editor.putString("deviceId", deviceId);
		editor.commit();
		Log.d(TAG, "Device ID : " + deviceId);
	}

	// Device ID 불러오기
	public static String getDeviceId(Context context){
		SharedPreferences pref = context.getSharedPreferences("savedValue", Context.MODE_PRIVATE);
		String deviceId = pref.getString("deviceId", "");
		return deviceId;
	}
	
	
	// Device ID 얻기
	private static String getCurrentDeviceId(Context context){
		String deviceId="";
			
		TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		
		return deviceId;
	}

}
