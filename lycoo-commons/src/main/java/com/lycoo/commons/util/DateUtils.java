package com.lycoo.commons.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    private static final String tag = "DateUtils";

    /**
     * 获取当期日期
     */
    public static String getToday() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        return dateFormat.format(new Date());
    }

    /**
     * 获取昨天
     */
    public static String getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date d = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LogUtils.info(tag, "昨天是:  " + simpleDateFormat.format(d));
        return simpleDateFormat.format(d);// 获取昨天日期

    }

    /**
     * 获取据当前日期指定的日期
     */
    public static List<String> getSpecifyDates(int endDate) {
        List<String> dates = new ArrayList<String>();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        Date today = new Date(); // 以当前
        Date beforeDate = null;
        Calendar calendar = Calendar.getInstance();
        String result = "";

        for (int i = 0; i < endDate; i++) {
            calendar.setTime(today);
            calendar.add(Calendar.DATE, -i);
            beforeDate = calendar.getTime();
            result = dateFormat.format(beforeDate);
            dates.add(result);
        }
        return dates;
    }

    /**
     * 判断指定日期是星期几
     */
    public static String getDayStrOfWeek(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = 0;
        String result = "";
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            dayOfWeek = 7;
        } else {
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        switch (dayOfWeek) {
            case 1:
                result = "星期一";
                break;
            case 2:
                result = "星期二";
                break;
            case 3:
                result = "星期三";
                break;
            case 4:
                result = "星期四";
                break;
            case 5:
                result = "星期五";
                break;
            case 6:
                result = "星期六";
                break;
            case 7:
                result = "星期日";
                break;
        }

        return result;
    }

    /**
     * 判断指定日期是星期几
     */
    public static int getDayOfWeek(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek;
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            dayOfWeek = 7;
        } else {
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        return dayOfWeek;
    }

    /**
     * 获取当前的日期<br>
     * 这里的"格式"我们可以随意的指定，这里指定为 "yyyy-MM-dd-HH-mm",输出如下：<br>
     * ****************** result : 2013-04-24-16-41
     */
    public static String getDateOfToday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    public static String getDateOfToday(String separator) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取当前的日期<br>
     * 这里的"格式"我们可以随意的指定，这里指定为 "yyyy-MM-dd-HH-mm",输出如下：<br>
     * ****************** result : 2013-04-24-16-41
     */
    public static String getDateOfTodayA(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取当前的时间<br>
     */
    public static String getTiem() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(new Date());

    }

    /**
     * 获取当前的时间<br>
     */
    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(new Date());

    }

    public static String convertAll(long mill) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String dates = formatter.format(new Date(mill * 1000l));
        return dates;
    }

}
