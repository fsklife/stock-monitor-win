package org.fsk.pojo;

import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/12 16:48
 */
@Data
public class StockHistoryObject {
    private String day;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private String ma_price5;
    private String ma_volume5;
    private String ma_price10;
    private String ma_volume10;
    private String ma_price20;
    private String ma_volume20;
    private String ma_price30;
    private String ma_volume30;

}
