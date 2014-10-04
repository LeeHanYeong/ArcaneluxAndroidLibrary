package arcanelux.library.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

public class ArcImage {
	private final static String TAG = "ArcImage";
	
	public static Options getOptimalOption() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		return options;
	}
	
	public static void setFileToImageView(String filePath, ImageView iv) {
		Bitmap bm = BitmapFactory.decodeFile(filePath, getOptimalOption());
		iv.setImageBitmap(bm);
	}
	
	public static void startPickImageFromGalleryActivity(int requestCode, Fragment fragment) {
		Intent intent = new Intent(fragment.getActivity(), ArcPickFromGalleryActivity.class);
		fragment.startActivityForResult(intent, requestCode);
	}
	public static void startPickImageFromGalleryActivity(int requestCode, Context context) {
		Activity callActivity = ((Activity) context);
		Intent intent = new Intent(callActivity, ArcPickFromGalleryActivity.class);
		callActivity.startActivityForResult(intent, requestCode);
	}
	
	public static String getFilePathFromPickImageResult(Intent data) {
		return data.getStringExtra("filePath");
	}
}
