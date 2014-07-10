package arcanelux.library.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import arcanelux.library.R;

public class ArcRoundedImageView extends ImageView {
	private static int strokeColor = android.R.color.black;
	private static float strokeWidth = 4.0f;
	private static float padding = 0.0f;

	public ArcRoundedImageView(Context context) {
		super(context);
	}

	public ArcRoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ArcRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs){
		 TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcRoundedImageView);
		 strokeColor = ta.getColor(R.styleable.ArcRoundedImageView_circleStrokeColor, android.R.color.black);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) return;
		if (getWidth() == 0 || getHeight() == 0) return;

		padding = (float)getWidth() / 40.0f;

		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int viewLength = getWidth();

		// 바깥쪽 원을 그려줌
		Paint paint = new Paint();
		paint.setColor(strokeColor);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
//		paint.setStrokeWidth(strokeWidth);
		int center = (int)((viewLength / 2));
		canvas.drawCircle(center, center, center, paint);
		

		// 안쪽 흰 원을 그려줌
		paint.setColor(Color.WHITE);
		// 이미지 비트맵 원의 반지름 (전체 View의 절반길이 - padding값)
		int circleRadius = (int)((viewLength / 2) - (int)(padding) - 2);
		// 원의 길이 (전체 View의 길이 - padding값)
		int circleLength = (int)((float)viewLength - padding - 2);
		// 원의 중심점 (전체 View의 중심)
		int circleCenter = (int)(viewLength/2);
		canvas.drawCircle(circleCenter, circleCenter, circleRadius, paint);
		
		// 안쪽 이미지를 그려줌
		Bitmap roundBitmap = getCroppedBitmap(bitmap, viewLength);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int viewLength) {
		Bitmap sbmp;

		// 정사각형 이미지가 아닐 경우 리사이즈된 직사각형 이미지의 가로 세로 값
		int scaledBitmapWidth, scaledBitmapHeight;

		// 직사각형 -> 정사각형 이미지로 바꿀 때 시작값 (가운데위치하도록)
		int startX = 0, startY = 0;

		// 정사각형 이미지가 아닐 경우, 짧은면을 판단해서 비율값 지정		
		float bmpSmallSideLength = Math.min(bmp.getWidth(), bmp.getHeight());
		float ratio = bmpSmallSideLength / viewLength;
		scaledBitmapWidth = (int)(bmp.getWidth() / ratio);
		scaledBitmapHeight = (int)(bmp.getHeight() / ratio);
		sbmp = Bitmap.createScaledBitmap(bmp, scaledBitmapWidth, scaledBitmapHeight, true);

		// 가로가 세로보다 길 때		
		if(scaledBitmapWidth > scaledBitmapHeight){
			int centerX = scaledBitmapWidth / 2;
			int viewHalfLength = viewLength / 2;
			startX = centerX - viewHalfLength;
			startY = 0;
		}
		// 세로가 가로보다 길 때
		else{
			int centerY = scaledBitmapHeight / 2;
			int viewHalfLength = viewLength / 2;
			startX = 0;
			startY = centerY - viewHalfLength;
		}

		// 이미지 작을경우 에러나는부분 일단 수정
		if(startY < 0) startY = 0;

		// 가운데 위치하도록 비트맵 설정
		sbmp = Bitmap.createBitmap(sbmp, startX, startY, scaledBitmapWidth-startX, scaledBitmapHeight-startY);

		Bitmap output = Bitmap.createBitmap(viewLength, viewLength, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);

		// 이미지 비트맵 원의 반지름 (전체 View의 절반길이 - padding값)
		int circleRadius = (int)((viewLength / 2) - (int)(padding) - 1);
		// 원의 길이 (전체 View의 길이 - padding값)
		int circleLength = (int)((float)viewLength - padding);
		// 원의 중심점 (전체 View의 중심)
		int circleCenter = (int)(viewLength/2);

		Rect rect = new Rect(0, 0, circleLength, circleLength);

		canvas.drawCircle(circleCenter, circleCenter, circleRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);



		return output;
	}

}