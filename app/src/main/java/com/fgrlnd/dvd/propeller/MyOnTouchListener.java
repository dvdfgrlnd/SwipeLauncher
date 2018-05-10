package com.fgrlnd.dvd.propeller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.BiConsumer;

/**
 * Created by david on 3/25/18.
 */

public class MyOnTouchListener implements View.OnTouchListener {

    private final long CLICK_UPPER_THRESHOLD = 500;
    private long startClickTime = 0;
    private Handler mHandler = null;
    private Position prev = null;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mUserPaint;
    private ArrayList<Position> drawnPoints = new ArrayList<>();
    private Path mPath;
    private OnClickInterface onClick;
    private OnSwipeInterface onSwipe;
    private float downX;
    private float downY;

    public MyOnTouchListener(int color, int userColor, Canvas canvas, OnClickInterface onClick, OnSwipeInterface onSwipe) {
        float lineWidth = 55.0f;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mUserPaint = new Paint();
        mUserPaint.setColor(userColor);
        mUserPaint.setStrokeWidth(lineWidth);
        mUserPaint.setStyle(Paint.Style.STROKE);

        this.mCanvas = canvas;
        this.onClick = onClick;
        this.onSwipe = onSwipe;
    }

    private long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
        Log.d("OnTouch", String.format("Action: %d", motionEvent.getAction()));
//                return false;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("onTouch", "action down");
                downX = x;
                downY = y;
                startClickTime = getTime();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("onTouch", "action up");
                Log.d("onTouch", String.format("diff: (%f, %f)", (x - downX), (y - downY)));
                // Distinguish between a click and swipe by checking the time between ACTION_DOWN and ACTION_UP
                float xDiff = Math.abs(x - downX);
                float yDiff = Math.abs(y - downY);
                float maxDiff = 15f;
                if ((getTime() - startClickTime) < CLICK_UPPER_THRESHOLD && xDiff < maxDiff && yDiff < maxDiff) {
                    Log.d("onTouch", String.format("x: %f, y: %f", x, y));
                    clearScreen();
                    onClick.onClick((int) x, (int) y);
                } else {
                    Log.d("onTouch", "onSwipe");
                    mHandler = new Handler();
                    // Wait for 1500 ms before accepting drawn letter
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("OnTouch", "Delay");
                            // Do letter recognition
                            int w = mCanvas.getWidth();
                            int h = mCanvas.getHeight();
                            int[] pixels = new int[w * h];
                            int newWidth = 28;
                            int newHeight = 28;
                            // Create new bitmap with white background and redraw all points on it
                            Bitmap tmpBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                            Canvas tmpCanvas = new Canvas(tmpBitmap);
                            tmpCanvas.drawColor(Color.rgb(255, 255, 255));
                            Position prevPos = null;
                            for (Position pos : drawnPoints) {
                                if (prevPos != null) {
                                    tmpCanvas.drawLine(prevPos.x, prevPos.y, pos.x, pos.y, mPaint);
                                }
                                prevPos = pos;
                            }

                            Bitmap b = Util.scaleImage(tmpBitmap, 100, 100);
                            int[] rect = Util.crop(b);
                            // Check that width and height is larger than 0
                            if (rect[2] > 0 && rect[3] > 0) {
                                b = Bitmap.createBitmap(b, rect[0], rect[1], rect[2], rect[3]);
                                b = Util.scaleImage(b, newWidth, newHeight);
                                b.getPixels(pixels, 0, newWidth, 0, 0, newWidth, newHeight);
                                int[][] pixel2D = new int[newHeight][newWidth];
                                for (int y = 0; y < newHeight; y++) {
                                    for (int x = 0; x < newWidth; x++) {
                                        pixel2D[y][x] = pixels[(y * newWidth) + x];
                                    }
                                }
                                onSwipe.onSwipe(pixel2D);
                            }
                            clearScreen();
                            view.invalidate();
                        }
                    }, 1000);
                }
                prev = null;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("onTouch", "action move");
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (prev != null) {
                    mPath.lineTo(x, y);
                    mCanvas.drawPath(mPath, mUserPaint);
                } else {
                    mPath = new Path();
                    mPath.moveTo(x, y);
                }
                prev = new Position(x, y);
                // Store the drawn point
                drawnPoints.add(prev);
                view.invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void clearScreen() {
        // Clear all drawn points
        drawnPoints.clear();
        // Clear canvas
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
}
