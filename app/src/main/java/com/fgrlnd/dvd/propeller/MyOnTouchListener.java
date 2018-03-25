package com.fgrlnd.dvd.propeller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by david on 3/25/18.
 */

public class MyOnTouchListener implements View.OnTouchListener {

    private final long CLICK_UPPER_THRESHOLD = 200;
    private long startClickTime = 0;
    private Handler mHandler = null;
    private Position prev = null;
    private Bitmap mBitmap;
    private Paint mPaint;
    private ArrayList<Position> drawnPoints = new ArrayList<>();

    public MyOnTouchListener(int color, Bitmap bitmap) {
        float lineWidth = 55.0f;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(lineWidth);

        this.mBitmap = bitmap;
    }

    private long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
//                Log.d("OnTouch", String.format("Action: %d", motionEvent.getAction()));
//                return false;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startClickTime = getTime();
                break;
            case MotionEvent.ACTION_UP:
                // Distinguish between a click and swipe by checking the time between ACTION_DOWN and ACTION_UP
                if ((getTime() - startClickTime) < CLICK_UPPER_THRESHOLD) {
//                    onClick((int) x, (int) y);
                } else {
                    mHandler = new Handler();
                    // Wait for 1500 ms before accepting drawn letter
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("OnTouch", "Delay");
                            // Do letter recognition
                            int w = mBitmap.getWidth();
                            int h = mBitmap.getHeight();
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
                            b = Bitmap.createBitmap(b, rect[0], rect[1], rect[2], rect[3]);
                            b = Util.scaleImage(b, newWidth, newHeight);
                            b.getPixels(pixels, 0, newWidth, 0, 0, newWidth, newHeight);
                            int[][] pixel2D = new int[newHeight][newWidth];
                            for (int y = 0; y < newHeight; y++) {
                                for (int x = 0; x < newWidth; x++) {
                                    pixel2D[y][x] = pixels[(y * newWidth) + x];
//                                            b.setPixel(x, y, pixel2D[y][x]);
                                }
//                                        int py=pixel2D[y][0];
//                                    Log.d("OnTouch", String.format("P: %d %d %d %d", Color.alpha(py), Color.red(py), Color.green(py), Color.blue(py)));
                            }
                            String letter = recognition.findClosestLetter(pixel2D);
                            Log.d("OnTouch", String.format("Closest letter: %s", letter));
//                                    mCanvas.drawBitmap();
                            // Clear all drawn points
                            drawnPoints.clear();
                            // Clear canvas
                            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                                    b = BitmapFactory.decodeResource(getResources(), R.drawable.square);
//                                    mImageView.setImageBitmap(b);
                            view.invalidate();
                        }
                    }, 1500);
                }
                prev = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (prev != null) {
                    mCanvas.drawLine(prev.x, prev.y, x, y, mPaint);
                }
                prev = new Position(x, y);
                // Store the drawn point
                drawnPoints.add(prev);
                view.invalidate();
//                        Log.d("OnTouch", String.format("x: %f, y: %f", x, y));
                break;
            default:
                break;
        }
        return true;
    }
}
