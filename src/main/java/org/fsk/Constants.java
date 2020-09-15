package org.fsk;

import org.fsk.pojo.StockInfoObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author fanshk
 * @date 2020/9/10 14:41
 */
public class Constants {
    public static Map<String, StockInfoObject> monitorStockMap = new HashMap<>();
    public static StockInfoObject monitorSh = null;

    public static final String LEFT_ZKH = "[";
    public static final String RIGHT_ZKH = "]";

    public static final String SH = "sh";
    public static final String SZ = "sz";
    public static final String SH_CODE = "sh000001";
    public static final String SZ_CODE = "sz399001";
    public static final String CY_CODE = "sz399006";

    public static final int OPEN_TIME = 91500;
    public static final int CLOSE_TIME = 150300;
    public static final BigDecimal ZERO_BIG = new BigDecimal("0");

    /**
     * 日k线
     */
    public static final int DAY_KX = 240;
    /**
     * 120分钟K线
     */
    public static final int TWO_HOUR_KX = 120;
    /**
     * 60分钟
     */
    public static final int ONE_HOUR_KX = 60;
    /**
     * 30分钟
     */
    public static final int THR_MM_KX = 30;
    /**
     * 15分钟
     */
    public static final int FIFTEEN_MM_KX = 15;

    public static final String FILE_PATH = "D:\\stock_data";

    public static Set<String> stockSet = new TreeSet<>();

    static {
        /*stockSet.add(getStockCode("601788"));
        stockSet.add(getStockCode("000876"));*/
//        stockSet.add("sz159995");
    }

}
