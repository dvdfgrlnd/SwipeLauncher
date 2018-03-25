package letterrec;

import com.fgrlnd.dvd.propeller.Util;

import java.util.ArrayList;

/**
 * Created by david on 3/18/18.
 */

public class Zoning {

    private static double computeZone(double[][] arr, int[] X, int[] Y) {
        double sum = 0.0;
        double tot = (X[1] - X[0]) * (Y[1] - Y[0]);
        for (int y = Y[0]; y < Y[1]; y += 1) {
            for (int x = X[0]; x < X[1]; x += 1) {
                    sum += Util.getGrayscale((int)arr[y][x]);
            }
        }
        return (sum / tot);
    }

    public static double[] compute(double[][] array) {
        int box = array.length / 4;
        double[] zones = new double[16];
        int i = 0;
        for (int oy = 0; oy < array.length; oy += box) {
            for (int ox = 0; ox < array[0].length; ox += box) {
                double z = computeZone(array,new int[]{ox, ox +box}, new int[]{oy, oy + box});
                zones[i++]=z;
            }
        }
        return zones;
    }
}
