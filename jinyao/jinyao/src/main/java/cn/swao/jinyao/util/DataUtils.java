package cn.swao.jinyao.util;

import java.text.*;
import java.util.Date;

import org.slf4j.*;

/**
 * 
 * @author ShenJX
 * @date 2017年2月4日
 * @desc desc:
 */
public class DataUtils {

    private static Logger log = LoggerFactory.getLogger(DataUtils.class);
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
        return isWithinTheDateRange(date, dataDate.getTime(), day);
    }

    public static long delayed(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(sDateFormat);
        String dateString = sdf.format(new Date());
        long timeFormat = timeFormat(dateString + " " + time, dateTimeFormat);
        long delay = timeFormat - System.currentTimeMillis();
        if (delay < 0) {
            delay = timeFormat + 60 * 60 * 24 * 1000;
        }
        return delay;
    }

    public static long timeFormat(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            log.info("format date fail", e);
            return 0L;
        }

    }
}
