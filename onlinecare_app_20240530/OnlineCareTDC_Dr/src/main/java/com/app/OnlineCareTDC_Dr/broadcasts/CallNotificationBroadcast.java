package com.app.OnlineCareTDC_Dr.broadcasts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CallNotificationBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        //com.app.emcuradr.acceptcall


        if (action != null && action.equals("com.journaldev.AN_INTENT")) {
            Toast.makeText(context, "Explicit Broadcast was triggered", Toast.LENGTH_SHORT).show();
        }
        if (("android.net.conn.CONNECTIVITY_CHANGE").equals(action)) {
            Toast.makeText(context, "Implicit Broadcast was triggered using registerReceiver", Toast.LENGTH_SHORT).show();
        }

    }




    //send bc from out side
    /*public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.journaldev.AN_INTENT");
        intent.setComponent(new ComponentName(getPackageName(),"com.journaldev.androidoreobroadcastreceiver.MyReceiver"));
        getApplicationContext().sendBroadcast(intent);
    }*/


}