package arcanelux.library.activity;

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
	protected boolean D = false;
	protected String packageName;
	protected int displayWidth, displayHeight;
	
	/** 커스텀 폰트를 사용하려면, BaseActionBarActivity를 상속받은 클래스에서 onCreate의 super()함수를 호출하기 전에 setCustomFont를 true로 바꾸어주어야 함 */
	protected boolean setCustomFont = false;
	protected static Typeface mTypeface;
	protected String fontFileName = "NanumBarunGothic.mp3";
	protected String idFlurry;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContext = this;
		setDisplaySize();
		setDeviceId();
		if (BaseActionBarActivity.mTypeface == null && setCustomFont)
			BaseActionBarActivity.mTypeface = Typeface.createFromAsset(getAssets(), fontFileName);
		idFlurry = getResources().getString(R.string.id_flurry);
		mActionBar = this.getSupportActionBar();
		packageName = getPackageName();
		
		if(D){
			Log.d(TAG, "PackageName : " + packageName);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
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

	// Font
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
