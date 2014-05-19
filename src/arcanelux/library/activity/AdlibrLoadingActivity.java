package arcanelux.library.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import arcanelux.library.R;
import arcanelux.library.baseclass.BaseAsyncTask;

public class AdlibrLoadingActivity extends AdlibrActionBarActivity {
	// 로딩메시지, 로딩 프로그레스바
	protected TextView tvLoadingMessage;
	protected ProgressBar mProgressBar;

	// setContentView 이전에 바꿀 수 있는 값
	protected String loadingMessage = "Loading...";
	protected int loadingMessageColor = Color.WHITE;
	protected float progressBarMarginPercentValue = 10;

	// 마지막으로 적용할 ProgressBar아래 여백
	private int progressBarMarginBottom;

	// VersionCheckTask, InitializeTask
	protected VersionCheckTask versionCheckTask;
	protected InitializeTask initializeTask;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		// 아래 여백값 계산
		progressBarMarginBottom = (int)(displayHeight * progressBarMarginPercentValue/100);
		LayoutParams llLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
		llLayoutParams.bottomMargin = progressBarMarginBottom;

		// TextView, ProgressBar담을 LinearLayout
		LinearLayout llProgress = new LinearLayout(mContext);
		llProgress.setLayoutParams(llLayoutParams);
		llProgress.setOrientation(LinearLayout.VERTICAL);

		// ProgressBar, TextView생성 및 설정
		mProgressBar = new ProgressBar(mContext);
		tvLoadingMessage = new TextView(mContext);
		tvLoadingMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		tvLoadingMessage.setGravity(Gravity.CENTER);
		tvLoadingMessage.setText(loadingMessage);
		tvLoadingMessage.setTextColor(loadingMessageColor);
		tvLoadingMessage.setTextSize(getResources().getDimensionPixelSize(R.dimen.tvLoadingTextSize));

		// LinearLayout에 TextView, ProgressBar 추가
		llProgress.addView(tvLoadingMessage);
		llProgress.addView(mProgressBar);

		// 생성되어있던 View에 Progress관련 View추가
		this.addContentView(llProgress, llLayoutParams);
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	protected void versionCheckInit(){

	}
	protected void versionCheckBackground(){

	}
	protected void versionCheckPostExecute(){

	}

	protected void initailizeInit(){

	}
	protected void initializeBackground(){

	}
	protected void initializePostExecute(){

	}
	/**
	 * VersionCheck API
	 * Request
	 * 		packageName, Current Version
	 * 
	 * Response
	 *		Available Version
	 *
	 *	Process
	 *		if(CurrentVersion < AvailableVersion)
	 *			Show Update Dialog
	 */
	protected class VersionCheckTask extends BaseAsyncTask{
		protected int currentVersion, avaliableVersion;

		public VersionCheckTask(Context context, String title) {
			super(context, title);
			versionCheckInit();
		}
		public VersionCheckTask(Context context, String title, boolean showDialog) {
			super(context, title, showDialog);
			versionCheckInit();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Void... params) {
			versionCheckBackground();
			return super.doInBackground(params);
		}
		@Override
		protected void onPostExecute(Integer result) {
			versionCheckPostExecute();
			super.onPostExecute(result);
		}		
	}

	protected class InitializeTask extends BaseAsyncTask{
		public InitializeTask(Context context, String title) {
			super(context, title);
			initailizeInit();
		}
		public InitializeTask(Context context, String title, boolean showDialog) {
			super(context, title, showDialog);
			initailizeInit();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Void... params) {
			return super.doInBackground(params);
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}		
	}




}
