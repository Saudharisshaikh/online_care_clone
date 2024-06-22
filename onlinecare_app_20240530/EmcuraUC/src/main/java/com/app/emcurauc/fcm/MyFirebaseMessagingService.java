/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.emcurauc.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.emcurauc.ActivityEliveAssignedPopup;
import com.app.emcurauc.BuildConfig;
import com.app.emcurauc.ConversationsActivity;
import com.app.emcurauc.GetLiveCare;
import com.app.emcurauc.LiveCareWaitingArea;
import com.app.emcurauc.Login;
import com.app.emcurauc.MainActivityNew;
import com.app.emcurauc.MyAppointments;
import com.app.emcurauc.OnlineCare;
import com.app.emcurauc.R;
import com.app.emcurauc.ShowGroupMessagePopup;
import com.app.emcurauc.ShowMessagePopupActivity;
import com.app.emcurauc.SupportMessagesActivity;
import com.app.emcurauc.VCallModule;
import com.app.emcurauc.ViewConversationActivity;
import com.app.emcurauc.model.ConversationBean;
import com.app.emcurauc.model.SupportMessageBean;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.EasyAES;
import com.app.emcurauc.util.IncomingCallResponse;
import com.app.emcurauc.util.SharedPrefsHelper;
import com.bumptech.glide.Glide;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            String message = remoteMessage.getData().get("message");

            generateAction(message, this);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    //=================================OLC Code====================================
    //int incoming_call_status = 0;
    JSONObject jsonObject = null;
    private void generateAction(String response, final Context context) {
        DATA.print("-- GCM msg "+response);
        // notifies user

        try {
            jsonObject = new JSONObject(response);

            String current_time = jsonObject.getString("current_time");
            String future_time = jsonObject.getString("future_time");

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("group_add_user")) {
                sendNotification(jsonObject.getJSONObject("data").getString("message"),MainActivityNew.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("group_message")) {

                new DATA().insertNotification(context,DATA.NOTIF_TYPE_GROUP_MESSAGE);

                JSONObject data = jsonObject.getJSONObject("data");
                sendNotification("New group message from: "+data.getString("from"),MainActivityNew.class);

                ShowGroupMessagePopup.messageFrom = data.getString("from");
                ShowGroupMessagePopup.messageText = data.getString("message");
                ShowGroupMessagePopup.messageTime = data.getString("date");
                ShowGroupMessagePopup.messageImageURL = data.getString("image");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCare.isAppInForground){
                        Intent intent = new Intent(context,ShowGroupMessagePopup.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        //app in background
                    }
                }else {
                    Intent intent = new Intent(context,ShowGroupMessagePopup.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return;
            }

            //DATA.msgTimeForPopup = current_time;
            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message")) {

                new DATA().insertNotification(context,DATA.NOTIF_TYPE_MESSAGE);

                //DATA.msgFromDoctor = jsonObject.getJSONObject("data").getString("message");
                //DATA.msgDoctorName = jsonObject.getJSONObject("data").getString("from");
                DATA.msgTextForPopup = jsonObject.getJSONObject("data").getString("message");
                DATA.msgPatientNameForPopup = jsonObject.getJSONObject("data").getString("from");
                DATA.msgPatientImageForPopup = jsonObject.getJSONObject("data").getString("image");

                DATA.msgTextForPopup = EasyAES.decryptString(DATA.msgTextForPopup);

                String to_id = "to_id";
                if (jsonObject.getJSONObject("data").has("to_id")) {
                    to_id = jsonObject.getJSONObject("data").getString("to_id");
                }

                DATA.selecetedBeanFromConversation = new ConversationBean("",
                        jsonObject.getJSONObject("data").getString("doctor_id"),
                        jsonObject.getJSONObject("data").getString("patient_id"),
                        DATA.msgTextForPopup,
                        jsonObject.getJSONObject("data").getString("send_from"),
                        jsonObject.getJSONObject("data").getString("date"),
                        DATA.msgPatientNameForPopup, "",
                        DATA.msgPatientImageForPopup,
                        "to",//"to"
                        jsonObject.getJSONObject("data").getString("doctor_id"),//"specialist_id"
                        jsonObject.getJSONObject("data").getString("send_from"),
                        to_id);//"user_type"
                //this value is for view conv from popup
                DATA.msgTimeForPopup = jsonObject.getJSONObject("data").getString("date");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCare.isAppInForground){
                        Intent i =new Intent(context,ShowMessagePopupActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else {
                        //app in background
                    }
                }else {
                    Intent i =new Intent(context,ShowMessagePopupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

                //generateMsgNotification(context, "New message from: "+jsonObject.getJSONObject("data").getString("from"));
                sendNotification("New message from: "+jsonObject.getJSONObject("data").getString("from"), ConversationsActivity.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("doctor_prescription")) {
                sendNotification(jsonObject.getJSONObject("data").getString("message") , MainActivityNew.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment")) {

                new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);//apptmnt confirmed
                //generateApptmntNotification(context, jsonObject.getJSONObject("data").getString("message"));
                sendNotification(jsonObject.getJSONObject("data").getString("message") , MyAppointments.class);
                return;
            }
            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("followup_nurse")) {
                new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);
                sendNotification(jsonObject.getJSONObject("data").getString("message") , MyAppointments.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment_refered")) {
                //generateApptmntNotification(context, jsonObject.getJSONObject("data").getString("message"));
                sendNotification(jsonObject.getJSONObject("data").getString("message") , MyAppointments.class);
                //alert(context, "Appointment Reffered", jsonObject.getJSONObject("data").getString("message"));
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("referred")) {
                //generateNotification(context, jsonObject.getJSONObject("data").getString("message"));
                sendNotification(jsonObject.getJSONObject("data").getString("message"), MainActivityNew.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("coordinator_message")) {

                String message = jsonObject.getJSONObject("data").getString("message");
                String from = jsonObject.getJSONObject("data").getString("from");
                String image = jsonObject.getJSONObject("data").getString("image");

                SupportMessageBean bean = new SupportMessageBean();
                bean.message = message;
                bean.image = image;
                bean.name = from;
                bean.type = "coordinator";
                if (SupportMessagesActivity.supportMsgList != null) {
                    SupportMessagesActivity.supportMsgList.add(bean);
                }

                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction("coordinator_message");
                intent.putExtra("message", message);
                intent.putExtra("from", from);
                intent.putExtra("image", image);
                context.sendBroadcast(intent);

                sendNotification("New message from support", SupportMessagesActivity.class);
                return;
            }

            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("coordinator")) {

                String status = jsonObject.getJSONObject("data").getString("status");
                if (status.equalsIgnoreCase("accept")) {
                    Intent intent = new Intent().setPackage(getPackageName());
                    intent.setAction(VCallModule.COORDINATOR_CALL_ACCEPTED);
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent().setPackage(getPackageName());
                    intent.setAction(VCallModule.COORDINATOR_CALL_REJECTED);
                    context.sendBroadcast(intent);
                }
                return;
            }
            if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message_delete")) {
                String message_id = jsonObject.getJSONObject("data").getString("message_id");
                //you also can remove messagebean from local array on behalf of this ID
                //at this time I m reloading service
                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction(ViewConversationActivity.REFRESH_CHAT_ACTION_MSG_DEL);
                context.sendBroadcast(intent);

                return;
            }

            jsonObject = jsonObject.getJSONObject("data");
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("disconnected")) {
                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
                context.sendBroadcast(intent);

                MyFirebaseMessagingService.stopCallNotAnswerTimer();

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    MyFirebaseMessagingService.sendStopBC(getBaseContext());
                }
            }else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("connected")) {
                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction(VCallModule.COORDINATOR_CALL_CONNECTED);
                context.sendBroadcast(intent);
                //Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
            }else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("disconnect_spe")) {
                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction(VCallModule.DISCONNECT_SPECIALIST);
                context.sendBroadcast(intent);
            }else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("assigned_doctor")) {

                String msg = jsonObject.optString("message");
                DATA.print("-- msg " + msg);
                sendNotification(msg, GetLiveCare.class);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCare.isAppInForground){
                        Intent i = new Intent(context, ActivityEliveAssignedPopup.class);
                        i.putExtra("popupMsg" , jsonObject.getString("message"));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else {
                        //app in background
                    }
                }else {
                    Intent i = new Intent(context, ActivityEliveAssignedPopup.class);
                    i.putExtra("popupMsg" , jsonObject.getString("message"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

                Intent intent = new Intent().setPackage(getPackageName());
                intent.setAction(LiveCareWaitingArea.ACTION_ASSIGNED_DOC);
                sendBroadcast(intent);

//                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
//                        setMessage("Your eLiveCare has been assigned to the Doctor. Please tap to check your eLiveCare status.").
//                        setPositiveButton("View Details", null);
//
//                try {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            Looper.prepare();
//                            final AlertDialog alertDialog = builder.create();
//                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                @Override
//                                public void onDismiss(DialogInterface dialog) {
//                                    Intent intent = new Intent(context,GetLiveCare.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(intent);
//                                }
//                            });
//                            //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//                            }
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//                            }
//                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                            }
//
//                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                //LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                                //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//                            } else {
//                                //LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
//                            }*/
//
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//                                if (!Settings.canDrawOverlays(getApplicationContext())) {
//                                    //Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                                    //getApplicationContext().startActivityForResult(intent, 0);
//                                }else {
//                                    //alertDialog.show();//android o issue
//                                }
//                            }else {
//                                alertDialog.show();
//                            }
//                            Looper.loop();
//
//                        }
//                    }.start();
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    e.printStackTrace();
//                }
            }else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("logout")) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);

                boolean debug_logs = SharedPrefsHelper.getInstance().get("debug_logs", false);

                String hospitalID = prefs.getString(Login.HOSP_ID_PREFS_KEY, "");
                String support_text = prefs.getString("support_text", "");

                SharedPreferences.Editor ed = prefs.edit();
				/*ed.putBoolean("HaveShownPrefs", false);
				ed.putBoolean("subUserSelected", false);
				ed.commit();*/
                ed.clear();
                ed.apply();


                //save hospital id and support_text on logout
                ed = prefs.edit();
                ed.putString(Login.HOSP_ID_PREFS_KEY, hospitalID);
                ed.putString("support_text", support_text);
                ed.commit();


                SharedPrefsHelper.getInstance().clearAllData();

                SharedPrefsHelper.getInstance().save("debug_logs", debug_logs);

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                return;
            }
            else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("newappointment")) {
                String msg = jsonObject.getString("message");
                sendNotification(msg, MyAppointments.class);
                return;
            }else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("update_covid_test")) {
                String msg = jsonObject.getString("message");
                sendNotification(msg, MainActivityNew.class);
                return;
            }else{

                int incoming_call_status = jsonObject.optInt("incoming_call_status");

                if(incoming_call_status == 1) {
                    DATA.print("--in incoming status 1");
                    if(jsonObject.has("first_name") && jsonObject.has("last_name")) {
                        DATA.incomingCallerName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");

                    }

                    if(jsonObject.has("picture")) {
                        DATA.incomingCallerImage = jsonObject.getString("picture");
                    }
                    if(jsonObject.has("session_id")) {
                        DATA.incomingCallSessionId = jsonObject.getString("session_id");
                    }
                    if(jsonObject.has("id")) {
                        DATA.incomingCallUserId = jsonObject.getString("id");
                    }
                    if(jsonObject.has("increment_id")) {
						/*SharedPreferences.Editor ed = prefs.edit();
						ed.putString("callingIDPatient", jsonObject.getString("increment_id"));
						ed.commit();*/
                        DATA.incommingCallId = jsonObject.getString("increment_id");
                    }


                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat format = new SimpleDateFormat(pattern);
                    Date callTime = format.parse(future_time);//"2016-01-28 19:44:31"
                    int compare = callTime.compareTo(new Date());
                    DATA.print("--compare dates "+compare);

                    if (compare == 1) {

                        if(MultiPartyVideoCallFragment.isInVideoCall){
                            IncomingCallResponse incomingCallResponse = new IncomingCallResponse(context, "reject");
                            //incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                            incomingCallResponse.sendResponse();
                            return;
                        }

                        /*Intent i = new Intent(context, MainActivity.class);//SampleActivity.class //changed context here from GCMintentservice.this back activity was closed due to that
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);//added context.
                        */

                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if(OnlineCare.isAppInForground){
                                Intent i = new Intent(this, MainActivity.class);//SampleActivity.class
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }else {
                                //callNotification();
                                OneTimeWorkRequest compressionWork =
                                        new OneTimeWorkRequest.Builder(BackgroundTaskJava.class)
                                                .build();
                                WorkManager.getInstance().enqueue(compressionWork);
                            }
                        }else {
                            Intent i = new Intent(this, MainActivity.class);//SampleActivity.class
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                        initCallNotAnswerTimer();

                    } else {
                        //generateNotification(context, "You have missed a live care call from "+DATA.incomingCallerName);
                        sendNotification("You have missed a live care call from "+DATA.incomingCallerName, MainActivityNew.class);
                    }


                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    //=================================OLC Code ends===================================

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, final Class<? extends Activity> activityToOpen) {

        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
        tempId = tempId+1;
        sharedPrefsHelper.save("notif_temp_id", tempId);
        DATA.print("-- notification id: "+tempId);

        Intent intent = new Intent(this, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, tempId /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(tempId /* ID of notification */, notificationBuilder.build());
    }




    //New call notification forground code for android 10 - Q- cant open activities from service anymore. GM
    public void callNotification(){

        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
        tempId = tempId+1;
        sharedPrefsHelper.save("notif_temp_id", tempId);
        DATA.print("-- notification id: "+tempId);

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notif_incomming_call);

        Intent fullScreenIntent = new Intent(this, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, tempId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      /*  *//*============================ Ahmer work =================================*//*
        //Intent for call Accept
        Intent callAcceptIntent = new Intent(this, MainActivity.class);
        callAcceptIntent.putExtra("fromNotification", true);
        PendingIntent callAcceptPendingIntent = PendingIntent.getActivity(this, 1, callAcceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for call reject
        Intent callDeclineIntent = new Intent(this, MainActivity.class);
        callDeclineIntent.putExtra("fromNotificationReject", false);
        PendingIntent callDeclinePendingIntent = PendingIntent.getActivity(this, 0, callDeclineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        *//*============================ Ahmer work end =================================*/

        String channelId = getString(R.string.app_name)+"_call_channel";//getString(R.string.default_notification_channel_id);
        Uri notifSoundUri = Uri.parse("android.resource://"+ getPackageName() +"/" + R.raw.incomingcall);
        //Uri notifSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,  channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setCustomContentView(notificationView)
//                        .setContentTitle(getResources().getString(R.string.app_name))
//                        .setContentText("New incoming video call from "+DATA.incomingCallerName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        .setSound(notifSoundUri)


                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(notifSoundUri, att);

            notificationManager.createNotificationChannel(channel);
        }


        Notification incomingCallNotification = notificationBuilder.build();


        /*incomingCallNotification.contentView = notificationView;
        //notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, fullScreenPendingIntent);
        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(this, CallNotificationBroadcast.class);
        switchIntent.setAction("com.app.emcuradr.acceptcall");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, pendingSwitchIntent);*/


        /*============================ Ahmer work =================================*/
        /*notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, callAcceptPendingIntent);

        notificationView.setOnClickPendingIntent(R.id.btnRejectCall, callDeclinePendingIntent);

        notificationView.setTextViewText(R.id.tvCallNotif , DATA.incomingCallerName+" is calling");

        try {
            Bitmap bitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(DATA.incomingCallerImage)
                    .submit(100, 100)
                    .get();
            notificationView.setImageViewBitmap(R.id.ivNotifCal, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*============================ Ahmer work end =================================*/


        // Provide a unique integer for the "notificationId" of each notification.
        startForeground(tempId, incomingCallNotification);

        //notificationManager.notify(tempId /* ID of notification */, incomingCallNotification);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).registerReceiver(stopJobReceiver , new IntentFilter(ACTION_STOP_JOB));
    }

    public static final String ACTION_STOP_JOB = BuildConfig.APPLICATION_ID+".actionStopCallNotification";

    private BroadcastReceiver stopJobReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_STOP_JOB)) {
                Log.d("unregister"," job stop receiver");
            /*try {
                unregisterReceiver(this); //Unregister receiver to avoid receiver leaks exception
            }catch (Exception e){e.printStackTrace();}*/
                stopForeground(true);
            }
        }
    };



    public static void sendStopBC(Context context){
        //ahmer add this line to remove notification
        NotificationManagerCompat.from(context).cancelAll();
        Intent stopJobService = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stopJobService = new Intent(MyFirebaseMessagingService.ACTION_STOP_JOB).setPackage(BuildConfig.APPLICATION_ID);
            LocalBroadcastManager.getInstance(context).sendBroadcast(stopJobService);//getBaseContext()
        }else{
            //Toast.makeText(context,"yet to be coded - stop service",Toast.LENGTH_SHORT).show();
        }
    }


    private static Timer timerNoAnswer;
    public void initCallNotAnswerTimer(){
        timerNoAnswer = new Timer();
        timerNoAnswer.schedule(new TimerTask() {
            public void run() {
                // when the task active then close the dialog
                DATA.print("------40 seconds");
                //callResponse = "noanswer";
                //showWaitingMessage();
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = () -> {
                    //Code that uses AsyncHttpClient in your case ConsultaCaract()
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(getApplicationContext(), "noanswer");
                    //incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    incomingCallResponse.sendResponse();
                };
                mainHandler.post(myRunnable);
                    /*if (mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                    }*/
                timerNoAnswer.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                //sendNotification("You have missed a live care call from "+DATA.incomingCallerName,MainActivityNew.class);
                //finish();

                Intent intent1 = new Intent().setPackage(getPackageName());
                intent1.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
                getApplicationContext().sendBroadcast(intent1);
                DATA.print("--broadcast sent");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    MyFirebaseMessagingService.sendStopBC(getBaseContext());
                }


            }
        }, 40000); //40000 // after 2 second (or 2000 miliseconds), the task will be active.
    }

    public static void stopCallNotAnswerTimer(){
        if(timerNoAnswer != null){
            timerNoAnswer.cancel();
            DATA.print("-- timerNoAnswer has been canceled - GM");
        }
    }

    //New call notification forground code for android 10 - Q- cant open activities from service anymore. GM
}
