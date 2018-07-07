package com.orepheus.udbhav.alarmz;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bhargavms.dotloader.DotLoader;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.orepheus.udbhav.alarmz.MainActivity.arrayList;
import static com.orepheus.udbhav.alarmz.MainActivity.c;
import static com.orepheus.udbhav.alarmz.MainActivity.f;
import static com.orepheus.udbhav.alarmz.MainActivity.mAdapter;

public class RecordingActivity extends AppCompatActivity {

    String AudioSavePathInDevice = null;
    MediaRecorder recorder;
    MediaPlayer mediaPlayer;
    public static final int RequestPermissionCode = 1;
    File directory;
    DotLoader loader;
    int pos;
    SQLiteDatabase db;

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
        setContentView(R.layout.activity_recording);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);

        final Button startRec = findViewById(R.id.startRec);
        final Button stop = findViewById(R.id.stop);
        loader = findViewById(R.id.dot_loader);
        final TextView listen = findViewById(R.id.listen);
        listen.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.INVISIBLE);
        stop.setEnabled(false);
        startRec.setAlpha(1);
        stop.setAlpha(0.5f);

        Intent intent = getIntent();
        pos = intent.getIntExtra("pos",-1);

        db = this.openOrCreateDatabase("Alarms",MODE_PRIVATE,null);

        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission()) {

                    directory = new File(Environment.getExternalStorageDirectory(), "AlarmzRecording");
                    if (!directory.exists()) {
                        final boolean mkdirs = directory.mkdirs();
                        if (!mkdirs) {
                            Log.i("ErrorFile", "Error creating directory");
                        }
                    }

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AlarmzRecording" + "/" + "AlarmzRecording" + System.nanoTime() + ".file.m4a";

                    MediaRecorderReady();

                    try {
                        stop.setAlpha(1f);
                        startRec.setAlpha(0.5f);
                        listen.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.VISIBLE);
                        loader.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loader.setNumberOfDots(5);
                            }
                        }, 3000);
                        recorder.prepare();
                        recorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    startRec.setEnabled(false);
                    stop.setEnabled(true);

                    Toast.makeText(RecordingActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRec.setAlpha(1);
                stop.setAlpha(0.5f);
                stop.setEnabled(false);
                startRec.setEnabled(true);
                listen.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                recorder.stop();

                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(RecordingActivity.this);
                View sheetView = RecordingActivity.this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.setCancelable(false);
                mBottomSheetDialog.show();

                final LinearLayout playRec = sheetView.findViewById(R.id.bottom_sheet_play);
                final LinearLayout stopPlaying = sheetView.findViewById(R.id.bottom_sheet_stop);
                LinearLayout save = sheetView.findViewById(R.id.bottom_sheet_save);
                LinearLayout deleteRec = sheetView.findViewById(R.id.bottom_sheet_delete);
                final LinearLayout bottom_pane = sheetView.findViewById(R.id.bottom_pane);
                final LinearLayout upper_pane = sheetView.findViewById(R.id.upper_pane);
                final Button yup = sheetView.findViewById(R.id.yup);
                final Button nope = sheetView.findViewById(R.id.nope);

                playRec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) throws IllegalArgumentException,
                            SecurityException, IllegalStateException {

                        stopPlaying.setEnabled(true);
                        playRec.setEnabled(false);

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(AudioSavePathInDevice);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mediaPlayer.start();
                        Toast.makeText(RecordingActivity.this, "Recording Playing",
                                Toast.LENGTH_LONG).show();

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                playRec.setEnabled(true);
                                stopPlaying.setEnabled(false);
                            }
                        });

                    }
                });


                stopPlaying.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        stopPlaying.setEnabled(false);
                        playRec.setEnabled(true);
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottom_pane.setVisibility(View.VISIBLE);
                        upper_pane.setVisibility(View.GONE);

                        yup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri recorded = Uri.parse(AudioSavePathInDevice);
                                db.execSQL("UPDATE AlarmsTable SET Ringtone = '" + recorded.toString() + "' WHERE Time = '" + arrayList.get(pos).time + "'");

                                arrayList.get(pos).setRingName(recorded.getLastPathSegment());
                                arrayList.get(pos).setRingtone(recorded);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        nope.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                            }
                        });


                    }
                });

                deleteRec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(AudioSavePathInDevice);
                        if (file.exists()) {
                            file.delete();
                        }
                        mBottomSheetDialog.dismiss();
                        Toast.makeText(RecordingActivity.this, "Recording Deleted", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

    }

    public void MediaRecorderReady() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(AudioSavePathInDevice);
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordingActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordingActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordingActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
