package com.huoli.flight.recommend.util;


import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by roverll on 16/02/2017.
 */
public class DateUtil {

    public static final String YYYY_MM_DD_T = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String HH_mm = "HH:mm";

    private static final Map<String, DateTimeFormatter> FORMATTER_MAP =
            new ConcurrentHashMap<>();

    static {
        FORMATTER_MAP.put(YYYY_MM_DD_T, DateTimeFormatter.ofPattern(YYYY_MM_DD_T));
    }

    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_MAP.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String dateTime, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return LocalDateTime.parse(dateTime, formatter);
    }


    public static LocalDateTime parse(String dateTime) {
        return parse(dateTime, YYYY_MM_DD_T);
    }

    public static LocalDate parseLocalDate(String date, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDate parseLocalDate(String date) {
        return parseLocalDate(date, YYYY_MM_DD);
    }

    public static Date parseDate(String date) {
        return localDateToDate(parseLocalDate(date));
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static long getTimestamp(String dateTime) {
        return getTime(parse(dateTime));
    }

    public static long getTimestamp(String dateTime, ZoneId zoneId) {
        return getTime(parse(dateTime), zoneId);
    }

    public static long getTimestamp(String dateTime, ZoneOffset offset) {
        return getTime(parse(dateTime), offset);
    }

    public static long getTime(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long getTime(LocalDateTime localDateTime, ZoneOffset offset) {
        return localDateTime.atOffset(offset).toInstant().toEpochMilli();
    }

    public static long getTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(DateUtil.getFormatter(DateUtil.YYYY_MM_DD_T));
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateUtil.getFormatter(pattern));
    }

    public static String formatDate(LocalDate localDate) {
        return localDate.format(DateUtil.getFormatter(DateUtil.YYYY_MM_DD));
    }


    /* old api */

    /**
     * 使用预设Format格式化Date成字符串
     */
    public static String format(Date date) {
        return date == null ? "" : format(date, YYYY_MM_DD);
    }

    public static String formatDateTime(Date date) {
        return date == null ? "" : format(date, YYYY_MM_DD_T);
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String format(Date date, String pattern) {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }

    public static Date parseDate(String strDate, String pattern) {
        try {
            return (strDate == null || strDate.equals("")) ? null
                    : new SimpleDateFormat(pattern).parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 比较两个时间的差，返回分钟数，用时间1-时间2 生成日期：Aug 15, 2009
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 两个时间相差的分钟数
     */
    public static int getDateMinute(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return 0;

        long time1 = date1.getTime();
        long time2 = date2.getTime();

        long diff = time1 - time2;

        Long longValue = new Long(diff / (60 * 1000));

        int di = longValue.intValue();
        return di;
    }

    /**
     * 在日期上增加数个整日
     *
     * @param date 日期
     * @param n    天数
     * @return
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, n);
        return cal.getTime();
    }

    public static String toJAVAOffset(String skyDataOffset) {
        //GMT+3 GMT+8.75 GMT-9.25
        String np = skyDataOffset.substring(3, 4);
        String offset = skyDataOffset.substring(4, 5);
        int indexOfDot = skyDataOffset.indexOf('.');
        String offsetMinutesStr = "00";
        if (indexOfDot >= 0) {
            String offsetMinuteRawStr = skyDataOffset.substring(6);
            if (offsetMinuteRawStr.equals("25")) {
                offsetMinutesStr = "15";
            } else if (offsetMinuteRawStr.equals("5")) {
                offsetMinutesStr = "30";
            } else if (offsetMinuteRawStr.equals("75")) {
                offsetMinutesStr = "45";
            } else { //通用算法
                int offsetMinutes = 60 * Integer.parseInt(StringUtils.rightPad(offsetMinuteRawStr, 2, '0')) / 100;
                offsetMinutesStr = StringUtils.leftPad(String.valueOf(offsetMinutes), 2, '0');
            }
        }
        return String.format("%s%s:%s", np, StringUtils.leftPad(offset, 2, '0'), offsetMinutesStr);
    }

}
