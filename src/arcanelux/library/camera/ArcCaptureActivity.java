package arcanelux.library.camera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ArcCaptureActivity extends Activity {
	private final String TAG = this.getClass().getName();
	private final int REQ_CAPTURE = 8823;
	private String mFilePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent getIntent = getIntent();
		mFilePath = getIntent.getStringExtra("filePath");
		
		File file = new File(mFilePath);
		Uri outputFileUri = Uri.fromFile(file);
		Log.d(TAG, "File uri : " + outputFileUri);
		
		Log.d(TAG, "- Start Intent -");
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, REQ_CAPTURE);
		Log.d(TAG, "- Start intent end -");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQ_CAPTURE){
			if(resultCode == RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra("filePath", mFilePath);
				setResult(RESULT_OK, intent);
				finish();
			}
			else if(resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "촬영을 취소하였습니다", Toast.LENGTH_SHORT).show();
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}
	
	

}
