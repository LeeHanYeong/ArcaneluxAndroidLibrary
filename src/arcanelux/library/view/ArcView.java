package arcanelux.library.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ArcView {
	public static View inflateWithCustomFont(int resLayout, String fontFileName, Context context){
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontFileName);
		return inflateWithCustomFont(resLayout, typeface, context);
	}
	
	public static View inflateWithCustomFont(int resLayout, Typeface typeface, Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(resLayout, null);
		setGlobalFont(view, typeface);
		return view;
	}
	
	private static void setGlobalFont(View view, Typeface typeface) {
		for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
			View child = ((ViewGroup) view).getChildAt(i);
			if (child instanceof TextView){
				((TextView)child).setTypeface(typeface);
			}
			else if (child instanceof ViewGroup){
				setGlobalFont((ViewGroup)child, typeface);
			}
		}
	}
}
