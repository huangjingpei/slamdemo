package com.hiscene.hiarslamdemo.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jucf on 2017/7/25.
 */

public class TimeUtils {

    /**
     * 将毫秒时间转换成 hh:mm:ss
     *
     * @param time
     *            :毫秒数
     * @return hh:mm:ss格式的时间字符串
     */
    public static String getTime(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");//24小时制
        return simpleDateFormat.format(time);
    }

    public static String millis2Date(long millisecond){
//        long millisecond = 1483159625851l;
        Date date = new Date(millisecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return format.format(date);

    }

    public static long date2Millis(String date){
        //输入日期，转化为毫秒数，用DATE方法()
        /**
         * 先用SimpleDateFormat.parse() 方法将日期字符串转化为Date格式
         * 通过Date.getTime()方法，将其转化为毫秒数
         */
//        String date = "2001-03-15 15-37-05";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");//24小时制
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");//12小时制
        long time2 = 0;
        try {
            time2 = simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time2;

    }

}
