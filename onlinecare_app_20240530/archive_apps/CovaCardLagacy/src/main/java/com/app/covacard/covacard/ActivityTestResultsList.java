package com.app.covacard.covacard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.covacard.BaseActivity;
import com.app.covacard.R;
import com.app.covacard.api.ApiManager;
import com.app.covacard.util.DATA;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityTestResultsList extends BaseActivity{


    ListView lvCardsList;
    TextView tvNoData;
    SwipeRefreshLayout swiperefresh;
    Button btnSyncEmail;
    TextView tvEmailInfo;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_list);

        lvCardsList = findViewById(R.id.lvCardsList);
        tvNoData = findViewById(R.id.tvNoData);
        btnSyncEmail = findViewById(R.id.btnSyncEmail);
        tvEmailInfo = findViewById(R.id.tvEmailInfo);


        String info = "Please make sure that you have to send your test results from the email registerd with your COVACARD acount. Your registered email is : "
                +prefs.getString("email", "");
        tvEmailInfo.setText(info);

        lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);

                selectedTestResultBean = testResultBeans.get(position);
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("COVID Test Results");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddTestResults.class, false);
            }
        });


        //======================swip to refresh==================================
        swiperefresh = findViewById(R.id.swiperefresh);
        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        swiperefresh.setColorSchemeColors(colorsArr);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!checkInternetConnection.isConnectedToInternet()){
                            swiperefresh.setRefreshing(false);
                        }
                        loadListData();
                    }
                }
        );
        //======================swip to refresh ends=============================


        btnSyncEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
                ApiManager apiManager = new ApiManager(ApiManager.FETCH_EMAIL, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        loadListData();
    }

    public void loadListData(){
        String all_test_results = sharedPrefsHelper.get("all_test_results", "");
        if(!TextUtils.isEmpty(all_test_results)){
            parseData(all_test_results);
            ApiManager.shouldShowLoader = false;
        }
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_RESULT, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<TestResultBean> testResultBeans;
    TestResultAdapter testResultAdapter;
    public static TestResultBean selectedTestResultBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_RESULT)){
            swiperefresh.setRefreshing(false);
            parseData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.FETCH_EMAIL)){
            //{"success":1,"message":"Fetched"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast("Email has been fetched successfully. We are loading your test results now.",0,0);
                    loadListData();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    private void parseData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray data = jsonObject.getJSONArray("data");

            if(data.length() == 0){
                tvNoData.setVisibility(View.VISIBLE);
            }else {
                tvNoData.setVisibility(View.GONE);
            }

            Type listType = new TypeToken<ArrayList<TestResultBean>>() {}.getType();
            testResultBeans = new Gson().fromJson(data.toString(), listType);

            testResultAdapter = new TestResultAdapter(activity, testResultBeans);
            lvCardsList.setAdapter(testResultAdapter);

            sharedPrefsHelper.save("all_test_results", content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }



    /*public void viewOrEditForm(int position, boolean isReadOnly){

        selectedDastListBean = dastListBeans.get(position);

        Intent intent = new Intent(activity,ActivityDAST_Form.class);
        intent.putExtra("isEdit",true);

        intent.putExtra("isReadOnly", isReadOnly);

        startActivity(intent);
    }*/


    int listPos;
    public void deleteCard(int pos){
        customToast.showToast("Feature will be available in the near future", 0,0);
        /*listPos = pos;
        RequestParams params = new RequestParams();
        params.put("id", cardBeans.get(pos).name);
        ApiManager apiManager = new ApiManager(ApiManager.DELETE_INSURANCE,"post",params,this, activity);
        //ApiManager.shouldShowLoader = showLoader;
        apiManager.loadURL();*/
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityBradenScale.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }*/
}
