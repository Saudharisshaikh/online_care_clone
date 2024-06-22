package com.app.msu_dr.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.util.DATA;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class SaveGCM_TokenBroadcast extends BroadcastReceiver {

	SharedPreferences prefs;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		prefs = context.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		if(intent.getAction().equals("com.app.onlinecaredr.SAVE_GCM_TOKEN")){
			saveToken(DATA.gcm_token, context);

		}
	}



	public void saveToken(String device_token, Context activity) {
		 
		/*if (device_token.contains("|ID|")) {
			device_token = device_token.substring(device_token.indexOf(":") +1);
		}*/
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("timezone", getDeviceTimeZone());
		params.put("platform", "android");
		params.put("device_token", device_token);

		DATA.print("--in saveDoctorToken regId: "+device_token+" userid: "+ prefs.getString("id", "0")+" timezone "+getDeviceTimeZone());

		String reqURL = DATA.baseUrl+"/saveDoctorToken";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" str responce: "+content);
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				// customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					//new GloabalMethods(activity).checkLogin(content , statusCode);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDoctorToken


	private String getDeviceTimeZone() {
		/*Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		Log.d("Time zone","="+tz.getDisplayName());	
		DATA.print("--time zone "+tz.getDisplayName());
		DATA.print("--time zone from util "+TimeZone.getDefault());
		DATA.print("--time zone id from util "+TimeZone.getDefault().getID());*/
		return TimeZone.getDefault().getID();
	}

}
