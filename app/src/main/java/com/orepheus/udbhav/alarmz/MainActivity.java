package com.orepheus.udbhav.alarmz;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static ArrayList<TimeSet> arrayList;
    int hr,min;
    static MyAdapter mAdapter;
    SQLiteDatabase db;
    RecyclerView rv;
    String aTime;
    BottomSheetDialog dialogColor;
    static LinearLayout message;
    static MediaPlayer mp = null;
    static TextToSpeech[] tts = null;
    int val = -1;
    static int f;
    boolean newClock = true;
    static SharedPreferences preferences;
    static int c;


    public void updateRv(){

        Cursor c = db.rawQuery("SELECT * FROM AlarmsTable",null);

        if(c!=null) {
            message.setVisibility(View.INVISIBLE);
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex("id");
                int hrIndex = c.getColumnIndex("Hour");
                int minIndex = c.getColumnIndex("Min");
                int timeIndex = c.getColumnIndex("Time");
                int ringIndex = c.getColumnIndex("Ringtone");
                int labelIndex = c.getColumnIndex("Label");
                int statusIndex = c.getColumnIndex("Status");
                int repIndex = c.getColumnIndex("RepeatDays");

                arrayList.clear();

                do{
                    String hrday = c.getString(hrIndex);
                    String minute = c.getString(minIndex);
                    int req = c.getInt(idIndex);
                    String time = c.getString(timeIndex);
                    String ringtone = c.getString(ringIndex);
                    String label= c.getString(labelIndex);
                    String status = c.getString(statusIndex);
                    String repdays = c.getString(repIndex);

                    int days[] = new int[7];
                    for(int i=0;i<repdays.length();i++){
                        days[i] = Integer.parseInt(String.valueOf(repdays.charAt(i)));

                    }


                    Uri ring = null;
                    if(ringtone!=null){
                        ring = Uri.parse(ringtone);
                    }

                    arrayList.add(new TimeSet(Integer.valueOf(hrday),Integer.valueOf(minute),time,ring,label,Boolean.parseBoolean(status),req,days));

                    mAdapter.notifyDataSetChanged();

                }

                while (c.moveToNext());

            }
            c.close();
        }

        if(arrayList.isEmpty()){

            mAdapter.notifyDataSetChanged();
            message.setVisibility(View.VISIBLE);

        }
        rv.setAdapter(mAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = this.getSharedPreferences("mode",MODE_PRIVATE);
        f = preferences.getInt("m",-1);
        c = preferences.getInt("c",-1);
        if(f==-1 ){
            preferences.edit().putInt("m",0).apply();
            if(c==-1) {
                preferences.edit().putInt("c", 2).apply();
                setTheme(R.style.DarkModeBlue);
            }

        }
        else if(f==0){
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
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        message = findViewById(R.id.message);
        FloatingActionButton fb = findViewById(R.id.fb);


        //this.deleteDatabase("Alarms");

        db = this.openOrCreateDatabase("Alarms",MODE_PRIVATE,null);
        //db.execSQL("DROP TABLE IF EXISTS AlarmsTable");
        db.execSQL("CREATE TABLE IF NOT EXISTS AlarmsTable(id INT PRIMARY KEY,Hour VARCHAR, Min VARCHAR, Time VARCHAR NOT NULL, Ringtone VARCHAR, Label VARCHAR, Status VARCHAR, RepeatDays VARCHAR)");

        arrayList = new ArrayList<>();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyAdapter(arrayList);

        updateRv();
        rv.setAdapter(mAdapter);




        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hr = hourOfDay;
                min = minute;

                for (int i = 0; i < arrayList.size(); i++) {

                    if (hr == arrayList.get(i).getHr() && min == arrayList.get(i).getMin()) {

                        newClock = false;

                    }
                }
                if (newClock) {
                    String timeSet = "";
                    if (hr > 12) {
                        hr -= 12;
                        timeSet = "PM";
                    } else if (hr == 0) {
                        hr += 12;
                        timeSet = "AM";
                    } else if (hr == 12)
                        timeSet = "PM";
                    else
                        timeSet = "AM";
                    String minutes = "";
                    if (min < 10)
                        minutes = "0" + min;
                    else
                        minutes = String.valueOf(min);
                    String hours = String.valueOf(hr);

                    Random random = new Random();
                    int n1 = random.nextInt(10);
                    int n2 = random.nextInt(10);
                    int n3 = random.nextInt(10);
                    int n4 = random.nextInt(10);
                    int req = Integer.valueOf(n1 + "" + n2 + "" + n3 + "" +n4);

                    aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();

                    String sql = "Insert into AlarmsTable(id,Hour,Min,Time,RepeatDays) values(?, ? ,? ,? ,?)";
                    SQLiteStatement statement = db.compileStatement(sql);
                    statement.bindString(1, String.valueOf(req));
                    statement.bindString(2, String.valueOf(hourOfDay));
                    statement.bindString(3, String.valueOf(min));
                    statement.bindString(4, aTime);
                    statement.bindString(5,"0000000");
                    statement.execute();


                    arrayList.add(new TimeSet(hourOfDay, min, aTime, null, null, true, req,new int[]{0,0,0,0,0,0,0}));
                    mAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(MainActivity.this, "Alarm Exists!!!", Toast.LENGTH_SHORT).show();
            }
        };
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.size() < 20) {
                    newClock = true;
                    new TimePickerDialog(MainActivity.this, listener, hr, min, false).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Time to delete some alarms!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Intent intent = getIntent();
        final int rq = intent.getIntExtra("pos",-1);
        if(arrayList!=null) {
            for (int i = 0; i < arrayList.size(); i++) {

                if (arrayList.get(i).getReq() == rq) {
                    val = i;
                }

            }
        }

        if(val>=0 && val<=19){

            final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.receiver);
            dialog.show();
            FloatingActionButton snooze = dialog.findViewById(R.id.snooze);
            FloatingActionButton cancel = dialog.findViewById(R.id.cancel);
            TextView labeltv= dialog.findViewById(R.id.labeltv);

            if(arrayList.get(val).getLabel()!=null){

                labeltv.setText(arrayList.get(val).getLabel());
                if(tts==null) {
                    tts = new TextToSpeech[1];
                }

                tts[0] = new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {

                            //Locale locale = new Locale("hi", "IN");
                            tts[0].setLanguage(Locale.ENGLISH);
                            String str = arrayList.get(val).getLabel();
                            tts[0].setSpeechRate((float) 0.8);
                            final HashMap<String,String> map = new HashMap<>();
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,String.valueOf(arrayList.get(val).getReq()));
                            tts[0].speak(str, TextToSpeech.QUEUE_FLUSH, map);

                            tts[0].setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                @Override
                                public void onStart(String utteranceId) {

                                }

                                @Override
                                public void onDone(String utteranceId) {
                                    //Locale locale = new Locale("hi", "IN");
                                    tts[0].setLanguage(Locale.ENGLISH);
                                    String str = arrayList.get(val).getLabel();
                                    tts[0].setSpeechRate((float) 0.8);
                                    tts[0].speak(str, TextToSpeech.QUEUE_FLUSH,map);

                                }

                                @Override
                                public void onError(String utteranceId) {

                                    Log.i("Texttospeech","Error");

                                }
                            });

                        }
                    }
                });



            }
            else {
                Uri alarmUri;
                alarmUri = arrayList.get(val).getRingtone();
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                }
                if (mp == null) {
                    mp = MediaPlayer.create(this, alarmUri);
                }
                if (!mp.isPlaying()) {
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                }
            }

            snooze.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });



            snooze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mp!=null) {
                        mp.stop();
                        mp.release();
                    }
                    if(tts!=null){
                        tts[0].stop();
                        tts[0].shutdown();
                    }
                    AlarmManager alarmManager =  (AlarmManager)MainActivity.this.getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("pos", rq);
                    PendingIntent intent1 = PendingIntent.getBroadcast(MainActivity.this,
                            0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5*60*1000,intent1);
                    intent.removeExtra("pos");
                    dialog.dismiss();
                    finish();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mp!=null) {
                        mp.stop();
                        mp.release();
                    }
                    if(tts!=null){
                        tts[0].stop();
                        tts[0].shutdown();
                    }
                    intent.removeExtra("pos");
                    dialog.dismiss();
                    finish();
                }
            });


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.mode);
        if(f==0||f==-1){
            item.setTitle("Light Mode");
        }
        else
            item.setTitle("Dark Mode");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){

            case R.id.mode:
                if(f==0||f==-1){
                    preferences.edit().putInt("m",1).apply();
                    item.setTitle("Dark Mode");
                    Toast.makeText(this,"There you go...",Toast.LENGTH_SHORT).show();
                }
                else if(f==1){
                    preferences.edit().putInt("m",0).apply();
                    item.setTitle("Light Mode");
                    Toast.makeText(this,"There you go...",Toast.LENGTH_SHORT).show();

                }
                recreate();
                return true;

            case R.id.color:
                dialogColor = new BottomSheetDialog(this);
                dialogColor.setTitle("Choose Color");
                dialogColor.setContentView(R.layout.colorpick);
                dialogColor.show();
                return true;
            case R.id.about:
                LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                View layout = inflater.inflate(R.layout.logolayout,(ViewGroup)findViewById(R.id.rootView));
                adb.setView(layout);
                adb.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==-1 && requestCode>=0 && requestCode<=9){
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone tone = RingtoneManager.getRingtone(this,ringtone);

            db.execSQL("UPDATE AlarmsTable SET Ringtone = '"+ringtone.toString()+"' WHERE Time = '"+arrayList.get(requestCode).time+"'");

            arrayList.get(requestCode).setRingName(tone.getTitle(this));
            arrayList.get(requestCode).setRingtone(ringtone);
            mAdapter.notifyDataSetChanged();

        }
    }

    public void colorClick(View view){
        if(dialogColor!=null){
            dialogColor.dismiss();
        }
        preferences.edit().putInt("c",Integer.valueOf(view.getTag().toString())).apply();
        recreate();
    }
}
