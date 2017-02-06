package cn.swao.jinyao.util;

import java.text.*;
import java.util.Date;

/**
 * 
 * @author ShenJX
 * @date 2017年2月4日
 * @desc desc:
 */
public class DataUtils {

    public final static String sDateFormat = "yyyy-MM-dd";
    public final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 是否在日期范围内
     * 
     * @param date 当前的date
     * @param dataTimeStamp 数据的时间戳
     * @param day 天数范围
     * @return
     */
    public static boolean isWithinTheDateRange(Date date, Long dataTimeStamp, int day) {
        // 当前日期的天的时间戳
        long dayTimeStamp = date.getTime() / (60 * 60 * 24 * 1000);
        // 数据天的时间戳
        long timeStamp = dataTimeStamp / (60 * 60 * 24 * 1000);
        if (timeStamp >= dayTimeStamp - day) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否在日期范围内
     * 
     * @param date 当前的date
     * @param dataDate 数据的date
     * @param day 天数范围
     * @return
     */
    public static boolean isWithinTheDateRange(Date date, Date dataDate, int day) {
<<<<<<< HEAD
        // 当前日期的天的时间戳
        long dayTimeStamp = date.getTime() / (60 * 60 * 24 * 1000);
        // 数据天的时间戳
        long timeStamp = dataDate.getTime() / (60 * 60 * 24 * 1000);
        if (timeStamp >= dayTimeStamp - day) {
            return true;
        } else {
            return false;
        }
=======
        return isWithinTheDateRange(date,dataDate.getTime(),day);
>>>>>>> 6efeed88df099eb3808bf2e50fd4d69fe892fdae
    }

    public static long timeFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(sDateFormat);
        String dateString = sdf.format(new Date());
        return timeFormat(dateString + " " + time, dateTimeFormat);
    }

    public static long timeFormat(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            return 0L;
        }

    }
}
