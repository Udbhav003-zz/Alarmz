package com.orepheus.udbhav.alarmz;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.startActivity;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public ArrayList<TimeSet> mDataset;
    boolean canPlay = false;
    PendingIntent pendingIntent;
    boolean flagExact = false;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeTextview;
        TextView toneTextview;
        TextView label;
        TextView delete;
        TextView play;
        Switch sb;
        CardView card;


        // each data item is just a string in this case
        public View view;

        public ViewHolder(View v) {
            super(v);
            timeTextview = v.findViewById(R.id.timeTextview);
            toneTextview = v.findViewById(R.id.toneSelect);
            label = v.findViewById(R.id.label);
            delete = v.findViewById(R.id.delete);
            play = v.findViewById(R.id.play);
            sb = v.findViewById(R.id.sw);
            card = v.findViewById(R.id.card);
            view = v;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<TimeSet> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customlayout, parent, false);


        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final SQLiteDatabase db;
        db = holder.sb.getContext().openOrCreateDatabase("Alarms", MODE_PRIVATE, null);

        final TimeSet item = mDataset.get(holder.getAdapterPosition());
        if (item != null) {
            MainActivity.message.setVisibility(View.INVISIBLE);
        }
        final TextToSpeech[] ttsHold = new TextToSpeech[1];
        ttsHold[0] = new TextToSpeech(holder.play.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    ttsHold[0].setLanguage(Locale.ENGLISH);
                    canPlay = true;

                }
            }
        });

        AlarmManager manager = (AlarmManager) holder.sb.getContext().getSystemService(ALARM_SERVICE);
        item.setManager(manager);

        final boolean[] repDays = {false};

        if (item.getRingtone() != null) {

            Ringtone ringtone = RingtoneManager.getRingtone(holder.toneTextview.getContext(), item.ringtone);
            item.setRingName(ringtone.getTitle(holder.toneTextview.getContext()));

        }
        for (int i = 0; i < 7; i++) {
            if (item.getDay(i) == 1) {
                repDays[0] = true;
                break;
            }
        }

        if (item.status) {
            holder.sb.setChecked(true);
            db.execSQL("UPDATE AlarmsTable SET Status = '" + item.status + "' WHERE Time = '" + item.time + "'");
            for (int i = 0; i < 7; i++) {
                if (item.getDay(i) == 1) {
                    repDays[0] = true;
                    if (i == 0) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        calendar1.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar1.set(Calendar.MINUTE, item.getMin());
                        calendar1.set(Calendar.SECOND, 0);
                        long timeToAlarm = calendar1.getTimeInMillis();
                        if (timeToAlarm < System.currentTimeMillis()) {

                            timeToAlarm += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent.putExtra("pos", item.getReq());
                        item.pintent[0] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "0"), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm, AlarmManager.INTERVAL_DAY * 7, item.pintent[0]);
                    } else if (i == 1) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        calendar2.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar2.set(Calendar.MINUTE, item.getMin());
                        calendar2.set(Calendar.SECOND, 0);
                        long timeToAlarm1 = calendar2.getTimeInMillis();
                        if (timeToAlarm1 < System.currentTimeMillis()) {

                            timeToAlarm1 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent1 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent1.putExtra("pos", item.getReq());
                        item.pintent[1] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "1"), myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm1, AlarmManager.INTERVAL_DAY * 7, item.pintent[1]);
                    } else if (i == 2) {
                        Calendar calendar3 = Calendar.getInstance();
                        calendar3.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        calendar3.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar3.set(Calendar.MINUTE, item.getMin());
                        calendar3.set(Calendar.SECOND, 0);
                        long timeToAlarm2 = calendar3.getTimeInMillis();
                        if (timeToAlarm2 < System.currentTimeMillis()) {

                            timeToAlarm2 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent2 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent2.putExtra("pos", item.getReq());
                        item.pintent[2] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "2"), myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm2, AlarmManager.INTERVAL_DAY * 7, item.pintent[2]);
                    } else if (i == 3) {
                        Calendar calendar4 = Calendar.getInstance();
                        calendar4.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        calendar4.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar4.set(Calendar.MINUTE, item.getMin());
                        calendar4.set(Calendar.SECOND, 0);
                        long timeToAlarm3 = calendar4.getTimeInMillis();
                        if (timeToAlarm3 < System.currentTimeMillis()) {

                            timeToAlarm3 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent3 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent3.putExtra("pos", item.getReq());
                        item.pintent[3] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "3"), myIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm3, AlarmManager.INTERVAL_DAY * 7, item.pintent[3]);
                    } else if (i == 4) {
                        Calendar calendar5 = Calendar.getInstance();
                        calendar5.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        calendar5.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar5.set(Calendar.MINUTE, item.getMin());
                        calendar5.set(Calendar.SECOND, 0);
                        long timeToAlarm4 = calendar5.getTimeInMillis();
                        if (timeToAlarm4 < System.currentTimeMillis()) {

                            timeToAlarm4 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent4 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent4.putExtra("pos", item.getReq());
                        item.pintent[4] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "4"), myIntent4, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm4, AlarmManager.INTERVAL_DAY * 7, item.pintent[4]);
                    } else if (i == 5) {
                        Calendar calendar6 = Calendar.getInstance();
                        calendar6.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        calendar6.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar6.set(Calendar.MINUTE, item.getMin());
                        calendar6.set(Calendar.SECOND, 0);
                        long timeToAlarm5 = calendar6.getTimeInMillis();
                        if (timeToAlarm5 < System.currentTimeMillis()) {

                            timeToAlarm5 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent5 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent5.putExtra("pos", item.getReq());
                        item.pintent[5] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "5"), myIntent5, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm5, AlarmManager.INTERVAL_DAY * 7, item.pintent[5]);
                    } else if (i == 6) {
                        Calendar calendar7 = Calendar.getInstance();
                        calendar7.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        calendar7.set(Calendar.HOUR_OF_DAY, item.getHr());
                        calendar7.set(Calendar.MINUTE, item.getMin());
                        calendar7.set(Calendar.SECOND, 0);
                        long timeToAlarm6 = calendar7.getTimeInMillis();
                        if (timeToAlarm6 < System.currentTimeMillis()) {

                            timeToAlarm6 += 7 * 24 * 60 * 60 * 1000;

                        }
                        Intent myIntent6 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                        myIntent6.putExtra("pos", item.getReq());
                        item.pintent[6] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "6"), myIntent6, PendingIntent.FLAG_UPDATE_CURRENT);

                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm6, AlarmManager.INTERVAL_DAY * 7, item.pintent[6]);
                    }

                }

                if (!repDays[0]) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    calendar.set(Calendar.HOUR_OF_DAY, item.getHr());
                    calendar.set(Calendar.MINUTE, item.getMin());
                    calendar.set(Calendar.SECOND, 0);

                    long timeToAlarm = calendar.getTimeInMillis();
                    if (timeToAlarm < System.currentTimeMillis()) {

                        timeToAlarm += 24 * 60 * 60 * 1000;

                    }

                    Intent myIntent = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                    myIntent.putExtra("pos", item.getReq());
                    pendingIntent = PendingIntent.getBroadcast(holder.sb.getContext(), item.req, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    item.manager.setExact(AlarmManager.RTC_WAKEUP, timeToAlarm, pendingIntent);
                    flagExact = true;
                } else if (repDays[0] && flagExact) {
                    item.manager.cancel(pendingIntent);
                }
            }

        }
            holder.timeTextview.setText(item.getTime());
            if (item.label != null)
                holder.label.setText("  " + item.label);
            else
                holder.label.setText("  Text for alarm");

            holder.toneTextview.setText((item.ringName == null) ? "  " + RingtoneManager.getRingtone(holder.toneTextview.getContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).getTitle(holder.toneTextview.getContext()) : "  " + item.ringName);

            holder.timeTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) holder.card.getContext();
                    Intent intent = new Intent(activity, Main2Activity.class);
                    activity.startActivity(intent);
                }
            });

            holder.sb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.sb.isChecked()) {
                        item.setStatus(true);
                        db.execSQL("UPDATE AlarmsTable SET Status = '" + item.status + "' WHERE Time = '" + item.getTime() + "'");
                        if (item.status) {
                            for (int i = 0; i < 7; i++) {
                                if (item.getDay(i) == 1) {
                                    repDays[0] = true;
                                    if (i == 0) {
                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                        calendar1.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar1.set(Calendar.MINUTE, item.getMin());
                                        calendar1.set(Calendar.SECOND, 0);
                                        long timeToAlarm = calendar1.getTimeInMillis();
                                        if (timeToAlarm < System.currentTimeMillis()) {

                                            timeToAlarm += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent.putExtra("pos", item.getReq());
                                        item.pintent[0] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "0"), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm, AlarmManager.INTERVAL_DAY * 7, item.pintent[0]);
                                    } else if (i == 1) {
                                        Calendar calendar2 = Calendar.getInstance();
                                        calendar2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                        calendar2.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar2.set(Calendar.MINUTE, item.getMin());
                                        calendar2.set(Calendar.SECOND, 0);
                                        long timeToAlarm1 = calendar2.getTimeInMillis();
                                        if (timeToAlarm1 < System.currentTimeMillis()) {

                                            timeToAlarm1 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent1 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent1.putExtra("pos", item.getReq());
                                        item.pintent[1] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "1"), myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm1, AlarmManager.INTERVAL_DAY * 7, item.pintent[1]);
                                    } else if (i == 2) {
                                        Calendar calendar3 = Calendar.getInstance();
                                        calendar3.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                                        calendar3.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar3.set(Calendar.MINUTE, item.getMin());
                                        calendar3.set(Calendar.SECOND, 0);
                                        long timeToAlarm2 = calendar3.getTimeInMillis();
                                        if (timeToAlarm2 < System.currentTimeMillis()) {

                                            timeToAlarm2 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent2 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent2.putExtra("pos", item.getReq());
                                        item.pintent[2] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "2"), myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm2, AlarmManager.INTERVAL_DAY * 7, item.pintent[2]);
                                    } else if (i == 3) {
                                        Calendar calendar4 = Calendar.getInstance();
                                        calendar4.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                                        calendar4.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar4.set(Calendar.MINUTE, item.getMin());
                                        calendar4.set(Calendar.SECOND, 0);
                                        long timeToAlarm3 = calendar4.getTimeInMillis();
                                        if (timeToAlarm3 < System.currentTimeMillis()) {

                                            timeToAlarm3 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent3 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent3.putExtra("pos", item.getReq());
                                        item.pintent[3] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "3"), myIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm3, AlarmManager.INTERVAL_DAY * 7, item.pintent[3]);
                                    } else if (i == 4) {
                                        Calendar calendar5 = Calendar.getInstance();
                                        calendar5.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                                        calendar5.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar5.set(Calendar.MINUTE, item.getMin());
                                        calendar5.set(Calendar.SECOND, 0);
                                        long timeToAlarm4 = calendar5.getTimeInMillis();
                                        if (timeToAlarm4 < System.currentTimeMillis()) {

                                            timeToAlarm4 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent4 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent4.putExtra("pos", item.getReq());
                                        item.pintent[4] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "4"), myIntent4, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm4, AlarmManager.INTERVAL_DAY * 7, item.pintent[4]);
                                    } else if (i == 5) {
                                        Calendar calendar6 = Calendar.getInstance();
                                        calendar6.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                                        calendar6.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar6.set(Calendar.MINUTE, item.getMin());
                                        calendar6.set(Calendar.SECOND, 0);
                                        long timeToAlarm5 = calendar6.getTimeInMillis();
                                        if (timeToAlarm5 < System.currentTimeMillis()) {

                                            timeToAlarm5 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent5 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent5.putExtra("pos", item.getReq());
                                        item.pintent[5] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "5"), myIntent5, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm5, AlarmManager.INTERVAL_DAY * 7, item.pintent[5]);
                                    } else if (i == 6) {
                                        Calendar calendar7 = Calendar.getInstance();
                                        calendar7.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                                        calendar7.set(Calendar.HOUR_OF_DAY, item.getHr());
                                        calendar7.set(Calendar.MINUTE, item.getMin());
                                        calendar7.set(Calendar.SECOND, 0);
                                        long timeToAlarm6 = calendar7.getTimeInMillis();
                                        if (timeToAlarm6 < System.currentTimeMillis()) {

                                            timeToAlarm6 += 7 * 24 * 60 * 60 * 1000;

                                        }
                                        Intent myIntent6 = new Intent(holder.sb.getContext(), AlarmReceiver.class);

                                        myIntent6.putExtra("pos", item.getReq());
                                        item.pintent[6] = PendingIntent.getBroadcast(holder.sb.getContext(), Integer.valueOf(item.req + "6"), myIntent6, PendingIntent.FLAG_UPDATE_CURRENT);

                                        item.manager.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm6, AlarmManager.INTERVAL_DAY * 7, item.pintent[6]);
                                    }

                                }
                            }

                            if (!repDays[0]) {
                                Toast.makeText(holder.sb.getContext(), "Alarm On", Toast.LENGTH_SHORT).show();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());

                                calendar.set(Calendar.HOUR_OF_DAY, item.getHr());
                                calendar.set(Calendar.MINUTE, item.getMin());
                                calendar.set(Calendar.SECOND, 0);

                                long timeToAlarm = calendar.getTimeInMillis();
                                if (timeToAlarm < System.currentTimeMillis()) {

                                    timeToAlarm += 24 * 60 * 60 * 1000;

                                }

                                Intent myIntent = new Intent(holder.sb.getContext(), AlarmReceiver.class);
                                myIntent.putExtra("pos", holder.getAdapterPosition());
                                pendingIntent = PendingIntent.getBroadcast(holder.sb.getContext(), item.req, myIntent, 0);

                                item.manager.setExact(AlarmManager.RTC_WAKEUP, timeToAlarm, pendingIntent);
                                flagExact = true;
                            }
                            if(repDays[0] && flagExact){
                                item.manager.cancel(pendingIntent);
                            }
                        }


                    } else {
                        Toast.makeText(holder.sb.getContext(), "Alarm Off", Toast.LENGTH_SHORT).show();
                        item.setStatus(false);
                        db.execSQL("UPDATE AlarmsTable SET Status = '" + item.status + "' WHERE Time = '" + item.getTime() + "'");

                        if (repDays[0]) {
                            for (int i = 0; i < 7; i++) {
                                if (item.getDay(i) == 1) {
                                    item.manager.cancel(item.pintent[i]);
                                }
                            }
                        } else
                            item.manager.cancel(pendingIntent);
                    }
                }
            });

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ttsHold[0] = new TextToSpeech(holder.play.getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {

                                ttsHold[0].setLanguage(Locale.ENGLISH);
                                String str = item.getLabel();
                                final HashMap<String,String> mapz = new HashMap<>();
                                mapz.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,String.valueOf(item.getReq()));

                                if (str == null) {
                                    ttsHold[0].setSpeechRate((float) 0.8);
                                    ttsHold[0].speak("This is a sample voice", TextToSpeech.QUEUE_FLUSH, mapz);
                                } else {
                                    ttsHold[0].setSpeechRate((float) 0.8);
                                    ttsHold[0].speak(str, TextToSpeech.QUEUE_FLUSH, mapz);
                                }

                                ttsHold[0].setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                    @Override
                                    public void onStart(String utteranceId) {

                                    }

                                    @Override
                                    public void onDone(String utteranceId) {
                                        ttsHold[0].shutdown();

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
            });

            holder.toneTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getLabel() == null) {

                        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);

                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone");

                        Activity activity = (Activity) holder.toneTextview.getContext();

                        activity.startActivityForResult(intent, holder.getAdapterPosition());
                    } else {
                        Toast.makeText(holder.toneTextview.getContext(), "You already have a text alarm!!!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.status) {

                        final Dialog dialog = new Dialog(holder.label.getContext());

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setCancelable(false);

                        dialog.setContentView(R.layout.labellayout);

                        dialog.show();

                        final EditText labelEdittext = dialog.findViewById(R.id.ed);
                        Button confButton = dialog.findViewById(R.id.confirm);
                        Button dismButton = dialog.findViewById(R.id.dismiss);

                        if (item.getLabel() != null) {

                            labelEdittext.setText(item.getLabel());

                        }

                        confButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (labelEdittext.getText().toString() != "") {

                                    item.setLabel(labelEdittext.getText().toString());

                                    db.execSQL("UPDATE AlarmsTable SET Label = '" + item.label + "' WHERE Time = '" + item.getTime() + "'");

                                    holder.label.setText("  " + item.label);

                                } else {
                                    item.setLabel(null);

                                    holder.label.setText("  Text for alarm");
                                }
                                dialog.dismiss();
                            }
                        });
                        dismButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });


                    } else
                        Toast.makeText(holder.delete.getContext(), "Switch on the alarm first...", Toast.LENGTH_SHORT).show();
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.status) {

                        db.execSQL("DELETE FROM AlarmsTable WHERE Time = '" + item.getTime() + "'");

                        mDataset.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), mDataset.size());

                        if (mDataset.isEmpty())
                            MainActivity.message.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(holder.delete.getContext(), "Switch off the alarm first...", Toast.LENGTH_SHORT).show();

                    }
                }
            });



    }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount () {

            return (mDataset == null) ? 0 : mDataset.size();
        }
    }


