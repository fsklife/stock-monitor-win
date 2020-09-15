package org.fsk.utils;

import org.fsk.Constants;
import org.fsk.pojo.StockInfoObject;

import java.util.Date;

/**
 * @author fanshk
 * @date 2020/9/15 14:30
 */
public class TransTimeUtil {
    public static boolean isTransTime() {
        Date date = new Date();
        int weekIndex = DateUtil.getDayWeekIndex(date);
        int timeInt = Integer.parseInt(DateUtil.getHHmmss());
        StockInfoObject monitorSh = Constants.monitorSh;
        int intervalDays = DateUtil.getIntervalDays(date, DateUtil.parseDate(monitorSh.getDate(), DateUtil.DATE_STYLE_YYYY_MM_DD));
        boolean transFlag =
                intervalDays == 0 && (weekIndex == 0 || weekIndex == 6 || (timeInt >= Constants.OPEN_TIME && timeInt <= Constants.CLOSE_TIME));
        return transFlag;
    }

    public static boolean isLunchBreak() {
        int timeInt = Integer.parseInt(DateUtil.getHHmmss());
        return timeInt >= 113001 && timeInt <= 130001;
    }

    public static boolean isTodayBeforeOpenTime() {
        Date date = new Date();
        int timeInt = Integer.parseInt(DateUtil.getHHmmss());
        StockInfoObject monitorSh = Constants.monitorSh;
        int intervalDays = DateUtil.getIntervalDays(date, DateUtil.parseDate(monitorSh.getDate(), DateUtil.DATE_STYLE_YYYY_MM_DD));
        boolean transFlag =
                intervalDays == 0 && (timeInt < Constants.OPEN_TIME);
        return transFlag;
    }

    public static boolean isTodayCloseTime() {
        Date date = new Date();
        int timeInt = Integer.parseInt(DateUtil.getHHmmss());
        StockInfoObject monitorSh = Constants.monitorSh;
        int intervalDays = DateUtil.getIntervalDays(date, DateUtil.parseDate(monitorSh.getDate(), DateUtil.DATE_STYLE_YYYY_MM_DD));
        boolean transFlag =
                intervalDays == 0 && (timeInt > Constants.CLOSE_TIME);
        return transFlag;
    }

    public static boolean isNotTodayTime() {
        Date date = new Date();
        StockInfoObject monitorSh = Constants.monitorSh;
        int intervalDays = DateUtil.getIntervalDays(date, DateUtil.parseDate(monitorSh.getDate(), DateUtil.DATE_STYLE_YYYY_MM_DD));
        return intervalDays != 0;
    }
}
