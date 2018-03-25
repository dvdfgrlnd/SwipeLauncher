package com.fgrlnd.dvd.propeller;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by david on 3/24/18.
 */

public class Util {
    public static Bitmap scaleImage(Bitmap mBitmap, int newWidth, int newHeight) {
        Bitmap b = Bitmap.createScaledBitmap(mBitmap, newWidth, newHeight, false);
        return b;
    }

    public static int getGrayscale(int rgb) {
        int r = Color.red(rgb);
        int g = Color.green(rgb);
        int b = Color.blue(rgb);
        return (r + g + b) / 3;
    }

    public static int[] crop(Bitmap im) {
        int left = im.getWidth();
        int right = 0;
        int upper = im.getHeight();
        int lower = 0;
        for (int y = 0; y < im.getHeight(); y++) {
            for (int x = 0; x < im.getWidth(); x++) {
                if (getGrayscale(im.getPixel(x, y)) < 255) {
                    if (x < left) {
                        left = x;
                    } else if (x > right) {
                        right = x;
                    }
                    if (y < upper) {
                        upper = y;
                    } else if (y > lower) {
                        lower = y;
                    }
                }
            }

        }

        return new int[]{left, upper, (right - left), (lower - upper)};
    }
}
