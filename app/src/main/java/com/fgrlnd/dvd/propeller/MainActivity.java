package com.fgrlnd.dvd.propeller;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import letterrec.Letter;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private RelativeLayout canvasLayout;
    private RelativeLayout imageContainer;
    private LetterRecognition recognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        canvasLayout = (RelativeLayout) findViewById(R.id.canvasLayout);
        imageContainer = (RelativeLayout) findViewById(R.id.imageContainer);
        listView = (ListView) findViewById(R.id.appList);

        canvasLayout.setOnTouchListener(new MyOnTouchListener());
        try {
            InputStream in = getAssets().open("results");
            ObjectInputStream objIn = new ObjectInputStream(in);

            ArrayList<letterrec.Letter> letters = (ArrayList<Letter>) objIn.readObject();
            recognition = new LetterRecognition(letters);

            objIn.close();
            in.close();
        } catch (IOException e) {
            Log.d("OnCreate", e.toString());
        } catch (ClassNotFoundException c) {
            Log.d("OnCreate", c.toString());
        }
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                int width = mImageView.getWidth();
                int height = mImageView.getHeight();
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mImageView.setImageBitmap(mBitmap);
                mCanvas = new Canvas(mBitmap);
//                mCanvas.drawColor(Color.rgb(255, 255, 255));
                mColor = ResourcesCompat.getColor(getResources(), R.color.colorRectangle, null);
//                mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.square));
            }
        });

        ArrayList<App> list = new ArrayList<>();
        Bitmap icon = null;
        try {
            icon = getBitmapFromAssets("topgear.png");
        } catch (IOException e) {
            Log.d("OnCreate", e.toString());
        }
        App app1 = new App("app one", icon);
        App app2 = new App("app two", icon);
        list.add(app1);
        list.add(app2);
        AppListAdapter adapter = new AppListAdapter(this, list);
        listView.setAdapter(adapter);

    }

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = getAssets();

        InputStream istr = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }


    public void onClick(int x, int y) {
        ListView container = imageContainer.findViewById(R.id.appList);
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            Rect _bounds = new Rect();
            child.getHitRect(_bounds);
            Log.d("onClick", String.format("%d", child.getId()));
            if (_bounds.contains(x, y)) {
                String name = getResources().getResourceEntryName(child.getId());
                Log.d("OnTouch", String.format("hit: %s", name));
                if (name.equals("listItem")) {
                    App app = (App) child.getTag();
                    Log.d("OnTouch", String.format("app name: %s", app.name));
                }
            }
        }
    }

}
