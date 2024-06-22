package com.app.mhcsn_uc;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.braintreepayments.demo.Settings;
import com.braintreepayments.demo.internal.ApiClient;
import com.braintreepayments.demo.internal.ApiClientRequestInterceptor;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.net.URISyntaxException;

import io.agora.openlive.Constants;
import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PrefManager;
import io.agora.rtc.RtcEngine;
import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit.RestAdapter;

/**
 * Created by Engr G M on 2/20/2017.
 */

public class OnlineCare extends Application  implements AppLifecycleHandler.LifeCycleDelegate{

    private static OnlineCare instance;
    SharedPreferences prefs;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //initImageLoader(getApplicationContext());


        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);




        AppLifecycleHandler lifeCycleHandler = new AppLifecycleHandler((AppLifecycleHandler.LifeCycleDelegate)this);
        this.registerLifecycleHandler(lifeCycleHandler);



        // register to be informed of activities starting up
        /*registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                DATA.print("--onActivityCreated "+activity.getClass().getName());
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
                DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
                DATA.print("-- DATA.baseUrl in Application Class "+DATA.baseUrl);
            }
            @Override
            public void onActivityDestroyed(Activity arg0) {}
            @Override
            public void onActivityPaused(Activity arg0) {}
            @Override
            public void onActivityResumed(Activity arg0) {}
            @Override
            public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {}
            @Override
            public void onActivityStarted(Activity arg0) {}
            @Override
            public void onActivityStopped(Activity arg0) {}
        });*/




        //===============Angora.io Starts============================================
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();
        //===============Angora.io Ends============================================
    }


    public static synchronized OnlineCare getInstance() {
        return instance;
    }

    /*public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    //final String CHAT_SERVER_URL = "http://ec2-18-220-180-242.us-east-2.compute.amazonaws.com:3000";
    //final String CHAT_SERVER_URL = "https://telelivecare.com:3000/";
    final String CHAT_SERVER_URL = "https://onlinecare.com:3000/";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }






    public static boolean isAppInForground = false;
    @Override
    public void onAppBackgrounded() {
        Log.d("-- App OLC lifeCicle: ", "App in background");
        isAppInForground = false;
    }
    @Override
    public void onAppForegrounded() {
        Log.d("-- App OLC lifeCicle: ", "App in foreground");
        isAppInForground = true;
    }

    private final void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        this.registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks)lifeCycleHandler);
        this.registerComponentCallbacks((ComponentCallbacks)lifeCycleHandler);
    }




    //===============Angora.io Starts============================================
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
    //===============Angora.io Ends============================================

    //============================BrainTree SDK================================
    private static ApiClient sApiClient;
    public static ApiClient getApiClient(Context context) {
        if (sApiClient == null) {
            sApiClient = new RestAdapter.Builder()
                    .setEndpoint(Settings.getEnvironmentUrl(context))
                    .setRequestInterceptor(new ApiClientRequestInterceptor())
                    .build()
                    .create(ApiClient.class);
        }

        return sApiClient;
    }

    public static void resetApiClient() {
        sApiClient = null;
    }
    //============================BrainTree SDK================================
}