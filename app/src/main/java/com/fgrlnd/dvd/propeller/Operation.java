package com.fgrlnd.dvd.propeller;

import android.util.Log;

/**
 * Created by david on 3/30/18.
 */

public class Operation {
    public enum Command {
        UPDATE, DELETE, GETALL, INSERT
    }

    private AppDatabase db;
    private Command command;
    private App[] apps;
    public OnEvent onEvent;

    public Operation(AppDatabase db, Command command, OnEvent onEvent, App... apps) {
        this.db = db;
        this.command = command;
        this.onEvent = onEvent;
        this.apps = apps;
    }

    public Object run() {
        Log.d("run", command.name());
        switch (this.command) {
            case INSERT:
                return db.appDao().insert(this.apps);
            case UPDATE:
                db.appDao().updateApp(this.apps);
                return 0;
            case GETALL:
                return db.appDao().getApps();
            case DELETE:
                db.appDao().deleteApp(this.apps);
                return 0;
        }
        return 0;
    }
}
