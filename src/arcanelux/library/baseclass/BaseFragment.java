package arcanelux.library.baseclass;

import com.androidquery.AQuery;

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
	protected AQuery aq;
	protected String mFontFileName = "NanumBarunGothic.mp3";
	protected boolean hasCustomFontFile = false;
	private static Typeface mTypeface;

	public BaseFragment(Context context) {
		mContext = context;
		aq = new AQuery(mContext);
	}
	
	/** CustomFont를 적용할 때 사용할 생성자 **/
	public BaseFragment(Context context, String fontFileName){
		mContext = context;
		aq = new AQuery(mContext);
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
		return inflateWithCustomFont(inflater, container, layoutRes);
	}
	
	/** Inflate시 CustomFont를 적용한 View를 리턴해주는 함수 **/ 
	protected View inflateWithCustomFont(LayoutInflater inflater, ViewGroup container, int layoutRes){
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
}
