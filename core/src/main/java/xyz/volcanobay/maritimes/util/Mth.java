package xyz.volcanobay.maritimes.util;

public class Mth {
    public static float map(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }
}
