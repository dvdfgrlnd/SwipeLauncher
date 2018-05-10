package com.fgrlnd.dvd.propeller;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by david on 3/24/18.
 */

@Entity
public class App {
    @NonNull
    @PrimaryKey
    public String packageName;

    @ColumnInfo(name = "name")
    public String name;

    @Ignore
    public Drawable icon;

    @ColumnInfo(name = "last_used")
    public long lastUsed = 0;

    @ColumnInfo(name = "times_used")
    public int timesUsed = 0;

    public App() {
    }

    public App(String packageName, String appName, Drawable icon) {
        this.packageName = packageName;
        this.name = appName;
        this.icon = icon;
    }

    public void updateProperties() {
        // Increase number of times used
        timesUsed += 1;
        lastUsed = Calendar.getInstance().getTimeInMillis();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }
}
