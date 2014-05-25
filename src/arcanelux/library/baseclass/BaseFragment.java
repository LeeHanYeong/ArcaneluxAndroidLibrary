package arcanelux.library.baseclass;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseFragment extends Fragment{
	protected final String TAG = this.getClass().getName();
	protected Context mContext;
	protected String mFontFileName = "NanumBarunGothic.mp3";
	protected boolean hasCustomFontFile = false;
	private static Typeface mTypeface;

	public BaseFragment() {
		super();
	}
	
	/** CustomFont를 적용할 때 사용할 생성자 **/
	public BaseFragment(String fontFileName){
		super();
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
	}

	/** 일반생성자로 생성한 경우, 이 함수를 이용해서 CustomFont 지정 **/
	protected void setCustomFont(String fontFileName){
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
	}

	/** Inflate시 CustomFont를 적용한 View를 리턴해주는 함수 **/ 
	protected View inflateWithCustomFont(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutRes){
		Context context = container.getContext();
		mContext = context;
		View view = inflater.inflate(layoutRes, container, false);
		if(!hasCustomFontFile){
			Log.e(TAG, "FontFileName Missing!");
		} else{
			if (BaseFragment.mTypeface == null)
				BaseFragment.mTypeface = Typeface.createFromAsset(context.getAssets(), mFontFileName);
			setGlobalFont((ViewGroup) view);
		}
		return view;
	}

	private void setGlobalFont(ViewGroup root) {
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
}
