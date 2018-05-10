package com.fgrlnd.dvd.propeller;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import letterrec.Letter;

public class MainActivity extends Activity {


    private GridView mMostUsedListView;
    private GridView mRecentListView;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private RelativeLayout mCanvasLayout;
    private LinearLayout mImageContainer;
    private LetterRecognition mRecognition;
    private PackageManager mPackageManager;
    private AppListAdapter mMostUsedAdapter;
    private AppListAdapter mRecentAdapter;
    private TextView searchField;

    private String searchTerm = null;
    private List<App> mInstalledApps;
    private AppDatabase db;
//    private AsyncDatabase dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mCanvasLayout = (RelativeLayout) findViewById(R.id.canvasLayout);
        mImageContainer = (LinearLayout) findViewById(R.id.imageContainer);
        mMostUsedListView = findViewById(R.id.mostUsedAppList);
        mRecentListView = findViewById(R.id.recentAppList);
        searchField = (TextView) findViewById(R.id.searchField);

        mPackageManager = getPackageManager();

        int color = ResourcesCompat.getColor(getResources(), R.color.colorRectangle, null);
        int userColor = ResourcesCompat.getColor(getResources(), R.color.colorUserStroke, null);
        try {
            InputStream in = getAssets().open("resultsWithSlash");
            ObjectInputStream objIn = new ObjectInputStream(in);

            ArrayList<letterrec.Letter> letters = (ArrayList<Letter>) objIn.readObject();
            mRecognition = new LetterRecognition(letters);

            objIn.close();
            in.close();
        } catch (IOException e) {
            Log.d("OnCreate", e.toString());
        } catch (ClassNotFoundException c) {
            Log.d("OnCreate", c.toString());
        }
        mCanvasLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mCanvasLayout.getWidth();
                int height = mCanvasLayout.getHeight();
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mImageView.setImageBitmap(mBitmap);
                mCanvas = new Canvas(mBitmap);
                OnClickInterface click = (x, y) -> onClick(x, y);
                OnSwipeInterface swipe = (px) -> onSwipe(px);
                mCanvasLayout.setOnTouchListener(new MyOnTouchListener(color, userColor, mCanvas, click, swipe));
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").allowMainThreadQueries().build();
//        dbHandler = new AsyncDatabase();

//        updateApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("onResume", "resume");
//        populateAppLists(null);
        updateApps(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateApps(false);
    }

    private void updateApps(boolean onlyReadDb) {
        // Get all new apps from the database
        new AsyncDatabase().execute(new Operation(db, Operation.Command.GETALL, obj -> populateAppLists((List<App>) obj, onlyReadDb)), null);
    }

    private void populateAppLists(List<App> storedApps, boolean onlyReadDb) {
        if (onlyReadDb) {
            for (App app : storedApps) {
                app.icon = getIcon(app.packageName);
            }
            mInstalledApps = storedApps;
        } else {
            mInstalledApps = getInstalledApps(storedApps);
        }
        List<App> mostUsedList = new ArrayList<>(mInstalledApps);
        Collections.sort(mostUsedList, new Comparator<App>() {
            @Override
            public int compare(App app1, App app2) {
                return (app2.timesUsed - app1.timesUsed);
            }
        });
        mMostUsedAdapter = new AppListAdapter(this, mostUsedList);
        mMostUsedListView.setAdapter(mMostUsedAdapter);

        List<App> recentList = new ArrayList<>(mInstalledApps);
        Collections.sort(recentList, new Comparator<App>() {
            @Override
            public int compare(App app1, App app2) {
                if (app2.lastUsed > app1.lastUsed) {
                    return 1;
                } else if (app2.lastUsed < app1.lastUsed) {
                    return -1;
                } else {
                    return 0;
                }
//                return (app2.lastUsed.compareTo(app1.lastUsed));
            }
        });
        mRecentAdapter = new AppListAdapter(this, recentList);
        mRecentListView.setAdapter(mRecentAdapter);
    }

    private Drawable getIcon(String packageName) {
        Drawable icon = null;
        try {
            icon = mPackageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("getIcon", e.toString());
            icon = getResources().getDrawable(R.drawable.square);
        }
        return icon;
    }

    private List<App> getInstalledApps(List<App> storedApps) {
        List<ApplicationInfo> installedApplications = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<App> appItemList = new ArrayList<>();
        List<App> newApps = new ArrayList<>();
        for (ApplicationInfo packageInfo : installedApplications) {
            if (mPackageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String name = packageInfo.loadLabel(mPackageManager).toString();
                Drawable icon = getIcon(packageInfo.packageName);
                App app = appExist(storedApps, packageInfo.packageName);
                if (app == null) {
                    app = new App(packageInfo.packageName, name, icon);
                    newApps.add(app);
                } else {
                    // Remove the app from the storedApps list
                    storedApps.remove(app);
                }
                app.icon = icon;
                appItemList.add(app);
            }
        }
        Log.d("getInstalledApps", String.format("Apps removed: %d, apps inserted: %d", storedApps.size(), newApps.size()));
        // Insert all new apps at once
        if (newApps.size() > 0) {
            new AsyncDatabase().execute(new Operation(db, Operation.Command.INSERT, obj -> {
                long[] ids = (long[]) obj;
                if (ids[0] == -1) {
                    Log.d("getInstalledApps", "App already exist in DB");
                }
            }, newApps.toArray(new App[newApps.size()])));
        }
        // Remove the uninstalled apps
        if (newApps.size() > 0) {
            new AsyncDatabase().execute(new Operation(db, Operation.Command.DELETE, o -> {
            }, storedApps.toArray(new App[storedApps.size()])));
        }
        return appItemList;
    }

    private App appExist(List<App> apps, String packageName) {
        for (App app : apps) {
            if (app.packageName.equals(packageName)) {
                return app;
            }
        }
        return null;
    }

    private void filterApps(String term) {
        List<App> sortedList;
        if (term == null) {
            sortedList = new ArrayList<>(mInstalledApps);
        } else {
            sortedList = new ArrayList<>();
            ArrayList<SearchResult> filteredList = new ArrayList<>();
            for (App app : mInstalledApps) {
                double distance = MinimumEditDistance.compute(term.toLowerCase(), app.name.toLowerCase());
                Log.d("filterApps", String.format("%f", distance));
                filteredList.add(new SearchResult(app, distance));
            }
            Collections.sort(filteredList, new Comparator<SearchResult>() {
                @Override
                public int compare(SearchResult sr1, SearchResult sr2) {
                    return Double.compare(sr1.editDistance, sr2.editDistance);
                }
            });
            for (SearchResult result : filteredList) {
                sortedList.add(result.app);
            }
        }

        mRecentAdapter.changeAppList(sortedList);
        mRecentAdapter.notifyDataSetChanged();
    }

    private void runApp(App app) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
        if (launchIntent != null) {
//            app.setLastUsed(Calendar.getInstance().getTimeInMillis());
            app.updateProperties();
            // Update app stats in background
            new AsyncDatabase().execute(new Operation(db, Operation.Command.UPDATE, obj -> {
            }, app));
            searchTerm = null;
            searchField.setText("");
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

    private void onSwipe(int[][] pixel2D) {
        String letter = mRecognition.findClosestLetter(pixel2D);
        Log.d("onSwipe", String.format("Closest letter: %s", letter));
        if (letter.equals("/")) {
            searchTerm = null;
        } else if (searchTerm == null) {
            searchTerm = letter;
        } else {
            searchTerm += letter;
        }
        Log.d("onSwipe", String.format("Search term: %s", searchTerm));
        searchField.setText(searchTerm == null ? "" : searchTerm.toLowerCase());
        filterApps(searchTerm);
    }

    private void onClick(int x, int y) {
//        Log.d("onClick", String.format("%d %d", x, y));
//        Log.d("onClick", String.format("%d", mImageContainer.getChildCount()));
        for (int i = 0; i < mImageContainer.getChildCount(); i++) {
            ViewGroup container = (ViewGroup) mImageContainer.getChildAt(i);
            Rect r = new Rect();
            container.getHitRect(r);
            Log.d("onClick", String.format("coord %d %d %d %d", r.bottom, r.top, r.left, r.right));
//            Log.d("onClick", String.format(" child count %d", container.getChildCount()));
//            ListView container = mImageContainer.findViewById(R.id.recentAppList);
            for (int k = 0; k < container.getChildCount(); k++) {
                View child = container.getChildAt(k);
                Rect _bounds = new Rect();
                child.getHitRect(_bounds);
                Log.d("onClick", String.format("child %d %d %d %d", _bounds.bottom, _bounds.top, _bounds.left, _bounds.right));
                String name = getResources().getResourceEntryName(child.getId());
//                Log.d("OnTouch", String.format("name: %s", name));
                if (_bounds.contains(x - r.left, y)) {
//                    Log.d("OnTouch", String.format("hit: %s", name));
                    if (name.equals("listItem")) {
                        App app = (App) child.getTag();
//                        Log.d("OnTouch", String.format("app name: %s %s", app.name, app.packageName));
                        runApp(app);
                    }
                }
            }
        }
    }

}
