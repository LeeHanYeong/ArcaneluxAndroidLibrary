package arcanelux.library.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import arcanelux.library.R;

public class ArcRoundedImageView extends ImageView {
	private int borderWidth;
	private int canvasSize;
	private Paint paint, paintBorder;
	private Bitmap bitmap;

	public ArcRoundedImageView(Context context) {
		super(context, null);
	}

	public ArcRoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ArcRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcRoundedImageView);
		if(ta.getBoolean(R.styleable.ArcRoundedImageView_border, false)) {
			int defaultBorderSize = (int) (4 * getContext().getResources().getDisplayMetrics().density + 0.5f);
			setBorderWidth(ta.getDimensionPixelOffset(R.styleable.ArcRoundedImageView_border_width, defaultBorderSize));
			setBorderColor(ta.getColor(R.styleable.ArcRoundedImageView_border_color, Color.WHITE));
		}

		if(ta.getBoolean(R.styleable.ArcRoundedImageView_shadow, false)) {
			addShadow();
		}
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.requestLayout();
		this.invalidate();
	}
	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}
	public void addShadow() {
		setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
		paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		bitmap = drawableToBitmap(getDrawable());

		if(bitmap != null) {
			canvasSize = canvas.getWidth();
			if(canvas.getHeight() < canvasSize) {
				canvasSize = canvas.getHeight();
			}

			// Drawable에서 Bitmap가져와 정사각형으로 자른 CroppedBitmap 생성
			Bitmap squareBitmap = getCroppedBitmap(bitmap, canvasSize);

			// BitmapShader 생성
			BitmapShader shader = new BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);

			int circleCenter = (canvasSize - (borderWidth * 2)) / 2;

			// 바깥쪽 원을 그려줌
			canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
			canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);
		}
	}

	/**  둥근 이미지뷰에 맞게 정사각형으로 자른 비트맵을 반환 */
	public Bitmap getCroppedBitmap(Bitmap bmp, int viewLength) {
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
		//		int circleRadius = (int)((viewLength / 2) - (int)(padding) - 1);
		// 원의 길이 (전체 View의 길이 - padding값)
		//		int circleLength = (int)((float)viewLength - padding);
		// 원의 중심점 (전체 View의 중심)
		int circleCenter = (int) (viewLength / 2);
		int circleRadius = (int) (viewLength / 2);

		Rect rect = new Rect(0, 0, canvasSize, canvasSize);

		canvas.drawCircle(circleCenter, circleCenter, circleRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		} else if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// The parent has determined an exact size for the child.
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// The parent has not imposed any constraint on the child.
			result = canvasSize;
		}

		return result;
	}

	private int measureHeight(int measureSpecHeight) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = canvasSize;
		}

		return (result + 2);
	}


}