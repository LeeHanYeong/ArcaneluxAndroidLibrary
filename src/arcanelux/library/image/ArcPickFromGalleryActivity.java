package arcanelux.library.image;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import arcanelux.library.file.ArcFile;

public class ArcPickFromGalleryActivity extends Activity {
	private final String TAG = this.getClass().getName();
	private final int REQ_GALLERY = 8824;
	private String mFilePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "- Start Intent -");
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, REQ_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQ_GALLERY){
			if(resultCode == RESULT_OK) {
				Intent intent = new Intent();
				mFilePath = ArcFile.getRealPathFromURI(ArcPickFromGalleryActivity.this, data.getData());
				intent.putExtra("filePath", mFilePath);
				setResult(RESULT_OK, intent);
				finish();
			}
			else if(resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "선택을 취소하였습니다", Toast.LENGTH_SHORT).show();
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}
	
	

}
