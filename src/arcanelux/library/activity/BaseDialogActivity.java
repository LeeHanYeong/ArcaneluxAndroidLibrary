package arcanelux.library.activity;

import android.os.Bundle;
import android.widget.TextView;
import arcanelux.library.R;


public class BaseDialogActivity extends BaseActionBarActivity {
	protected TextView tvTitle, tvContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_dialog);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		mActionBar.hide();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
