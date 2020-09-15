package org.fsk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/12 15:53
 */
@Builder
@Data
public class StockMaTable {
    private String maStName;
    private String chgRate;
    private String todayMA5;
    private String todayMA10;
    private String todayMA20;
    private String tomorrowMA5;
    private String afterDayMA5;
    private String tomorrowMA10;
    private String afterDayMA10;
    private String maShape;
    private String maRemark;
}
