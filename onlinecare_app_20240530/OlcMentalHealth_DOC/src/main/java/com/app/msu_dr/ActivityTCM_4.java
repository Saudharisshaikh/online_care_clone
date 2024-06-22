package com.app.msu_dr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_dr.R;
import com.app.msu_dr.adapter.LiveCareAdapter;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.MyAppointmentsModel;
import com.app.msu_dr.model.ReportsModel;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.GloabalMethods;
import com.app.msu_dr.util.HideShowKeypad;
import com.app.msu_dr.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityTCM_4 extends BaseActivity implements View.OnClickListener{
//note : this activity is for select patient for EMS
    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    //ViewFlipper vfTCM;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,tvTabAll;
    ListView lvTCM;//,lvCC,lvHomeHealth,lvNursingHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcm2);

        activity = ActivityTCM_4.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        //vfTCM = (ViewFlipper) findViewById(R.id.vfTCM);
        lvTCM = (ListView) findViewById(R.id.lvTCM);
        //lvCC = (ListView) findViewById(R.id.lvCC);
        //lvHomeHealth = (ListView) findViewById(R.id.lvHomeHealth);
        //lvNursingHome = (ListView) findViewById(R.id.lvNursingHome);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        tvTabAll = (TextView) findViewById(R.id.tvTabAll);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);

        lvTCM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DATA.selectedLiveCare = DATA.allAppointments.get(position);

                new AlertDialog.Builder(activity).setTitle("NEMT").setMessage("Please select video call or meassage to the NEMT Command Center")
                        .setPositiveButton("Video Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DATA.incomingCall = false;
                                DATA.isFromDocToDoc = true;
                                Intent i = new Intent(activity,MainActivity.class);
                                i.putExtra("isFromCallToEMS", true);
                                startActivity(i);
                            }
                        }).setNegativeButton("Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity,SupportMessagesActivity.class);
                        intent.putExtra("isFromEmsMsg",true);
                        startActivity(intent);
                    }
                }).show();
            }
        });
        if (checkInternetConnection.isConnectedToInternet()){
            getPatientBycategory(patient_category);
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(0);
                patient_category = "tcm";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(1);
                patient_category = "complex_care";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(2);
                patient_category = "home_health";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(3);
                patient_category = "nursing_home";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabAll:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));

                //vfTCM.setDisplayedChild(3);
                patient_category = "all";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            default:
                break;
        }
    }

    String patient_category = "all";
    private void getPatientBycategory(String patient_category) {

        DATA.showLoaderDefault(activity,"");

        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_category", patient_category);

        String reqURL = DATA.baseUrl+"getPatientBycategory";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    DATA.print("--online care response in get livecare patients: "+content);
                    try {

                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray appointmentsArray = jsonObject.getJSONArray("livecheckups");

                        /*if (appointmentsArray.length() == 0) {
                            lvLiveCare.setVisibility(View.GONE);
                            tvNoLiveCares.setVisibility(View.VISIBLE);
                        } else {
                            lvLiveCare.setVisibility(View.VISIBLE);
                            tvNoLiveCares.setVisibility(View.GONE);*/

                        DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
                        MyAppointmentsModel temp = new MyAppointmentsModel();

                        for(int i = 0; i<appointmentsArray.length(); i++) {


                            temp = new MyAppointmentsModel();
                            temp.id = appointmentsArray.getJSONObject(i).getString("id");
                            temp.liveCheckupId = "";//appointmentsArray.getJSONObject(i).getString("live_checkup_id");
                            temp.first_name = appointmentsArray.getJSONObject(i).getString("first_name");
                            temp.last_name = appointmentsArray.getJSONObject(i).getString("last_name");
                            temp.symptom_name = "";//appointmentsArray.getJSONObject(i).getString("symptom_name");
                            temp.condition_name = "";//appointmentsArray.getJSONObject(i).getString("condition_name");
                            temp.description = "";//appointmentsArray.getJSONObject(i).getString("description");
                            temp.patients_qbid = appointmentsArray.getJSONObject(i).getString("patients_qbid");
                            temp.datetime = "";//appointmentsArray.getJSONObject(i).getString("datetime");

                            temp.latitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("latitude"));
                            temp.longitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("longitude"));
                            if (appointmentsArray.getJSONObject(i).has("image")) {
                                temp.image = appointmentsArray.getJSONObject(i).getString("image");
                            } else {
                                temp.image = "";
                            }

                            temp.birthdate = appointmentsArray.getJSONObject(i).getString("birthdate");
                            temp.gender = appointmentsArray.getJSONObject(i).getString("gender");
                            temp.residency = appointmentsArray.getJSONObject(i).getString("residency");
                            temp.patient_phone = appointmentsArray.getJSONObject(i).getString("patient_phone");
                            temp.StoreName = "";//appointmentsArray.getJSONObject(i).getString("StoreName");
                            temp.PhonePrimary = "";//appointmentsArray.getJSONObject(i).getString("PhonePrimary");

                            temp.is_online = appointmentsArray.getJSONObject(i).getString("is_online");

                            if (appointmentsArray.getJSONObject(i).has("reports")) {
                                String rp = appointmentsArray.getJSONObject(i).getString("reports");
                                JSONArray reports = new JSONArray(rp);
                                temp.sharedReports = new ArrayList<ReportsModel>();
                                //ReportsModel reportsModel;
                                for (int j = 0; j < reports.length(); j++) {
                                    ReportsModel reportsModel = new ReportsModel();

                                    reportsModel.name = reports.getJSONObject(j).getString("file_display_name");
                                    reportsModel.url = reports.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
                                    reportsModel.patientID = reports.getJSONObject(j).getString("patient_id");//patient_id

                                    temp.sharedReports.add(reportsModel);

                                    //reportsModel = null;
                                }
                            } else {
                                temp.sharedReports = new ArrayList<ReportsModel>();
                            }

                            DATA.allAppointments.add(temp);

                            temp = null;
                        }

						/*String reprts = jsonObject.getString("reports");

						reportsArray = new JSONArray(reprts);

						DATA.allReports = new ArrayList<ReportsModel>();

						ReportsModel temp1;

						for(int j = 0; j<reportsArray.length(); j++) {

							temp1 = new ReportsModel();

							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id

							DATA.allReports.add(temp1);

							temp1 = null;
						}*/

                        LiveCareAdapter liveCareAdapter = new LiveCareAdapter(activity);
                        lvTCM.setAdapter(liveCareAdapter);

                        //}



                    } catch (JSONException e) {
                        DATA.print("--online care exception in getlivecare patients: "+e);
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content , statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });
    }
}