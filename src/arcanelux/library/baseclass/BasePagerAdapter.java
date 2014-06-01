package arcanelux.library.baseclass;

import java.util.ArrayList;

import com.androidquery.AQuery;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BasePagerAdapter<T> extends PagerAdapter {
	private final String TAG = this.getClass().getSimpleName();
	protected Context mContext;
	protected AQuery aq;
	private String mFontFileName = "NanumBarunGothic.mp3";
	private boolean hasCustomFontFile = false;
	private static Typeface mTypeface;
	protected ArrayList<T> mPagerItemList;
	
	public BasePagerAdapter(Context context, ArrayList<T> objects) {
		mContext = context;
		aq = new AQuery(mContext);
		hasCustomFontFile = false;
		mPagerItemList = objects;
	}
	
	public BasePagerAdapter(Context context, ArrayList<T> objects, String fontFileName) {
		mContext = context;
		aq = new AQuery(mContext);
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
		mPagerItemList = objects;
	}
	
	@Override public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	@Override public void destroyItem(View pager, int position, Object view) {
		((ViewPager)pager).removeView((View)view);
	}
	@Override public int getCount() { return mPagerItemList.size(); }
	@Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
	
	
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
			if (BasePagerAdapter.mTypeface == null)
				BasePagerAdapter.mTypeface = Typeface.createFromAsset(mContext.getAssets(), mFontFileName);
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
