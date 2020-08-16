package com.futech.coffeeshop.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import calendar.PersianDate;

@SuppressLint("DefaultLocale")
public class DateHelper {


    public static Date convertPersianDateToGregorianDate(PersianDate persianDate) {

        int pYear = persianDate.getYear();
        int pMonth = persianDate.getMonth();
        int pDay = persianDate.getDayOfMonth();

        int gYear = pYear + 621;
        int gMonth, gDay;

        int[] gregorianMonths = {30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31};

        int remainDay, marchDayDifferent;
        int dayCount, i = 0;

        if (isGregorianDateLeap(gYear)) {
            marchDayDifferent = 12;
        }else {
            marchDayDifferent = 11;
        }

        if (isGregorianDateLeap(gYear + 1)) {
            //افزودن یک روز به روز فبریه
            gregorianMonths[10] = gregorianMonths[10] + 1;
        }

        //محاسبه روز های گذشته از روز اول فروردین
        if (pMonth <= 6 && pMonth >= 1) dayCount = ((pMonth - 1) * 31) + pDay;
        else dayCount = (6 * 31) + ((pMonth - 7) * 30) + pDay;

        //یافتن ماه و روز مطابق با ماه و روز تاریخ پارسی
        if (dayCount <= marchDayDifferent) {
            gDay = dayCount + 31 - marchDayDifferent;
            gMonth = 3;
        }else {
            remainDay = dayCount - marchDayDifferent;
            while ((remainDay > gregorianMonths[i])) {
                remainDay = remainDay - gregorianMonths[i];
                i++;
            }

            gDay = remainDay;

            if (i > 8) {
                gMonth = i - 8;
                gYear++;
            }else {
                gMonth = i + 4;
            }
        }

        return parseDate(String.format("%d-%d-%d", gYear, gMonth, gDay));
    }


    private static boolean isGregorianDateLeap(int year) {
        return year % 100 != 0 && year % 4 == 0 || year % 100 == 0 && year % 400 == 0;
    }

    @SuppressLint("SimpleDateFormat")
    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }


}
