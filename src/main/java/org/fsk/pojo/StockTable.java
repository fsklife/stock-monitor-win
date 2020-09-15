package org.fsk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author fanshk
 * @date 2020/9/11 10:41
 */
@Builder
@Data
public class StockTable {
    private String stCode;
    private String stName;
    private String stCurrPrice;
    private String stChg;
    private String stDeltaPrice;
    private String stVol;
    private String stMinPrice;
    private String stMaxPrice;
    private String currOpen;
    private String prevClose;
}
