package com.orepheus.udbhav.alarmz;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import static com.orepheus.udbhav.alarmz.MainActivity.arrayList;
import static com.orepheus.udbhav.alarmz.MainActivity.f;
import static com.orepheus.udbhav.alarmz.MainActivity.c;
import static com.orepheus.udbhav.alarmz.MainActivity.mAdapter;



public class Main2Activity extends AppCompatActivity {

    int pos;
    public static final int RC_AUDIOPICKER = 3;
    SQLiteDatabase db;
    String to_db;
    Button button1,button2,button3,button4,button5,button6,button7,record,show;

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void initializeViews(){
        record = findViewById(R.id.record);
        show = findViewById(R.id.show);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);

    }

    public void setMode(){
        if(f==0){
            if(c==0){
                setTheme(R.style.DarkModeRed);
            }
            else if(c==1){
                setTheme(R.style.DarkModePink);
            }
            else if(c==2){
                setTheme(R.style.DarkModeBlue);
            }
            else if(c==3){
                setTheme(R.style.DarkModeGreen);
            }
            else if(c==4){
                setTheme(R.style.DarkModeOrange);
            }

        }
        else if(f==1){
            if(c==0){
                setTheme(R.style.LightModeRed);
            }
            else if(c==1){
                setTheme(R.style.LightModePink);
            }
            else if(c==2){
                setTheme(R.style.LightModeBlue);
            }
            else if(c==3){
                setTheme(R.style.LightModeGreen);
            }
            else if(c==4){
                setTheme(R.style.LightModeOrange);
            }

        }
        setContentView(R.layout.activity_main2);
    }

    public void setInitBackgroundForDays(){
        for(int i = 0 ; i<7 ; i++){

            if(arrayList.get(pos).getDay(i)==1) {

                if (i == 0) {
                    button1.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 1) {
                    button2.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 2) {
                    button3.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 3) {
                    button4.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 4) {
                    button5.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 5) {
                    button6.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
                if (i == 6) {
                    button7.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
                }
            }

        }

    }

    private void animateButtons() {
        show.setScaleX(0);
        show.setScaleY(0);
        record.setScaleX(0);
        record.setScaleY(0);

        record.animate().scaleXBy(1).scaleYBy(1).setDuration(400);
        show.animate().scaleXBy(1).scaleYBy(1).setDuration(400);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setMode();
        super.onCreate(savedInstanceState);

        TextView tv = findViewById(R.id.tv);

        final Intent intent = getIntent();
        intent.getIntExtra("pos",-1);
        if(pos!=-1) {
            tv.setText(arrayList.get(pos).getTime());
        }

        db = this.openOrCreateDatabase("Alarms",MODE_PRIVATE,null);

        initializeViews();

        setInitBackgroundForDays();

        animateButtons();


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(Main2Activity.this, RecordingActivity.class);
                intent1.putExtra("pos",pos);
                startActivity(intent1);
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(Intent.createChooser(i,"Choose tone from file"),RC_AUDIOPICKER);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RC_AUDIOPICKER && resultCode==RESULT_OK){
            Uri recording = data.getData();
            String path = getPathFromUri(this,recording);
            Log.i("AudioPath",path);
            Uri audio = Uri.parse(path);
            if(audio!=null) {

                db.execSQL("UPDATE AlarmsTable SET Ringtone = '" + audio.toString() + "' WHERE Time = '" + arrayList.get(pos).time + "'");

                arrayList.get(pos).setRingName(audio.getLastPathSegment());
                arrayList.get(pos).setRingtone(audio);
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void dayClick(View view) {

        if(arrayList.get(pos).getDay(Integer.valueOf(view.getTag().toString()))==0) {
            arrayList.get(pos).setDay(Integer.valueOf(view.getTag().toString()),1);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.selectday));
            TimeSet item = arrayList.get(pos);

            if (Integer.valueOf(view.getTag().toString())== 0) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                calendar1.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar1.set(Calendar.MINUTE, item.getMin());
                calendar1.set(Calendar.SECOND, 0);
                long timeToAlarm = calendar1.getTimeInMillis();
                if (timeToAlarm < System.currentTimeMillis()) {

                    timeToAlarm += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent = new Intent(this, AlarmReceiver.class);

                myIntent.putExtra("pos", item.getReq());
                item.pintent[0] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "0"), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm, AlarmManager.INTERVAL_DAY * 7, item.pintent[0]);
            } else if (Integer.valueOf(view.getTag().toString()) == 1) {
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendar2.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar2.set(Calendar.MINUTE, item.getMin());
                calendar2.set(Calendar.SECOND, 0);
                long timeToAlarm1 = calendar2.getTimeInMillis();
                if (timeToAlarm1 < System.currentTimeMillis()) {

                    timeToAlarm1 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent1 = new Intent(this, AlarmReceiver.class);

                myIntent1.putExtra("pos", item.getReq());
                item.pintent[1] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "1"), myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm1, AlarmManager.INTERVAL_DAY * 7, item.pintent[1]);
            } else if (Integer.valueOf(view.getTag().toString()) == 2) {
                Calendar calendar3 = Calendar.getInstance();
                calendar3.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                calendar3.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar3.set(Calendar.MINUTE, item.getMin());
                calendar3.set(Calendar.SECOND, 0);
                long timeToAlarm2 = calendar3.getTimeInMillis();
                if (timeToAlarm2 < System.currentTimeMillis()) {

                    timeToAlarm2 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent2 = new Intent(this, AlarmReceiver.class);

                myIntent2.putExtra("pos", item.getReq());
                item.pintent[2] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "2"), myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm2, AlarmManager.INTERVAL_DAY * 7, item.pintent[2]);
            } else if (Integer.valueOf(view.getTag().toString())== 3) {
                Calendar calendar4 = Calendar.getInstance();
                calendar4.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                calendar4.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar4.set(Calendar.MINUTE, item.getMin());
                calendar4.set(Calendar.SECOND, 0);
                long timeToAlarm3 = calendar4.getTimeInMillis();
                if (timeToAlarm3 < System.currentTimeMillis()) {

                    timeToAlarm3 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent3 = new Intent(this, AlarmReceiver.class);

                myIntent3.putExtra("pos", item.getReq());
                item.pintent[3] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "3"), myIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm3, AlarmManager.INTERVAL_DAY * 7, item.pintent[3]);
            } else if (Integer.valueOf(view.getTag().toString()) == 4) {
                Calendar calendar5 = Calendar.getInstance();
                calendar5.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                calendar5.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar5.set(Calendar.MINUTE, item.getMin());
                calendar5.set(Calendar.SECOND, 0);
                long timeToAlarm4 = calendar5.getTimeInMillis();
                if (timeToAlarm4 < System.currentTimeMillis()) {

                    timeToAlarm4 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent4 = new Intent(this, AlarmReceiver.class);

                myIntent4.putExtra("pos", item.getReq());
                item.pintent[4] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "4"), myIntent4, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm4, AlarmManager.INTERVAL_DAY * 7, item.pintent[4]);
            } else if (Integer.valueOf(view.getTag().toString()) == 5) {
                Calendar calendar6 = Calendar.getInstance();
                calendar6.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                calendar6.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar6.set(Calendar.MINUTE, item.getMin());
                calendar6.set(Calendar.SECOND, 0);
                long timeToAlarm5 = calendar6.getTimeInMillis();
                if (timeToAlarm5 < System.currentTimeMillis()) {

                    timeToAlarm5 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent5 = new Intent(this, AlarmReceiver.class);

                myIntent5.putExtra("pos", item.getReq());
                item.pintent[5] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "5"), myIntent5, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm5, AlarmManager.INTERVAL_DAY * 7, item.pintent[5]);
            } else if (Integer.valueOf(view.getTag().toString()) == 6) {
                Calendar calendar7 = Calendar.getInstance();
                calendar7.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                calendar7.set(Calendar.HOUR_OF_DAY, item.getHr());
                calendar7.set(Calendar.MINUTE, item.getMin());
                calendar7.set(Calendar.SECOND, 0);
                long timeToAlarm6 = calendar7.getTimeInMillis();
                if (timeToAlarm6 < System.currentTimeMillis()) {

                    timeToAlarm6 += 7 * 24 * 60 * 60 * 1000;

                }
                Intent myIntent6 = new Intent(this, AlarmReceiver.class);

                myIntent6.putExtra("pos", item.getReq());
                item.pintent[6] = PendingIntent.getBroadcast(this, Integer.valueOf(item.req + "6"), myIntent6, PendingIntent.FLAG_UPDATE_CURRENT);

                item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm6, AlarmManager.INTERVAL_DAY * 7, item.pintent[6]);

            }
            arrayList.get(pos).setDay(Integer.valueOf(view.getTag().toString()),1);

            to_db = "";

            for(int j = 0;j<item.days.length;j++){
                to_db += String.valueOf(item.getDay(j));
            }
            db.execSQL("UPDATE AlarmsTable SET RepeatDays = '"+to_db+"' WHERE Time = '"+item.getTime()+"'");
            Log.i("Value",to_db);

        }
        else {
            arrayList.get(pos).setDay(Integer.valueOf(view.getTag().toString()), 0);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.recording));
            TimeSet item = arrayList.get(pos);
            item.manager.cancel(item.pintent[Integer.valueOf(view.getTag().toString())]);
            to_db = "";

            for(int j = 0;j<item.days.length;j++){
                to_db += String.valueOf(item.getDay(j));
            }
            db.execSQL("UPDATE AlarmsTable SET RepeatDays = '"+to_db+"' WHERE Time = '"+item.getTime()+"'");
            Log.i("Value",to_db);

        }
        mAdapter.notifyDataSetChanged();
    }
}
