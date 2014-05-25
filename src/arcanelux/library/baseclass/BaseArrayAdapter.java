package arcanelux.library.baseclass;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BaseArrayAdapter<T> extends ArrayAdapter<T>{
	protected final String TAG = this.getClass().getName();
	protected Context mContext;
	protected int layoutResource;
	private String mFontFileName = "NanumBarunGothic.mp3";
	private boolean hasCustomFontFile = false;
	private static Typeface mTypeface;

	public BaseArrayAdapter(Context context, int resource, ArrayList<T> objects) {
		super(context, resource, objects);
		mContext = context;
		layoutResource = resource;
		hasCustomFontFile = false;
	}

	public BaseArrayAdapter(Context context, int resource, ArrayList<T> objects, String fontFileName){
		super(context, resource, objects);
		mContext = context;
		mFontFileName = fontFileName;
		layoutResource = resource;
		hasCustomFontFile = true;
	}
	
	/** 일반생성자로 생성한 경우, 이 함수를 이용해서 CustomFont 지정 **/
	protected void setCustomFont(String fontFileName){
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
	}
	
	/** Inflate시 CustomFont를 적용한 View를 리턴해주는 함수 **/
	protected View inflateWithCustomFont(LayoutInflater inflater, int layoutRes){
		return inflateWithCustomFont(inflater, null, layoutRes);
	}
	protected View inflateWithCustomFont(LayoutInflater inflater, ViewGroup container, int layoutRes){
		View view = inflater.inflate(layoutRes, container, false);
		if(!hasCustomFontFile){
			Log.e(TAG, "FontFileName Missing!");
		} else{
			if (BaseArrayAdapter.mTypeface == null)
				BaseArrayAdapter.mTypeface = Typeface.createFromAsset(mContext.getAssets(), mFontFileName);
			setGlobalFont((ViewGroup) view);
		}
		return view;
	}

	void setGlobalFont(ViewGroup root) {
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
