package arcanelux.library;
/**
 * Lhy_Function
 *  자잘한 함수입니다
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ArcFunction {
	private static final String TAG = "lhy_Function";
	public static final int REQ_UPDATE = 1009;

	public static String getImageName(String imageUrl){
		String url = imageUrl.replaceAll(".*/", "");
		Log.d(TAG, "getImageName : " + url);
		return url;
	}

	public static String getImageNameExceptSlash(String imageUrl){
		String url = imageUrl.replaceAll("/", "");
		Log.d(TAG, "getImageNameExceptSlash : " + url);
		return url;
	}

	public static void setViewTag(View[] viewList, int position){
		for(View curView : viewList){
			curView.setTag(Integer.valueOf(position));
		}
	}

	public static void setViewTag(View view, int position){
		view.setTag(Integer.valueOf(position));
	}

	/** 여러개의 뷰에 같은 클릭리스너 할당 **/
	public static void setViewClickListener(View[] viewList, OnClickListener listener){
		for(View curView : viewList){
			curView.setOnClickListener(listener);
		}
	}

	/** 여러개의 뷰에 같은 토글리스너 할당 **/
	public static void setViewCheckListener(View[] viewList, OnCheckedChangeListener listener){
		for(View curView : viewList){
			((ToggleButton) curView).setOnCheckedChangeListener(listener);
		}
	}

	public static void makeToast(String msg, Context context){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/** 이메일 정규식 확인 **/
	public static boolean isValidEmail(String email) {
		boolean returnValue = false;
		//		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
		String regex = "^[_0-9a-zA-Z-.]+@[0-9a-zA-Z]+(.[_0-9a-zA-Z-]+)*$";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		if( m.matches() ) {
			returnValue = true; 
		}
		return returnValue;
	}

	/** MD5 String을 돌려준다 **/
	public static String getMD5Hash(String s) {
		MessageDigest m = null;
		String hash = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(),0,s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hash;
	}

	/** Asset폴더의 File이름을 인자로 전달, String을 반환한다 **/
	public static String getStringAssetText(String file, Context context) throws IOException {
		InputStream is = context.getAssets().open(file);

		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();

		String text = new String(buffer);

		return text;
	}

	/** Cache 삭제 **/
	public static void clearCache(Context context) {        
		final File cacheDirFile = context.getCacheDir();        
		if (null != cacheDirFile && cacheDirFile.isDirectory()) {
			clearSubCacheFiles(cacheDirFile);
		}
	}
	private static void clearSubCacheFiles(File cacheDirFile) {
		if (null == cacheDirFile || cacheDirFile.isFile()) {
			return;
		}
		for (File cacheFile : cacheDirFile.listFiles()) {
			if (cacheFile.isFile()) {
				if (cacheFile.exists()) {
					cacheFile.delete();                    
				}
			} else {
				clearSubCacheFiles(cacheFile);
			}
		}
	}

	/** 현재 앱 버전 확인 **/
	public static int checkAppVersion(Context context){
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** HTML페이지에서 버전 확인하기 **/
	public static int checkLhyVersion(Context context){
		String result = ""; 
		try {
			String strUrl = "http://kkoksara.com/def/" + context.getPackageName();
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					String line = br.readLine();
					result = line;
					br.close();
				}
				conn.disconnect();
			}
		} 
		catch (Exception ex) {;}

		return Integer.parseInt(result);
	}
	/** 업데이트 Dialog **/
	public static Dialog updateForceDialog(int id, final Context context) {
		return updateForceDialog("마켓에 새로운 버전이 있습니다. 업데이트를 해주세요", id, context);
	}
	public static Dialog updateForceDialog(String title, int id, final Context context) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch(id) {
		case REQ_UPDATE:
			builder = new AlertDialog.Builder(context);
			builder.setMessage(title	)      
			.setCancelable(false)      
			.setPositiveButton("업데이트하기", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String pkgName = context.getPackageName();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName));
					((Activity)context).startActivity(intent);
					((Activity)context).finish();
				}      
			})      
			.setNegativeButton("종료", new DialogInterface.OnClickListener() {          
				public void onClick(DialogInterface dialog, int id) {               
					((Activity)context).finish();              
				}      
			});
			dialog = builder.create();
			break;
		default :
			dialog = null;
		}
		return dialog;
	}

	/** 업데이트 Dialog **/
	public static Dialog updateDialog(int id, final Context context) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch(id) {
		case REQ_UPDATE:
			builder = new AlertDialog.Builder(context);
			builder.setMessage("마켓에 새로운 버전이 있습니다.")      
			.setCancelable(false)      
			.setPositiveButton("업데이트하기", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String pkgName = context.getPackageName();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName));
					((Activity)context).startActivity(intent);
					((Activity)context).finish();
				}      
			})      
			.setNegativeButton("종료", new DialogInterface.OnClickListener() {          
				public void onClick(DialogInterface dialog, int id) {               
					((Activity)context).finish();              
				}      
			});
			dialog = builder.create();
			break;
		default :
			dialog = null;
		}
		return dialog;
	}

	public static boolean isRequireUpdate(Context context){
		int curVersion = checkAppVersion(context);
		int lhyVersion = checkLhyVersion(context);
		if(curVersion < lhyVersion){
			return false;
		} else{
			return true;
		}
	}
	/** 자동 업데이트 Dialog **/
	public static void showUpdateDialog(Context context){
		int curVersion = checkAppVersion(context);
		int lhyVersion = checkLhyVersion(context);
		if(curVersion < lhyVersion){
			updateForceDialog(REQ_UPDATE, context).show();
		}
	}
	/** 자동 업데이트 Dialog, 미리 버전을 알고있을 때 **/
	public static void showForceUpdateDialog(int curVersion, int lhyVersion, Context context){
		if(curVersion < lhyVersion){
			updateForceDialog(REQ_UPDATE, context).show();
		}
	}

	/** 강제 키보드 내리기(임의의 EditText 필요) **/
	public static void forceHideKeyboard(Context context, EditText editText){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//키보드를 띄운다.
		imm.showSoftInput(editText, 0);
		//키보드를 없앤다.
		imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
	}

	// 지정한 해상도에 가장 최적화 된 카메라 캡쳐 사이즈 구해주는 함수
	public static Size getOptimalPictureSize(List<Size> sizeList, int width, int height){
		Log.d(TAG, "--getOptimalPictureSize Start, 기준 width,height : (" + width + ", " + height + ")--");
		Size prevSize = sizeList.get(0);
		Size optSize = sizeList.get(0);
		for(Size size : sizeList){
			// 현재 사이즈와 원하는 사이즈의 차이
			int diffWidth = Math.abs((size.width - width));
			int diffHeight = Math.abs((size.height - height));

			// 이전 사이즈와 원하는 사이즈의 차이
			int diffWidthPrev = Math.abs((prevSize.width - width));
			int diffHeightPrev = Math.abs((prevSize.height - height));

			// 현재까지 최적화 사이즈와 원하는 사이즈의 차이
			int diffWidthOpt = Math.abs((optSize.width - width));
			int diffHeightOpt = Math.abs((optSize.height - height));

			// 이전 사이즈보다 현재 사이즈의 가로사이즈 차이가 적을 경우 && 현재까지 최적화 된 세로높이 차이보다 현재 세로높이 차이가 적거나 같을 경우에만 적용
			if(diffWidth < diffWidthPrev && diffHeight <= diffHeightOpt){
				optSize = size;
				Log.d(TAG, "가로사이즈 변경 / 기존 가로사이즈 : " + prevSize.width + ", 새 가로사이즈 : " + optSize.width);
			}
			// 이전 사이즈보다 현재 사이즈의 세로사이즈 차이가 적을 경우 && 현재까지 최적화 된 가로길이 차이보다 현재 가로길이 차이가 적거나 같을 경우에만 적용
			if(diffHeight < diffHeightPrev && diffWidth <= diffWidthOpt){
				optSize = size;
				Log.d(TAG, "세로사이즈 변경 / 기존 세로사이즈 : " + prevSize.height + ", 새 세로사이즈 : " + optSize.height);
			}

			// 현재까지 사용한 사이즈를 이전 사이즈로 지정
			prevSize = size;
		}
		Log.d(TAG, "--getOptimalPictureSize End, 결과 width,height : (" + optSize.width + ", " + optSize.height + ")--");
		return optSize;
	}

	public static void cameraCaptureAndSaveCacheFileAndStartActivity(Context context, int requestCode, int optWidth, int optHeight, String fileName){
		Intent intent = new Intent();
		Camera camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		List<Size> sizeList = parameters.getSupportedPictureSizes();
		//카메라 SupportedPictureSize목록 출력 로그
		Log.d(TAG, "--SupportedPictureSizeList Start--");
		for(int i=0; i<sizeList.size(); i++){
			Size size = sizeList.get(i);
			Log.d(TAG, "Width : " + size.width + ", Height : " + size.height);
		}
		Log.d(TAG, "--SupportedPictureSizeList End--");
		// 원하는 최적화 사이즈를 1280x720 으로 설정
		Camera.Size size = ArcFunction.getOptimalPictureSize(parameters.getSupportedPictureSizes(), optWidth, optHeight);
		Log.d(TAG, "Selected Optimal Size : (" + size.width + ", " + size.height + ")");
		parameters.setPreviewSize(size.width,  size.height);
		parameters.setPictureSize(size.width,  size.height);
		//		parameters.set("orientation", "landscape");
		camera.setParameters(parameters);
		camera.release();


		// 임시파일 생성
//		File file = null;
//		try {
//			file = File.createTempFile(fileName, null, context.getCacheDir());
//			Log.d(TAG, "FilePath : " + file.getAbsolutePath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		File file = new File(context.getCacheDir(), fileName);
		File file = new File(context.getFilesDir(), fileName);
		Uri outputFileUri = Uri.fromFile(file);
		Log.d(TAG, "FilePath : " + file.getAbsolutePath());

		// 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
		// requestCode지정해서 인텐트 실행
		((Activity) context).startActivityForResult(intent, requestCode);
	}
	public static void cameraCaptureAndSaveFileAndStartActivity(Context context, int requestCode, int optWidth, int optHeight, String pathDir, String pathFile){
		Intent intent = new Intent();
		Camera camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		List<Size> sizeList = parameters.getSupportedPictureSizes();
		//카메라 SupportedPictureSize목록 출력 로그
		Log.d(TAG, "--SupportedPictureSizeList Start--");
		for(int i=0; i<sizeList.size(); i++){
			Size size = sizeList.get(i);
			Log.d(TAG, "Width : " + size.width + ", Height : " + size.height);
		}
		Log.d(TAG, "--SupportedPictureSizeList End--");
		// 원하는 최적화 사이즈를 1280x720 으로 설정
		Camera.Size size = ArcFunction.getOptimalPictureSize(parameters.getSupportedPictureSizes(), optWidth, optHeight);
		Log.d(TAG, "Selected Optimal Size : (" + size.width + ", " + size.height + ")");
		parameters.setPreviewSize(size.width,  size.height);
		parameters.setPictureSize(size.width,  size.height);
		//		parameters.set("orientation", "landscape");
		camera.setParameters(parameters);
		camera.release();

		// 폴더 생성
		File file = new File(pathDir);
		boolean isSuccess = true;
		if(!file.exists()){
			isSuccess = file.mkdirs();
		}
		Log.d(TAG, "DirPath : " + pathDir + ", mkdirs result : " + isSuccess);

		// 파일 이름 지정
		file = new File(pathFile);
		Log.d(TAG, "FilePath : " + pathFile);
		Uri outputFileUri = Uri.fromFile(file);

		// 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
		// requestCode지정해서 인텐트 실행
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	public static void cropImageAndStartActivity(Context context, int requestCode, String fromFilePath, String toFilePath, int outputX, int outputY, int aspectX, int aspectY){
		File fromFile = new File(fromFilePath);
		Uri fromUri = Uri.fromFile(fromFile);

		File toFile = new File(toFilePath);
		Uri toUri = Uri.fromFile(toFile);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromUri, "image/*");
		intent.putExtra("outputX", outputX); // crop한 이미지의 x축 크기
		intent.putExtra("outputY", outputY); // crop한 이미지의 y축 크기
		intent.putExtra("aspectX", aspectX); // crop 박스의 x축 비율 
		intent.putExtra("aspectY", aspectY); // crop 박스의 y축 비율
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, toUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	public static void cropImageAndStartActivity(Context context, int requestCode, String fromFilePath, String toFilePath, int aspectX, int aspectY){
		File fromFile = new File(fromFilePath);
		Uri fromUri = Uri.fromFile(fromFile);

		File toFile = new File(toFilePath);
		Uri toUri = Uri.fromFile(toFile);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromUri, "image/*");
		intent.putExtra("aspectX", aspectX); // crop 박스의 x축 비율 
		intent.putExtra("aspectY", aspectY); // crop 박스의 y축 비율
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, toUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	public static void pickImageAndStartActivity(Context context, int requestCode){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static Calendar getCalendarFromStringFormat(String strDate, String format){
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static int getPxFromDp(Context context, float dp) {
		int px = 0;
		px = (int) (dp * context.getResources().getDisplayMetrics().density);
		return px;
	}

	public static void combineImage(Bitmap bmFirst, Bitmap bmSecond, boolean isVerticalMode, String savePath){
		combineImage(bmFirst, bmSecond, isVerticalMode, savePath, true);
	}
	public static void combineImageBitmapNotRecycle(Bitmap bmFirst, Bitmap bmSecond, boolean isVerticalMode, String savePath){
		combineImage(bmFirst, bmSecond, isVerticalMode, savePath, false);
	}

	private static void combineImage(Bitmap bmFirst, Bitmap bmSecond, boolean isVerticalMode, String savePath, boolean recycle){
		Options option = new Options();
		option.inDither = true;
		option.inPurgeable = true;

		Bitmap bitmap = null;
		if(isVerticalMode)
			bitmap = Bitmap.createScaledBitmap(bmFirst, bmFirst.getWidth(), bmFirst.getHeight()+bmSecond.getHeight(), true);
		else
			bitmap = Bitmap.createScaledBitmap(bmFirst, bmFirst.getWidth()+bmSecond.getWidth(), bmFirst.getHeight(), true);

		Paint p = new Paint();
		p.setDither(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);

		Canvas c = new Canvas(bitmap);
		c.drawBitmap(bmFirst, 0, 0, p);
		if(isVerticalMode)
			c.drawBitmap(bmSecond, 0, bmFirst.getHeight(), p);
		else
			c.drawBitmap(bmSecond, bmFirst.getWidth(), 0, p);

		if(recycle){
			bmFirst.recycle();
			bmSecond.recycle();
		}
		try{
			FileOutputStream fos = new FileOutputStream(savePath);
			bitmap.compress(CompressFormat.JPEG, 90, fos);

			fos.close();
			bitmap.recycle();
		}
		catch(Exception e){e.printStackTrace();}
	}

	public static boolean copyFile(File file, String saveFilePath){
		boolean result;
		if(file!=null&&file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(saveFilePath);
				int readcount=0;
				byte[] buffer = new byte[1024];
				while((readcount = fis.read(buffer,0,1024))!= -1){
					newfos.write(buffer,0,readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		}else{
			result = false;
		}
		return result;
	}
	public static File makeDirectory(String dirPath){
        File dir = new File(dirPath);
        if (!dir.exists())
        {
            dir.mkdirs();
            Log.i( TAG , "!dir.exists" );
        }else{
            Log.i( TAG , "dir.exists" );
        }
 
        return dir;
    }
 
	public static File makeFile(File dir , String filePath){
		File file = null;
		boolean isSuccess = false;
		if(dir.isDirectory()){
			file = new File(filePath);
			if(file!=null&&!file.exists()){
				Log.i( TAG , "!file.exists" );
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					Log.i(TAG, "파일생성 여부 = " + isSuccess);
				}
			}else{
				Log.i( TAG , "file.exists" );
			}
		}
		return file;
	}

	public static boolean copyFile(String originFilePath, String saveDirPath, String saveFilePath){
		File file = new File(originFilePath);
		File saveDir = new File(saveDirPath);
		if(!saveDir.exists()){
			saveDir.mkdirs();
		}
		File saveFile = new File(saveFilePath);
		if(!saveFile.exists()){
			makeFile(saveDir, saveFilePath);
		}

		boolean result;
		if(file!=null&&file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(saveFilePath);
				int readcount=0;
				byte[] buffer = new byte[1024];
				while((readcount = fis.read(buffer,0,1024))!= -1){
					newfos.write(buffer,0,readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		}else{
			result = false;
		}
		return result;
	}

	public static void refreshGallery(Context context){
		// media center 새로고침 
		// 사진 촬영 후 새로 고침 하지 않으면 갤러리에서 촬영된 사진을 볼 수 없음 
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

	}

	public static boolean deleteFile(String deletePath){
		File file = new File(deletePath);
		if(file.exists()){
			File[] childFileList = file.listFiles();
			for(File childFile : childFileList){
				if(childFile.isDirectory()){
					deleteFile(childFile.getAbsolutePath());
				}
				else{
					childFile.delete();
				}
			}
			file.delete();
			return true;
		}else{
			return false;
		}
	}
	
	public static void setTypeface(Context context, TextView tv, String fontFileName){
		tv.setTypeface(Typeface.createFromAsset(context.getAssets(), fontFileName));
	}

}
