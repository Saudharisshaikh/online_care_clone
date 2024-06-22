package com.app.mhcsn_cp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.StateBean;
import com.app.mhcsn_cp.util.ChoosePictureDialog;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.UploadImageCall;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UpdateProfile extends BaseActivity {

	Activity activity;

	EditText etUpdateProfileFname,
			etUpdateProfileLname,
			etUpdateProfilBday,
			etUpdateProfilePhone,
			etUpdateProfileAddress,
			etUpdateProfileCountry,
			etUpdateProfileOccupation,
			etUpdateProfileDrCity,
	//etUpdateProfileState,
	etUpdateProfileIntroduction,
			etUpdateProfileQualification,
			etUpdateProfileCareerData,
			etUpdateProfileZipcode;

	Spinner spUpdateProfileState;
	String userState = "";

	RadioGroup rgEditProfilGender,rgMaritalStatus;
	RadioButton radioMale,radioFemale,radioOther;
	ImageView imgChangeProfileImg;

	AsyncHttpClient client;
	ProgressDialog pd;

	JSONObject jsonObject, user_info;

	String msg, status, gender = "1", maritalStatus;
	SharedPreferences prefs;
	ActionBar ab;

	Button btnUpdtPrflSbmt,btnUpdtPrflPwd;

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	protected void onResume() {
		super.onResume();


		DATA.print("--online care update profile onResume");

		if(DATA.isImageCaptured) {


			DATA.print("--online care update profile DATA.isImageCaptured onResume");

			try {

				UploadImageCall upldImg = new UploadImageCall(activity);
				upldImg.execute("");

			}catch(Exception e) {
				e.printStackTrace();
			}

		}
		else {
			DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, imgChangeProfileImg);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_profile);

		activity = UpdateProfile.this;

		pd = new ProgressDialog(activity , ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		pd.setMessage("UPDATING PROFILE...");
		//pd.setTitle("Please wait..!");
		pd.setCanceledOnTouchOutside(false);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		client = new AsyncHttpClient();

		etUpdateProfileFname = (EditText) findViewById(R.id.etUpdateProfileFname);
		etUpdateProfileLname = (EditText) findViewById(R.id.etUpdateProfileLname);
		etUpdateProfilBday = (EditText) findViewById(R.id.etUpdateProfilBday);

		etUpdateProfilePhone = (EditText) findViewById(R.id.etUpdateProfilePhone);
		etUpdateProfileAddress = (EditText) findViewById(R.id.etUpdateProfileAddress);
		etUpdateProfileCountry = (EditText) findViewById(R.id.etUpdateProfileCountry);
		etUpdateProfileOccupation = (EditText) findViewById(R.id.etUpdateProfileOccupation);
		etUpdateProfileDrCity = (EditText) findViewById(R.id.etUpdateProfileDrCity);
		//etUpdateProfileState = (EditText) findViewById(R.id.etUpdateProfileState);
		etUpdateProfileIntroduction = (EditText) findViewById(R.id.etUpdateProfileIntroduction);
		etUpdateProfileQualification = (EditText) findViewById(R.id.etUpdateProfileQualification);
		etUpdateProfileCareerData = (EditText) findViewById(R.id.etUpdateProfileCareerData);
		etUpdateProfileZipcode = (EditText) findViewById(R.id.etUpdateProfileZipcode);

		imgChangeProfileImg  = (ImageView) findViewById(R.id.imgChangeProfileImg);

		etUpdateProfileFname.setText(prefs.getString("first_name", ""));
		etUpdateProfileLname.setText(prefs.getString("last_name", ""));
		etUpdateProfilBday.setText(prefs.getString("birthdate", ""));
		etUpdateProfileAddress.setText(prefs.getString("address", ""));
		etUpdateProfileCountry.setText(prefs.getString("country", ""));
		etUpdateProfileOccupation.setText(prefs.getString("designation", ""));
		etUpdateProfilePhone.setText(prefs.getString("mobile", ""));
		etUpdateProfileDrCity.setText(prefs.getString("city", ""));
		//etUpdateProfileState.setText(prefs.getString("state", ""));
		etUpdateProfileIntroduction.setText(prefs.getString("introduction", ""));
		etUpdateProfileZipcode.setText(prefs.getString("zipcode", ""));
		etUpdateProfileQualification.setText(prefs.getString("qualification", ""));
		etUpdateProfileCareerData.setText(prefs.getString("career_data", ""));

		btnUpdtPrflPwd = (Button) findViewById(R.id.btnUpdtPrflPwd);
		btnUpdtPrflSbmt = (Button) findViewById(R.id.btnUpdtPrflSbmt);

		radioMale = (RadioButton) findViewById(R.id.radioMale);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);
		radioOther = (RadioButton) findViewById(R.id.radioOther);
		rgEditProfilGender = (RadioGroup) findViewById(R.id.rgEditProfilGender);

		//==========State DropDown==================================================================
		spUpdateProfileState = (Spinner) findViewById(R.id.spUpdateProfileState);

		final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
		ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(
				activity,
				R.layout.spinner_item_lay2,
				stateBeans
		);
		spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUpdateProfileState.setAdapter(spStateAdapter);

		for (int i = 0; i < stateBeans.size(); i++) {
			if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) {
				spUpdateProfileState.setSelection(i);
			}
		}

		spUpdateProfileState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				userState = stateBeans.get(pos).getAbbreviation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		//=================================State Dropdown===========================================================

		rgEditProfilGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.radioMale:

						gender = "1";

						break;
					case R.id.radioFemale:
						gender = "0";
						break;
					case R.id.radioOther:
						gender = "2";
						break;
					default:
						break;
				}
			}
		});

		rgMaritalStatus = (RadioGroup) findViewById(R.id.rgMaritalStatus);
		rgMaritalStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.radioSingle:

						maritalStatus = "0";
						break;

					case R.id.radioMarried:

						maritalStatus = "1";
						break;
					default:
						break;
				}
			}
		});

//		ab = getSupportActionBar();
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4b6162"));
//		ab.setBackgroundDrawable(colorDrawable);


//		UrlImageViewHelper.setUrlDrawable(imgChangeProfileImg, prefs.getString("id", ""), R.drawable.dummy_user_big);

		etUpdateProfilBday.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus) {

					//DATA.isDatePickerCallFromSignup = false;

					DialogFragment newFragment = new DatePickerFragment(etUpdateProfilBday);
					newFragment.show(getSupportFragmentManager(), "datePicker");

				}

			}
		});
		etUpdateProfilBday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etUpdateProfilBday);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		etUpdateProfileCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).showContrySelectionDialog(etUpdateProfileCountry);
			}
		});

		imgChangeProfileImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(activity,ChoosePictureDialog.class);
				intent.putExtra("isForProfilePic", true);
				startActivity(intent);

			}
		});

		switch (Integer.parseInt(prefs.getString("gender", "1"))) {//causes exception address please---->addressed
			case 1:
				radioMale.setChecked(true);
				radioFemale.setChecked(false);
				break;
			case 0:
				radioMale.setChecked(false);
				radioFemale.setChecked(true);
				break;
			case 2:
				radioFemale.setChecked(false);
				radioOther.setChecked(true);
				break;
			default:
				break;
		}
		Button btnEditPicture = (Button) findViewById(R.id.btnEditPicture);
		btnEditPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(activity,ChoosePictureDialog.class);
				intent.putExtra("isForProfilePic", true);
				startActivity(intent);

			}
		});
		btnUpdtPrflSbmt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				if(etUpdateProfileFname.getText().toString().isEmpty()
						|| etUpdateProfileLname.getText().toString().isEmpty()
						|| etUpdateProfilBday.getText().toString().isEmpty()
						|| etUpdateProfileAddress.getText().toString().isEmpty()
						|| etUpdateProfileCountry.getText().toString().isEmpty()
						|| etUpdateProfileOccupation.getText().toString().isEmpty()
						|| etUpdateProfileDrCity.getText().toString().isEmpty()
						//|| etUpdateProfileState.getText().toString().isEmpty()
						|| userState.isEmpty()
						|| etUpdateProfilePhone.getText().toString().isEmpty()
						|| etUpdateProfileZipcode.getText().toString().length() < 5
				)
				{
					customToast.showToast("Incomplete information, Please make sure all required fields are filled out correctly.",0,0);
				}
				else {
					updateProfileNoFinish();
				}

			}
		});

		btnUpdtPrflPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(UpdateProfile.this,UpdatePasswordActivity.class));

			}
		});

	}


//	@Override
//	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
//
//		if (item.getItemId() == android.R.id.home) {
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
//		}
//
//		switch (item.getItemId()) {
//
//		case R.id.action_change_status:
//
//			refreshMenuItem = item;
//
//			if(isConnectedToInternet()) {
//
//				//Toast.makeText(activity, DATA.isImageCaptured+"", 0).show();
//
//				if(DATA.isImageCaptured) {
//
//
//					UploadImageCall uploadImage = new UploadImageCall(activity);
//					uploadImage.execute("");
//
//					if(etUpdateProfileFname.getText().toString().isEmpty() || etUpdateProfileLname.getText().toString().isEmpty())
//					{
//						Toast.makeText(activity, "Empty first name/last name", 0).show();
//					}
//					else {
//
//						updateProfileNoFinish();
//
//					}
//
//					refreshMenuItem.setActionView(R.layout.action_progressbar);
//					pd.show();
//
//				}
//				else {
//
//					if(etUpdateProfileFname.getText().toString().isEmpty() || etUpdateProfileLname.getText().toString().isEmpty())
//					{
//						Toast.makeText(activity, "Empty first name/last name", 0).show();
//					}
//					else {
//
//						updateProfile();
//						refreshMenuItem.setActionView(R.layout.action_progressbar);
//						pd.show();
//
//					}
//
//				}
//			}
//			else {
//				Toast.makeText(activity, "Not connected to Internet", 0).show();
//			}
//
//
//
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	String firstNameInput,lastNameInput,addressInput,countryInput,phoneInput,cityInput,zipcodeInput,birthdateInput,
			introductionInput, desigInput,qualificationInput,careerDataInput;
	public void updateProfileNoFinish() {

		firstNameInput = etUpdateProfileFname.getText().toString().trim();
		lastNameInput = etUpdateProfileLname.getText().toString().trim();
		addressInput = etUpdateProfileAddress.getText().toString().trim();
		countryInput = etUpdateProfileCountry.getText().toString().trim();
		phoneInput = etUpdateProfilePhone.getText().toString().trim();
		cityInput = etUpdateProfileDrCity.getText().toString();
		zipcodeInput = etUpdateProfileZipcode.getText().toString().trim();
		birthdateInput = etUpdateProfilBday.getText().toString().trim();
		introductionInput = etUpdateProfileIntroduction.getText().toString().trim();
		desigInput = etUpdateProfileOccupation.getText().toString().trim();
		qualificationInput = etUpdateProfileQualification.getText().toString().trim();
		careerDataInput = etUpdateProfileCareerData.getText().toString().trim();

		pd.show();

		RequestParams params = new RequestParams();
		params.put("id",prefs.getString("id", ""));
		params.put("first_name", firstNameInput);
		params.put("last_name", lastNameInput);
		params.put("address", addressInput);
		params.put("country", countryInput);
		params.put("phone", phoneInput);
		params.put("birthdate", birthdateInput);
		params.put("city", cityInput);
		params.put("state", userState);//etUpdateProfileState.getText().toString()
		params.put("introduction", introductionInput);
		params.put("designation", desigInput);
		params.put("zipcode", zipcodeInput);
		params.put("qualification", qualificationInput);
		params.put("career_data", careerDataInput);
		params.put("gender", gender);
		params.put("type", "doctor");

		ApiManager.addHeader(activity,client);

		DATA.print("-- params in updateProfile: "+params.toString());

		client.get(DATA.baseUrl+"updateProfile", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("-- Response in updateProfile : "+content);

					// remove the progress bar view
					//refreshMenuItem.setActionView(null);

					try {
						jsonObject = new JSONObject(content);
						status = jsonObject.getString("status");
						msg = jsonObject.getString("msg");

						if(status.equals("success")) {

							String userinfo = jsonObject.getString("user_info");

							user_info = new JSONObject(userinfo);

							//update from API not working anymore. Hence update locally
							//update preferences
							/*SharedPreferences.Editor ed = prefs.edit();
							ed.putString("first_name", user_info.getString("first_name"));
							ed.putString("last_name", user_info.getString("last_name"));
							ed.putString("country", user_info.getString("country"));
							//ed.putString("gender", user_info.getString("gender"));
							ed.putString("gender", user_info.getString("sex"));
							ed.putString("birthdate", user_info.getString("birthdate"));
							ed.putString("mobile", user_info.getString("mobile"));
							ed.putString("designation", user_info.getString("designation"));
							ed.putString("city", user_info.getString("city"));
							ed.putString("state", user_info.getString("state"));
							ed.putString("address", user_info.getString("address1"));
							ed.putString("introduction", user_info.getString("introduction"));
							ed.putString("zipcode", user_info.getString("zip_code"));
							ed.putString("qualification", user_info.getString("qualification"));
							ed.putString("career_data", user_info.getString("career_data"));
							ed.commit();*/


							SharedPreferences.Editor ed = prefs.edit();
							ed.putString("first_name", firstNameInput);
							ed.putString("last_name", lastNameInput);
							ed.putString("country", countryInput);
							//ed.putString("gender", user_info.getString("gender"));
							ed.putString("gender", gender);
							ed.putString("birthdate", birthdateInput);
							ed.putString("mobile", phoneInput);
							ed.putString("designation", desigInput);
							ed.putString("city", cityInput);
							ed.putString("state", userState);
							ed.putString("address", addressInput);
							ed.putString("introduction", introductionInput);
							ed.putString("zipcode", zipcodeInput);
							ed.putString("qualification", qualificationInput);
							ed.putString("career_data", careerDataInput);
							ed.commit();

//						DATA.imagePath = "";
//						DATA.isImageCaptured = false;

							//to remove after service becomes functioning
							customToast.showToast("Your profile has been updated successfully",0,0);
							finish();

						} else {
							customToast.showToast(msg,0,0);
						}

					} catch (JSONException e) {
						DATA.print("-- Exception in updateProfile: "+e);
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: updateProfile, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure updateProfile: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	public void setResultsFromImageUpload() {

		// remove the progress bar view
		DATA.imagePath = "";
		DATA.isImageCaptured = false;
//		DATA.isFromUpdateProfile = false;
//		pd.dismiss();

		DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, imgChangeProfileImg);

		customToast.showToast("Your profile image updated successfully",0,0);
//		Intent intent = new Intent(activity,MainActivityNew.class);
//		startActivity(intent);
//		finish();
	}


	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inPurgeable = true;
		options.inDensity = 1;
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		//Bitmap bmp = BitmapFactory.decodeFile(path, options);
		//	bmp = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, false);
		return BitmapFactory.decodeFile(path, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

}