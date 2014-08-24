package arcanelux.library.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BaseViewPager extends ViewPager {
	private boolean isPagingEnabled;

	public BaseViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public BaseViewPager(Context context) {
		super(context);
		init();
	}

	private void init(){
		this.isPagingEnabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isPagingEnabled){
			return super.onTouchEvent(event);	
		}
		return false;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if(isPagingEnabled){
			return super.onInterceptTouchEvent(event);	
		}
		return false;
	}
	
	public void setPagingEnabled(boolean value) {
		this.isPagingEnabled = value;
	}
	


}
