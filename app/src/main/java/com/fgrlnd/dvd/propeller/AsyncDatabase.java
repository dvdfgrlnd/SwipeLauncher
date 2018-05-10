package com.fgrlnd.dvd.propeller;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by david on 3/29/18.
 */

public class AsyncDatabase extends AsyncTask<Operation, Integer, Object> {
    private Operation operation;

    public AsyncDatabase() {
    }

    @Override
    protected Object doInBackground(Operation... operation) {
        this.operation = operation[0];
        Log.d("doInBackground", "run");
        return this.operation.run();
    }

    protected void onPostExecute(Object result) {
        Log.d("onPostExecute", "onEvent");
        this.operation.onEvent.onEvent(result);
    }
}
