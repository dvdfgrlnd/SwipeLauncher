package com.fgrlnd.dvd.propeller;

import android.content.Context;
import android.support.annotation.NonNull;
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
//    private final ArrayList<App> appList;

    public AppListAdapter(@NonNull Context context, @NonNull List<App> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        App app = getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        convertView.setTag(app);
        ImageView iv=(ImageView) convertView.findViewById(R.id.icon);
        TextView tv=(TextView) convertView.findViewById(R.id.appname);
        iv.setImageBitmap(app.icon);
        tv.setText(app.name);
        return convertView;
    }
}
