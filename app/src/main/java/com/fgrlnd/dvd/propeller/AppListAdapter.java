package com.fgrlnd.dvd.propeller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 3/24/18.
 */

public class AppListAdapter extends ArrayAdapter<App> {

    private List<App> appList;

    public void changeAppList(List<App> newList) {
        appList.clear();
        appList.addAll(newList);
    }

    public AppListAdapter(@NonNull Context context, @NonNull List<App> objects) {
        super(context, 0, objects);
        this.appList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        App app = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
//        Log.d("getView", String.format("update %s %d", app.name, position));
        convertView.setTag(app);
        ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
        TextView tv = (TextView) convertView.findViewById(R.id.appname);
        iv.setImageDrawable(app.icon);
        tv.setText(app.name);
        return convertView;
    }
}
