package com.futech.coffeeshop.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    public static String getString(String number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

    public static String getString(float number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

    public static String convertToEnglishDigits(String value) {
        return value.replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("7", "٧").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0").replaceAll("۱", "1").replaceAll("۲", "2").replaceAll("۳", "3").replaceAll("۴", "4").replaceAll("۵", "5").replaceAll("۶", "6").replaceAll("۷", "7").replaceAll("۸", "8").replaceAll("۹", "9").replaceAll("۰", "0");
    }

    public static String convertToArabicDigits(String value) {
        return value.replaceAll("1", "۱").replaceAll("2", "۲").replaceAll("3", "۳").replaceAll("4", "۴").replaceAll("5", "۵").replaceAll("6", "۶").replaceAll("7", "۷").replaceAll("8", "۸").replaceAll("9", "۹").replaceAll("0", "۰");
    }

}
