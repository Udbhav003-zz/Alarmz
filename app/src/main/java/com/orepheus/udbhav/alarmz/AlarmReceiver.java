package com.orepheus.udbhav.alarmz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;



public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ReceivedBroad","Beep Beep");
        int pos = intent.getIntExtra("pos",-1);
        PackageManager pm = context.getPackageManager();
        Intent launch = pm.getLaunchIntentForPackage("com.orepheus.udbhav.alarmz");
        launch.putExtra("pos",pos);
        context.startActivity(launch);
    }
}
