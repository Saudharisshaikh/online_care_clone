package com.covacard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.covacard.R;
import com.covacard.api.ApiManager;
import com.covacard.adapter.CardAdapter;
import com.covacard.model.CardBean;
import com.covacard.util.DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityIdCardsList extends ActivityBaseDrawer {


    ListView lvCardsList;
    SwipeRefreshLayout swiperefresh;
    LinearLayout layNoData;
    Button btnAddNew;

    static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;
            loadListData();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_id_cards_list);
        getLayoutInflater().inflate(R.layout.activity_id_cards_list, container_frame);

        lvCardsList = findViewById(R.id.lvCardsList);
        layNoData = findViewById(R.id.layNoData);
        btnAddNew = findViewById(R.id.btnAddNew);

        /*lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);

                selectedCardBean = cardBeans.get(position);
            }
        });*/

        /*setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ID Cards");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddIdCard.class, false);
            }
        });*/

        tvToolbarTitle.setText("ID Cards");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.VISIBLE);
        btnToolbarAdd.setText("Add New");
        btnToolbarAdd.setOnClickListener(v -> {
            openActivity.open(ActivityAddIdCard.class, false);
        });
        btnAddNew.setOnClickListener(v -> {
            openActivity.open(ActivityAddIdCard.class, false);
        });


        lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityCardDetail.class, false);
                ActivityCardDetail.cardBeanSelected = cardBeans.get(position);
                Intent intent = new Intent(activity, ActivityCardDetail.class);
                intent.putExtra("isFromIdCard", true);
                startActivity(intent);
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


        loadListData();


        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }

    public void loadListData(){
        String all_id_cards_json = sharedPrefsHelper.get("all_id_cards_json", "");
        if(!TextUtils.isEmpty(all_id_cards_json)){
            parseData(all_id_cards_json);
            ApiManager.shouldShowLoader = false;
        }
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_ID_CARDS, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CardBean> cardBeans;
    CardAdapter cardAdapter;
    public static CardBean selectedCardBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_ID_CARDS)){
            swiperefresh.setRefreshing(false);
            parseData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.DELETE_ID_CARD)){
            //{"status":"success","message":"Deleted."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    cardBeans.remove(listPos);
                    cardAdapter.notifyDataSetChanged();
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
                layNoData.setVisibility(View.VISIBLE);
            }else {
                layNoData.setVisibility(View.GONE);
            }

            Type listType = new TypeToken<ArrayList<CardBean>>() {}.getType();
            cardBeans = new Gson().fromJson(data.toString(), listType);

            cardAdapter = new CardAdapter(activity, cardBeans);
            lvCardsList.setAdapter(cardAdapter);

            sharedPrefsHelper.save("all_id_cards_json",content);

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
        listPos = pos;
        RequestParams params = new RequestParams();
        params.put("id", cardBeans.get(pos).id);
        params.put("patient_id", prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.DELETE_ID_CARD,"post",params,this, activity);
        //ApiManager.shouldShowLoader = showLoader;
        apiManager.loadURL();
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
