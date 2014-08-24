package arcanelux.library.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseData {
	protected final String JSON_STATUS = "json_status";
	protected final int JSON_SUCCESS = 837293;
	protected final int JSON_FAILED = 837294;
	protected final int JSON_BLANK = 837295;
	protected JSONObject mJsonObject;

	public BaseData(JSONObject jsonObject){
		mJsonObject = jsonObject;
		try {
			mJsonObject.put(JSON_STATUS, JSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mJsonObject = new JSONObject();
			try {
				mJsonObject.put(JSON_STATUS, JSON_FAILED);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	public BaseData(){
		mJsonObject = new JSONObject();
		try {
			mJsonObject.put(JSON_STATUS, JSON_BLANK);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 자신의 생성자에 전달된 mJsonObject를 리턴해준다
	 * @return JSONObject mJsonObject
	 */
	public JSONObject getSelfJsonObject(){
		return mJsonObject;
	}
	/**
	 * 자신의 생성자에 전달된 mJsonObject의 String변환 값을 리턴해준다
	 * @return String strJsonObject
	 */
	public String getSelfStrJsonObject(){
		return mJsonObject.toString();
	}


	/**
	 * 생성자를 이용해서 생성한 Data에 정상적인 JSONObject가 전달되었는지를 리턴해준다
	 */
	public boolean isConvertJsonSuccess(){
		if(getBoolean(JSON_STATUS)){
			return true;
		} else{
			return false;
		}
	}
	protected int getInt(String key){
		try{
			return mJsonObject.getInt(key);
		} catch (JSONException e){
			e.printStackTrace();
			return 0;
		}
	}
	protected String getString(String key){
		try {
			return mJsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	protected boolean getBoolean(String key){
		try{
			return mJsonObject.getBoolean(key);
		} catch (JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	protected double getDouble(String key){
		try{
			return mJsonObject.getDouble(key);
		} catch (JSONException e){
			e.printStackTrace();
			return 0.0f;
		}
	}
	protected long getLong(String key){
		try {
			return mJsonObject.getLong(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	


	protected Calendar getCalendarFromString(String strDate, String strFormat){
		SimpleDateFormat format = new SimpleDateFormat(strFormat);
		Date date = null;
		Calendar calendar;
		try {
			date = format.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar;
	}

	/**
	 * mJsonObject에서 key로 JSONObject꺼내오기
	 * 실패시 getFailedJsonObject()  실행
	 */
	protected JSONObject getJsonObject(String key){
		try {
			JSONObject jsonObject = mJsonObject.getJSONObject(key);
			jsonObject.put(JSON_STATUS, JSON_SUCCESS);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return getFailedJsonObject();
		}
	}
	/**
	 * getJsonObject로 JSONObject 꺼내올 때, 내용이 없을 경우 돌려줄 실패메시지 넣은 JSONObject리턴
	 */
	private JSONObject getFailedJsonObject(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(JSON_STATUS, JSON_FAILED);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * mJsonObject에서 key로 JSONArray꺼내오기
	 * 실패시 getBlankJsonArray()  실행
	 */
	protected JSONArray getJsonArray(String key){
		try {
			return mJsonObject.getJSONArray(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return getBlankJsonArray();
		}
	}

	/**
	 * 빈 JSONArray 리턴
	 */
	private JSONArray getBlankJsonArray(){
		return new JSONArray();
	}

	/**
	 * String, Integer, Double, Long값을 mJsonObject 에 삽입
	 */
	public boolean putObject(String key, Object obj){
		try {
			mJsonObject.put(JSON_STATUS, JSON_SUCCESS);
			if(obj.getClass().equals(String.class)){
				mJsonObject.put(key, (String) obj);
				return true;
			} 
			else if(obj.getClass().equals(Integer.class)){
				mJsonObject.put(key, (Integer) obj);
				return true;
			}
			else if(obj.getClass().equals(Double.class)){
				mJsonObject.put(key, (Double) obj);
				return true;
			}
			else if(obj.getClass().equals(Long.class)){
				mJsonObject.put(key, (Long) obj);
				return true;
			}
			else {
				mJsonObject.put(JSON_STATUS, JSON_FAILED);
				return false;
			}
		} catch (JSONException e) {
			try {
				mJsonObject.put(JSON_STATUS, JSON_FAILED);
			} catch (JSONException e1) {
				e1.printStackTrace();
				return false;
			}
			e.printStackTrace();
			return false;
		}
	}
}
