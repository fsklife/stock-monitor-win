package org.fsk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtil {

    public static final String DATE_STYLE_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_STYLE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_STYLE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_STYLE_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_STYLE_HHMMSS = "HHmmss";
    public static final String DATE_STYLE_HH_MM_SS = "HH:mm:ss";
    public static final String DATE_STYLE_YYYYMMDD_DOT = "yyyy.MM.dd";
    public static final String DATE_STYLE_YYYYMM_DOT = "yyyy.MM";
    public static final String DATE_STYLE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String MONTH = "MONTH";
    public static final String DAY = "DAY";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";

    /**
     * 获取系统当前时间（毫秒）
     *
     * @return int
     */
    public static Long getCurrentSecond() {
        return System.currentTimeMillis();
    }

    public static String getFormatTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getYYYYmmddHHmmss() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_STYLE_YYYY_MM_DD_HH_MM_SS);
        return sdf.format(date);
    }

    public static String getYYYYmmdd() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_STYLE_YYYYMMDD);
        return sdf.format(date);
    }

    public static String getHHmmss() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_STYLE_HHMMSS);
        return sdf2.format(date);
    }

    public static String getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return DateFormatUtils.format(calendar.getTime(), DATE_STYLE_YYYYMMDD_DOT);
    }

    public static String getNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return DateFormatUtils.format(calendar.getTime(), DATE_STYLE_YYYYMM_DOT);
    }


    /**
     * 获取上一天凌晨00：00：00的时间毫秒数
     *
     * @return
     */
    public static Long getLastDayZero() {
        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        Calendar calendar2 = new GregorianCalendar(year, month, date - 1);
        return calendar2.getTime().getTime();
    }

    /**
     * 获取本月第一天凌晨00：00：00的时间
     *
     * @return
     */
    public static Date getLastMontZero() {
        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, 1, 0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天凌晨00：00：00的时间毫秒数
     *
     * @return
     */
    public static Long getCurDayZero() {
        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        calendar.set(year, month, date, 0, 0, 0);
        return (calendar.getTime().getTime());
    }

    /**
     * 获取某年的第几周
     *
     * @param date
     * @return
     */
    public static Integer getWeeks(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            //美国是以周日为每周的第一天 现把周一设成第一天
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(date);
            return calendar.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取年
     *
     * @param date
     * @return
     */
    public static Integer getYear(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取月
     *
     * @param date
     * @return
     */
    public static Integer getMonth(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取日
     *
     * @param date
     * @return
     */
    public static Integer getDay(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取时
     *
     * @param date
     * @return
     */
    public static Integer getHour(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.HOUR_OF_DAY);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取分
     *
     * @param date
     * @return
     */
    public static Integer getMinute(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MINUTE);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取秒
     *
     * @param date
     * @return
     */
    public static Integer getSecond(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.SECOND);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 获取季度
     *
     * @param date
     * @return
     */
    public static Integer getQuarter(Date date) {
        Integer quarter = null;
        Integer m = getMonth(date);
        if (m >= 1 && m <= 3) {
            quarter = 1;
        }
        if (m >= 4 && m <= 6) {
            quarter = 2;
        }
        if (m >= 7 && m <= 9) {
            quarter = 3;
        }
        if (m >= 10 && m <= 12) {
            quarter = 4;
        }
        return quarter;
    }


    /**
     * 根据日期取得星期几
     *
     * @param date
     * @return
     */
    public static String getDayWeek(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weeks[getDayWeekIndex(date)];
    }

    /**
     * 根据日期取得星期几
     *
     * @param date
     * @return
     */
    public static int getDayWeekIndex(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        return weekIndex;
    }


    public static Date parseDate(String date) {
        return parseDate(date, DATE_STYLE_YYYY_MM_DD_HH_MM_SS);
    }

    public static Date parseUsDate(String date) {
        return parseUsDate(date, null);
    }

    public static Date parseUsDate(String date, String dateStyle) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        if (StringUtils.isBlank(dateStyle)) {
            dateStyle = "EEE MMM dd HH:mm:ss z yyyy";
        }
        SimpleDateFormat df = new SimpleDateFormat(dateStyle, Locale.US);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    public static Date parseDate(String date, String dateStyle) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(dateStyle);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param format
     * @return
     */
    public static Calendar parseCalendar(String date, String format) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date d = df.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c;
        } catch (ParseException e) {
            log.error(e.getMessage() + " [date={}, format={}]", date, format);
        }
        return null;
    }

    public static String addDays(String startdate, int duration) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_STYLE_YYYYMMDD);
        Date parse;
        try {
            parse = df.parse(startdate);
        } catch (ParseException e) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(parse);
        calendar.add(GregorianCalendar.DATE, duration);
        return df.format(calendar.getTime());
    }

    public static Date addMonths(Date date, int duration) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.MONTH, duration);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int duration) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.DATE, duration);
        return calendar.getTime();
    }

    public static Date addMinutes(Date date, int duration) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.MINUTE, duration);
        return calendar.getTime();
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差天数
     */
    public static int getIntervalDays(Date date, Date otherDate) {
        long time = Math.abs(date.getTime() - otherDate.getTime());
        return (int) (time / (24 * 60 * 60 * 1000));
    }

    /**
     * 获取两个日期相差的天数
     * 以yyyy-MM-dd的格式进行计算，例如2015-12-02与2015-12-01相差一天
     *
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差天数
     */
    public static int getIntervalDaysUseDate(Date date, Date otherDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_STYLE_YYYY_MM_DD);
        try {
            date = sdf.parse(sdf.format(date));
            otherDate = sdf.parse(sdf.format(otherDate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long time1 = cal.getTimeInMillis();
            cal.setTime(otherDate);
            long time2 = cal.getTimeInMillis();
            long betweenDays = Math.abs((time2 - time1) / (1000 * 3600 * 24));

            return (int) betweenDays;
        } catch (ParseException e) {
            log.error("计算日期差异常.", e);
        }

        return 0;
    }

    /**
     * 计算某日期是否在指定时间段内
     *
     * @param start 开始日期
     * @param end   结束日期
     * @param date  某日期
     * @return
     */
    public static boolean getDateBetween(Date start, Date end, Date date) {
        if (start == null || end == null || date == null) {
            log.error("判断有参数为空。[start={},end={},date={}]", start, end, date);
            return false;
        }
        long stal = start.getTime();
        long endl = end.getTime();
        long datel = date.getTime();
        if (stal <= datel && datel <= endl) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获得当前日期yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate() {
        Date date = new Date();
        return format(date, DATE_STYLE_YYYY_MM_DD);
    }

    /**
     * 获得当前日期yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDateTime() {
        Date date = new Date();
        return format(date, DATE_STYLE_YYYY_MM_DD_HH_MM_SS);
    }

    public static String format(Date date) {
        return format(date, DATE_STYLE_YYYY_MM_DD_HH_MM_SS);
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 两日期相差时间
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static Long timeDiff(Date startTime, Date endTime, String format) {
        //一天的毫秒数
        long nd = 1000 * 24 * 60 * 60;
        //一小时的毫秒数
        long nh = 1000 * 60 * 60;
        //一分钟的毫秒数
        long nm = 1000 * 60;
        //一秒钟的毫秒数long diff
        long ns = 1000;
        //获得两个时间的毫秒时间差异
        Long diff = endTime.getTime() - startTime.getTime();
        if (DAY.equals(format)) {
            return diff / nd;
        } else if (HOUR.equals(format)) {
            return diff / nh;
        } else if (MINUTE.equals(format)) {
            return diff / nm;
        } else if (SECOND.equals(format)) {
            return diff / ns;
        }
        throw new IllegalArgumentException("format格式不正确");
    }

    public static Date addYear(Date date, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, amount);
            return c.getTime();
        }
    }

    public static boolean isExpired(Date date) {
        if (date.compareTo(new Date()) < 0) {
            return true;
        }

        return false;
    }

    /**
     * 日期转化为cron表达式
     *
     * @param date
     * @return
     */
    public static String getCron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return format(date, dateFormat);
    }

    /**
     * cron表达式转为日期
     *
     * @param cron
     * @return
     */
    public static Date getCronToDate(String cron) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * 世界标准时间格式（yyyy-MM-dd'T'HH:mm:ss.SSS Z）处理
     *
     * @param zTime 2020-08-10T05:51:30.000Z
     * @return 2020-08-10 13:51:30
     */
    private Date convertZTime(String zTime) {
        try {
            String tempTime = zTime.replace("Z", " UTC");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            return sdf.parse(tempTime);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取日期格式
     *
     * @param date
     * @return
     */
    public static String getDateStyle(String date) {
        String timeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        // yyyy-MM-dd HH:mm:ss
        boolean flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return DATE_STYLE_YYYY_MM_DD_HH_MM_SS;
        }

        timeRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))-02-29)$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return DATE_STYLE_YYYY_MM_DD;
        }

        timeRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})\\/(((0[13578]|1[02])\\/(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)\\/(0[1-9]|[12][0-9]|30))|(02\\/(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))\\/02\\/29)$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return "yyyy/MM/dd";
        }

        timeRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))0229)$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return DATE_STYLE_YYYYMMDD;
        }

        timeRegex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})\\/(((0[13578]|1[02])\\/(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)\\/(0[1-9]|[12][0-9]|30))|(02\\/(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))\\/02\\/29))\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return "yyyy/MM/dd HH:mm:ss";
        }

        timeRegex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))" +
                "([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return DATE_STYLE_YYYYMMDDHHMMSS;
        }
        timeRegex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))0229))([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])([0-9]{3})$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return "yyyyMMddHHmmssSSS";
        }
        timeRegex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))0229))\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        flag = Pattern.matches(timeRegex, date);
        if (flag) {
            return "yyyyMMdd HH:mm:ss";
        }

        return null;
    }

    public static void main(String[] args) {
        Date fix = DateUtil.parseDate("2022-5-5 5:5:5");
        System.out.println(timeDiff(new Date(), fix, SECOND));
    }

}
