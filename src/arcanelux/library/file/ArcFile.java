package arcanelux.library.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class ArcFile {
	private static final String TAG = "ArcFile";
	
	public static boolean copyFile(File file, String saveFilePath){
		boolean result;
		if(file != null && file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(saveFilePath);
				int readcount=0;
				byte[] buffer = new byte[1024];
				while((readcount = fis.read(buffer,0,1024)) != -1){
					newfos.write(buffer,0,readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		} else {
			result = false;
		}
		return result;
	}
	
	public static File makeDirectory(String dirPath){
        File dir = new File(dirPath);
        if (! dir.exists()) {
            dir.mkdirs();
            Log.d(TAG , "Dir makes" );
        } else {
            Log.d(TAG , "Dir exists" );
        }
        return dir;
    }
 
	public static File makeFile(File dir , String filePath){
		File file = null;
		boolean isSuccess = false;
		if(dir.isDirectory()) {
			file = new File(filePath);
			if(file != null && !file.exists()) {
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					Log.d(TAG, "File create result : " + isSuccess);
				}
			} else {
				Log.i( TAG , "File exists" );
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
		} else {
			return false;
		}
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
}
