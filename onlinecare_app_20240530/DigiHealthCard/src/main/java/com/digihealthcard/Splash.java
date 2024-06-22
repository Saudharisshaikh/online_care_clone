package com.digihealthcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.digihealthcard.R;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.SharedPrefsHelper;

public class Splash extends Activity {
	
	TextView tvSplashContinue,tvSplashCAllninonon;
	SharedPreferences prefs;
	public SharedPrefsHelper sharedPrefsHelper;
	
	@Override
	protected void onStart() {
		super.onStart();

	}
	@Override
	protected void onResume() {
		super.onResume();
		
		/*if((!isMyServiceRunning(IncomingCallServiceNew.class))) {
			
			System.out.println("--online care service started from splash check...");

			Intent intent1 = new Intent();
			intent1.setAction("com.app.onlinecare.START_SERVICE");
			sendBroadcast(intent1);

		}*/

		/*System.out.println("-- livecareTimerRunning: "+prefs.getBoolean("livecareTimerRunning", false));
		if(!(isMyServiceRunning(LiveCareWaitingService1.class)) && prefs.getBoolean("livecareTimerRunning", false)) {
			System.out.println("--online care liveCareWaitingService1 started from splash check...");
			Intent intent1 = new Intent();
			intent1.setAction("LIVE_CARE_WAITING_TIMER");
			sendBroadcast(intent1);
		}*/

//	loginToQB();

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		sharedPrefsHelper = SharedPrefsHelper.getInstance();


		tvSplashContinue = (TextView) findViewById(R.id.tvSplashContinue);
		tvSplashCAllninonon = (TextView) findViewById(R.id.tvSplashCAllninonon);
		tvSplashContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				/*Intent intent = new Intent(getApplicationContext(),MainActivityNew.class);
				intent.putExtra("show_med_history_popup", true);
				startActivity(intent);
				finish();*/

				startActivity(new Intent(Splash.this, ActivityCovacardHome.class));
				finish();
	
			}
		});
		
		tvSplashCAllninonon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
					callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(callIntent);
				}catch (Exception  e){e.printStackTrace();}
			}
		});

		findViewById(R.id.tvPolicy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(Splash.this).showWebviewDialog(DATA.PRIVACY_POLICY_URL,"Privacy Policy");
			}
		});
		findViewById(R.id.tvAgreement).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(Splash.this).showWebviewDialog(DATA.USER_AGREEMENT_URL,"OnlineCare and its partner’s Virtual Care End User Agreement");
			}
		});

		try {
			if(sharedPrefsHelper.get("darkMode", 0) == 0){
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
			}else if(sharedPrefsHelper.get("darkMode", 0) == 1){
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			}else if(sharedPrefsHelper.get("darkMode", 0) == 2){
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	

	/*private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            System.out.println("--service already running");
	        	return true;
	        }
	    }
        System.out.println("--service not running");
	    return false;
	}*/
}