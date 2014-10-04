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
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

	
	public void setPagingEnabled(boolean value) {
		this.isPagingEnabled = value;
	}
	


}
