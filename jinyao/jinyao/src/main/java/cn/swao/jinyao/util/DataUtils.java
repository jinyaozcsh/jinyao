package cn.swao.jinyao.util;

import java.util.Date;

/**
 * 
 * @author ShenJX
 * @date 2017年2月4日
 * @desc desc:
 */
public class DataUtils {

    /**
     * 是否在日期范围内
     * 
     * @param date 当前的date
     * @param dataTimeStamp  数据的时间戳
     * @param day 天数范围
     * @return
     */
    public static boolean isWithinTheDateRange(Date date, Long dataTimeStamp, int day) {
        // 当前日期的天的时间戳
        long dayTimeStamp = date.getTime() / (60 * 60 * 24 *1000);
        // 数据天的时间戳
        long timeStamp = dataTimeStamp / (60 * 60 * 24*1000);
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
        return isWithinTheDateRange(date,dataDate.getTime(),day);
    }
}
