package arcanelux.library.baseclass;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class BaseDialog extends Dialog {
	protected final String TAG = this.getClass().getSimpleName();
	protected Context mContext;
	protected boolean D = true;
	
	/** 커스텀 폰트를 사용하려면, BaseDialog를 상속받은 클래스에서 onCreate의 super()함수를 호출하기 전에 setCustomFont함수 호출 */
	protected LayoutInflater mInflater;
	protected static Typeface mTypeface;
	private boolean hasCustomFontFile = false;
	private String mFontFileName = "NanumBarunGothic.mp3";

	/** Constructor **/
	public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}
	public BaseDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}
	public BaseDialog(Context context, String fontFileName) {
		super(context);
		init(context);
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
	}
	private void init(Context context){
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	}
	
	/** SetContentView **/
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (BaseDialog.mTypeface == null && hasCustomFontFile)
			BaseDialog.mTypeface = Typeface.createFromAsset(mContext.getAssets(), mFontFileName);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setContentView에 전달된 layout으로 view생성
		View view = inflater.inflate(layoutResID, null);
		setGlobalFont((ViewGroup)view);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		super.setContentView(view);
	}
	@Override
	public void setContentView(View view) {
		setGlobalFont((ViewGroup)view);
		super.setContentView(view);
	}
	@Override
	public void setContentView(View view, LayoutParams params) {
		setGlobalFont((ViewGroup)view);
		super.setContentView(view, params);
	}

	/** CustomFont를 사용하려면 setContentView 이전에 호출 */
	protected void useCustomFont(String fontFileName){
		hasCustomFontFile = true;
		mFontFileName = fontFileName;
		if (BaseDialog.mTypeface == null && hasCustomFontFile)
			BaseDialog.mTypeface = Typeface.createFromAsset(mContext.getAssets(), mFontFileName);
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
		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), fontFileName);
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
	
}
