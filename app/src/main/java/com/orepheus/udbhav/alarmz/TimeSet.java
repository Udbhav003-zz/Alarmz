package com.orepheus.udbhav.alarmz;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.net.Uri;

public class TimeSet {
    int hr;
    int min;
    String time;
    Uri ringtone;
    String ringName;
    String label;
    AlarmManager manager;
    PendingIntent[] pintent = {null,null,null,null,null,null,null};
    int req = -1;
    boolean status;
    int[] days;

    public TimeSet(){

    }

    public TimeSet(int hr, int min, String time, Uri ringtone, String label,Boolean status,int req, int[] days) {
        this.hr = hr;
        this.min = min;
        this.time = time;
        this.ringtone = ringtone;
        this.label = label;
        this.status = status;
        this.req = req;
        this.days = days;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Uri getRingtone() {
        return ringtone;
    }

    public void setRingtone(Uri ringtone) {
        this.ringtone = ringtone;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public AlarmManager getManager() {
        return manager;
    }

    public void setManager(AlarmManager manager) {
        this.manager = manager;
    }

    public PendingIntent getPintent(int p) {
        return pintent[p];
    }

    public void setPintent(PendingIntent pintent,int p) {
        this.pintent[p] = pintent;
    }

    public int getReq() {
        return req;
    }


    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setRingName(String ringName) {
        this.ringName = ringName;
    }

    public int getDay(int p) {
        return days[p];
    }

    public void setDay(int p,int v) {
        this.days[p] = v;
    }
}
