package com.fgrlnd.dvd.propeller;

import android.graphics.Bitmap;

/**
 * Created by david on 3/24/18.
 */

public class App {
    public String name;
    public Bitmap icon;

    public App(String appName, Bitmap icon) {
        this.name = appName;
        this.icon = icon;
    }
}
