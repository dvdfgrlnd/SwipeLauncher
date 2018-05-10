package com.fgrlnd.dvd.propeller;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by david on 3/25/18.
 */

public class MinimumEditDistance {
    public static double compute(String source, String target) {
        int distance = computeEditDistance(source, target);
        if (target.startsWith(source)) {
            distance -= 1;
        }
        // Normalize the edit distance with target length to avoid short words to get a low edit distance
        return (double) distance / (double) target.length();
    }

    public static int computeEditDistance(String source, String target) {
        int lenSource = source.length();
        int lenTarget = target.length();
        int[][] distance = new int[lenTarget + 1][lenSource + 1];
        for (int t = 1; t < lenTarget + 1; t++) {
            distance[t][0] = t;
        }
        for (int s = 1; s < lenSource + 1; s++) {
            distance[0][s] = s;
        }
//        StringBuilder sb = new StringBuilder();
        for (int t = 1; t < lenTarget + 1; t++) {
            for (int s = 1; s < lenSource + 1; s++) {
                int ins = distance[t - 1][s] + 1;
                int del = distance[t][s - 1] + 1;
                // No cost if both characters are equal
                int subCost = source.charAt(s - 1) == target.charAt(t - 1) ? 0 : 2;
                int sub = distance[t - 1][s - 1] + subCost;
                int[] arr = new int[]{ins, del, sub};
                Arrays.sort(arr);
                // First element is the smallest
                distance[t][s] = arr[0];
//                sb.append(distance[t][s]);
//                sb.append(", ");
            }
//            sb.append("\n");
        }
//        Log.d("compute", sb.toString());

        return distance[lenTarget][lenSource];
    }
}
