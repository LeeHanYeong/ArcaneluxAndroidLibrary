package arcanelux.library.camera;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import arcanelux.library.ArcCalendar;
import arcanelux.library.file.ArcFile;

public class ArcCamera {
	private static final String TAG = "ArcCamera";
	private String mDirPath;
	private String mDirName = "ArcCamera";
	private static final String DEFAULT_CAMERA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ArcCamera";

	private String mFilePath;
	private String mFileName = "ArcCameraFile";
	public static final String EXT_JPG = ".jpg";

	public static void startCaptureActivity(Integer requestCode, Context context) {
		startCaptureActivity(requestCode, "", "", context);
	}
	public static void startCaptureActivity(Integer requestCode, Fragment fragment) {
		startCaptureActivity(requestCode, "", "", fragment);
	}
	public static void startCaptureActivity(Integer requestCode, String fileName, Context context) {
		startCaptureActivity(requestCode, "", fileName, context);
	}
	public static void startCaptureActivity(Integer requestCode, String fileName, Fragment fragment) {
		startCaptureActivity(requestCode, "", fileName, fragment);
	}
	public static void startCaptureActivity(Integer requestCode, String dirName, String fileName, Fragment fragment) {
		String mFilePath = initStartCaptureActivity(requestCode, dirName, fileName);

		Intent intent = new Intent(fragment.getActivity(), ArcCaptureActivity.class);
		intent.putExtra("filePath", mFilePath);
		fragment.startActivityForResult(intent, requestCode);
	}
	public static void startCaptureActivity(Integer requestCode, String dirName, String fileName, Context context) {
		String mFilePath = initStartCaptureActivity(requestCode, dirName, fileName);

		// 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
		Activity callActivity = ((Activity) context);
		Intent intent = new Intent(callActivity, ArcCaptureActivity.class);
		intent.putExtra("filePath", mFilePath);
		callActivity.startActivityForResult(intent, requestCode);
	}
	
	private static String initStartCaptureActivity(Integer requestCode, String dirName, String fileName) {
		String mDirPath;
		String mDirName = "ArcCamera";

		String mFilePath;
		String mFileName = "Arc";
		
		if(dirName != null && dirName.length() != 0) {
			mDirName = dirName;
		}
		if(fileName != null && fileName.length() != 0) {
			mFileName = fileName;
		} else {
			Calendar cal = Calendar.getInstance();
			mFileName = mFileName + ArcCalendar.getDateStringFromCalendar(cal, "yyyyMMddhhmmss");
		}
		mDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + mDirName;
		Log.d(TAG, "Camera Dir Path : " + mDirPath);

		mFilePath = mDirPath + File.separator + mFileName + EXT_JPG;
		Log.d(TAG, "Camera File Path : " + mFilePath);

		// 폴더생성
		Log.d(TAG, "- Make dir start -");
		File file = new File(mDirPath);
		boolean isSuccess = true;
		if(file.exists()){
			Log.d(TAG, "Dir exists");
		} else {
			isSuccess = file.mkdirs();
			Log.d(TAG, "Dir make result : " + isSuccess);
		}
		Log.d(TAG, "- Make dir end -");

		// 파일 이름 지정
		Log.d(TAG, "- Make file start -");
		file = new File(mFilePath);
		Uri outputFileUri = Uri.fromFile(file);
		Log.d(TAG, "File uri : " + outputFileUri);
		Log.d(TAG, "- Make file end -");
		
		return mFilePath;
	}

	public static String getFilePathFromCaptureResult(Intent data) {
		return data.getStringExtra("filePath");
	}
	
	public static boolean clearCameraFolder() {
		return ArcFile.deleteFile(DEFAULT_CAMERA_DIR);
	}
	public static boolean clearCameraFolder(String dirPath) {
		return ArcFile.deleteFile(dirPath);
	}
	
}
