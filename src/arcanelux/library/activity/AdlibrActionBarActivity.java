package arcanelux.library.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import arcanelux.library.R;

import com.mocoplex.adlib.AdlibAdViewContainer;
import com.mocoplex.adlib.AdlibConfig;
import com.mocoplex.adlib.AdlibManager;
import com.mocoplex.adlib.AdlibManager.AdlibVersionCheckingListener;

/**
 * Adlibr광고를 사용하기 위한 Activity
 * 		showAd : 광고 출력 여부
 *		Adlibr관련 변수 : AdlibManager, AdlibAdViewcontainer
 *
 *		D(debug) true시, 광고아이디와 init패키지 명 출력
 *		setContentView Override, 광고영역 동적 생성 및 본문 영역 setGlobalFont로 Typeface적용
 */
public class AdlibrActionBarActivity extends BaseActionBarActivity {
	/** Adlib **/
	protected boolean showAd = true;
	protected AdlibManager _amanager;
	protected AdlibAdViewContainer adlibView = null;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_amanager = new AdlibManager();
		_amanager.onCreate(this);
		
		if(D){
			String idAdlibr = getResources().getString(R.string.id_adlibr);
			String idAdam = getResources().getString(R.string.id_adam);
			String idCauly = getResources().getString(R.string.id_cauly);
//			String idInmobi = getResources().getString(R.string.ad_inmobi);
			String idFlurry = getResources().getString(R.string.id_flurry);
			Log.d(TAG, "----- APP Info Start -----------------------------------");
			Log.d(TAG, "- PackageName : " + getPackageName());
			Log.d(TAG, "- Adlibr : " + idAdlibr);
			Log.d(TAG, "- Adam : " + idAdam);
			Log.d(TAG, "- Cauly : " + idCauly);
//			Log.d(TAG, "- InMobi : " + idInmobi);
			Log.d(TAG, "- Flurry : " + idFlurry);
			Log.d(TAG, "----- APP Info End ------------------------------------");
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		setGlobalFont(root);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setContentView에 전달된 layout으로 view생성
		View view = inflater.inflate(layoutResID, null);
		setGlobalFont((ViewGroup)view);

		// 전체화면 구성하는 LinearLayout llRootView 생성
		LinearLayout llRootView = new LinearLayout(mContext);
		// llRootView는 LinearLayout, Vertical
		llRootView.setOrientation(LinearLayout.VERTICAL);
		// llRootView는 width-match_parent, height-match_parent
		llRootView.setLayoutParams(new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		// llRootView에 setContentView에서 지정한 layout을 추가
		// weight 1.0f로 광고를 제외한 모든 부분을 차지하게 함
		llRootView.addView(view, new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
				0, 	1.0f));

		// 광고를 위한 LayoutParmas
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

		// 광고 생성
		adlibView = new AdlibAdViewContainer(mContext);
		if(showAd){
			llRootView.addView(adlibView, params);
		}

		// Adlib뷰에 스케쥴러 바인드
		bindAdsContainer(adlibView);

		super.setContentView(llRootView);
	}

	protected void onResume(){		
		_amanager.onResume(this);
		super.onResume();
	}

	protected void onPause(){    	
		_amanager.onPause();
		super.onPause();
	}

	protected void onDestroy(){    	
		_amanager.onDestroy(this);
		super.onDestroy();
	}

	/** Adlib **/
	public void setAdsContainer(int rid){_amanager.setAdsContainer(rid);}
	public void bindAdsContainer(AdlibAdViewContainer a){	_amanager.bindAdsContainer(a);}
	public void setVersionCheckingListner(AdlibVersionCheckingListener l){ _amanager.setVersionCheckingListner(l);}
	public void destroyAdsContainer() {_amanager.destroyAdsContainer();}

	protected void initAds() {
//		packageName = getPackageName();
		packageName = "arcanelux.library";
		String idAdlibr = getResources().getString(R.string.id_adlibr);
		String idCauly = getResources().getString(R.string.id_cauly);
		String idAdam = getResources().getString(R.string.id_adam);
		
		if(D){
			Log.d(TAG, "--initAds Start--");
			//			Log.d(TAG, "-INMOBI : " + packageName + ".ads.SubAdlibAdViewInmobi");
			Log.d(TAG, "-ADAM : " + packageName + ".ads.SubAdlibAdViewAdam");
			Log.d(TAG, "-CAULY : " + packageName + ".ads.SubAdlibAdViewCauly");
			Log.d(TAG, "--initAds End--");
		}

		//AdlibConfig.getInstance().bindPlatform("INMOBI", packageName + ".ads.SubAdlibAdViewInmobi");
		//쓰지 않을 광고플랫폼은 삭제해주세요.
		AdlibConfig.getInstance().bindPlatform("ADAM", packageName + ".ads.SubAdlibAdViewAdam");
		//AdlibConfig.getInstance().bindPlatform("ADMOB","lhy.undernation.ads.SubAdlibAdViewAdmob");
		AdlibConfig.getInstance().bindPlatform("CAULY", packageName + ".ads.SubAdlibAdViewCauly");
		//AdlibConfig.getInstance().bindPlatform("TAD","lhy.undernation.ads.SubAdlibAdViewTAD");
		//AdlibConfig.getInstance().bindPlatform("NAVER","lhy.undernation.ads.SubAdlibAdViewNaverAdPost");
		//AdlibConfig.getInstance().bindPlatform("SHALLWEAD","lhy.undernation.ads.SubAdlibAdViewShallWeAd");
		AdlibConfig.getInstance().setAdlibKey(idAdlibr);        
	}
}
