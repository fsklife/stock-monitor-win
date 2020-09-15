package org.fsk.pojo;

import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/10 14:10
 */
@Data
public class StockInfoObject {
    private String stCode;
    private String gpName;
    private String currentPrice;
    private String open;
    private String prevClose;
    private String chgRate;
    private String minPrice;
    private String maxPrice;
    private String volPrice;
    private String deltaPrice;
    private String date;
    private String time;
}
