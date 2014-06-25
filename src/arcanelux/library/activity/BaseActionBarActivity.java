package arcanelux.library.activity;

import java.util.zip.Inflater;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import arcanelux.library.R;
import arcanelux.library.common.BasePref;

import com.flurry.android.FlurryAgent;

/**
 * onCreate - Typeface, mContext, idFlurry, mActionBar 설정
 * setContentView - setContentView에서 사용될 View에 Typeface 설정
 * 그 외
 * 		Flurry설정 (onStart, onStop)
 * 		setGlobalFont함수
 */
public class BaseActionBarActivity extends ActionBarActivity {
	protected final String TAG = this.getClass().getSimpleName();
	protected ActionBar mActionBar;
	protected Context mContext;
	protected boolean D = true;
	protected String packageName;
	protected int displayWidth, displayHeight;
	

	/** 커스텀 폰트를 사용하려면, BaseActionBarActivity를 상속받은 클래스에서 onCreate의 super()함수를 호출하기 전에 setCustomFont함수 호출 */
	protected LayoutInflater mInflater;
	protected static Typeface mTypeface;
	private boolean hasCustomFontFile = false;
	private String mFontFileName = "NanumBarunGothic.mp3";
	private String idFlurry;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setDisplaySize();
		setDeviceId();
		idFlurry = getResources().getString(R.string.id_flurry);
		mActionBar = this.getSupportActionBar();
		packageName = getPackageName();

		if(D){
			Log.d(TAG, "PackageName : " + packageName);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		if (BaseActionBarActivity.mTypeface == null && hasCustomFontFile)
			BaseActionBarActivity.mTypeface = Typeface.createFromAsset(getAssets(), mFontFileName);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setContentView에 전달된 layout으로 view생성
		View view = inflater.inflate(layoutResID, null);
		setGlobalFont((ViewGroup)view);
		super.setContentView(view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, idFlurry);
	}
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	/** CustomFont를 사용하려면 setContentView 이전에 호출 */
	protected void useCustomFont(String fontFileName){
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
		if (BaseActionBarActivity.mTypeface == null && hasCustomFontFile)
			BaseActionBarActivity.mTypeface = Typeface.createFromAsset(getAssets(), mFontFileName);
	}

	/**
	 * inflateWithCustomFont
	 * 		Activity 자체에 useCustomFont(String fontFileName)을 사용한 경우, 폰트 파일명을 지정하지 않으면 Static타입의 Typeface를 재활용해서 사용
	 * 		parameter에 fontFileName을 사용할 경우, 해당 함수가 호출될 때 Typeface를 동적생성해서 View를 반환한다 (폰트를 두 가지 이상 써야할 때 사용) 
	 */
	
	/** 
	 * Inflate시 CustomFont를 적용한 View를 리턴해주는 함수 
	 * 		Static Typeface를 사용
	 */
	protected View inflateWithCustomFont(LayoutInflater inflater, int layoutRes){
		return inflateWithCustomFont(inflater, null, layoutRes);
	}

	/** 
	 * Inflate시 CustomFont를 적용한 View를 리턴해주는 함수
	 * 		Static Typeface를 사용	
	 */
	protected View inflateWithCustomFont(LayoutInflater inflater, ViewGroup container, int layoutRes){
		View view = inflater.inflate(layoutRes, container, false);
		if(!hasCustomFontFile){
			Log.e(TAG, "FontFileName Missing!");
		} else{
			setGlobalFont((ViewGroup) view);
		}
		return view;
	}

	/** 
	 * Inflate시 CustomFont를 적용한 View를 리턴해주는 함수
	 * 		Typeface를 동적 생성	
	 */
	protected View inflateWithCustomFont(LayoutInflater inflater, int layoutRes, String fontFileName){
		return inflateWithCustomFont(inflater, null, layoutRes, fontFileName);
	}

	/** 
	 * Inflate시 CustomFont를 적용한 View를 리턴해주는 함수
	 * 		Typeface를 동적 생성	
	 */
	protected View inflateWithCustomFont(LayoutInflater inflater, ViewGroup container, int layoutRes, String fontFileName) {
		View view = inflater.inflate(layoutRes, container, false);
		Typeface typeface = Typeface.createFromAsset(getAssets(), fontFileName);
		setGlobalFont((ViewGroup) view, typeface);
		return view;
	}

	/** View와 하위 Child들에 커스텀 폰트 적용하는 함수, static으로 저장된 하나의 Typeface를 사용할 때 **/
	protected void setGlobalFont(TextView view) {
		view.setTypeface(mTypeface);
	}
	protected void setGlobalFont(ViewGroup root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);
			if (child instanceof TextView){
				((TextView)child).setTypeface(mTypeface);
			}
			else if (child instanceof ViewGroup){
				setGlobalFont((ViewGroup)child);
			}
		}
	}

	/** View와 하위 Child들에 Typeface 받아 적용하는 함수. 한 개 이상의 Typeface를 사용할 때, 동적생성해서 사용 **/
	protected void setGlobalFont(TextView view, Typeface typeface) {
		view.setTypeface(typeface);
	}
	protected void setGlobalFont(ViewGroup root, Typeface typeface) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);
			if (child instanceof TextView){
				((TextView)child).setTypeface(typeface);
			}
			else if (child instanceof ViewGroup){
				setGlobalFont((ViewGroup)child, typeface);
			}
		}
	}

	private void setDisplaySize(){
		/** 화면크기 **/
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		BasePref.setDisplaySize(mContext, displayWidth, displayHeight);
	}

	private void setDeviceId(){
		BasePref.setDeviceId(mContext);
	}
}
