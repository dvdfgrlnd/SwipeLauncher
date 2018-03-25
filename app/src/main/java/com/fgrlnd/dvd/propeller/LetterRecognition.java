package com.fgrlnd.dvd.propeller;

import android.util.Log;

import java.util.ArrayList;

import letterrec.Letter;

/**
 * Created by david on 3/18/18.
 */

public class LetterRecognition {

    private ArrayList<Letter> listOfLetters = null;

    public LetterRecognition(ArrayList<Letter> list) {
        this.listOfLetters = list;
    }

    private static double computeZone(int[][] arr, int[] X, int[] Y) {
        double sum = 0.0;
        double tot = (X[1] - X[0]) * (Y[1] - Y[0]);
        for (int y = Y[0]; y < Y[1]; y += 1) {
            for (int x = X[0]; x < X[1]; x += 1) {
                sum += Util.getGrayscale(arr[y][x]);
            }
        }
        return (sum / tot);
    }

    private static double[] computeVector(int[][] array) {
        int box = array.length / 4;
        double[] zones = new double[16];
        int i = 0;
        // System.out.format("y: %d, x: %d\n", array.length, array[0].length);
        for (int oy = 0; oy < array.length; oy += box) {
            for (int ox = 0; ox < array[0].length; ox += box) {
                double z = computeZone(array, new int[]{ox, ox + box}, new int[]{oy, oy + box});
                // System.out.println(z);
                zones[i++] = z;
            }
        }
        return zones;
    }

    private static double computeEuclideanDistance(double[] vecOne, double[] vecTwo) {
        double sum = 0.0;
        for (int i = 0; i < vecOne.length; i++) {
            sum += Math.pow((vecOne[i] - vecTwo[i]), 2);
        }
        return Math.sqrt(sum);
    }

    private static String vectorToString(double[] vector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            sb.append(", ");
        }
        return sb.toString();
    }

    public String findClosestLetter(int[][] pixelData) {
        double[] vector = computeVector(pixelData);
        Log.d("FindClosestLetter", vectorToString(vector));
        int minInd = 0;
        double minVal = computeEuclideanDistance(listOfLetters.get(0).vector, vector);
        for (int i = 1; i < listOfLetters.size(); i++) {
            double tmp = computeEuclideanDistance(listOfLetters.get(i).vector, vector);
//            Log.d("FindClosestLetter", String.format("value: %f", tmp));
            if (tmp < minVal && i != 233) {
                minInd = i;
                minVal = tmp;
            }
        }
        Log.d("FindClosestLetter", String.format("index: %d, value: %f", minInd, minVal));
        Log.d("FindClosestLetter2", vectorToString(listOfLetters.get(minInd).vector));
        return listOfLetters.get(minInd).letter;
    }
}
