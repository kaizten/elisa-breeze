package com.kaizten.prmp.conversor.utils;

public class StringUtils {
    
    public static String toWitdh(int number, int width) {
        StringBuilder sb = new StringBuilder();
        sb.append(number);
        while (sb.length() < width) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
